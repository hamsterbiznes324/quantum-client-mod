package com.quantumclient.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

/** Настройка модуля (слайдер или выбор из списка). */
public abstract class Setting {
    public final String id;
    public final String title;

    protected Setting(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public abstract JsonElement save();

    public abstract void load(JsonElement element);

    public abstract void reset();

    /** Числовой слайдер. */
    public static class Slider extends Setting {
        public final double min;
        public final double max;
        public final double def;
        public final double step;
        public final String suffix;
        public double value;

        public Slider(String id, String title, double min, double max, double def, double step, String suffix) {
            super(id, title);
            this.min = min;
            this.max = max;
            this.def = def;
            this.step = step;
            this.suffix = suffix;
            this.value = def;
        }

        public void set(double v) {
            v = Math.max(min, Math.min(max, v));
            if (step > 0) {
                v = Math.round(v / step) * step;
                v = Math.max(min, Math.min(max, v));
            }
            this.value = v;
        }

        public int asInt() {
            return (int) Math.round(value);
        }

        public float asFloat() {
            return (float) value;
        }

        public String display() {
            if (step >= 1) {
                return asInt() + suffix;
            }
            return String.format(Locale.ROOT, "%.1f%s", value, suffix);
        }

        @Override
        public JsonElement save() {
            return new JsonPrimitive(value);
        }

        @Override
        public void load(JsonElement element) {
            try {
                set(element.getAsDouble());
            } catch (Exception ignored) {
            }
        }

        @Override
        public void reset() {
            value = def;
        }
    }

    /** Выбор одного варианта из списка. */
    public static class Mode extends Setting {
        public final String[] options;
        public final int defIndex;
        public int index;

        public Mode(String id, String title, int defIndex, String... options) {
            super(id, title);
            this.options = options;
            this.defIndex = defIndex;
            this.index = defIndex;
        }

        public String get() {
            return options[index];
        }

        public void next() {
            index = (index + 1) % options.length;
        }

        public void prev() {
            index = (index - 1 + options.length) % options.length;
        }

        @Override
        public JsonElement save() {
            return new JsonPrimitive(index);
        }

        @Override
        public void load(JsonElement element) {
            try {
                int i = element.getAsInt();
                if (i >= 0 && i < options.length) {
                    index = i;
                }
            } catch (Exception ignored) {
            }
        }

        @Override
        public void reset() {
            index = defIndex;
        }
    }
}
