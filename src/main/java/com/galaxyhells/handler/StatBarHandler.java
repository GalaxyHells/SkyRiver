package com.galaxyhells.handler;

import com.galaxyhells.SkyRiverConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.math.MatrixStack;

public class StatBarHandler {
    private static float currentHp, maxHp, currentDefense, currentMana, maxMana;
    private static String healthText = "0/0";
    private static String defenseText = "0";
    private static String manaText = "0/0";

    private static final Pattern HP_PATTERN = Pattern.compile("(\\d+)/(\\d+)\\s*\\u2764");
    private static final Pattern DEF_PATTERN = Pattern.compile("(\\d+)\\s*\\u2747");
    private static final Pattern MANA_PATTERN = Pattern.compile("(\\d+)/(\\d+)\\s*\\u270e");

    public static void onActionbarMessage(Text message) {
        if (!SkyRiverConfig.statsBarsEnabled) return;
        String content = message.getString().replaceAll("(?i)§[0-9A-FK-ORX]", "");

        Matcher hpM = HP_PATTERN.matcher(content);
        if (hpM.find()) {
            currentHp = Float.parseFloat(hpM.group(1));
            maxHp = Float.parseFloat(hpM.group(2));
            healthText = hpM.group(1) + "/" + hpM.group(2);
        }
        Matcher defM = DEF_PATTERN.matcher(content);
        if (defM.find()) {
            currentDefense = Float.parseFloat(defM.group(1));
            defenseText = defM.group(1);
        }
        Matcher manaM = MANA_PATTERN.matcher(content);
        if (manaM.find()) {
            currentMana = Float.parseFloat(manaM.group(1));
            maxMana = Float.parseFloat(manaM.group(2));
            manaText = manaM.group(1) + "/" + manaM.group(2);
        }
    }

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden || !SkyRiverConfig.statsBarsEnabled) return;

            int w = client.getWindow().getScaledWidth();
            int h = client.getWindow().getScaledHeight();
            int cx = w / 2;

            // Timer do Boss (se existir)
//            long now = System.currentTimeMillis();
//            if (now - lastSecondMillis >= 1000) {
//                BossAlertHandler.tickTimer();
//                lastSecondMillis = now;
//            }

            // --- CONFIGURAÇÃO DE CORES E TAMANHOS ---
            int yBars = h - 39;
            int yDefense = h - 49;
            int colorHealth = 0xFFFF0000; // Vermelho
            int colorMana = 0xFF00AAFF;   // Azul Mana
            int colorDefense = 0xFF267C01; // Azul Armadura (🛡)

            // --- DESENHO DAS BARRAS (FUNDOS E PREENCHIMENTO) ---

            // 1. Vida (Lado Esquerdo)
            drawBar(context, cx - 91, yBars, 81, 9, currentHp, maxHp, colorHealth);

            // 2. Mana (Lado Direito)
            drawBar(context, cx + 10, yBars, 81, 9, currentMana, maxMana, colorMana);

            // 3. Defesa (Central - Menor)
            // Como não temos "Max Defesa", passamos currentDefense nos dois campos para a barra ficar cheia
            drawBar(context, cx - 25, yDefense, 50, 9, currentDefense, currentDefense, colorDefense);

            // --- DESENHO DOS TEXTOS (NA FRENTE) ---

            // Texto Vida
            renderSimpleText(context, client, healthText, cx - 91 + 40, yBars - 1);

            // Texto Mana
            renderSimpleText(context, client, manaText, cx + 10 + 40, yBars - 1);

            // Texto Defesa (Com o ícone de escudo)
            renderSimpleText(context, client, "§f🛡 " + (int)currentDefense, cx, yDefense - 1);

            renderMutantPointer(context, client, cx, h / 2);
        });
    }

    private static void drawBar(DrawContext context, int x, int y, int width, int height, float cur, float max, int color) {
        context.fill(x, y, x + width, y + height, 0xFF000000); // Borda/Fundo
        if (max > 0) {
            float pct = Math.max(0, Math.min(1.0f, cur / max));
            int fillW = (int)((width - 2) * pct);
            context.fill(x + 1, y + 1, x + 1 + fillW, y + height - 1, color);
        }
    }

    private static void renderSimpleText(DrawContext context, MinecraftClient client, String text, int centerX, int y) {
        if (text == null) return;
        int textWidth = client.textRenderer.getWidth(text);
        // Desenhando com sombra e cor branca pura (0xFFFFFFFF)
        context.drawTextWithShadow(client.textRenderer, text, centerX - (textWidth / 2), y, 0xFFFFFFFF);
    }

    private static void renderMutantPointer(DrawContext context, MinecraftClient client, int cx, int cy) {
        Entity target = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : client.world.getEntities()) {
            if (entity.getName().getString().contains("Mutante")) {
                double d = client.player.distanceTo(entity);
                if (d < closestDist) {
                    closestDist = d;
                    target = entity;
                }
            }
        }

        if (target != null) {
            double dx = target.getX() - client.player.getX();
            double dz = target.getZ() - client.player.getZ();

            float angleToTarget = (float) Math.atan2(dz, dx);
            float playerYaw = (float) Math.toRadians(client.player.getYaw() + 90);
            float finalAngle = angleToTarget - playerYaw;

            // 1. Pegamos a matriz que o jogo forneceu (Matrix3x2fStack)
            var matrices = context.getMatrices();

            // 2. Usamos os métodos específicos da JOML
            matrices.pushMatrix();

            // Move para o centro da tela (cx, cy)
            matrices.translate(cx, cy);

            // Gira no ângulo calculado (finalAngle já está em radianos)
            matrices.rotate(finalAngle);

            int color = (closestDist < 20) ? 0xFFFF0000 : 0xFFFFAA00;

            // 3. Desenho da Linha (Agora ela sofrerá a rotação da matriz)
            context.fill(20, -1, 45, 1, color);
            context.fill(43, -2, 45, 2, color);

            matrices.popMatrix(); // Fecha a matriz

            // Texto da distância (desenhado fora da matriz rotacionada para o texto não ficar de ponta-cabeça)
            String distTxt = (int)closestDist + "m";
            int tx = (int) (Math.cos(finalAngle) * 55);
            int ty = (int) (Math.sin(finalAngle) * 55);
            context.drawTextWithShadow(client.textRenderer, distTxt, cx + tx - (client.textRenderer.getWidth(distTxt)/2), cy + ty - 4, 0xFFFFFF);
        }
    }
}