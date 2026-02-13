package org.chess.animations;

import org.chess.interfaces.Animation;

import java.awt.*;

public class ToastAnimation implements Animation {
    private final String message;
    private double time = 0;
    private final double duration = 3.0;
    private float alpha = 0f;
    private int baseY;

    public ToastAnimation(String message, int panelHeight) {
        this.message = message;
        this.baseY = panelHeight - 80;
    }

    @Override
    public void update(double delta) {
        time += delta;

        if (time < 0.4) {
            alpha = (float)(time / 0.4);
        } else if (time > duration - 0.4) {
            alpha = (float)((duration - time) / 0.4);
        } else {
            alpha = 1f;
        }
    }

    @Override
    public void render(Graphics2D g2) {
        if (alpha <= 0f) return;

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha));

        int width = 300;
        int height = 50;
        int x = 250;
        int y = baseY - (int)(time * 20);

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRoundRect(x, y, width, height, 20, 20);

        g2.setColor(Color.WHITE);
        g2.drawString(message, x + 20, y + 30);

        g2.setComposite(old);
    }

    @Override
    public boolean isFinished() {
        return time >= duration;
    }
}
