package com.quantumclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.quantumclient.QuantumClientMod;
import com.quantumclient.modules.Module;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File configFile;

    public ConfigManager() {
        File gameDir = MinecraftClient.getInstance().runDirectory;
        File configDir = new File(gameDir, "config");
        configDir.mkdirs();
        configFile = new File(configDir, "quantum-client.json");
    }

    public void save() {
        if (QuantumClientMod.moduleManager == null) return;
        JsonObject root = new JsonObject();
        JsonObject modules = new JsonObject();
        for (Module module : QuantumClientMod.moduleManager.getModules()) {
            modules.addProperty(module.getName(), module.isEnabled());
        }
        root.add("modules", modules);
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            QuantumClientMod.LOGGER.error("Failed to save config", e);
        }
    }

    public void load() {
        if (!configFile.exists()) return;
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("modules")) return;
            JsonObject modules = root.getAsJsonObject("modules");
            for (Module module : QuantumClientMod.moduleManager.getModules()) {
                if (modules.has(module.getName())) {
                    boolean enabled = modules.get(module.getName()).getAsBoolean();
                    if (enabled != module.isEnabled()) {
                        module.setEnabled(enabled);
                    }
                }
            }
        } catch (IOException e) {
            QuantumClientMod.LOGGER.error("Failed to load config", e);
        }
    }
}
