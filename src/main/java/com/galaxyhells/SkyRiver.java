package com.galaxyhells;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkyRiver implements ModInitializer {

    @Override
    public void onInitialize() {
        SkyRiverConfig.load();
        StatBarHandler.register();

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) StatBarHandler.onActionbarMessage(message);
            else ChatAlertHandler.onChatMessage(message);
        });

        ClientSendMessageEvents.COMMAND.register(VipFeaturesHandler::handleChatColor);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("skyriver")
                    .then(ClientCommandManager.literal("alerta").executes(context -> {
                        SkyRiverConfig.alertaMencaoAtivo = !SkyRiverConfig.alertaMencaoAtivo;
                        SkyRiverConfig.save();

                        // Mensagem Bonita
                        context.getSource().sendFeedback(Text.literal("§b§lSkyRiver §8» §fAlertas de menção: ")
                                .append(SkyRiverConfig.alertaMencaoAtivo ? Text.literal("§a✔ ATIVADO") : Text.literal("§c✘ DESATIVADO")));
                        return 1;
                    }))

                    .then(ClientCommandManager.literal("status").executes(context -> {
                        SkyRiverConfig.barraVidaAtiva = !SkyRiverConfig.barraVidaAtiva;
                        SkyRiverConfig.barraManaAtiva = SkyRiverConfig.barraVidaAtiva;
                        SkyRiverConfig.save();

                        // Mensagem Bonita
                        context.getSource().sendFeedback(Text.literal("§b§lSkyRiver §8» §fBarras de HUD: ")
                                .append(SkyRiverConfig.barraVidaAtiva ? Text.literal("§a✔ ATIVADO") : Text.literal("§c✘ DESATIVADO")));
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("ajuda").executes(context -> {
                        sendHelp(context.getSource());
                        return 1;
                    }))
            );
        });
    }

    private void sendHelp(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("\n§b§lSkyRiver - Ajuda:"));
        source.sendFeedback(Text.literal("§e/skyriver alerta §7- Liga/Desliga menções."));
        source.sendFeedback(Text.literal("§e/skyriver status §7- Liga/Desliga as barras de HUD."));
        source.sendFeedback(Text.literal("§e/skyriver ajuda §7- Mostra esta lista.\n"));
    }
}