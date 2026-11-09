package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Prevents fall damage", "Player");
    }

    @Override
    public void onTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.getNetworkHandler() != null) {
            if (client.player.fallDistance > 2.0f && !client.player.isOnGround()) {
                client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.OnGroundOnly(true, client.player.horizontalCollision)
                );
            }
        }
    }
}
