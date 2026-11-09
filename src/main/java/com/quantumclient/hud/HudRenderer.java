package com.quantumclient.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class HudRenderer implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (client.options.hudHidden) return;

        PlayerEntity player = client.player;
        int x = 4;
        int y = 4;
        int lineHeight = 11;
        int textColor = 0xFFFFFF;
        int shadowColor = 0x55000000;

        // FPS counter
        int fps = client.getCurrentFps();
        drawText(context, client, "FPS: " + fps, x, y, fps > 60 ? 0x55FF55 : fps > 30 ? 0xFFFF55 : 0xFF5555);
        y += lineHeight;

        // Coordinates
        BlockPos pos = player.getBlockPos();
        drawText(context, client, String.format("XYZ: %d / %d / %d", pos.getX(), pos.getY(), pos.getZ()), x, y, textColor);
        y += lineHeight;

        // Direction
        String dir = getDirection(player.getYaw());
        drawText(context, client, "Facing: " + dir, x, y, 0xAAAAAA);
        y += lineHeight;

        // Speed
        double hSpeed = Math.sqrt(player.getVelocity().x * player.getVelocity().x + player.getVelocity().z * player.getVelocity().z);
        drawText(context, client, String.format("Speed: %.2f", hSpeed * 20), x, y, 0xAAAAAA);
        y += lineHeight;

        // Armor durability (bottom right area)
        int screenWidth = context.getScaledWindowWidth();
        int armorX = screenWidth - 100;
        int armorY = 4;
        int armorVal = player.getArmor();
        drawText(context, client, "Armor: " + armorVal, armorX, armorY, 0x88BBFF);

        // Health
        float health = player.getHealth();
        drawText(context, client, String.format("HP: %.1f", health), armorX, armorY + lineHeight, health > 10 ? 0x55FF55 : health > 5 ? 0xFFFF55 : 0xFF5555);
    }

    private void drawText(DrawContext context, MinecraftClient client, String text, int x, int y, int color) {
        context.drawText(client.textRenderer, text, x + 1, y + 1, 0x55000000, false);
        context.drawText(client.textRenderer, text, x, y, color, false);
    }

    private String getDirection(float yaw) {
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw < 22.5 || yaw >= 337.5) return "South";
        if (yaw < 67.5) return "SW";
        if (yaw < 112.5) return "West";
        if (yaw < 157.5) return "NW";
        if (yaw < 202.5) return "North";
        if (yaw < 247.5) return "NE";
        if (yaw < 292.5) return "East";
        return "SE";
    }
}
