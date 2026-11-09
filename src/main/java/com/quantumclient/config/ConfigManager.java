package com.quantumclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quantumclient.QuantumClientMod;
import com.quantumclient.gui.Theme;
import com.quantumclient.modules.Module;
import com.quantumclient.modules.ModuleManager;
import com.quantumclient.modules.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Сохраняет и загружает настройки в config/quantum-client.json. */
public class ConfigManager {
    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("quantum-client.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void load(ModuleManager manager) {
        if (!Files.exists(file)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("accent")) {
                Theme.index = Math.max(0, Math.min(Theme.COLORS.length - 1, root.get("accent").getAsInt()));
            }
            JsonObject mods = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();
            for (Module m : manager.getModules()) {
                if (!mods.has(m.id)) {
                    continue;
                }
                JsonObject o = mods.getAsJsonObject(m.id);
                for (Setting s : m.settings) {
                    if (o.has(s.id)) {
                        s.load(o.get(s.id));
                    }
                }
                if (o.has("enabled")) {
                    m.setEnabled(o.get("enabled").getAsBoolean());
                }
            }
        } catch (Exception e) {
            QuantumClientMod.LOGGER.warn("Не удалось прочитать конфиг, используются настройки по умолчанию", e);
        }
    }

    public void save(ModuleManager manager) {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("accent", Theme.index);
            JsonObject mods = new JsonObject();
            for (Module m : manager.getModules()) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                for (Setting s : m.settings) {
                    o.add(s.id, s.save());
                }
                mods.add(m.id, o);
            }
            root.add("modules", mods);
            Files.createDirectories(file.getParent());
            Files.writeString(file, gson.toJson(root), StandardCharsets.UTF_8);
        } catch (Exception e) {
            QuantumClientMod.LOGGER.warn("Не удалось сохранить конфиг", e);
        }
    }
}
