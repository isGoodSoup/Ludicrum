package org.lud.engine.render.menu;

import org.lud.engine.core.Version;
import org.lud.engine.entities.Button;
import org.lud.engine.entities.ButtonSprite;
import org.lud.engine.enums.GameMenu;
import org.lud.engine.enums.GameState;
import org.lud.engine.input.KeyboardInput;
import org.lud.engine.input.Mouse;
import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.MenuRender;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.BooleanService;
import org.lud.engine.service.GameService;
import org.lud.engine.service.UIService;
import org.lud.engine.util.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

@SuppressWarnings("ALL")
public class MainMenu implements UI {
    private static final int ARC = 32;
    private static final int CENTER_Y = 800;
    private static final int PADDING_X = 25;
    private static final int PADDING_Y = 25;
    private static final Logger log = LoggerFactory.getLogger(MainMenu.class);

    private final RenderContext render;
    private final GameService gameService;
    private final UIService uiService;
    private final KeyboardInput keyUI;
    private final Mouse mouse;

    private BufferedImage smallButton;
    private BufferedImage smallYellowButton;

    private Button playButton;
    private Button gameButton;
    private Button achievementsButton;
    private Button settingsButton;
    private Button langButton;
    private Button exitButton;
    private Button themeButton;

    private int logoSize = 0;
    private int logoDelta = 1;
    private final float FADE_SPEED = 0.15f;
    private float fadeAlpha = 1f;

    private boolean isMenuInit;
    private boolean isFadingIn;

    public MainMenu(RenderContext render, GameService gameService,
                    UIService uiService, KeyboardInput keyUI,
                    Mouse mouse) {
        this.render = render;
        this.gameService = gameService;
        this.uiService = uiService;
        this.keyUI = keyUI;
        this.mouse = mouse;
        this.isFadingIn = true;
        this.isMenuInit = true;
    }

    private int getTotalWidth() {
        return render.scale(RenderContext.BASE_WIDTH);
    }

    private int getCenterX(int containerWidth, int elementWidth) {
        return render.getOffsetX() + (containerWidth - elementWidth)/2;
    }

    @Override
    public void drawMenu(Graphics2D g2) {
        if(MenuRender.getButtonMap().isEmpty()) {
            initButtons();
        }
        draw(g2);
        fadeFrom(g2);
        mouse.reset();
    }

    @Override
    public boolean canDraw(State state) {
        return state == GameState.MENU;
    }

