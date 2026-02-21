package org.lud.engine.render;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AchievementLock {

    public static Color apply(Color original) {
        int r = original.getRed();
        int g = original.getGreen();
        int b = original.getBlue();

        int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
        return new Color(gray, gray, gray, original.getAlpha());
    }

    public static BufferedImage apply(BufferedImage img) {
        BufferedImage filtered = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color orig = new Color(img.getRGB(x, y), true);
                Color cb = apply(orig);

                Color finalColor = new Color(cb.getRed(), cb.getGreen(),
                        cb.getBlue(), orig.getAlpha());
                filtered.setRGB(x, y, finalColor.getRGB());
            }
        }
        return filtered;
    }

    public static BufferedImage filter(BufferedImage img, boolean isUnlocked) {
        return isUnlocked ? apply(img) : img;
    }
}
