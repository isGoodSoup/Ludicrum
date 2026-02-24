package org.lud.engine.util;

import org.lud.engine.enums.Theme;
import org.lud.engine.service.PieceService;
import org.lud.engine.service.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class Colors {
    public static final Color BUTTON = new Color(198, 114, 11);
    public static final Color PROGRESS_BAR = new Color(61, 187, 47);
    public static final Color SETTINGS = new Color(0 ,0 , 0, 180);
    private static final Logger log = LoggerFactory.getLogger(Colors.class);
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
        log.debug("Theme changed to {}", Colors.getTheme());
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