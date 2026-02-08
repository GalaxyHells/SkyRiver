package com.galaxyhells;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HealthBarHandler {
    private static float hpAtual = 0;
    private static float hpMax = 1;
    private static String textoExibicao = "0/0";
    private static final Pattern HP_PATTERN = Pattern.compile("(\\d+)/(\\d+)❤");

    public static void onActionbarMessage(Text message) {
        String raw = message.getString();
        Matcher m = HP_PATTERN.matcher(raw);
        if (m.find()) {
            hpAtual = Float.parseFloat(m.group(1));
            hpMax = Float.parseFloat(m.group(2));
            textoExibicao = m.group(1) + "/" + m.group(2);
        }
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!SkyRiverConfig.barraVidaAtiva) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            int width = drawContext.getScaledWindowWidth();
            int height = drawContext.getScaledWindowHeight();

            // Posição padrão dos corações (Lado esquerdo da hotbar)
            int x = width / 2 - 91;
            int y = height - 39;
            int larguraBarraTotal = 81;

            // 1. Desenha o fundo da barra (Preto semi-transparente)
            drawContext.fill(x, y, x + larguraBarraTotal, y + 9, 0x90000000);

            // 2. Calcula e desenha o progresso (Vermelho)
            float percentual = Math.min(1.0f, hpAtual / hpMax);
            int larguraProgresso = (int) (larguraBarraTotal * percentual);

            if (larguraProgresso > 0) {
                drawContext.fill(x, y, x + larguraProgresso, y + 9, 0xFFFF5555);
            }

            // 3. Desenha o texto centralizado na barra
            int textoX = x + (larguraBarraTotal / 2) - (client.textRenderer.getWidth(textoExibicao) / 2);
            drawContext.drawTextWithShadow(client.textRenderer, textoExibicao, textoX, y + 1, 0xFFFFFF);
        });
    }
}