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

    // Novos nomes em inglês (Padrão)
    public static boolean mentionAlertEnabled = true;
    public static String vipColor = "6";
    public static boolean statsBarsEnabled = true; // Nova variável única
    public static boolean bossAlertEnabled = true;
    public static boolean rareEndermanGlow = true;

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            ConfigData data = new ConfigData();
            data.mentionAlertEnabled = mentionAlertEnabled;
            data.vipColor = vipColor;
            data.statsBarsEnabled = statsBarsEnabled;
            data.bossAlertEnabled = bossAlertEnabled;
            data.rareEndermanGlow = rareEndermanGlow;
            GSON.toJson(data, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) { save(); return; }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                mentionAlertEnabled = data.mentionAlertEnabled;
                vipColor = data.vipColor != null ? data.vipColor : "6";
                statsBarsEnabled = data.statsBarsEnabled;
                bossAlertEnabled = data.bossAlertEnabled;
                rareEndermanGlow = data.rareEndermanGlow;
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static class ConfigData {
        boolean mentionAlertEnabled;
        String vipColor;
        boolean statsBarsEnabled;
        boolean bossAlertEnabled;
        boolean rareEndermanGlow;
    }
}