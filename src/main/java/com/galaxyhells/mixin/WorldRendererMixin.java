//package com.galaxyhells.mixin;
//
//import com.galaxyhells.SkyRiverConfig;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.render.*;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.mob.EndermanEntity;
//import net.minecraft.util.math.Vec3d;
//import org.joml.Matrix4f;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(WorldRenderer.class)
//public class WorldRendererMixin {
//
//    @Inject(method = "render", at = @At("RETURN"))
//    private void onRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.player == null || client.world == null || !SkyRiverConfig.alertaMutanteAtivo) return;
//
//        float tickDelta = tickCounter.getTickProgress(false);
//
//        // SEU CASO ESPECÍFICO: .getCameraPos()
//        Vec3d camPos = camera.getCameraPos();
//
//        MatrixStack matrices = new MatrixStack();
//        VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();
//
//        // CORREÇÃO: Tente usar getDebugLineStrip(1.0F) se getLines() não existir
//        VertexConsumer buffer = consumers.getBuffer(RenderLayer);
//
//        for (Entity entity : client.world.getEntities()) {
//            if (entity instanceof EndermanEntity && entity.getName().getString().toLowerCase().contains("mutante")) {
//
//                // SEU CASO ESPECÍFICO: .lastX / .lastY / .lastZ
//                double x = entity.lastX + (entity.getX() - entity.lastX) * tickDelta - camPos.x;
//                double y = entity.lastY + (entity.getY() - entity.lastY) * tickDelta + (entity.getHeight() / 2) - camPos.y;
//                double z = entity.lastZ + (entity.getZ() - entity.lastZ) * tickDelta - camPos.z;
//
//                Vec3d look = client.player.getRotationVec(tickDelta).multiply(0.2);
//                Matrix4f posMatrix = matrices.peek().getPositionMatrix();
//
//                // Desenha a linha
//                buffer.vertex(posMatrix, (float)look.x, (float)look.y, (float)look.z)
//                        .color(160, 0, 255, 255).normal(0, 1, 0);
//
//                buffer.vertex(posMatrix, (float)x, (float)y, (float)z)
//                        .color(160, 0, 255, 255).normal(0, 1, 0);
//            }
//        }
//
//        // Desenha na tela (Use a mesma camada que usou no getBuffer)
//        consumers.draw(RenderLayer.getDebugLineStrip(1.0F));
//    }
//}