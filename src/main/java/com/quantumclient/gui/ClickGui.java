package com.quantumclient.gui;

import com.quantumclient.QuantumClientMod;
import com.quantumclient.modules.Module;
import com.quantumclient.modules.Module.Category;
import com.quantumclient.modules.ModuleManager;
import com.quantumclient.modules.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Главное меню клиента (открывается на Right Shift). */
public class ClickGui extends Screen {
    private static final String[] TAB_NAMES = {"Визуалы", "HUD", "Цвета", "Настройки"};
    private static final String[] TAB_DESC = {
            "Настрой свой внешний вид игры!",
            "Информация на экране",
            "Выбери цвет интерфейса",
            "Управление клиентом"
    };

    private static final int SIDEBAR = 130;
    private static final int COL_BG = 0xFF140C26;
    private static final int COL_SIDEBAR = 0xFF0E0819;
    private static final int COL_CARD = 0xFF1D1333;
    private static final int COL_TRACK = 0xFF3B3052;
    private static final int COL_MUTED = 0xFF8E84A8;
    private static final int COL_TEXT = 0xFFD6CCEE;

    private static int selectedTab = 0;

    private interface Click {
        void run(double mx, double my, int button);
    }

    private interface Drag {
        void run(double mx);
    }

    private record Hit(int x, int y, int w, int h, boolean scrolled, Click click, Drag drag) {
        boolean contains(double mx, double my) {
            return Draw.inside(mx, my, x, y, w, h);
        }
    }

    private final List<Hit> hits = new ArrayList<>();
    private Hit dragging;

    private double scroll;
    private int contentHeight;

    // Прямоугольник области с карточками
    private int cx;
    private int cy;
    private int cw;
    private int ch;

