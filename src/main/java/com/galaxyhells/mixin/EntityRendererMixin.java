//package com.galaxyhells.mixin;
//
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.entity.EntityRenderer;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.mob.EndermanEntity;
//import net.minecraft.text.Text;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(EntityRenderer.class)
//public abstract class EntityRendererMixin<T extends Entity> {
//
//    @Inject(method = "render", at = @At("HEAD"))
//    private void renderMutanteLabel(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
//        if (entity instanceof EndermanEntity && entity.getName().getString().toLowerCase().contains("mutante")) {
//
//            MinecraftClient client = MinecraftClient.getInstance();
//            if (client.player == null) return;
//
//            // Calcula a distância para o Waypoint
//            double dist = client.player.distanceTo(entity);
//
//            // Texto do Waypoint colorido (Roxo e Branco)
//            Text label = Text.literal("§5§l⚠ MUTANTE §f[" + (int)dist + "m]");
//
//            // Chama o método nativo de renderizar etiquetas (o mesmo dos Nicknames)
//            // Na 1.21.11, esse método é renderLabelIfPresent
//            float height = entity.getHeight() + 0.5f;
//
//            matrices.push();
//            matrices.translate(0.0D, height, 0.0D);
//
//            // O IntelliJ deve reconhecer este método. Se ele pedir 'displayContextualName', use ele.
//            renderLabelIfPresent(entity, label, matrices, vertexConsumers, light, tickDelta);
//
//            matrices.pop();
//        }
//    }
//
//    // Método dummy para o Mixin compilar
//    protected abstract void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta);
//}