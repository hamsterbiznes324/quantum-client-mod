package com.quantumclient;

import com.quantumclient.config.ConfigManager;
import com.quantumclient.gui.ClickGui;
import com.quantumclient.hud.HudRenderer;
import com.quantumclient.modules.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantumClientMod implements ClientModInitializer {
    public static final String MOD_ID = "quantum-client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Quantum Client initializing...");

        configManager = new ConfigManager();
        moduleManager = new ModuleManager();
        configManager.load();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.quantum-client.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.quantum-client"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGui());
                }
            }
        });

        HudRenderCallback.EVENT.register(new HudRenderer());

        LOGGER.info("Quantum Client loaded! {} modules active", moduleManager.getModules().size());
    }
}
