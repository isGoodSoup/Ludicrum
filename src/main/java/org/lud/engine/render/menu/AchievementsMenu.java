package org.lud.engine.render.menu;

import org.lud.engine.entities.Achievement;
import org.lud.engine.entities.Button;
import org.lud.engine.entities.ButtonSprite;
import org.lud.engine.enums.GameState;
import org.lud.engine.enums.Theme;
import org.lud.engine.service.*;
import org.lud.engine.util.Colors;
import org.lud.engine.input.KeyboardInput;
import org.lud.engine.input.Mouse;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.AchievementLock;
import org.lud.engine.render.AchievementSprites;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.RenderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementsMenu implements UI {
    private static final int ARC = 32;
    private static final int STROKE = 6;
    private static final int OPTION_Y = 160;
    private static final Logger log = LoggerFactory.getLogger(AchievementsMenu.class);

    private final Map<Clickable, Rectangle> achievementBoxes;

    private final RenderContext render;
    private final UIService uiService;
    private final KeyboardInput keyUI;
    private final AchievementService achievementService;
    private final GameService gameService;
    private final Mouse mouse;

    private Button nextButton;
    private Button prevButton;
    private Button backButton;

    public AchievementsMenu(RenderContext render, UIService uiService, KeyboardInput keyUI,
                            AchievementService achievementService, GameService gameService, Mouse mouse) {
        this.render = render;
        this.uiService = uiService;
        this.keyUI = keyUI;
        this.achievementService = achievementService;
        this.gameService = gameService;
        this.mouse = mouse;
        this.achievementBoxes = new HashMap<>();
    }

    private int getTotalWidth() {
        return render.scale(RenderContext.BASE_WIDTH);
    }

    private int getCenterX(int containerWidth, int elementWidth) {
        return render.getOffsetX() + (containerWidth - elementWidth)/2;
    }

    private int getCenterY(int containerHeight, int elementHeight) {
        return render.getOffsetY() + (containerHeight - elementHeight)/2;
    }

    @Override
    public void drawMenu(Graphics2D g2) {
        draw(g2);
    }

    @Override
    public boolean canDraw(State state) {
        return state == GameState.ACHIEVEMENTS;
    }

    public void draw(Graphics2D g2) {
        int totalWidth = getTotalWidth();

        g2.setColor(Colorblindness.filter(Colors.getBackground()));
        g2.fillRect(0, 0, totalWidth, render.scale(RenderContext.BASE_HEIGHT));

        List<Achievement> list = achievementService.getUnlockedAchievements();
        int x = 32, y = 32;

        String text = Localization.lang.t("achievements.header");
        int headerY = render.getOffsetY() + render.scale(OPTION_Y);
        int headerWidth = g2.getFontMetrics().stringWidth(text);
        g2.setFont(UIService.getFont(UIService.fontSize()[4]));
        g2.setColor(Colorblindness.filter(Colors.getTheme() == Theme.DEFAULT
                ? Color.WHITE : Colors.getForeground()));

        int spacing = 25;
        int startY = headerY + spacing * 2;
        int width = RenderContext.BASE_WIDTH/2;
        int height = 100;
        x = getCenterX(totalWidth, width);
        boolean hasBackground = true;

        int itemsPerPage = KeyboardInput.getITEMS_PER_PAGE();
        int start = keyUI.getCurrentPage() * itemsPerPage;
        int end = Math.min(start + itemsPerPage, list.size());

        BufferedImage img = null;
        for(int i = start; i < end; i++) {
            Achievement a = list.get(i);

            int textX = x + render.scale(110);
            int titleY = startY + render.scale(60);
            int descY = titleY;

            g2.setColor(Colorblindness.filter(Colors.getTheme() == Theme.DEFAULT
                    ? Color.WHITE : Colors.getForeground()));
            g2.setFont(UIService.getFont(UIService.fontSize()[4]));

            achievementBoxes.put(a, new Rectangle(x, startY, width, height));
            if(isHovered(a)) {
                UIService.drawBox(g2, STROKE, x, startY,
                        width, height, ARC, hasBackground,
                        true, 255);

                if(!BooleanService.canZoomIn) {
                    String desc = a.getId().getDescription();
                    uiService.drawTooltip(g2, desc,
                            16, ARC, true,
                            render.scale(RenderContext.BASE_WIDTH/2 - g2.getFontMetrics().stringWidth(desc)/2),
                            render.scale(100));
                }
            } else {
                UIService.drawBox(g2, STROKE, x, startY,
                        width, height, ARC, hasBackground,
                        false, 255);
            }

            g2.drawString(a.getId().getTitle(), textX, descY);

            img = AchievementSprites.getSprite(a);
            if(img != null && !a.isUnlocked()) {
                img = AchievementLock.filter(img, a.isUnlocked());
            }

            if(img != null) {
                int iconSize = render.scale(64);
                int iconX = x + render.scale(20);
                int iconY = startY + (height - iconSize)/2;

                g2.drawImage(img, iconX, iconY, iconSize, iconSize, null);
            }

            startY += height + spacing;
        }
        initButtons();
        drawButtons(g2);

        if(BooleanService.canZoomIn) {
            int zoomWidth  = render.scale(RenderContext.BASE_WIDTH/2);
            int zoomHeight = render.scale(RenderContext.BASE_HEIGHT/2);
            int zoomX = getCenterX(totalWidth, zoomWidth);
            int zoomY = getCenterY(render.scale(RenderContext.BASE_HEIGHT), zoomHeight);

            UIService.drawBox(g2, STROKE,
                    zoomX, zoomY, zoomWidth, zoomHeight,
                    ARC, true, false, 180);

            int selectedIndex = keyUI.getSelectedIndexY();
            int actualIndex = start + selectedIndex;

            if(actualIndex >= 0 && actualIndex < list.size()) {
                Achievement selected = list.get(actualIndex);
                BufferedImage zoomImg = AchievementSprites.getSprite(selected);

                if(zoomImg != null) {
                    int padding = render.scale(40);
                    int imgSize = Math.min(zoomWidth - padding*2,
                            zoomHeight - padding*2);
                    int imgX = zoomX + (zoomWidth - imgSize)/2;
                    int imgY = zoomY + (zoomHeight - imgSize)/2;
                    g2.drawImage(zoomImg, imgX, imgY, imgSize, imgSize, null);
                }
            }
        }
    }

    private void initButtons() {
        int baseY = render.scale(RenderContext.BASE_HEIGHT - 115);
        int x = 0, y = baseY;
        Map<Clickable, Rectangle> buttons = render.getMenuRender().getButtons();

        x = render.scale(50);
        backButton = createButton(backButton, x, y, getSprites()[0].getWidth(), getSprites()[0].getHeight(),
                () -> {
                    log.debug("Back to Menu");
                    gameService.setState(GameState.MENU);
                    render.getMenuRender().onClose();
                });

        x = getTotalWidth()/2 - 80;
        prevButton = createButton(prevButton, x, y, getSprites()[0].getWidth(), getSprites()[0].getHeight(),
                () -> {
                    log.debug("Previous page");
                    int page = keyUI.getCurrentPage() - 1;
                    if (page < 0) {
                        page = 0;
                    }
                    keyUI.setCurrentPage(page);
                });

        x = getTotalWidth()/2;
        nextButton = createButton(nextButton, x, y, getSprites()[0].getWidth(), getSprites()[0].getHeight(),
                () -> {
                    log.debug("Next page");
                    int totalPages = (achievementService.getUnlockedAchievements().size()
                            + KeyboardInput.getITEMS_PER_PAGE() - 1)
                            /KeyboardInput.getITEMS_PER_PAGE() - 1;
                    int page = keyUI.getCurrentPage() + 1;
                    if(page > totalPages) {
                        page = totalPages;
                    }
                    keyUI.setCurrentPage(page);
                });
    }

    private Button createButton(Button button, int x, int y, int width, int height, Runnable action) {
        if(button == null) {
            button = new Button(x, y, width, height, action);
        }
        render.getMenuRender().addButton(button, new Rectangle(x, y, width, height));
        return button;
    }

    private void drawButtons(Graphics2D g2) {
        BufferedImage nextImg = render.isHovered(nextButton)
                ? Colorblindness.filter(getSprites()[3])
                : Colorblindness.filter(getSprites()[2]);

        BufferedImage prevImg = render.isHovered(prevButton)
                ? Colorblindness.filter(getSprites()[1])
                : Colorblindness.filter(getSprites()[0]);

        BufferedImage backImg = render.isHovered(backButton)
                ? Colorblindness.filter(getSprites()[1])
                : Colorblindness.filter(getSprites()[0]);

        render.getMenuRender().drawButtonsLayer(g2, nextButton, prevButton, backButton);
        g2.drawImage(nextImg, nextButton.getX(), nextButton.getY(), null);
        g2.drawImage(prevImg, prevButton.getX(), prevButton.getY(), null);
        g2.drawImage(backImg, backButton.getX(), backButton.getY(), null);
    }

    private BufferedImage[] getSprites() {
        ButtonSprite sprite1 = render.getMenuRender().getButtonRegistry().get("previous_page");
        ButtonSprite sprite2 = render.getMenuRender().getButtonRegistry().get("next_page");

        BufferedImage previousPage = sprite1.normal;
        BufferedImage nextPage = sprite2.normal;
        BufferedImage previousPageOn = sprite1.highlighted;
        BufferedImage nextPageOn = sprite2.highlighted;
        return new BufferedImage[]{previousPage, previousPageOn, nextPage, nextPageOn};
    }

    private boolean isHovered(Achievement param) {
        Rectangle r = achievementBoxes.get(param);
        return r.contains(mouse.getX(), mouse.getY());
    }
}