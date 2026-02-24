package com.galaxyhells.handler;

import com.galaxyhells.model.ProfileData;
import com.galaxyhells.screen.ProfileScreen;
import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProfileHandler {
    private static final Map<String, ProfileData> cache = new HashMap<>();
    private static final Gson gson = new Gson();
    private static final String API_URL = "https://skyapi.onrender.com/skyblock/player/profile?id=%s&key=UNLIMITED_KEY";

    public static void fetchProfile(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        String lowerName = name.toLowerCase();
        long now = System.currentTimeMillis();

        // 1. LÓGICA DE CACHE COM ATRASO DE SEGURANÇA (Para evitar erro do IPN)
        if (cache.containsKey(lowerName)) {
            ProfileData cached = cache.get(lowerName);
            if (now - cached.lastFetch < 300000 && cached.getActive() != null) {
                // Adicionamos um pequeno atraso de 100ms antes de abrir
                // Isso dá tempo para o Minecraft fechar o chat e o IPN terminar o tick dele
                CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS, client).execute(() -> {
                    client.setScreen(new ProfileScreen(cached));
                });
                return;
            }
        }

        // 2. BUSCA NA API
        client.player.sendMessage(Text.literal("§b§lSkyRiver §8» §fBuscando perfil de §e" + name + "..."), false);

        CompletableFuture.runAsync(() -> {
            try {
                HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(String.format(API_URL, name))).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                ProfileData data = gson.fromJson(response.body(), ProfileData.class);

                if (data != null && data.getActive() != null) {
                    data.lastFetch = System.currentTimeMillis();
                    cache.put(lowerName, data);

                    // Aqui o delay natural da rede já resolve o conflito
                    client.execute(() -> client.setScreen(new ProfileScreen(data)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}