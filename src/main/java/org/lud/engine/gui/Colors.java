package org.lud.engine.gui;

import org.lud.engine.enums.Theme;
import org.lud.engine.service.PieceService;
import org.lud.engine.service.ServiceFactory;

import java.awt.*;

public class Colors {
    public static final Color BUTTON = new Color(101, 106, 130);
    public static final Color SETTINGS = new Color(0 ,0 , 0, 180);
    private static Theme currentTheme = Theme.DEFAULT;
    private static ServiceFactory service;

    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

    public static Theme getTheme() {
        return currentTheme;
    }

    public static ServiceFactory getService() {
        return service;
    }

    public static void setService(ServiceFactory service) {
        Colors.service = service;
    }

    public static void nextTheme() {
        Theme[] themes = Theme.values();
        int nextIndex = (currentTheme.ordinal() + 1) % themes.length;
        setTheme(themes[nextIndex]);
        service.getRender().getMenuRender().reloadButtons();
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