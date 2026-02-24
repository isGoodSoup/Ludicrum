package org.lud.engine.animations;

import org.lud.engine.interfaces.Animation;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.UIService;
import org.lud.engine.util.Colors;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ToastAnimation implements Animation {
    private final String title;
    private final String description;
    private final BufferedImage icon;
    private double time = 0;

    private static final double SLIDE_TIME = 0.3;
    private static final double STAY_TIME = 2.0;
    private static final int SLIDE_DISTANCE = 400;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 150;
    private static final int ARC = 25;
    private static final int ICON_SIZE = 64;
    private int baseY;

    public ToastAnimation(String title, String description, BufferedImage icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public ToastAnimation(String title) {
        this(title, null, null);
    }

    @Override
    public void update(double delta) {
        time += delta;
    }

    @Override
    public void render(Graphics2D g2) {
        double totalTime = SLIDE_TIME * 2 + STAY_TIME;
        if (time > totalTime) return;

        int x = (RenderContext.BASE_WIDTH - WIDTH) / 2;
        int y = baseY + getSlideOffset();

        g2.setColor(Colorblindness.filter(Colors.SETTINGS));
        g2.fillRoundRect(x, y, WIDTH, HEIGHT, ARC, ARC);
        g2.setColor(Colorblindness.filter(Colors.getForeground()));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(x, y, WIDTH, HEIGHT, ARC, ARC);

        if (icon != null) {
            g2.drawImage(icon, x + 40, y + (HEIGHT - ICON_SIZE)/2, ICON_SIZE, ICON_SIZE, null);
        }

        g2.setFont(UIService.getFont(UIService.fontSize()[4]));
        g2.setColor(Colorblindness.filter(Colors.getHighlight()));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + 20 + (icon != null ? ICON_SIZE + 32 : 0);
        int titleY = y + (HEIGHT + fm.getAscent()) / 2 - (description != null ? 36 : 0);
        g2.drawString(title.toUpperCase(), textX, titleY);

        if (description != null) {
            g2.drawString(description, textX, titleY + 50);
        }
    }

    private int getSlideOffset() {
        if (time < SLIDE_TIME) {
            return (int) ((1 - time / SLIDE_TIME) * SLIDE_DISTANCE);
        } else if (time > SLIDE_TIME + STAY_TIME) {
            double t = (time - SLIDE_TIME - STAY_TIME) / SLIDE_TIME;
            return (int) (t * SLIDE_DISTANCE);
        }
        return 0;
    }

    public void setStackIndex(int index, int panelHeight) {
        int spacing = 40;
        baseY = panelHeight - 180 - index * (HEIGHT + spacing);
    }

    @Override
    public boolean isFinished() {
        return time >= SLIDE_TIME * 2 + STAY_TIME;
    }

    @Override
    public boolean affects(Object obj) {
        return false;
    }
}