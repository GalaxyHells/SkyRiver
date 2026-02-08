package com.galaxyhells;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SkyRiverConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("skyriver.json").toFile();

    public static boolean alertaMencaoAtivo = true;
    public static String corVip = "6"; // Padrão: Gold
    public static boolean barraVidaAtiva = true; // Nova opção
    public static boolean barraManaAtiva = true; // Nova

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            ConfigData data = new ConfigData();
            data.alertaMencaoAtivo = alertaMencaoAtivo;
            data.corVip = corVip;
            data.barraVidaAtiva = barraVidaAtiva;
            data.barraManaAtiva = barraManaAtiva;
            GSON.toJson(data, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) { save(); return; }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                alertaMencaoAtivo = data.alertaMencaoAtivo;
                corVip = data.corVip != null ? data.corVip : "6";
                barraVidaAtiva = data.barraVidaAtiva;
                barraManaAtiva = data.barraManaAtiva;
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static class ConfigData {
        boolean alertaMencaoAtivo;
        String corVip;
        boolean barraVidaAtiva;
        boolean barraManaAtiva;
    }
}