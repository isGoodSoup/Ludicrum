package org.lud.engine.render.menu;

import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.BooleanService;
import org.lud.engine.sound.Sound;
import org.lud.engine.util.Colors;

import javax.swing.*;
import java.awt.*;

public class VolumeMenu implements UI {
    private final static int ARC = 32;
    private final RenderContext render;
    private final Sound sound;
    private Timer fadeTimer;

    public VolumeMenu(RenderContext render, Sound sound) {
        this.render = render;
        this.sound = sound;
    }

    @Override
    public void drawMenu(Graphics2D g2) {
        draw(g2);
    }

    @Override
    public boolean canDraw(State state) {
        return true;
    }

    public void showVolumeSlider() {
        BooleanService.isMovingVolume = true;

        if(fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }

        fadeTimer = new Timer(2000, e
                -> BooleanService.isMovingVolume = false);
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }

    private void draw(Graphics2D g2) {
        if(!BooleanService.isMovingVolume) { return; }

        int x = render.scale(RenderContext.BASE_WIDTH - 100);
        int baseY = render.scale(RenderContext.BASE_HEIGHT/2);

        int levels = Sound.getVOLUME_LEVELS().length;
        int blockHeight = 16;
        int blockWidth = 32;
        int spacing = 4;
        int currentLevel = sound.getVolumeScale();

        for(int i = 0; i < levels; i++) {
            int y = baseY - (i * (blockHeight + spacing));
            if(i <= currentLevel) {
                g2.setColor(Colors.getHighlight());
            } else {
                g2.setColor(Colors.getForeground());
            }
            g2.fillRect(x, y, blockWidth, blockHeight);
        }
    }
}
