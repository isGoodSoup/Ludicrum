package org.lud.engine.render;

import org.lud.engine.entities.Button;
import org.lud.engine.enums.*;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.interfaces.UI;
import org.lud.engine.service.GameService;
import org.lud.engine.service.UIService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MenuRender {
    public static final GameMenu[] MENU = GameMenu.values();
    public static final Games[] GAMES = Games.values();
    public static final GameSettings[] SETTINGS_MENU = GameSettings.values();
    public static BufferedImage[] OPTION_BUTTONS;
    private static final int ARC = 32;
    private static final int STROKE = 6;

    private final Map<Clickable, Rectangle> buttons;
    private final Map<Button, Boolean> buttonsClicked;
    private transient Map<BufferedImage, BufferedImage> colorblindCache;
    private final List<UI> menus;

    private transient BufferedImage TOGGLE_ON, TOGGLE_OFF, TOGGLE_ON_HIGHLIGHTED, TOGGLE_OFF_HIGHLIGHTED;
    private transient BufferedImage HARD_MODE_ON, HARD_MODE_ON_HIGHLIGHTED;
    private transient BufferedImage NEXT_PAGE, NEXT_PAGE_ON;
    private transient BufferedImage PREVIOUS_PAGE, PREVIOUS_PAGE_ON;
    private transient BufferedImage UNDO, UNDO_HIGHLIGHTED;
    private transient BufferedImage RESET, RESET_HIGHLIGHTED;
    private transient BufferedImage ACHIEVEMENTS, ACHIEVEMENTS_HIGHLIGHTED;
    private transient BufferedImage SETTINGS, SETTINGS_HIGHLIGHTED;
    private transient BufferedImage EXIT, EXIT_HIGHLIGHTED;

    private transient BufferedImage BUTTON, BUTTON_HIGHLIGHTED;
    private transient BufferedImage BUTTON_SMALL, BUTTON_SMALL_HIGHLIGHTED;

    private static ColorblindType cb;
    private int lastHoveredIndex = -1;
    private int scrollOffset = 0;
    private static int totalWidth;

    private RenderContext render;
    private GameService gameService;
    private AchievementSprites sprites;

    public MenuRender(RenderContext render, UI... menus) {
        this.buttons = new HashMap<>();
        this.buttonsClicked = new HashMap<>();
        this.menus = new ArrayList<>();
        Collections.addAll(this.menus, menus);
        this.render = render;
        cb = ColorblindType.PROTANOPIA;
    }

    public Map<Clickable, Rectangle> getButtons() {
        return buttons;
    }

    public List<UI> getMenus() {
        return menus;
    }

    public static ColorblindType getCb() {
        return cb;
    }

    public static void setCb(ColorblindType cb) {
        MenuRender.cb = cb;
    }

    public static int getARC() {
        return ARC;
    }

    public static int getSTROKE() {
        return STROKE;
    }

    public GameService getGameService() {
        return gameService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public BufferedImage getUNDO() {
        return UNDO;
    }

    public BufferedImage getUNDO_HIGHLIGHTED() {
        return UNDO_HIGHLIGHTED;
    }

    public BufferedImage getRESET() {
        return RESET;
    }

    public BufferedImage getRESET_HIGHLIGHTED() {
        return RESET_HIGHLIGHTED;
    }

    public BufferedImage getPREVIOUS_PAGE() {
        return PREVIOUS_PAGE;
    }

    public BufferedImage getPREVIOUS_PAGE_ON() {
        return PREVIOUS_PAGE_ON;
    }

    public BufferedImage getBUTTON() {
        return BUTTON;
    }

    public BufferedImage getBUTTON_HIGHLIGHTED() {
        return BUTTON_HIGHLIGHTED;
    }

    public BufferedImage getBUTTON_SMALL() {
        return BUTTON_SMALL;
    }

    public void setBUTTON_SMALL(BufferedImage BUTTON_SMALL) {
        this.BUTTON_SMALL = BUTTON_SMALL;
    }

    public BufferedImage getBUTTON_SMALL_HIGHLIGHTED() {
        return BUTTON_SMALL_HIGHLIGHTED;
    }

    public void setBUTTON_SMALL_HIGHLIGHTED(BufferedImage BUTTON_SMALL_HIGHLIGHTED) {
        this.BUTTON_SMALL_HIGHLIGHTED = BUTTON_SMALL_HIGHLIGHTED;
    }

    public BufferedImage getACHIEVEMENTS() {
        return ACHIEVEMENTS;
    }

    public BufferedImage getACHIEVEMENTS_HIGHLIGHTED() {
        return ACHIEVEMENTS_HIGHLIGHTED;
    }

    public BufferedImage getSETTINGS() {
        return SETTINGS;
    }

    public BufferedImage getSETTINGS_HIGHLIGHTED() {
        return SETTINGS_HIGHLIGHTED;
    }

    public BufferedImage getEXIT() {
        return EXIT;
    }

    public BufferedImage getEXIT_HIGHLIGHTED() {
        return EXIT_HIGHLIGHTED;
    }

    public void draw(Graphics2D g2) {
        for (UI menu : menus) {
            if(menu.canDraw(gameService.getState())) {
                menu.drawMenu(g2);
            }
        }
    }

    public void init() {
        this.sprites = new AchievementSprites();

        try {
            TOGGLE_ON = UIService.getImage("/ui/toggle_on");
            TOGGLE_OFF = UIService.getImage("/ui/toggle_off");
            TOGGLE_ON_HIGHLIGHTED = UIService.getImage("/ui/toggle_onh");
            TOGGLE_OFF_HIGHLIGHTED = UIService.getImage("/ui/toggle_offh");

            HARD_MODE_ON = UIService.getImage("/ui/hardmode_on");
            HARD_MODE_ON_HIGHLIGHTED = UIService.getImage("/ui/hardmode_onh");

            NEXT_PAGE = UIService.getImage("/ui/next_page");
            NEXT_PAGE_ON = UIService.getImage("/ui/next_page_highlighted");
            PREVIOUS_PAGE = UIService.getImage("/ui/previous_page");
            PREVIOUS_PAGE_ON = UIService.getImage("/ui/previous_page_highlighted");

            UNDO = UIService.getImage("/ui/undo");
            UNDO_HIGHLIGHTED = UIService.getImage("/ui/undo_highlighted");
            RESET = UIService.getImage("/ui/reset");
            RESET_HIGHLIGHTED = UIService.getImage("/ui/reset_highlighted");

            BUTTON = UIService.getImage("/ui/button");
            BUTTON_HIGHLIGHTED = UIService.getImage("/ui/button_highlighted");

            BUTTON_SMALL = UIService.getImage("/ui/button_small");
            BUTTON_SMALL_HIGHLIGHTED = UIService.getImage("/ui/button_small_highlighted");

            ACHIEVEMENTS = UIService.getImage("/ui/achievements");
            ACHIEVEMENTS_HIGHLIGHTED = UIService.getImage("/ui/achievements_highlighted");

            SETTINGS = UIService.getImage("/ui/settings");
            SETTINGS_HIGHLIGHTED = UIService.getImage("/ui/settings_highlighted");

            EXIT = UIService.getImage("/ui/exit");
            EXIT_HIGHLIGHTED = UIService.getImage("/ui/exit_highlighted");

            initCache();
            OPTION_BUTTONS = new BufferedImage[]{
                    TOGGLE_ON, TOGGLE_OFF, TOGGLE_ON_HIGHLIGHTED, TOGGLE_OFF_HIGHLIGHTED,
                    HARD_MODE_ON, HARD_MODE_ON_HIGHLIGHTED, NEXT_PAGE, NEXT_PAGE_ON,
                    PREVIOUS_PAGE, PREVIOUS_PAGE_ON
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to load menu sprites", e);
        }
    }

    public void initCache() {
        colorblindCache = new HashMap<>();
        BufferedImage[] allSprites = new BufferedImage[] {
                TOGGLE_ON, TOGGLE_OFF, TOGGLE_ON_HIGHLIGHTED, TOGGLE_OFF_HIGHLIGHTED,
                HARD_MODE_ON, HARD_MODE_ON_HIGHLIGHTED,
                NEXT_PAGE, NEXT_PAGE_ON,
                PREVIOUS_PAGE, PREVIOUS_PAGE_ON,
                UNDO, UNDO_HIGHLIGHTED,
                RESET, RESET_HIGHLIGHTED,
                BUTTON, BUTTON_HIGHLIGHTED,
                BUTTON_SMALL, BUTTON_SMALL_HIGHLIGHTED,
                ACHIEVEMENTS, ACHIEVEMENTS_HIGHLIGHTED,
                SETTINGS, SETTINGS_HIGHLIGHTED,
                EXIT, EXIT_HIGHLIGHTED
        };

        for (BufferedImage sprite : allSprites) {
            colorblindCache.put(sprite, Colorblindness.filter(sprite));
        }
    }

    public BufferedImage getColorblindSprite(BufferedImage img) {
        return colorblindCache.getOrDefault(img, img);
    }

    public void drawButtonsLayer(Graphics2D g2, Button... buttons) {
        for (Button b : buttons) {
            g2.drawImage(BUTTON_SMALL, b.getX(), b.getY(), null);
            BufferedImage frame = render.getMenuRender().defineButton(b, ButtonSize.SMALL);
            g2.drawImage(frame, b.getX(), b.getY(), null);
        }
    }

    public BufferedImage defineButton(Clickable c, ButtonSize size) {
        return switch(size) {
            case BIG -> render.isHovered(c) ? BUTTON_HIGHLIGHTED : null;
            case SMALL -> render.isHovered(c) ? BUTTON_SMALL_HIGHLIGHTED : null;
        };
    }
}