package org.lud.engine.render.menu;

import org.lud.engine.render.RenderContext;
import org.lud.engine.service.UIService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("ALL")
public class Intro {
    private int logoSize = 0;
    private int logoDelta = 2;
    private int timer = 0;
    private int MIN_SIZE = 25;
    private int MAX_SIZE = 100;
    private float alpha = 0f;
    private boolean fadingIn = true;
    private final float FADE_SPEED = 0.02f;
    private BufferedImage logo;
    private final int DURATION = 180;

    public Intro() {
        try {
            this.logo = UIService.getImage("/ui/intro/logo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, RenderContext.BASE_WIDTH, RenderContext.BASE_HEIGHT);

        int originalWidth = logo.getWidth();
        int originalHeight = logo.getHeight();

        logoSize += logoDelta;
        if (logoSize > MAX_SIZE) {
            logoSize = MAX_SIZE;
            logoDelta = 0;
        } else if (logoSize < MIN_SIZE) {
            logoSize = MIN_SIZE;
        }

        double scale = 1.0 + logoSize / 1000.0;
        int width = (int) (originalWidth * scale);
        int height = (int) (originalHeight * scale);

        int x = (RenderContext.BASE_WIDTH / 2) - (width / 2);
        int y = (RenderContext.BASE_HEIGHT / 2) - (height / 2);

        if(fadingIn) {
            alpha += FADE_SPEED;
            if (alpha >= 1f) {
                alpha = 1f;
                fadingIn = false;
            }
        } else {}

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.drawImage(logo, x, y, width, height, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        timer++;
        if(timer > 100) {
            if(!fadingIn) {
                alpha-= FADE_SPEED;
                if(alpha <= 0) {
                    alpha = 0;
                }
            }
        }
    }

    public boolean isFinished() {
        return timer > DURATION;
    }
}
