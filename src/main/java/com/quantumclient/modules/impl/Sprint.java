package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import net.minecraft.client.MinecraftClient;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Always sprint automatically", "Movement");
    }

    @Override
    public void onTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.input != null) {
            if (client.player.input.movementForward > 0 && !client.player.isSprinting()) {
                client.player.setSprinting(true);
            }
        }
    }
}
