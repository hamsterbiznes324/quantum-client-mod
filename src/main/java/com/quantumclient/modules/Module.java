package com.quantumclient.modules;

public abstract class Module {
    private final String name;
    private final String description;
    private final String category;
    private boolean enabled;

    public Module(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) onEnable();
        else onDisable();
        com.quantumclient.QuantumClientMod.configManager.save();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
}
