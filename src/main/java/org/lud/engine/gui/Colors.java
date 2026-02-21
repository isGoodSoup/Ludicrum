package org.lud.engine.gui;

import org.lud.engine.enums.Theme;
import org.lud.engine.service.PieceService;

import java.awt.*;

public class Colors {
    public static final Color SETTINGS = new Color(0 ,0 , 0, 180);
    private static Theme currentTheme = Theme.DEFAULT;

    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

    public static Theme getTheme() {
        return currentTheme;
    }

    public static void nextTheme() {
        Theme[] themes = Theme.values();
        int nextIndex = (currentTheme.ordinal() + 1) % themes.length;
        setTheme(themes[nextIndex]);
        PieceService.clearCache();
    }

    public static void previousTheme() {
        Theme[] themes = Theme.values();
        int beforeIndex = (currentTheme.ordinal() - 1) % themes.length;
        setTheme(themes[beforeIndex]);
        PieceService.clearCache();
    }

    public static Color getBackground() {
        return currentTheme.getBackground();
    }

    public static Color getForeground() {
        return currentTheme.getForeground();
    }

    public static Color getEdge() {
        return currentTheme.getEdge();
    }

    public static Color getHighlight() {
        return currentTheme.getHighlight();
    }
}