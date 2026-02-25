package org.lud.engine.render.menu;

import org.lud.engine.core.Version;
import org.lud.engine.entities.Button;
import org.lud.engine.entities.ButtonSprite;
import org.lud.engine.enums.*;
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
    }

    private int getTotalWidth() {
        return render.scale(RenderContext.BASE_WIDTH);
    }

    private int getCenterX(int containerWidth, int elementWidth) {
        return render.getOffsetX() + (containerWidth - elementWidth)/2;
    }

    @Override
    public void drawMenu(Graphics2D g2) {
        draw(g2, MenuRender.MENU);
        fadeFrom(g2);
    }

    @Override
    public boolean canDraw(State state) {
        return state == GameState.MENU;
    }

    private void fadeFrom(Graphics2D g2) {
        if(isFadingIn) {
            fadeAlpha -= FADE_SPEED;
            if (fadeAlpha <= 0f) {
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
            } else if (logoSize < 0) {
                logoSize = 0;
                logoDelta = -logoDelta;
            }
        }

        double scale = 1.0 + logoSize/400.0;
        int width = (int)(originalWidth * scale);
        int height = (int)(originalHeight * scale);

        int x = getCenterX(getTotalWidth(), width);
        int y = render.getOffsetY() + render.scale(RenderContext.BASE_HEIGHT) / 3;
        g2.drawImage(img, x, y, width, height, null);
        g2.setFont(UIService.getFont(UIService.fontSize()[3]));
        g2.setColor(Color.YELLOW);
        g2.drawString(Version.LATEST.toUpperCase(), x + width
                - g2.getFontMetrics().stringWidth(Version.LATEST.toUpperCase()), y + height);
    }

    public void draw(Graphics2D g2, GameMenu[] options) {
        render.getMenuRender().clearButtons();
        playButton = settingsButton = achievementsButton = langButton
                = exitButton = themeButton = gameButton = null;

        int totalWidth = getTotalWidth();
        g2.setColor(Colorblindness.filter(Colors.getBackground()));
        g2.fillRect(0, 0, totalWidth,
                render.scale(RenderContext.BASE_HEIGHT));

        drawLogo(g2);

        smallButton = render.getMenuRender().getButtonRegistry().get("button_small").normal;
        smallYellowButton = render.getMenuRender().getButtonRegistry().get("button").normal;

        int startX = render.scale(50);
        int startY = render.scale(RenderContext.BASE_HEIGHT - 115);
        int x = startX;
        int y = startY;
        int spacing = 4;

        for(GameMenu option : options) {
            BufferedImage baseImg = getSprites(option.name().toLowerCase())[0];
            BufferedImage altImg = getSprites(option.name().toLowerCase())[1];
            int width = baseImg.getWidth();
            int height = baseImg.getHeight();

            switch(option) {
                case PLAY -> {
                    if(playButton == null) {
                        playButton = createButton(x, y, width, height, () -> {
                            option.run(gameService);
                            render.getMenuRender().deactivateAll();
                        });
                    }
                    BufferedImage img = render.isHovered(playButton) || render.isSelected(playButton)
                            ? render.getMenuRender().getColorblindSprite(altImg)
                            : render.getMenuRender().getColorblindSprite(baseImg);
                    drawButtonLayers(g2, smallYellowButton, playButton, x, y);
                    g2.drawImage(img, x ,y, null);
                    x += width + spacing;

                    if(render.isHovered(playButton)) {
                        drawTooltip(g2, showTooltip(option));
                    }
                }
                case GAMES -> {
                    if(gameButton == null) {
                        gameButton = createButton(x, y, width, height, () ->
                                option.run(gameService));
                    }
                    BufferedImage img = render.isHovered(gameButton) || render.isSelected(gameButton)
                            ? render.getMenuRender().getColorblindSprite(altImg)
                            : render.getMenuRender().getColorblindSprite(baseImg);
                    drawButtonLayers(g2, smallButton, gameButton, x, y);
                    g2.drawImage(img, x ,y, null);
                    x += width + spacing;

                    if(render.isHovered(gameButton)) {
                        drawTooltip(g2, showTooltip(option));
                    }
                }
                case SETTINGS -> {
                    if(settingsButton == null) {
                        settingsButton = createButton(x, y, width, height, () -> {
                            option.run(gameService);
                            render.getMenuRender().onClose();
                        });
                    }
                    BufferedImage img = render.isHovered(settingsButton) || render.isSelected(settingsButton)
                            ? render.getMenuRender().getColorblindSprite(altImg)
                            : render.getMenuRender().getColorblindSprite(baseImg);
                    drawButtonLayers(g2, smallButton, settingsButton, x, y);
                    g2.drawImage(img, x ,y, null);
                    x += width + spacing;

                    if(render.isHovered(settingsButton)) {
                        drawTooltip(g2, showTooltip(option));
                    }
                }
                case ACHIEVEMENTS -> {
                    if(achievementsButton == null) {
                        achievementsButton = createButton(x, y, width, height, () -> {
                            option.run(gameService);
                            render.getMenuRender().onClose();
                        });
                    }
                    BufferedImage img = render.isHovered(achievementsButton) || render.isSelected(achievementsButton)
                            ? render.getMenuRender().getColorblindSprite(altImg)
                            : render.getMenuRender().getColorblindSprite(baseImg);
                    drawButtonLayers(g2, smallButton, achievementsButton, x, y);
                    g2.drawImage(img, x, y, null);
                    x += width + spacing;

                    if(render.isHovered(achievementsButton)) {
                        drawTooltip(g2, showTooltip(option));
                    }
                }
                case LANG -> {
                    if(langButton == null) {
                        langButton = createButton(x, y, width, height, () ->
                                option.run(gameService));
                    }
                    BufferedImage img = render.isHovered(langButton) || render.isSelected(langButton)
                            ? render.getMenuRender().getColorblindSprite(altImg)
                            : render.getMenuRender().getColorblindSprite(baseImg);
                    drawButtonLayers(g2, smallButton, langButton, x, y);
                    g2.drawImage(img, x, y, null);
                    x += width + spacing;

                    if(render.isHovered(langButton)) {
                        drawTooltip(g2, showTooltip(option));
                    }
                }
                case EXIT -> {
                    if(exitButton == null) {
                        exitButton = createButton(x, y, width, height, () ->
                                option.run(gameService));
                    }
                    BufferedImage img = render.isHovered(exitButton) || render.isSelected(exitButton)
                            ? render.getMenuRender().getColorblindSprite(altImg)
                            : render.getMenuRender().getColorblindSprite(baseImg);
                    drawButtonLayers(g2, smallButton, exitButton, x, y);
                    g2.drawImage(img, x, y, null);
                    x += width + spacing;

                    if(render.isHovered(exitButton)) {
                        drawTooltip(g2, showTooltip(option));
                    }
                }
                case THEME -> {
                    if(BooleanService.canTheme) {
                        if(themeButton == null) {
                            themeButton = createButton(x, y, width, height, Colors::nextTheme);
                        }
                        BufferedImage img = render.isHovered(themeButton) || render.isSelected(themeButton)
                                ? render.getMenuRender().getColorblindSprite(altImg)
                                : render.getMenuRender().getColorblindSprite(baseImg);
                        drawButtonLayers(g2, smallButton, themeButton, x, y);
                        g2.drawImage(img, x, y, null);
                        x += width + spacing;

                        if(render.isHovered(themeButton)) {
                            drawTooltip(g2, showTooltip(option));
                        }
                    }
                }
            }
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
        uiService.drawTooltip(g2, text, padding, ARC, true,
                render.scale(RenderContext.BASE_WIDTH/2 - g2.getFontMetrics().stringWidth(text)/2 - 15),
                render.scale(RenderContext.BASE_HEIGHT - 131));
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