package com.galaxyhells;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.regex.Pattern;

public class ChatAlertHandler {
    public static void onChatMessage(Text message) {
        if (!SkyRiverConfig.alertaMencaoAtivo) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        String playerName = client.player.getName().getString();
        String content = message.getString();

        // REGEX: Procura seu nick seguido de qualquer coisa e um ":"
        // Isso pega "Nick:", "Nick >", "[MVP+] Nick:", "Nick [G]:" etc.
        Pattern autorPattern = Pattern.compile(".*" + Pattern.quote(playerName) + ".*[:>»].*");

        // Se a mensagem contiver o seu nick ANTES do primeiro ":" ou ">", ela é sua.
        // Dividimos a mensagem no primeiro ":" e vemos se seu nick está na parte da esquerda.
        String[] partes = content.split("[:>»]", 2);
        if (partes.length > 1 && partes[0].contains(playerName)) {
            return; // É sua mensagem, ignora.
        }

        // Se o nick aparecer no resto da mensagem, toca o alerta
        if (content.toLowerCase().contains(playerName.toLowerCase())) {
            client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            client.inGameHud.setOverlayMessage(
                    Text.literal("⚠ VOCÊ FOI MENCIONADO!").formatted(Formatting.YELLOW, Formatting.BOLD),
                    false
            );
        }
    }
}