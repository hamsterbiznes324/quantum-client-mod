package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import com.quantumclient.modules.Setting;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Приближение: удерживай клавишу C (меняется в настройках управления). */
public class Zoom extends Module {
    private final KeyBinding key;
    private final Setting.Slider zoomFov;

    private boolean zooming;
    private int before = 70;

    public Zoom() {
        super("Zoom", "Зум", "Удерживай C, чтобы приблизить картинку", Category.VISUALS);
        zoomFov = add(new Setting.Slider("fov", "FOV зума", 10, 60, 30, 1, "°"));
        key = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.quantum-client.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.quantum-client"));
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) {
            return;
        }
        boolean held = key.isPressed();
        if (held && !zooming) {
            zooming = true;
            before = mc.options.getFov().getValue();
        }
        if (zooming) {
            if (held) {
                mc.options.getFov().setValue(zoomFov.asInt());
            } else {
                mc.options.getFov().setValue(before);
                zooming = false;
            }
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (zooming && mc.options != null) {
            mc.options.getFov().setValue(before);
        }
        zooming = false;
    }
}
