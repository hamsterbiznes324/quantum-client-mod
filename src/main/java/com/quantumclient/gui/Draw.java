package com.quantumclient.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Небольшие помощники для рисования интерфейса. */
public final class Draw {
    private Draw() {
    }

    /** Прямоугольник со скруглёнными углами. */
    public static void rrect(DrawContext c, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            c.fill(x, y, x + w, y + h, color);
            return;
        }
        c.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt((double) r * r - dy * dy));
            c.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    public static void text(DrawContext c, String s, int x, int y, int color) {
        c.drawText(MinecraftClient.getInstance().textRenderer, s, x, y, color, false);
    }

    public static void textShadow(DrawContext c, String s, int x, int y, int color) {
        c.drawText(MinecraftClient.getInstance().textRenderer, s, x, y, color, true);
    }

    public static void centered(DrawContext c, String s, int centerX, int y, int color) {
        text(c, s, centerX - width(s) / 2, y, color);
    }

    public static int width(String s) {
        return MinecraftClient.getInstance().textRenderer.getWidth(s);
    }

    public static String trim(String s, int maxWidth) {
        return MinecraftClient.getInstance().textRenderer.trimToWidth(s, maxWidth);
    }

    public static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    /** Меняет прозрачность цвета (a = 0..255). */
    public static int alpha(int rgb, int a) {
        return (a << 24) | (rgb & 0xFFFFFF);
    }

    /** Смешивает два цвета (t = 0..1). */
    public static int mix(int c1, int c2, float t) {
        int a = (int) (((c1 >>> 24) & 0xFF) * (1 - t) + ((c2 >>> 24) & 0xFF) * t);
        int r = (int) (((c1 >> 16) & 0xFF) * (1 - t) + ((c2 >> 16) & 0xFF) * t);
        int g = (int) (((c1 >> 8) & 0xFF) * (1 - t) + ((c2 >> 8) & 0xFF) * t);
        int b = (int) ((c1 & 0xFF) * (1 - t) + (c2 & 0xFF) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
