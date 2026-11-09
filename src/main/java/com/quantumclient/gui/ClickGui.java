package com.quantumclient.gui;

import com.quantumclient.QuantumClientMod;
import com.quantumclient.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickGui extends Screen {
    private static final int BG_COLOR        = 0xE5090B12;
    private static final int PANEL_COLOR     = 0xF00F172A;
    private static final int PANEL_BORDER    = 0xFF1E293B;
    private static final int ACCENT_BLUE     = 0xFF39A9FF;
    private static final int ACCENT_PURPLE   = 0xFF925CFF;
    private static final int TEXT_WHITE      = 0xFFFFFFFF;
    private static final int TEXT_GRAY       = 0xFF94A3B8;
    private static final int TOGGLE_ON_L     = 0xFF39A9FF;
    private static final int TOGGLE_ON_R     = 0xFF925CFF;
    private static final int TOGGLE_OFF      = 0xFF334155;

    private static final int PANEL_WIDTH     = 200;
    private static final int PANEL_PADDING   = 12;
    private static final int MODULE_HEIGHT   = 44;
    private static final int TOGGLE_W        = 38;
    private static final int TOGGLE_H        = 20;

    public ClickGui() {
        super(Text.literal("Quantum Client"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Dim background
        ctx.fill(0, 0, ctx.getScaledWindowWidth(), ctx.getScaledWindowHeight(), 0x88000000);

        Map<String, List<Module>> byCategory = QuantumClientMod.moduleManager.getModules()
            .stream()
            .collect(Collectors.groupingBy(Module::getCategory, LinkedHashMap::new, Collectors.toList()));

        int panelX = 20;
        int panelY = 20;

        // Title bar
        int titleBarH = 36;
        ctx.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + titleBarH, PANEL_COLOR);
        ctx.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 2, ACCENT_BLUE);
        drawCenteredGradientText(ctx, "QUANTUM CLIENT", panelX, panelY + 12, PANEL_WIDTH, ACCENT_BLUE, ACCENT_PURPLE);
        panelY += titleBarH + 4;

        for (Map.Entry<String, List<Module>> entry : byCategory.entrySet()) {
            String category = entry.getKey();
            List<Module> modules = entry.getValue();

            int panelH = PANEL_PADDING + modules.size() * MODULE_HEIGHT + PANEL_PADDING;

            // Panel background
            ctx.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + panelH, PANEL_COLOR);
            ctx.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 1, PANEL_BORDER);
            ctx.fill(panelX, panelY + panelH - 1, panelX + PANEL_WIDTH, panelY + panelH, PANEL_BORDER);

            // Category header
            ctx.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 22, 0xFF0F172A);
            ctx.fill(panelX, panelY, panelX + 3, panelY + 22, ACCENT_BLUE);
            ctx.drawText(textRenderer, category.toUpperCase(), panelX + 10, panelY + 7, ACCENT_BLUE, false);

            int modY = panelY + 22;

            for (Module module : modules) {
                boolean hovered = mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH
                    && mouseY >= modY && mouseY <= modY + MODULE_HEIGHT;

                // Row background
                ctx.fill(panelX, modY, panelX + PANEL_WIDTH, modY + MODULE_HEIGHT,
                    hovered ? 0xFF1E293B : 0x00000000);

                // Module name
                ctx.drawText(textRenderer, module.getName(), panelX + PANEL_PADDING, modY + 8, TEXT_WHITE, false);

                // Description
                ctx.drawText(textRenderer, module.getDescription(),
                    panelX + PANEL_PADDING, modY + 20, TEXT_GRAY, false);

                // Toggle switch
                int toggleX = panelX + PANEL_WIDTH - TOGGLE_W - PANEL_PADDING;
                int toggleY = modY + (MODULE_HEIGHT - TOGGLE_H) / 2;
                drawToggle(ctx, toggleX, toggleY, module.isEnabled());

                // Separator line
                ctx.fill(panelX + 4, modY + MODULE_HEIGHT - 1, panelX + PANEL_WIDTH - 4, modY + MODULE_HEIGHT, PANEL_BORDER);

                modY += MODULE_HEIGHT;
            }

            panelY += panelH + 6;

            // Move to next column if too tall
            if (panelY > ctx.getScaledWindowHeight() - 40) {
                panelY = 20 + titleBarH + 4;
                panelX += PANEL_WIDTH + 10;
            }
        }
    }

    private void drawToggle(DrawContext ctx, int x, int y, boolean on) {
        // Track background
        if (on) {
            // Gradient left
            for (int i = 0; i < TOGGLE_W; i++) {
                float t = (float) i / TOGGLE_W;
                int r = (int)(0x39 + t * (0x92 - 0x39));
                int g = (int)(0xA9 + t * (0x5C - 0xA9));
                int b = (int)(0xFF + t * (0xFF - 0xFF));
                int col = 0xFF000000 | (r << 16) | (g << 8) | b;
                ctx.fill(x + i, y, x + i + 1, y + TOGGLE_H, col);
            }
        } else {
            ctx.fill(x, y, x + TOGGLE_W, y + TOGGLE_H, TOGGLE_OFF);
        }

        // Round edges (simple corner fill)
        int cornerColor = 0xFF0F172A;
        ctx.fill(x, y, x + 4, y + 4, cornerColor);
        ctx.fill(x + TOGGLE_W - 4, y, x + TOGGLE_W, y + 4, cornerColor);
        ctx.fill(x, y + TOGGLE_H - 4, x + 4, y + TOGGLE_H, cornerColor);
        ctx.fill(x + TOGGLE_W - 4, y + TOGGLE_H - 4, x + TOGGLE_W, y + TOGGLE_H, cornerColor);

        // Knob
        int knobSize = TOGGLE_H - 6;
        int knobX = on ? x + TOGGLE_W - knobSize - 4 : x + 4;
        int knobY = y + 3;
        ctx.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, TEXT_WHITE);
    }

    private void drawCenteredGradientText(DrawContext ctx, String text, int panelX, int y, int width, int colorL, int colorR) {
        int textWidth = textRenderer.getWidth(text);
        int x = panelX + (width - textWidth) / 2;
        // Simple: draw in accent blue
        ctx.drawText(textRenderer, text, x, y, colorL, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        Map<String, List<Module>> byCategory = QuantumClientMod.moduleManager.getModules()
            .stream()
            .collect(Collectors.groupingBy(Module::getCategory, LinkedHashMap::new, Collectors.toList()));

        int panelX = 20;
        int panelY = 20 + 36 + 4;

        for (Map.Entry<String, List<Module>> entry : byCategory.entrySet()) {
            List<Module> modules = entry.getValue();
            int panelH = PANEL_PADDING + modules.size() * MODULE_HEIGHT + PANEL_PADDING;

            int modY = panelY + 22;
            for (Module module : modules) {
                // Toggle click area
                int toggleX = panelX + PANEL_WIDTH - TOGGLE_W - PANEL_PADDING;
                int toggleY = modY + (MODULE_HEIGHT - TOGGLE_H) / 2;
                if (mouseX >= toggleX && mouseX <= toggleX + TOGGLE_W
                    && mouseY >= toggleY && mouseY <= toggleY + TOGGLE_H) {
                    module.toggle();
                    return true;
                }
                // Also clicking the whole row toggles
                if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH
                    && mouseY >= modY && mouseY <= modY + MODULE_HEIGHT) {
                    module.toggle();
                    return true;
                }
                modY += MODULE_HEIGHT;
            }

            panelY += panelH + 6;
            if (panelY > this.height - 40) {
                panelY = 20 + 36 + 4;
                panelX += PANEL_WIDTH + 10;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT
            || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
