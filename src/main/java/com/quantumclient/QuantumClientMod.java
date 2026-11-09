package com.quantumclient;

import com.quantumclient.config.ConfigManager;
import com.quantumclient.gui.ClickGui;
import com.quantumclient.hud.HudRenderer;
import com.quantumclient.hud.HudLayout;
import com.quantumclient.hud.HudEditorScreen;
import com.quantumclient.modules.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantumClientMod implements ClientModInitializer {
    public static final String MOD_ID = "quantum-client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModuleManager moduleManager;
    public static ConfigManager configManager;

    private boolean wasRightShiftDown = false;
    private boolean wasF8Down = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Quantum Client initializing...");

        moduleManager = new ModuleManager();
        configManager = new ConfigManager();
        configManager.load(moduleManager);
        HudLayout.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Right Shift открывает и закрывает меню
            long window = client.getWindow().getHandle();
            boolean isDown = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
            if (isDown && !wasRightShiftDown) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGui());
                } else if (client.currentScreen instanceof ClickGui) {
                    client.setScreen(null);
                }
            }
            wasRightShiftDown = isDown;

            boolean f8Down = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_F8);
            if (f8Down && !wasF8Down && client.currentScreen == null) {
                client.setScreen(new HudEditorScreen());
            }
            wasF8Down = f8Down;

            if (client.player != null && client.world != null) {
                moduleManager.onTick();
            }
        });

        HudRenderCallback.EVENT.register(HudRenderer::render);

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            configManager.save(moduleManager);
            moduleManager.shutdown();
        });

        LOGGER.info("Quantum Client loaded! {} modules", moduleManager.getModules().size());
    }
}
