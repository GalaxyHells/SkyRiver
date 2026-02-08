package com.galaxyhells;

public class VipFeaturesHandler {
    public static String handleChatColor(String message) {
        // Se você digitou "/g oi", o 'message' é "g oi"
        if (message.startsWith("g ")) {
            // Pegamos o que vem depois do "g "
            String texto = message.substring(2);

            // Retornamos exatamente o que o servidor espera, mas com a cor
            // Ex: "g &6oi"
            return "g &" + SkyRiverConfig.corVip + texto;
        }
        return message;
    }
}