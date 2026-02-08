package com.galaxyhells;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatBarHandler {
    private static float hpAtual, hpMax, manaAtual, manaMax;
    private static String textoVida = "0/0";
    private static String textoMana = "0/0";

    // Regex mais robusto para capturar números mesmo com cores ou símbolos
    private static final Pattern STATS_PATTERN = Pattern.compile("(\\d+)/(\\d+).*?(\\d+)/(\\d+)");

    public static void onActionbarMessage(Text message) {
        String content = message.getString();
        Matcher m = STATS_PATTERN.matcher(content);
        if (m.find()) {
            try {
                hpAtual = Float.parseFloat(m.group(1));
                hpMax = Float.parseFloat(m.group(2));
                textoVida = m.group(1) + "/" + m.group(2);

                manaAtual = Float.parseFloat(m.group(3));
                manaMax = Float.parseFloat(m.group(4));
                textoMana = m.group(3) + "/" + m.group(4);
            } catch (Exception ignored) {}
        }
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            int width = drawContext.getScaledWindowWidth();
            int height = drawContext.getScaledWindowHeight();

            // Barra de Vida
            if (SkyRiverConfig.barraVidaAtiva) {
                renderBar(drawContext, client, width / 2 - 91, height - 39, hpAtual, hpMax, textoVida, 0xFFFF0000);
            }
            // Barra de Mana
            if (SkyRiverConfig.barraManaAtiva) {
                renderBar(drawContext, client, width / 2 + 10, height - 39, manaAtual, manaMax, textoMana, 0xFF0792DE);
            }
        });
    }

    private static void renderBar(DrawContext context, MinecraftClient client, int x, int y, float atual, float max, String texto, int corBarra) {
        int larguraTotal = 81;
        int alturaTotal = 9;

        // 1. Desenha a Borda (Quase preta)
        context.fill(x, y, x + larguraTotal, y + alturaTotal, 0xFF121212);

        // 2. Desenha o Fundo (Preto)
        context.fill(x + 1, y + 1, x + larguraTotal - 1, y + alturaTotal - 1, 0xFF000000);

        // 3. Desenha o Progresso
        if (max > 0) {
            float percentual = Math.max(0, Math.min(1.0f, atual / max));
            int larguraProgresso = (int) ((larguraTotal - 2) * percentual);
            if (larguraProgresso > 0) {
                context.fill(x + 1, y + 1, x + 1 + larguraProgresso, y + alturaTotal - 1, corBarra);
            }
        }

        // 4. Desenha o Texto (Sem matrizes)
        // O DrawContext renderiza na ordem da chamada. Como o texto é o último, ele fica no topo.
        if (texto != null && !texto.isEmpty()) {
            int textoX = x + (larguraTotal / 2) - (client.textRenderer.getWidth(texto) / 2);
            // Desenhamos 1 pixel abaixo para centralizar visualmente na barra de 9px de altura
            context.drawTextWithShadow(client.textRenderer, texto, textoX, y - 1, 0xFFFFFFFF);
        }
    }

    public static boolean isStatusMessage(String content) {
        return content.contains("/") && (content.contains("❤") || content.toLowerCase().contains("mana"));
    }
}