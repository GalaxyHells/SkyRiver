package com.galaxyhells.handler;

import com.galaxyhells.SkyRiverConfig;

public class VipFeaturesHandler {

    public static String handleChatColor(String message) {
        // Support for /g (global) and /l (local)
        if (message.startsWith("g ") || message.startsWith("l ")) {
            String command = message.substring(0, 2); // "g " or "l "
            String content = message.substring(2);

            // If player already started with a color code, don't override it
            if (content.startsWith("&") || content.startsWith("§")) {
                return message;
            }

            return command + "&" + SkyRiverConfig.vipColor + content;
        }
        return message;
    }
}