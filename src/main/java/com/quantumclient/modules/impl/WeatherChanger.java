package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import com.quantumclient.modules.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

/** Меняет погоду только у тебя на экране. На сервере погода не меняется. */
public class WeatherChanger extends Module {
    private final Setting.Mode type;
    private final Setting.Slider intensity;

    private boolean captured;
    private float savedRain;
    private float savedThunder;

    public WeatherChanger() {
        super("Weather", "Погода", "Своя погода на твоём экране", Category.VISUALS);
        type = add(new Setting.Mode("type", "Тип погоды", 1, "Ясно", "Дождь", "Гроза"));
        intensity = add(new Setting.Slider("intensity", "Интенсивность", 10, 100, 70, 5, "%"));
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) {
            captured = false;
            return;
        }
        if (!captured) {
            savedRain = world.getRainGradient(1.0f);
            savedThunder = world.getThunderGradient(1.0f);
            captured = true;
        }
        float strength = intensity.asFloat() / 100.0f;
        switch (type.index) {
            case 0 -> {
                world.setRainGradient(0.0f);
                world.setThunderGradient(0.0f);
            }
            case 1 -> {
                world.setRainGradient(strength);
                world.setThunderGradient(0.0f);
            }
            default -> {
                world.setRainGradient(strength);
                world.setThunderGradient(strength);
            }
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world != null && captured) {
            world.setRainGradient(savedRain);
            world.setThunderGradient(savedThunder);
        }
        captured = false;
    }
}
