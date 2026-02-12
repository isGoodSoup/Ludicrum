package org.chess.render;

public class RenderContext {
    public static final int BASE_WIDTH = 1920;
    public static final int BASE_HEIGHT = 1080;
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    public void updateTransform(int windowWidth, int windowHeight) {
        double scaleX = windowWidth / (double) BASE_WIDTH;
        double scaleY = windowHeight / (double) BASE_HEIGHT;
        scale = Math.min(scaleX, scaleY);

        int drawWidth = (int)(BASE_WIDTH * scale);
        int drawHeight = (int)(BASE_HEIGHT * scale);

        offsetX = (windowWidth - drawWidth) / 2;
        offsetY = (windowHeight - drawHeight) / 2;
    }

    public void updateScale(int windowWidth, int windowHeight) {
        double scaleX = windowWidth/(double) BASE_WIDTH;
        double scaleY = windowHeight/(double) BASE_HEIGHT;
        scale = Math.min(scaleX, scaleY);
    }

    public double getScale() {
        return scale;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int scale(int value) {
        return (int) (value * scale);
    }

    public int unscaleX(int rawX) {
        return (int) ((rawX - offsetX) / scale);
    }

    public int unscaleY(int rawY) {
        return (int) ((rawY - offsetY) / scale);
    }
}
