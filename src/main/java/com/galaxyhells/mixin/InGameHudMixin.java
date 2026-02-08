package com.galaxyhells.mixin;

import com.galaxyhells.SkyRiverConfig;
import com.galaxyhells.StatBarHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow private Text overlayMessage;

    // Cancela a Vida (Corações)
    // Na 1.21.1 os parâmetros de renderHealthBar são esses:
    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void onRenderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        if (SkyRiverConfig.barraVidaAtiva) {
            ci.cancel();
        }
    }

    // Cancela a Fome
    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void onRenderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        if (SkyRiverConfig.barraManaAtiva) {
            ci.cancel();
        }
    }

    // O culpado do seu crash: renderOverlayMessage
    // O Minecraft agora usa RenderTickCounter em vez de float tickDelta
    @Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void onRenderOverlayMessage(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.overlayMessage != null) {
            String msg = this.overlayMessage.getString();
            // Se for a mensagem de status, cancelamos a renderização desta vez
            // Se NÃO for (ex: mensagem de "Você não pode dormir agora"), o ci.cancel() NÃO é chamado.
            if (StatBarHandler.isStatusMessage(msg)) {
                ci.cancel();
            }
        }
    }
}