package com.galaxyhells.handler;

import com.galaxyhells.SkyRiverConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatAlertHandler {

    public static void onChatMessage(Text message) {
        if (!SkyRiverConfig.mentionAlertEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        String myNick = client.player.getName().getString();
        String cleanMessage = message.getString().replaceAll("(?i)§[0-9A-FK-ORX]", "");

        if (isOwnMessage(cleanMessage, myNick)) return;

        if (cleanMessage.toLowerCase().contains(myNick.toLowerCase())) {
            client.execute(() -> {
                client.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                client.inGameHud.setOverlayMessage(
                        Text.literal("§b§l(!) §fYou were mentioned in chat!").formatted(Formatting.AQUA),
                        false
                );
            });
        }
    }

    private static boolean isOwnMessage(String message, String myNick) {
        int separatorIndex = -1;
        String[] separators = {":", ">", "»"};

        for (String sep : separators) {
            int pos = message.indexOf(sep);
            if (pos != -1) {
                separatorIndex = pos;
                break;
            }
        }

        if (separatorIndex != -1) {
            String namePart = message.substring(0, separatorIndex);
            return namePart.contains(myNick);
        }
        return false;
    }
}