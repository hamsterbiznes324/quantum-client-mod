package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import com.quantumclient.modules.Setting;
import net.minecraft.client.MinecraftClient;

/** Фиксация угла обзора (FOV). */
public class FovChanger extends Module {
    private final Setting.Slider fov;

    private boolean captured;
    private int original = 70;

    public FovChanger() {
        super("Fov", "Фиксация FOV", "Свой угол обзора", Category.VISUALS);
        fov = add(new Setting.Slider("fov", "FOV", 30, 110, 90, 1, "°"));
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) {
            return;
        }
        if (!captured) {
            original = mc.options.getFov().getValue();
            captured = true;
        }
        int target = fov.asInt();
        if (mc.options.getFov().getValue() != target) {
            mc.options.getFov().setValue(target);
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (captured && mc.options != null) {
            mc.options.getFov().setValue(original);
        }
        captured = false;
    }
}
