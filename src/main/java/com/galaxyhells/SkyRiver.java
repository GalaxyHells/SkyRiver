package com.galaxyhells;

import com.galaxyhells.handler.ChatAlertHandler;
import com.galaxyhells.handler.MutantTrackerHandler;
import com.galaxyhells.handler.StatBarHandler;
import com.galaxyhells.handler.VipFeaturesHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class SkyRiver implements ModInitializer {

    @Override
    public void onInitialize() {
        // Carrega configurações e registra handlers básicos
        SkyRiverConfig.load();
        StatBarHandler.register();
        MutantTrackerHandler.register();

        // Registro de eventos de mensagem
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) {
                StatBarHandler.onActionbarMessage(message);
            } else {
                ChatAlertHandler.onChatMessage(message);
            }
        });

        // Handler para cores VIP
        ClientSendMessageEvents.COMMAND.register(VipFeaturesHandler::handleChatColor);

        // Registro centralizado de comandos
        registerCommands();
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("skyriver")
                    .then(ClientCommandManager.literal("alerta").executes(context -> toggleConfig(context.getSource(), "Alertas de menção", "alertaMencaoAtivo")))
                    .then(ClientCommandManager.literal("status").executes(context -> toggleHud(context.getSource())))
                    .then(ClientCommandManager.literal("mutante").executes(context -> toggleConfig(context.getSource(), "Alerta de Mutante", "alertaMutanteAtivo")))
                    .then(ClientCommandManager.literal("glow").executes(context -> toggleConfig(context.getSource(), "Glow em Endermans Raros", "glowEndermanRaro")))
                    .then(ClientCommandManager.literal("resetmutante").executes(context -> {
                        MutantTrackerHandler.setMutantTimer(720);
                        sendFeedback(context.getSource(), "§fTimer do Mutante resetado para 12m!");
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("ajuda").executes(context -> {
                        sendHelp(context.getSource());
                        return 1;
                    }))
            );
        });
    }

    // Métodos auxiliares para manter o código limpo
    private int toggleConfig(FabricClientCommandSource source, String nome, String fieldName) {
        try {
            // Alterna o valor booleano via reflexão ou acesso direto (simplificado aqui)
            if (fieldName.equals("alertaMencaoAtivo")) SkyRiverConfig.mentionAlertEnabled = !SkyRiverConfig.mentionAlertEnabled;
            if (fieldName.equals("alertaMutanteAtivo")) SkyRiverConfig.bossAlertEnabled = !SkyRiverConfig.bossAlertEnabled;
            if (fieldName.equals("glowEndermanRaro")) SkyRiverConfig.rareEndermanGlow = !SkyRiverConfig.rareEndermanGlow;

            SkyRiverConfig.save();
            boolean ativo = getFieldValue(fieldName);
            sendFeedback(source, String.format("§f%s: %s", nome, ativo ? "§a✔ ATIVADO" : "§c✘ DESATIVADO"));
        } catch (Exception e) { e.printStackTrace(); }
        return 1;
    }

    private int toggleHud(FabricClientCommandSource source) {
        SkyRiverConfig.statsBarsEnabled = !SkyRiverConfig.statsBarsEnabled;
        SkyRiverConfig.save();
        sendFeedback(source, "§fBarras de HUD: " + (SkyRiverConfig.statsBarsEnabled ? "§a✔ ATIVADO" : "§c✘ DESATIVADO"));
        return 1;
    }

    private boolean getFieldValue(String fieldName) {
        if (fieldName.equals("alertaMencaoAtivo")) return SkyRiverConfig.mentionAlertEnabled;
        if (fieldName.equals("alertaMutanteAtivo")) return SkyRiverConfig.bossAlertEnabled;
        return SkyRiverConfig.rareEndermanGlow;
    }

    private void sendFeedback(FabricClientCommandSource source, String message) {
        source.sendFeedback(Text.literal("§b§lSkyRiver §8» " + message));
    }

    private void sendHelp(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("\n§b§lSkyRiver - Ajuda:"));
        source.sendFeedback(Text.literal("§e/skyriver ajuda §7- Lista de comandos."));
        source.sendFeedback(Text.literal("§e/skyriver alerta §7- Menções no chat."));
        source.sendFeedback(Text.literal("§e/skyriver status §7- HUD de Vida/Mana."));
        source.sendFeedback(Text.literal("§e/skyriver mutante §7- Alerta de spawn do Boss."));
        source.sendFeedback(Text.literal("§e/skyriver glow §7- Brilho em Endermans raros."));
        source.sendFeedback(Text.literal("§e/skyriver resetmutante §7- Reinicia o timer do Boss."));
    }
}