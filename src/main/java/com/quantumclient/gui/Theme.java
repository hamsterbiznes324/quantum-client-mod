package com.quantumclient.gui;

/** Цветовая тема клиента. */
public final class Theme {
    public static final String[] NAMES = {"Фиолетовый", "Розовый", "Синий", "Зелёный", "Оранжевый", "Красный"};
    public static final int[] COLORS = {0xFF9B5CFF, 0xFFFF5FA8, 0xFF4DA3FF, 0xFF3DDC84, 0xFFFFA23D, 0xFFFF4D5E};

    public static int index = 3;

    private Theme() {
    }

    public static int accent() {
        return COLORS[Math.max(0, Math.min(COLORS.length - 1, index))];
    }

    public static int accentDark() {
        return Draw.mix(accent(), 0xFF000000, 0.45f);
    }
}
