package com.quantumclient.hud;

import com.quantumclient.gui.Draw;
import com.quantumclient.gui.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Редактор HUD: перетаскивай блоки мышью; Shift + перетаскивание двигает весь HUD. */
public class HudEditorScreen extends Screen {
    private enum Target { NONE, WATERMARK, INFO, KEYS, ALL }
    private Target dragging = Target.NONE;
    private double lastX, lastY;

    public HudEditorScreen() { super(Text.literal("Hamster_Anime HUD Editor")); }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x99060012);
        Draw.centered(ctx, "HAMSTER_ANIME  /  HUD EDITOR", width / 2, 18, Theme.accent());
        Draw.centered(ctx, "Перетаскивай блоки мышью  •  Shift + перетаскивание = двигать весь HUD", width / 2, 32, 0xFFE5DDF5);
        Draw.centered(ctx, "ESC — сохранить и выйти  •  R — сбросить позиции", width / 2, 45, 0xFFAAA0BE);

        int accent = Theme.accent();
        int ox = HudLayout.offsetX, oy = HudLayout.offsetY;
        int wx = HudLayout.watermarkX + ox, wy = HudLayout.watermarkY + oy;
        int ww = Draw.width("Hamster_Anime") + 18;
        panel(ctx, wx, wy, ww, 20, "Hamster_Anime", accent);

        int iw = 112, ih = 49;
        int ix = HudLayout.infoX < 0 ? width - iw - 8 : HudLayout.infoX;
        int iy = HudLayout.infoY;
        ix += ox; iy += oy;
        Draw.rrect(ctx, ix - 2, iy - 2, iw + 4, ih + 4, 7, Draw.alpha(accent, 0xA0));
        Draw.rrect(ctx, ix, iy, iw, ih, 6, 0xE0120B22);
        Draw.text(ctx, "FPS: 144", ix + 8, iy + 7, 0xFFFFFFFF);
        Draw.text(ctx, "Ping: 32ms", ix + 8, iy + 20, 0xFFFFFFFF);
        Draw.text(ctx, "CPS: 5 | 2", ix + 8, iy + 33, 0xFFFFFFFF);

        int kx = HudLayout.keysX < 0 ? width - 82 : HudLayout.keysX;
        int ky = HudLayout.keysY < 0 ? iy + ih + 10 : HudLayout.keysY;
        kx += ox; ky += oy;
        Draw.rrect(ctx, kx - 2, ky - 2, 76, 76, 7, Draw.alpha(accent, 0xA0));
        String[] labels = {"W", "A", "S", "D"};
        int[][] pos = {{26,0},{2,26},{26,26},{50,26}};
        for (int i=0;i<4;i++) {
            Draw.rrect(ctx,kx+pos[i][0],ky+pos[i][1],22,22,5,0xE0120B22);
            Draw.centered(ctx,labels[i],kx+pos[i][0]+11,ky+pos[i][1]+7,0xFFFFFFFF);
        }
        Draw.rrect(ctx,kx+2,ky+50,34,22,5,0xE0120B22);
        Draw.rrect(ctx,kx+40,ky+50,34,22,5,0xE0120B22);
        Draw.centered(ctx,"LMB",kx+19,ky+57,0xFFFFFFFF);
        Draw.centered(ctx,"RMB",kx+57,ky+57,0xFFFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void panel(DrawContext ctx, int x, int y, int w, int h, String text, int accent) {
        Draw.rrect(ctx,x-2,y-2,w+4,h+4,7,Draw.alpha(accent,0xA0));
        Draw.rrect(ctx,x,y,w,h,5,0xE0120B22);
        Draw.textShadow(ctx,text,x+8,y+6,accent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX,mouseY,button);
        int ox=HudLayout.offsetX, oy=HudLayout.offsetY;
        int wx=HudLayout.watermarkX+ox, wy=HudLayout.watermarkY+oy;
        int iw=112, ih=49;
        int ix=(HudLayout.infoX<0?width-iw-8:HudLayout.infoX)+ox, iy=HudLayout.infoY+oy;
        int kx=(HudLayout.keysX<0?width-82:HudLayout.keysX)+ox;
        int ky=(HudLayout.keysY<0?iy+ih+10:HudLayout.keysY)+oy;
        long win=client.getWindow().getHandle();
        boolean shift=org.lwjgl.glfw.GLFW.glfwGetKey(win,GLFW.GLFW_KEY_LEFT_SHIFT)==GLFW.GLFW_PRESS
                || org.lwjgl.glfw.GLFW.glfwGetKey(win,GLFW.GLFW_KEY_RIGHT_SHIFT)==GLFW.GLFW_PRESS;
        if (shift) dragging=Target.ALL;
        else if (Draw.inside(mouseX,mouseY,wx-3,wy-3,Draw.width("Hamster_Anime")+24,26)) dragging=Target.WATERMARK;
        else if (Draw.inside(mouseX,mouseY,ix-3,iy-3,iw+6,ih+6)) dragging=Target.INFO;
        else if (Draw.inside(mouseX,mouseY,kx-3,ky-3,80,80)) dragging=Target.KEYS;
        else return super.mouseClicked(mouseX,mouseY,button);
        lastX=mouseX; lastY=mouseY;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX,double mouseY,int button,double dx,double dy) {
        if (dragging==Target.NONE || button!=0) return super.mouseDragged(mouseX,mouseY,button,dx,dy);
        int mx=(int)Math.round(mouseX-lastX), my=(int)Math.round(mouseY-lastY);
        switch(dragging) {
            case WATERMARK -> { HudLayout.watermarkX+=mx; HudLayout.watermarkY+=my; }
            case INFO -> { if(HudLayout.infoX<0) HudLayout.infoX=width-120; HudLayout.infoX+=mx; HudLayout.infoY+=my; }
            case KEYS -> { if(HudLayout.keysX<0) HudLayout.keysX=width-90; if(HudLayout.keysY<0) HudLayout.keysY=HudLayout.infoY+60; HudLayout.keysX+=mx; HudLayout.keysY+=my; }
            case ALL -> { HudLayout.offsetX+=mx; HudLayout.offsetY+=my; }
            default -> {}
        }
        lastX=mouseX; lastY=mouseY;
        return true;
    }

    @Override public boolean mouseReleased(double x,double y,int button) {
        dragging=Target.NONE; HudLayout.save(); return super.mouseReleased(x,y,button);
    }

    @Override public boolean keyPressed(int key,int scanCode,int modifiers) {
        if(key==GLFW.GLFW_KEY_ESCAPE) { HudLayout.save(); close(); return true; }
        if(key==GLFW.GLFW_KEY_R) {
            HudLayout.watermarkX=6; HudLayout.watermarkY=6; HudLayout.infoX=-1; HudLayout.infoY=6;
            HudLayout.keysX=-1; HudLayout.keysY=-1; HudLayout.offsetX=0; HudLayout.offsetY=0;
            HudLayout.save(); return true;
        }
        return super.keyPressed(key,scanCode,modifiers);
    }

    @Override public void close() { HudLayout.save(); super.close(); }
}
