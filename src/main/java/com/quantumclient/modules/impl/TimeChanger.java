package com.quantumclient.modules.impl;

import com.quantumclient.modules.Module;
import com.quantumclient.modules.Setting;
import net.minecraft.client.MinecraftClient;

/** Фиксирует время суток только на твоём экране. */
public class TimeChanger extends Module {
    private static final long[] TICKS = {1000L, 6000L, 12500L, 14500L, 18000L};

    private final Setting.Mode time;

    public TimeChanger() {
        super("Time", "Время суток", "Своё время суток на твоём экране", Category.VISUALS);
        time = add(new Setting.Mode("time", "Время", 4, "Утро", "День", "Закат", "Ночь", "Полночь"));
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null) {
            mc.world.setTimeOfDay(TICKS[time.index]);
        }
    }
}
