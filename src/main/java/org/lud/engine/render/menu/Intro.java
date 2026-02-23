package org.lud.engine.render.menu;

import org.lud.engine.render.RenderContext;
import org.lud.engine.service.UIService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Intro {
    private int logoSize = 0;
    private int logoDelta = 2;
    private int timer = 0;
    private final int DURATION = 180;
    private BufferedImage logo;

    public Intro() {
        try {
            this.logo = UIService.getImage("/ui/intro/logo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, RenderContext.BASE_WIDTH, RenderContext.BASE_HEIGHT);

        int originalWidth = logo.getWidth();
        int originalHeight = logo.getHeight();

        logoSize += logoDelta;
        int MAX_SIZE = 50;
        if(logoSize > MAX_SIZE) {
            logoSize = MAX_SIZE;
            logoDelta = -logoDelta;
        } else if (logoSize < 0) {
            logoSize = 0;
            logoDelta = -logoDelta;
        }

        double scale = 1.0 + logoSize/1000.0;
        int width = (int)(originalWidth * scale);
        int height = (int)(originalHeight * scale);

        int x = (RenderContext.BASE_WIDTH / 2) - (width / 2);
        int y = (RenderContext.BASE_HEIGHT / 2) - (height / 2);
        g2.drawImage(logo, x, y, width, height, null);
        timer++;
    }

    public boolean isFinished() {
        return timer > DURATION;
    }
}
