package org.lud.engine.render;

import org.lud.engine.entities.Button;
import org.lud.engine.entities.ButtonSprite;
import org.lud.engine.enums.*;
import org.lud.engine.input.Mouse;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.interfaces.UI;
import org.lud.engine.service.BooleanService;
import org.lud.engine.service.GameService;
import org.lud.engine.service.UIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MenuRender {
    public static final GameMenu[] MENU = GameMenu.values();
    public static final Games[] GAMES = Games.values();
    public static final GameSettings[] SETTINGS_MENU = GameSettings.values();
    private static final Logger log = LoggerFactory.getLogger(MenuRender.class);
    public static BufferedImage[] OPTION_BUTTONS;
    private static final int ARC = 32;
    private static final int STROKE = 6;

    private final Map<String, ButtonSprite> buttonRegistry;
    private final Map<Clickable, Rectangle> buttons;
    private final Map<Button, Boolean> buttonsClicked;
    private final List<UI> menus;
    private Map<BufferedImage, BufferedImage> cache;

    private transient BufferedImage TOGGLE_ON, TOGGLE_OFF, TOGGLE_ON_HIGHLIGHTED, TOGGLE_OFF_HIGHLIGHTED;
    private transient BufferedImage HARD_MODE_ON, HARD_MODE_ON_HIGHLIGHTED;

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
        this.buttonRegistry = new HashMap<>();
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

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public Map<String, ButtonSprite> getButtonRegistry() {
        return buttonRegistry;
    }

    public void draw(Graphics2D g2) {
        for(UI menu : menus) {
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

            loadButtons();
            initCache();
            OPTION_BUTTONS = new BufferedImage[]{
                    TOGGLE_ON, TOGGLE_OFF, TOGGLE_ON_HIGHLIGHTED, TOGGLE_OFF_HIGHLIGHTED,
                    HARD_MODE_ON, HARD_MODE_ON_HIGHLIGHTED
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to load menu sprites", e);
        }
    }

    public void initCache() {
        cache = new HashMap<>();
        BufferedImage[] allSprites = new BufferedImage[] {
                TOGGLE_ON, TOGGLE_OFF, TOGGLE_ON_HIGHLIGHTED, TOGGLE_OFF_HIGHLIGHTED,
                HARD_MODE_ON, HARD_MODE_ON_HIGHLIGHTED,
        };

        for(BufferedImage sprite : allSprites) {
            cache.put(sprite, Colorblindness.filter(sprite));
        }
    }

    public void reloadButtons() {
        try {
            loadButtons();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadButtons() throws IOException {
        for(UIButton type : UIButton.values()) {
            String suffix = type.getSuffix();
            String basePath = "/ui/button";
            if(!suffix.isEmpty()) {
                basePath += "_" + suffix;
            }

            String normalPath = basePath;
            String highlightedPath = basePath + "_highlighted";

            ButtonSprite sprite = new ButtonSprite();
            sprite.normal = UIService.getImage(normalPath);
            sprite.highlighted = UIService.getImage(highlightedPath);
            buttonRegistry.put(type.name().toLowerCase(), sprite);
        }
    }

    public BufferedImage getColorblindSprite(BufferedImage img) {
        return cache.getOrDefault(img, img);
    }

    public void drawButtonsLayer(Graphics2D g2, Button... buttons) {
        for(Button b : buttons) {
            ButtonSprite sprite = buttonRegistry.get("button_small");
            g2.drawImage(sprite.normal, b.getX(), b.getY(), null);
            BufferedImage frame = render.getMenuRender().defineButton(b, ButtonSize.SMALL);
            g2.drawImage(frame, b.getX(), b.getY(), null);
        }
    }

    public BufferedImage defineButton(Clickable c, ButtonSize size) {
        String key = size == ButtonSize.BIG ? "button" : "button_small";
        ButtonSprite sprite = buttonRegistry.get(key);
        return render.isHovered(c) ? sprite.highlighted : null;
    }

    public void clearButtons() {
        buttons.clear();
    }

    public void onEnter() {

    }

    public void onClose() {
        clearButtons();
        BooleanService.haveButtonsInit = false;
    }
}