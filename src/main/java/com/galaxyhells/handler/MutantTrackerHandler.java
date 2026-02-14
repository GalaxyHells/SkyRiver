package com.galaxyhells.handler;

import com.galaxyhells.SkyRiverConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import org.joml.Matrix3x2fStack; // Importante para 1.21
import java.util.concurrent.TimeUnit;

public class MutantTrackerHandler {

    private static long mutantSpawnTime = 0;
    private static boolean active = false;

    // Métodos para o Timer (Antigo BossAlert)
    public static void setMutantTimer(int seconds) {
        mutantSpawnTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
        active = true;
    }

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null || client.options.hudHidden) return;
            if (!SkyRiverConfig.statsBarsEnabled) return; // Reutilizando o toggle global

            int cx = client.getWindow().getScaledWidth() / 2;
            int cy = client.getWindow().getScaledHeight() / 2;

            // 1. Renderizar Timer (Onde o mutante vai aparecer)
            renderCountdown(context, client, cx);

            // 2. Renderizar Rastreador (Onde o mutante está agora)
            renderPointer(context, client, cx, cy);
        });
    }

    private static void renderCountdown(DrawContext context, MinecraftClient client, int cx) {
        if (!active) return;

        long remaining = mutantSpawnTime - System.currentTimeMillis();
        if (remaining > 0) {
            long seconds = remaining / 1000;
            String text = String.format("§eMutante em: §f%02d:%02d", seconds / 60, seconds % 60);
            int tw = client.textRenderer.getWidth(text);
            context.drawTextWithShadow(client.textRenderer, text, cx - (tw / 2), 20, 0xFFFFFF);
        } else {
            active = false; // Timer acabou
        }
    }

    private static void renderPointer(DrawContext context, MinecraftClient client, int cx, int cy) {
        Entity mutant = null;
        double closestDist = Double.MAX_VALUE;

        // Busca o mutante mais próximo
        for (Entity entity : client.world.getEntities()) {
            if (entity.getName().getString().contains("Mutante")) {
                double d = client.player.distanceTo(entity);
                if (d < closestDist) {
                    closestDist = d;
                    mutant = entity;
                }
            }
        }

        if (mutant != null) {
            double dx = mutant.getX() - client.player.getX();
            double dz = mutant.getZ() - client.player.getZ();

            float angleToTarget = (float) Math.atan2(dz, dx);
            float playerYaw = (float) Math.toRadians(client.player.getYaw() + 90);
            float finalAngle = angleToTarget - playerYaw;

            // Lógica de Renderização 2D (JOML)
            Matrix3x2fStack matrices = context.getMatrices();
            matrices.pushMatrix();
            matrices.translate(cx, cy);
            matrices.rotate(finalAngle);

            // Cor: Laranja (Longe), Vermelho (Perto)
            int color = (closestDist < 20) ? 0xFFFF0000 : 0xFFFFAA00;

            // Linha do Ponteiro
            context.fill(25, -1, 50, 1, color); // Linha
            context.fill(48, -2, 50, 2, color); // Pontinha

            matrices.popMatrix();

            // Texto da Distância
            String distTxt = (int)closestDist + "m";
            int tx = (int) (Math.cos(finalAngle) * 60);
            int ty = (int) (Math.sin(finalAngle) * 60);
            context.drawTextWithShadow(client.textRenderer, distTxt, cx + tx - (client.textRenderer.getWidth(distTxt)/2), cy + ty - 4, 0xFFFFFF);
        }
    }
}