package com.quantumclient.hud;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Координаты HUD. Редактор сохраняет их локально, только на клиенте. */
public final class HudLayout {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("hamster-anime-hud.properties");
    public static int watermarkX = 6, watermarkY = 6;
    public static int infoX = -1, infoY = 6;
    public static int keysX = -1, keysY = 6;
    public static int offsetX = 0, offsetY = 0;

    private HudLayout() {}

    public static void load() {
        if (!Files.exists(FILE)) return;
        Properties p = new Properties();
        try (var in = Files.newInputStream(FILE)) {
            p.load(in);
            watermarkX = number(p, "watermarkX", watermarkX);
            watermarkY = number(p, "watermarkY", watermarkY);
            infoX = number(p, "infoX", infoX);
            infoY = number(p, "infoY", infoY);
            keysX = number(p, "keysX", keysX);
            keysY = number(p, "keysY", keysY);
            offsetX = number(p, "offsetX", offsetX);
            offsetY = number(p, "offsetY", offsetY);
        } catch (Exception ignored) {}
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("watermarkX", String.valueOf(watermarkX));
        p.setProperty("watermarkY", String.valueOf(watermarkY));
        p.setProperty("infoX", String.valueOf(infoX));
        p.setProperty("infoY", String.valueOf(infoY));
        p.setProperty("keysX", String.valueOf(keysX));
        p.setProperty("keysY", String.valueOf(keysY));
        p.setProperty("offsetX", String.valueOf(offsetX));
        p.setProperty("offsetY", String.valueOf(offsetY));
        try {
            Files.createDirectories(FILE.getParent());
            try (var out = Files.newOutputStream(FILE)) { p.store(out, "Hamster Anime HUD layout"); }
        } catch (Exception ignored) {}
    }

    private static int number(Properties p, String key, int fallback) {
        try { return Integer.parseInt(p.getProperty(key)); } catch (Exception e) { return fallback; }
    }
}
