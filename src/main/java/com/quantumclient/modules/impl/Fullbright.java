package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import net.minecraft.client.MinecraftClient;

public class Fullbright extends Module {
    private float savedGamma = 1.0f;

    public Fullbright() {
        super("Fullbright", "Maximum brightness everywhere", "Visuals");
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options != null) {
            savedGamma = (float)(double) client.options.getGamma().getValue();
            client.options.getGamma().setValue(100.0);
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options != null) {
            client.options.getGamma().setValue((double) savedGamma);
        }
    }
}
