package org.lud.engine.render.menu;

import org.lud.engine.entities.Button;
import org.lud.engine.enums.GameMenu;
import org.lud.engine.enums.GameState;
import org.lud.engine.gui.Colors;
import org.lud.engine.input.KeyboardInput;
import org.lud.engine.input.Mouse;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.MenuRender;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.GameService;
import org.lud.engine.service.UIService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MainMenu implements UI {
    private static final int ARC = 32;
    private static final int CENTER_Y = 800;
    private static final int PADDING_X = 25;
    private static final int PADDING_Y = 25;

    private final RenderContext render;
    private final GameService gameService;
    private final UIService uiService;
    private final KeyboardInput keyUI;
    private final Mouse mouse;

    private final Map<Clickable, Rectangle> buttons;
    private Button playButton;
    private Button gameButton;
    private Button achievementsButton;
    private Button settingsButton;
    private Button exitButton;

    public MainMenu(RenderContext render, GameService gameService,
                    UIService uiService, KeyboardInput keyUI,
                    Mouse mouse) {
        this.render = render;
        this.gameService = gameService;
        this.uiService = uiService;
        this.keyUI = keyUI;
        this.mouse = mouse;
        this.buttons = new HashMap<>();
    }

    public Map<Clickable, Rectangle> getButtons() {
        return buttons;
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
    }

    @Override
    public boolean canDraw(State state) {
        return state == GameState.MENU;
    }

    private void drawLogo(Graphics2D g2) {
        BufferedImage logo = UIService.getLogo();
        if(logo == null) { return; }
        BufferedImage img = Colorblindness.filter(logo);
        int logoWidth = logo.getWidth() * 2;
        int logoHeight = logo.getHeight() * 2;
        int x = getCenterX(getTotalWidth(), logoWidth);
        int y = render.getOffsetY()
                + render.scale(RenderContext.BASE_HEIGHT)/3;
        g2.drawImage(img, x, y, logoWidth, logoHeight, null);
    }

    public void draw(Graphics2D g2, GameMenu[] options) {
        buttons.clear();
        int totalWidth = getTotalWidth();
        g2.setColor(Colorblindness.filter(Colors.getBackground()));
        g2.fillRect(0, 0, totalWidth,
                render.scale(RenderContext.BASE_HEIGHT));

        drawLogo(g2);

        int startX = getStart()[0];
        int startY = getStart()[1];
        int x = startX, y = startY;

        for(GameMenu option : options) {
            if(option == GameMenu.PLAY) {
                BufferedImage baseImg = render.getMenuRender().getPLAY();
                BufferedImage altImg = render.getMenuRender().getPLAY_HIGHLIGHTED();
                int width = baseImg.getWidth();
                int height = baseImg.getHeight();
                x = startX; y = startY;
                x -= width; y -= height;

                if(playButton == null) {
                    playButton = createButton(x, y, width, height, () ->
                            option.run(gameService));
                }

                BufferedImage img = render.isHovered(playButton)
                        ? render.getMenuRender().getColorblindSprite(altImg)
                        : render.getMenuRender().getColorblindSprite(baseImg);
                g2.drawImage(img, x, y, null);
            }

            if(option == GameMenu.SETTINGS) {
                BufferedImage baseImg = render.getMenuRender().getSETTINGS();
                BufferedImage altImg = render.getMenuRender().getSETTINGS_HIGHLIGHTED();
                int width = baseImg.getWidth();
                int height = baseImg.getHeight();
                x = startX; y = startY;
                x -= 2;

                if(settingsButton == null) {
                    settingsButton = createButton(x, y, width, height, () ->
                            option.run(gameService));
                }

                BufferedImage img = render.isHovered(settingsButton)
                        ? render.getMenuRender().getColorblindSprite(altImg)
                        : render.getMenuRender().getColorblindSprite(baseImg);
                g2.drawImage(img, x, y, null);
            }

            if(option == GameMenu.ADVANCEMENTS) {
                BufferedImage baseImg = render.getMenuRender().getACHIEVEMENTS();
                BufferedImage altImg = render.getMenuRender().getACHIEVEMENTS_HIGHLIGHTED();
                int width = baseImg.getWidth();
                int height = baseImg.getHeight();
                x = startX; y = startY;
                x -= 2; y -= height;

                if(achievementsButton == null) {
                    achievementsButton = createButton(x, y, width, height, () ->
                            option.run(gameService));
                }

                BufferedImage img = render.isHovered(achievementsButton)
                        ? render.getMenuRender().getColorblindSprite(altImg)
                        : render.getMenuRender().getColorblindSprite(baseImg);
                g2.drawImage(img, x, y, null);
            }

            if(option == GameMenu.GAMES) {
                BufferedImage baseImg = render.getMenuRender().getGAME();
                BufferedImage altImg = render.getMenuRender().getGAME_HIGHLIGHTED();
                int width = baseImg.getWidth();
                int height = baseImg.getHeight();
                x = startX; y = startY;
                x -= width;

                if(gameButton == null) {
                    gameButton = createButton(x, y, width, height, () ->
                            option.run(gameService));
                }

                BufferedImage img = render.isHovered(gameButton)
                        ? render.getMenuRender().getColorblindSprite(altImg)
                        : render.getMenuRender().getColorblindSprite(baseImg);
                g2.drawImage(img, x, y, null);
            }

            if(option == GameMenu.EXIT) {
                BufferedImage baseImg = render.getMenuRender().getEXIT();
                BufferedImage altImg = render.getMenuRender().getEXIT_HIGHLIGHTED();
                int width = baseImg.getWidth();
                int height = baseImg.getHeight();

                x = render.scale((int) (RenderContext.BASE_WIDTH - width * 1.75f));
                y = render.scale(RenderContext.BASE_HEIGHT - 115);

                if(exitButton == null) {
                    exitButton = createButton(x, y, width, height, () ->
                            option.run(gameService));
                }

                BufferedImage img = render.isHovered(exitButton)
                        ? render.getMenuRender().getColorblindSprite(altImg)
                        : render.getMenuRender().getColorblindSprite(baseImg);
                g2.drawImage(img, x, y, null);
            }
        }
    }

    private int[] getStart() {
        int startX = getTotalWidth()/2 + 75;
        int startY = render.scale(RenderContext.BASE_HEIGHT - 300);
        return new int[]{startX, startY};
    }

    private Button createButton(int x, int y, int w, int h, Runnable action) {
        Button b = new Button(x, y, w, h, action);
        render.getMenuRender().getButtons().put(b, new Rectangle(x, y, w, h));
        return b;
    }

    private String showTooltip(GameMenu op) {
        if(op == GameMenu.PLAY) {
            return gameService.getTooltip(GameService.getGame(),
                    gameService.getSaveManager().autosaveExists()
            );
        }
        return op.getTooltip();
    }
}