    private void fadeFrom(Graphics2D g2) {
        if(isFadingIn) {
            fadeAlpha -= FADE_SPEED;
            if(fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                isFadingIn = false;
            }
        }

        if(fadeAlpha > 0f) {
            Composite original = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    fadeAlpha
            ));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0,
                    render.scale(RenderContext.BASE_WIDTH),
                    render.scale(RenderContext.BASE_HEIGHT)
            );
            g2.setComposite(original);
        }
    }

    private void drawLogo(Graphics2D g2) {
        BufferedImage img = Colorblindness.filter(UIService.getLogo());
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();

        if(BooleanService.canAnimateLogo) {
            logoSize += logoDelta;
            int MAX_SIZE = 40;
            if(logoSize > MAX_SIZE) {
                logoSize = MAX_SIZE;
                logoDelta = -logoDelta;
            } else if(logoSize < 0) {
                logoSize = 0;
                logoDelta = -logoDelta;
            }
        }

        double scale = 1.0 + logoSize/400.0;
        int width = (int)(originalWidth * scale);
        int height = (int)(originalHeight * scale);

        int x = getCenterX(getTotalWidth(), width);
        int y = render.getOffsetY() + render.scale(RenderContext.BASE_HEIGHT)/3;
        g2.drawImage(img, x, y, width, height, null);
        g2.setFont(UIService.getFont(UIService.fontSize()[3]));
        g2.setColor(Color.YELLOW);
        g2.drawString(Version.LATEST.toUpperCase(), x + width
                - g2.getFontMetrics().stringWidth(Version.LATEST.toUpperCase()), y + height);
    }

    private void initButtons() {
        MenuRender.getButtonMap().clear();
        smallButton = render.getMenuRender().getButtonRegistry().get("button_small").normal;
        smallYellowButton = render.getMenuRender().getButtonRegistry().get("button").normal;
        int x = render.scale(50);
        int y = render.scale(RenderContext.BASE_Y);

        for(GameMenu option : GameMenu.values()) {
            option.reset();
            if(option == GameMenu.THEME && !BooleanService.canTheme) { continue; }
            BufferedImage[] sprites = getSprites(option.name().toLowerCase());
            BufferedImage baseImg = sprites[0];
            BufferedImage altImg = sprites[1];
            BufferedImage layerImg = (option == GameMenu.PLAY) ? smallYellowButton : smallButton;

            Button button = createButton(x, y, baseImg.getWidth(), baseImg.getHeight(), () -> {
                option.run(gameService);
                render.getMenuRender().deactivateAll();
            });

            if(button != null && option != null) {
                MenuRender.put(button, option);
            }
            x += baseImg.getWidth();
        }
    }

    private void draw(Graphics2D g2) {
        int totalWidth = getTotalWidth();
        g2.setColor(Colorblindness.filter(Colors.getBackground()));
        g2.fillRect(0, 0, totalWidth, render.scale(RenderContext.BASE_HEIGHT));

        drawLogo(g2);

        int x = render.scale(50);
        int y = render.scale(RenderContext.BASE_Y);

        for(Map.Entry<Button, GameMenu> entry : MenuRender.getButtonMap().entrySet()) {
            Button button = entry.getKey();
            GameMenu option = entry.getValue();

            BufferedImage[] sprites = getSprites(option.name().toLowerCase());
            BufferedImage baseImg = sprites[0];
            BufferedImage altImg = sprites[1];
            BufferedImage layerImg = (option == GameMenu.PLAY) ? smallYellowButton : smallButton;

            drawMenuButton(g2, button, x, y, baseImg, altImg, option, layerImg);
            x += baseImg.getWidth();
        }
    }

    private void drawMenuButton(Graphics2D g2, Button button, int x, int y,
                                BufferedImage baseImg, BufferedImage altImg,
                                GameMenu option, BufferedImage layerImg) {
        BufferedImage img = render.isHovered(button) || render.isSelected(option)
                ? render.getMenuRender().getColorblindSprite(altImg)
                : render.getMenuRender().getColorblindSprite(baseImg);

        drawButtonLayers(g2, layerImg, button, x, y);
        g2.drawImage(img, x, y, null);

        if(render.isHovered(button)) {
            drawTooltip(g2, option.getTooltip());
        }
    }

    private Button createButton(int x, int y, int w, int h, Runnable action) {
        Button b = new Button(x, y, w, h, action);
        render.getMenuRender().addButton(b, new Rectangle(x, y, w, h));
        return b;
    }

    private void drawButtonLayers(Graphics2D g2, BufferedImage img, Button b, int x, int y) {
        g2.drawImage(Colorblindness.filter(img), x, y, null);
        BufferedImage frame = render.getMenuRender().defineButton(b);
        g2.drawImage(frame, x, y, null);
    }

    public void drawTooltip(Graphics2D g2, String text) {
        int padding = 16;
        g2.setFont(UIService.getFont(UIService.fontSize()[3]));
        uiService.drawTooltip(g2, text, padding, ARC, false, 0, 0);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private String showTooltip(GameMenu op) {
        if(op == GameMenu.PLAY) {
            if(GameService.getGame() != null) {
                return GameService.getGame().getTooltip();
            } else {}
        }
        return op.getTooltip();
    }

    private BufferedImage[] getSprites(String key) {
        ButtonSprite sprite = render.getMenuRender().getButtonRegistry().get(key);
        if(sprite == null) {
            log.warn("Button sprite not found for key: {}", key);
            return new BufferedImage[]{null, null};
        }
        return new BufferedImage[]{sprite.normal, sprite.highlighted};
    }
}