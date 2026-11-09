package com.quantumclient.modules;

import com.quantumclient.modules.impl.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        register(new Fullbright());
        register(new Sprint());
        register(new NoHurtCam());
        register(new NoFall());
        register(new FastPlace());
        register(new AntiFireOverlay());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Module module : modules) {
                if (module.isEnabled()) {
                    module.onTick();
                }
            }
        });
    }

    private void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module getModule(String name) {
        return modules.stream()
            .filter(m -> m.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
}
