package com.quantumclient.hud;

import com.quantumclient.QuantumClientMod;
import com.quantumclient.gui.Draw;
import com.quantumclient.gui.Theme;
import com.quantumclient.modules.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Рисует информацию на экране: FPS, пинг, онлайн, клавиши, CPS и т.д. */
public final class HudRenderer {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private static final ArrayDeque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final ArrayDeque<Long> RIGHT_CLICKS = new ArrayDeque<>();
    private static boolean lastLeft;
    private static boolean lastRight;
    private static boolean leftDown;
    private static boolean rightDown;

    private HudRenderer() {
    }

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ModuleManager mm = QuantumClientMod.moduleManager;
        if (mm == null || mc.player == null || mc.options.hudHidden) {
            return;
        }

        pollMouse(mc);

        int accent = Theme.accent();
        int screenW = ctx.getScaledWindowWidth();

        // Водяной знак слева сверху
        if (mm.isOn("Watermark")) {
            String name = "Hamster_Anime";
            int w = Draw.width(name) + 16;
            int x = HudLayout.watermarkX + HudLayout.offsetX;
            int wy = HudLayout.watermarkY + HudLayout.offsetY;
            Draw.rrect(ctx, x - 1, wy - 1, w + 2, 18, 6, Draw.alpha(accent, 0x70));
            Draw.rrect(ctx, x, wy, w, 16, 5, 0xC0120B22);
            Draw.textShadow(ctx, "Hamster_Anime", x + 8, wy + 4, accent);
        }

        int y = 6;

        // Информационная панель
        List<String[]> rows = new ArrayList<>();
        if (mm.isOn("FPS")) {
            rows.add(new String[]{"FPS", String.valueOf(mc.getCurrentFps())});
        }
        if (mm.isOn("Ping")) {
            rows.add(new String[]{"Ping", getPing(mc) + "ms"});
        }
        if (mm.isOn("Online")) {
            rows.add(new String[]{"Online", String.valueOf(getOnline(mc))});
        }
        if (mm.isOn("Coordinates")) {
            rows.add(new String[]{"XYZ", mc.player.getBlockX() + " " + mc.player.getBlockY() + " " + mc.player.getBlockZ()});
        }
        if (mm.isOn("Clock")) {
            rows.add(new String[]{"Time", LocalTime.now().format(TIME)});
        }
        if (mm.isOn("CPS")) {
            rows.add(new String[]{"CPS", cps(LEFT_CLICKS) + " | " + cps(RIGHT_CLICKS)});
        }

        if (!rows.isEmpty()) {
            int textW = 0;
            for (String[] row : rows) {
                textW = Math.max(textW, Draw.width(row[0] + ": " + row[1]));
            }
            int w = textW + 16;
            int h = rows.size() * 11 + 8;
            int x = HudLayout.infoX < 0 ? screenW - w - 6 : HudLayout.infoX;
            y = HudLayout.infoY;
            x += HudLayout.offsetX;
            y += HudLayout.offsetY;
            Draw.rrect(ctx, x - 1, y - 1, w + 2, h + 2, 7, Draw.alpha(accent, 0x90));
            Draw.rrect(ctx, x, y, w, h, 6, 0xD0120B22);
            int ty = y + 5;
            for (String[] row : rows) {
                Draw.textShadow(ctx, row[0] + ":", x + 8, ty, accent);
                Draw.textShadow(ctx, row[1], x + 8 + Draw.width(row[0] + ": "), ty, 0xFFFFFFFF);
                ty += 11;
            }
            y += h + 8;
        }

        // Клавиши
        if (mm.isOn("Keystrokes")) {
            int total = 70;
            int x0 = HudLayout.keysX < 0 ? screenW - total - 6 : HudLayout.keysX;
            int keysY = HudLayout.keysY < 0 ? y : HudLayout.keysY;
            x0 += HudLayout.offsetX;
            keysY += HudLayout.offsetY;
            boolean showCps = mm.isOn("CPS");
            key(ctx, x0 + 24, keysY, 22, 22, "W", mc.options.forwardKey.isPressed(), null);
            key(ctx, x0, keysY + 24, 22, 22, "A", mc.options.leftKey.isPressed(), null);
            key(ctx, x0 + 24, keysY + 24, 22, 22, "S", mc.options.backKey.isPressed(), null);
            key(ctx, x0 + 48, keysY + 24, 22, 22, "D", mc.options.rightKey.isPressed(), null);
            key(ctx, x0, keysY + 48, 34, 22, "LMB", leftDown, showCps ? String.valueOf(cps(LEFT_CLICKS)) : null);
            key(ctx, x0 + 36, keysY + 48, 34, 22, "RMB", rightDown, showCps ? String.valueOf(cps(RIGHT_CLICKS)) : null);
        }
    }

    private static void key(DrawContext ctx, int x, int y, int w, int h, String label, boolean pressed, String sub) {
        int accent = Theme.accent();
        Draw.rrect(ctx, x - 1, y - 1, w + 2, h + 2, 6, Draw.alpha(accent, pressed ? 0xFF : 0x70));
        Draw.rrect(ctx, x, y, w, h, 5, pressed ? Draw.alpha(accent, 0xE0) : 0xD0120B22);
        int textColor = 0xFFFFFFFF;
        if (sub == null) {
            Draw.centered(ctx, label, x + w / 2, y + (h - 8) / 2, textColor);
        } else {
            Draw.centered(ctx, label, x + w / 2, y + 3, textColor);
            Draw.centered(ctx, sub, x + w / 2, y + 12, pressed ? 0xFFFFFFFF : accent);
        }
    }

    private static void pollMouse(MinecraftClient mc) {
        long window = mc.getWindow().getHandle();
        boolean free = mc.currentScreen == null;
        leftDown = free && GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        rightDown = free && GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (leftDown && !lastLeft) {
            LEFT_CLICKS.add(now);
        }
        if (rightDown && !lastRight) {
            RIGHT_CLICKS.add(now);
        }
        lastLeft = leftDown;
        lastRight = rightDown;
        prune(LEFT_CLICKS, now);
        prune(RIGHT_CLICKS, now);
    }

    private static void prune(ArrayDeque<Long> clicks, long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000L) {
            clicks.pollFirst();
        }
    }

    private static int cps(ArrayDeque<Long> clicks) {
        return clicks.size();
    }

    private static int getPing(MinecraftClient mc) {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null || mc.player == null) {
            return 0;
        }
        PlayerListEntry entry = handler.getPlayerListEntry(mc.player.getUuid());
        return entry == null ? 0 : entry.getLatency();
    }

    private static int getOnline(MinecraftClient mc) {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null) {
            return 1;
        }
        return Math.max(1, handler.getPlayerList().size());
    }
}