    public ClickGui() {
        super(Text.literal("Quantum Client"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void removed() {
        if (QuantumClientMod.configManager != null && QuantumClientMod.moduleManager != null) {
            QuantumClientMod.configManager.save(QuantumClientMod.moduleManager);
        }
        super.removed();
    }

    // ------------------------------------------------------------------ рисование

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        hits.clear();
        int accent = Theme.accent();

        ctx.fill(0, 0, width, height, 0x88000000);

        int gw = Math.min(width - 24, 580);
        int gh = Math.min(height - 24, 350);
        int gx = (width - gw) / 2;
        int gy = (height - gh) / 2;

        // Рамка и фон окна
        Draw.rrect(ctx, gx - 1, gy - 1, gw + 2, gh + 2, 9, Draw.alpha(accent, 0x90));
        Draw.rrect(ctx, gx, gy, gw, gh, 8, COL_BG);

        // Боковая панель
        Draw.rrect(ctx, gx, gy, SIDEBAR, gh, 8, COL_SIDEBAR);
        ctx.fill(gx + SIDEBAR - 8, gy, gx + SIDEBAR, gy + gh, COL_SIDEBAR);

        drawProfile(ctx, gx, gy);
        drawTabs(ctx, gx, gy, mouseX, mouseY);
        Draw.centered(ctx, "v1.0.0", gx + SIDEBAR / 2, gy + gh - 14, COL_MUTED);

        // Заголовок вкладки
        int hx = gx + SIDEBAR + 12;
        Draw.rrect(ctx, hx, gy + 10, 18, 18, 5, accent);
        Draw.textShadow(ctx, TAB_NAMES[selectedTab], hx + 26, gy + 10, 0xFFFFFFFF);
        Draw.text(ctx, TAB_DESC[selectedTab], hx + 26, gy + 21, accent);

        // Область содержимого
        cx = gx + SIDEBAR + 12;
        cy = gy + 44;
        cw = gw - SIDEBAR - 26;
        ch = gh - 44 - 10;

        int maxScroll = Math.max(0, contentHeight - ch);
        scroll = Math.max(0, Math.min(maxScroll, scroll));

        ctx.enableScissor(cx - 2, cy, cx + cw + 2, cy + ch);
        int used;
        switch (selectedTab) {
            case 0 -> used = drawModules(ctx, Category.VISUALS, mouseX, mouseY);
            case 1 -> used = drawModules(ctx, Category.HUD, mouseX, mouseY);
            case 2 -> used = drawColors(ctx, mouseX, mouseY);
            default -> used = drawSettings(ctx, mouseX, mouseY);
        }
        ctx.disableScissor();
        contentHeight = used;

        // Полоса прокрутки
        if (maxScroll > 0) {
            int bx = gx + gw - 7;
            Draw.rrect(ctx, bx, cy, 3, ch, 1, 0x33FFFFFF);
            int thumb = Math.max(14, ch * ch / contentHeight);
            int ty = cy + (int) ((ch - thumb) * (scroll / maxScroll));
            Draw.rrect(ctx, bx, ty, 3, thumb, 1, accent);
        }
    }

    private void drawProfile(DrawContext ctx, int gx, int gy) {
        int accent = Theme.accent();
        String name = "Player";
        if (client != null && client.getSession() != null) {
            name = client.getSession().getUsername();
        }
        Draw.rrect(ctx, gx + 10, gy + 10, 30, 30, 8, Theme.accentDark());
        Draw.rrect(ctx, gx + 11, gy + 11, 28, 28, 7, accent);
        String initial = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
        Draw.centered(ctx, initial, gx + 25, gy + 21, 0xFFFFFFFF);
        Draw.textShadow(ctx, Draw.trim(name, SIDEBAR - 56), gx + 46, gy + 14, 0xFFFFFFFF);
        Draw.text(ctx, "Visual Client", gx + 46, gy + 26, accent);
    }

    private void drawTabs(DrawContext ctx, int gx, int gy, int mouseX, int mouseY) {
        int accent = Theme.accent();
        int top = gy + 56;
        for (int i = 0; i < TAB_NAMES.length; i++) {
            int tx = gx + 8;
            int ty = top + i * 28;
            int tw = SIDEBAR - 16;
            int th = 24;
            boolean selected = i == selectedTab;
            boolean hover = Draw.inside(mouseX, mouseY, tx, ty, tw, th);
            if (selected) {
                Draw.rrect(ctx, tx, ty, tw, th, 6, accent);
            } else if (hover) {
                Draw.rrect(ctx, tx, ty, tw, th, 6, 0xFF221736);
            }
            Draw.rrect(ctx, tx + 8, ty + 8, 8, 8, 2, selected ? 0xFFFFFFFF : accent);
            Draw.text(ctx, TAB_NAMES[i], tx + 22, ty + 8, selected ? 0xFFFFFFFF : COL_TEXT);
            final int index = i;
            hits.add(new Hit(tx, ty, tw, th, false, (a, b, button) -> {
                if (button == 0) {
                    selectedTab = index;
                    scroll = 0;
                    contentHeight = 0;
                }
            }, null));
        }
    }

    /** Рисует карточки модулей категории в 1-2 колонки. Возвращает высоту содержимого. */
    private int drawModules(DrawContext ctx, Category category, int mouseX, int mouseY) {
        ModuleManager manager = QuantumClientMod.moduleManager;
        int cols = cw >= 320 ? 2 : 1;
        int gap = 8;
        int colW = (cw - gap * (cols - 1)) / cols;
        int start = cy - (int) scroll;
        int[] colY = new int[cols];
        for (int i = 0; i < cols; i++) {
            colY[i] = start;
        }
        for (Module m : manager.getByCategory(category)) {
            int col = 0;
            for (int i = 1; i < cols; i++) {
                if (colY[i] < colY[col]) {
                    col = i;
                }
            }
            int x = cx + col * (colW + gap);
            int h = drawModuleCard(ctx, m, x, colY[col], colW, mouseX, mouseY);
            colY[col] += h + gap;
        }
        int bottom = start;
        for (int y : colY) {
            bottom = Math.max(bottom, y);
        }
        return bottom - start;
    }

    private void card(DrawContext ctx, int x, int y, int w, int h, boolean highlight) {
        Draw.rrect(ctx, x - 1, y - 1, w + 2, h + 2, 7, highlight ? Draw.alpha(Theme.accent(), 0x90) : 0x30FFFFFF);
        Draw.rrect(ctx, x, y, w, h, 6, COL_CARD);
    }

    private int drawModuleCard(DrawContext ctx, Module m, int x, int y, int w, int mouseX, int mouseY) {
        int accent = Theme.accent();
        int h = 36 + m.settings.size() * 22 + 6;
        card(ctx, x, y, w, h, m.isEnabled());

        Draw.textShadow(ctx, m.title, x + 10, y + 8, 0xFFFFFFFF);
        Draw.text(ctx, Draw.trim(m.description, w - 20), x + 10, y + 22, COL_MUTED);

        // Переключатель
        int sx = x + w - 36;
        int sy = y + 7;
        Draw.rrect(ctx, sx, sy, 26, 12, 6, m.isEnabled() ? accent : COL_TRACK);
        Draw.rrect(ctx, m.isEnabled() ? sx + 15 : sx + 1, sy + 1, 10, 10, 5, 0xFFFFFFFF);
        hits.add(new Hit(x, y, w, 22, true, (a, b, button) -> {
            if (button == 0) {
                m.toggle();
            }
        }, null));

        int rowY = y + 36;
        for (Setting s : m.settings) {
            drawSetting(ctx, s, x, rowY, w, mouseX, mouseY);
            rowY += 22;
        }
        return h;
    }

    private void drawSetting(DrawContext ctx, Setting s, int x, int y, int w, int mouseX, int mouseY) {
        int accent = Theme.accent();
        Draw.text(ctx, s.title, x + 10, y + 7, COL_TEXT);

        int bx = x + w / 2 - 4;
        int bw = w - (w / 2 - 4) - 10;

        if (s instanceof Setting.Slider slider) {
            String value = slider.display();
            int vw = Draw.width(value);
            int tw = Math.max(20, bw - vw - 8);
            int ty = y + 10;
            double p = (slider.value - slider.min) / (slider.max - slider.min);
            int fw = (int) Math.round(tw * p);
            Draw.rrect(ctx, bx, ty, tw, 4, 2, COL_TRACK);
            if (fw > 0) {
                Draw.rrect(ctx, bx, ty, fw, 4, 2, accent);
            }
            Draw.rrect(ctx, bx + fw - 4, ty - 2, 8, 8, 4, 0xFFFFFFFF);
            Draw.text(ctx, value, x + w - 10 - vw, y + 7, 0xFFFFFFFF);

            final int trackX = bx;
            final int trackW = tw;
            hits.add(new Hit(bx - 4, y, tw + 8, 22, true, null, mx -> {
                double t = (mx - trackX) / (double) trackW;
                slider.set(slider.min + t * (slider.max - slider.min));
            }));
        } else if (s instanceof Setting.Mode mode) {
            boolean hover = Draw.inside(mouseX, mouseY, bx, y + 3, bw, 16);
            Draw.rrect(ctx, bx, y + 3, bw, 16, 5, hover ? 0xFF30244A : 0xFF271C3C);
            Draw.centered(ctx, mode.get(), bx + bw / 2, y + 7, 0xFFFFFFFF);
            Draw.text(ctx, "<", bx + 5, y + 7, accent);
            Draw.text(ctx, ">", bx + bw - 5 - Draw.width(">"), y + 7, accent);
            hits.add(new Hit(bx, y + 3, bw, 16, true, (a, b, button) -> {
                if (button == 0) {
                    mode.next();
                } else if (button == 1) {
                    mode.prev();
                }
            }, null));
        }
    }

    private int drawColors(DrawContext ctx, int mouseX, int mouseY) {
        int x = cx;
        int y = cy - (int) scroll;
        int w = cw;
        int h = 96;
        card(ctx, x, y, w, h, false);
        Draw.textShadow(ctx, "Акцентный цвет", x + 10, y + 8, 0xFFFFFFFF);
        Draw.text(ctx, "Цвет меню и HUD", x + 10, y + 22, COL_MUTED);

        int size = 26;
        int gap = 10;
        for (int i = 0; i < Theme.COLORS.length; i++) {
            int sx = x + 10 + i * (size + gap);
            int sy = y + 40;
            if (i == Theme.index) {
                Draw.rrect(ctx, sx - 2, sy - 2, size + 4, size + 4, 8, 0xFFFFFFFF);
            }
            Draw.rrect(ctx, sx, sy, size, size, 6, Theme.COLORS[i]);
            final int index = i;
            hits.add(new Hit(sx, sy, size, size, true, (a, b, button) -> {
                if (button == 0) {
                    Theme.index = index;
                }
            }, null));
        }
        Draw.text(ctx, "Выбрано: " + Theme.NAMES[Theme.index], x + 10, y + 78, Theme.accent());
        return h;
    }

    private int drawSettings(DrawContext ctx, int mouseX, int mouseY) {
        int x = cx;
        int y = cy - (int) scroll;
        int w = cw;
        int h = 132;
        card(ctx, x, y, w, h, false);
        Draw.textShadow(ctx, "Hamster_Anime", x + 10, y + 8, 0xFFFFFFFF);
        Draw.text(ctx, "Меню: Right Shift  |  HUD Editor: F8", x + 10, y + 26, COL_TEXT);
        Draw.text(ctx, "Зум: C (меняется в управлении)", x + 10, y + 40, COL_TEXT);
        Draw.text(ctx, "Конфиг: config/quantum-client.json", x + 10, y + 54, COL_MUTED);

        int bw = (w - 30) / 2;
        button(ctx, x + 10, y + 76, bw, 22, "Сохранить", mouseX, mouseY, false, () ->
                QuantumClientMod.configManager.save(QuantumClientMod.moduleManager));
        button(ctx, x + 20 + bw, y + 76, bw, 22, "Сбросить всё", mouseX, mouseY, true, () -> {
            QuantumClientMod.moduleManager.resetAll();
            Theme.index = 0;
            QuantumClientMod.configManager.save(QuantumClientMod.moduleManager);
        });
        Draw.text(ctx, "Настройки также сохраняются при закрытии меню.", x + 10, y + 108, COL_MUTED);
        return h;
    }

    private void button(DrawContext ctx, int x, int y, int w, int h, String label, int mouseX, int mouseY,
                        boolean danger, Runnable action) {
        boolean hover = Draw.inside(mouseX, mouseY, x, y, w, h);
        int base = danger ? 0xFF5A1F33 : Theme.accentDark();
        int color = hover ? Draw.mix(base, 0xFFFFFFFF, 0.15f) : base;
        Draw.rrect(ctx, x, y, w, h, 6, color);
        Draw.centered(ctx, label, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
        hits.add(new Hit(x, y, w, h, true, (a, b, button) -> {
            if (button == 0) {
                action.run();
            }
        }, null));
    }

    // ------------------------------------------------------------------ ввод

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit hit = hits.get(i);
            if (hit.scrolled() && (mouseY < cy || mouseY >= cy + ch)) {
                continue;
            }
            if (hit.contains(mouseX, mouseY)) {
                if (hit.click() != null) {
                    hit.click().run(mouseX, mouseY, button);
                }
                if (hit.drag() != null && button == 0) {
                    dragging = hit;
                    hit.drag().run(mouseX);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null && dragging.drag() != null && button == 0) {
            dragging.drag().run(mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, contentHeight - ch);
        scroll = Math.max(0, Math.min(maxScroll, scroll - verticalAmount * 18));
        return true;
    }
}
