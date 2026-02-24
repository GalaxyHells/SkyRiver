package com.galaxyhells.screen;

import com.galaxyhells.model.ProfileData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import java.util.Locale;

public class ProfileScreen extends Screen {
    private final ProfileData data;
    private final ProfileData.Profile active;

    public ProfileScreen(ProfileData data) {
        super(Text.literal("Profile Viewer"));
        this.data = data;
        this.active = (data != null) ? data.getActive() : null;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Corrigindo o fundo para não dar crash e ter boa visibilidade
        this.renderInGameBackground(context);

        if (active == null) return;

        // Centralização dinâmica
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 70;

        // Fundo da Janela (Preto levemente transparente)
        context.fill(x - 20, y - 20, x + 220, y + 150, 0xCC000000);
        context.fill(x - 20, y - 20, x + 220, y - 18, 0xFF55FF55); // Borda verde no topo

        // 1. Informações do Jogador (Texto em BRANCO com 0xFFFFFFFF)
        context.drawItem(new ItemStack(Items.PLAYER_HEAD), x, y);
        context.drawText(this.textRenderer, "§b§l" + data.name, x + 30, y, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "§7Perfil: §e" + active.profile_name, x + 30, y + 12, 0xFFFFFFFF, true);

        // Dinheiro (Purse)
        String purseText = String.format(Locale.US, "§6Purse: $%,.1f", active.data_model.bank_model.purse);
        context.drawText(this.textRenderer, purseText, x, y + 30, 0xFFFFFFFF, true);

        // 2. Grid de Skills (Coluna da Direita)
        int skillX = x + 110;
        var skills = active.data_model.skills_model.level;

        // Renderizando cada skill com cor branca e sombra
        drawSkill(context, skillX, y, Items.GOLDEN_HOE, "Farming", skills.getOrDefault("FARMING_SKILL", 0));
        drawSkill(context, skillX, y + 24, Items.DIAMOND_PICKAXE, "Mining", skills.getOrDefault("MINING_SKILL", 0));
        drawSkill(context, skillX, y + 48, Items.DIAMOND_SWORD, "Combat", skills.getOrDefault("COMBAT_SKILL", 0));
        drawSkill(context, skillX, y + 72, Items.FISHING_ROD, "Fishing", skills.getOrDefault("FISHING_SKILL", 0));
        drawSkill(context, skillX, y + 96, Items.DIAMOND_AXE, "Foraging", skills.getOrDefault("FORAGING_SKILL", 0));
        drawSkill(context, skillX, y + 120, Items.ENCHANTED_BOOK, "Enchant", skills.getOrDefault("ENCHANTING_SKILL", 0));

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawSkill(DrawContext context, int x, int y, net.minecraft.item.Item icon, String label, int level) {
        context.drawItem(new ItemStack(icon), x, y);
        // §f força o branco do Minecraft, 0xFFFFFFFF garante o branco do Java (Alpha 100%)
        context.drawText(this.textRenderer, "§f" + label, x + 22, y, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "§eNível " + level, x + 22, y + 10, 0xFFFFFFFF, true);
    }

    @Override
    public boolean shouldPause() { return false; }
}