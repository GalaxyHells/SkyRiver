package com.galaxyhells.mixin;

import com.galaxyhells.SkyRiverConfig;
import com.galaxyhells.handler.StatBarHandler;
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
    @Shadow private int overlayRemaining; // Controla quanto tempo a msg fica na tela

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void onRenderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        if (SkyRiverConfig.statsBarsEnabled) ci.cancel();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void onRenderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        if (SkyRiverConfig.statsBarsEnabled) ci.cancel();
    }

    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void onSetOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {
        if (message != null) {
            StatBarHandler.onActionbarMessage(message);
        }
    }

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void onRenderOverlayMessage(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (SkyRiverConfig.statsBarsEnabled && this.overlayMessage != null) {
            String content = this.overlayMessage.getString();

            // Lógica Inteligente:
            // Só esconde a Action Bar original se ela contiver os símbolos de RPG.
            // \u2764 = Coração, \u270e = Pena/Mana
            if (content.contains("\u2764") || content.contains("\u270e")) {
                ci.cancel();
            }
            // Se for mensagem de "Herbalismo +10", ela não tem coração, então o código passa
            // e o Minecraft desenha a mensagem normalmente.
        }
    }
}