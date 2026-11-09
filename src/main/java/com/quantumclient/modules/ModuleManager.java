package com.quantumclient.modules;

import com.quantumclient.modules.Module.Category;
import com.quantumclient.modules.impl.FovChanger;
import com.quantumclient.modules.impl.Fullbright;
import com.quantumclient.modules.impl.TimeChanger;
import com.quantumclient.modules.impl.WeatherChanger;
import com.quantumclient.modules.impl.Zoom;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Визуалы
        register(new Fullbright());
        register(new WeatherChanger());
        register(new TimeChanger());
        register(new FovChanger());
        register(new Zoom()); // после FovChanger, чтобы зум перебивал FOV

        // HUD (отрисовка лежит в HudRenderer)
        register(new Module("FPS", "FPS", "Счётчик кадров в секунду", Category.HUD).defaultOn());
        register(new Module("Ping", "Пинг", "Задержка до сервера", Category.HUD).defaultOn());
        register(new Module("Online", "Онлайн", "Количество игроков на сервере", Category.HUD).defaultOn());
        register(new Module("Coordinates", "Координаты", "Твои X, Y, Z", Category.HUD));
        register(new Module("Clock", "Часы", "Текущее время", Category.HUD));
        register(new Module("CPS", "CPS", "Клики в секунду на кнопках мыши", Category.HUD).defaultOn());
        register(new Module("Keystrokes", "Клавиши", "WASD и кнопки мыши на экране", Category.HUD).defaultOn());
        register(new Module("Watermark", "Водяной знак", "Название клиента в углу", Category.HUD).defaultOn());
    }

    private void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module getModule(String id) {
        for (Module m : modules) {
            if (m.id.equals(id)) {
                return m;
            }
        }
        return null;
    }

    public boolean isOn(String id) {
        Module m = getModule(id);
        return m != null && m.isEnabled();
    }

    public List<Module> getByCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module m : modules) {
            if (m.category == category) {
                result.add(m);
            }
        }
        return result;
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }

    /** При закрытии игры возвращаем всё, что меняли в настройках игры. */
    public void shutdown() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onDisable();
            }
        }
    }

    public void resetAll() {
        for (Module m : modules) {
            m.setEnabled(false);
            for (Setting s : m.settings) {
                s.reset();
            }
            if (m.defaultEnabled) {
                m.setEnabled(true);
            }
        }
    }
}
