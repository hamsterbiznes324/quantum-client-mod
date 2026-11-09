package com.quantumclient.modules;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public enum Category {
        VISUALS("Визуалы"),
        HUD("HUD");

        public final String title;

        Category(String title) {
            this.title = title;
        }
    }

    public final String id;
    public final String title;
    public final String description;
    public final Category category;
    public final List<Setting> settings = new ArrayList<>();

    private boolean enabled;
    public boolean defaultEnabled;

    public Module(String id, String title, String description, Category category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
    }

    /** Модуль включён по умолчанию. */
    public Module defaultOn() {
        this.defaultEnabled = true;
        this.enabled = true;
        return this;
    }

    protected <T extends Setting> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        if (value == enabled) {
            return;
        }
        enabled = value;
        if (value) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }
}
