package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import net.minecraft.client.MinecraftClient;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", "Remove delay between block placements", "Player");
    }

    @Override
    public void onTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            if (client.player.itemUseTimeLeft > 0) {
                client.player.itemUseTimeLeft = 0;
            }
        }
    }
}
