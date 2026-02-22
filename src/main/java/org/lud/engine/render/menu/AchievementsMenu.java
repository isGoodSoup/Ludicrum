package org.lud.engine.render.menu;

import org.lud.engine.entities.Achievement;
import org.lud.engine.enums.GameState;
import org.lud.engine.enums.Theme;
import org.lud.engine.gui.Colors;
import org.lud.engine.input.KeyboardInput;
import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.AchievementLock;
import org.lud.engine.render.AchievementSprites;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.AchievementService;
import org.lud.engine.service.BooleanService;
import org.lud.engine.service.UIService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class AchievementsMenu implements UI {
    private static final int ARC = 32;
    private static final int STROKE = 6;
    private static final int OPTION_Y = 160;
    private static final String ACHIEVEMENTS = "ACHIEVEMENTS";

    private final RenderContext render;
    private final UIService uiService;
    private final KeyboardInput keyUI;
    private final AchievementService achievementService;

    public AchievementsMenu(RenderContext render,
                            UIService uiService,
                            KeyboardInput keyUI,
                            AchievementService achievementService) {
        this.render = render;
        this.uiService = uiService;
        this.keyUI = keyUI;
        this.achievementService = achievementService;
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
        render.getMenuRender().clearButtons();
        int totalWidth = getTotalWidth();

        g2.setColor(Colorblindness.filter(Colors.getBackground()));
        g2.fillRect(0, 0, totalWidth, render.scale(RenderContext.BASE_HEIGHT));

        List<Achievement> list = achievementService.init();
        int x = 32, y = 32;

        String text = ACHIEVEMENTS;
        int headerY = render.getOffsetY() + render.scale(OPTION_Y);
        int headerWidth = g2.getFontMetrics().stringWidth(text);
        g2.setFont(UIService.getFont(UIService.getMENU_FONT()));
        g2.setColor(Colorblindness.filter(Colors.getTheme() == Theme.DEFAULT
                ? Color.WHITE : Colors.getForeground()));

        int spacing = 25;
        int startY = headerY + spacing * 2;
        int width = RenderContext.BASE_WIDTH / 2;
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
            int titleY = startY + render.scale(70);
            int descY = titleY;

            g2.setColor(Colorblindness.filter(Colors.getTheme() == Theme.DEFAULT
                    ? Color.WHITE : Colors.getForeground()));
            g2.setFont(UIService.getFont(UIService.getMENU_FONT()));

            render.getMenuRender().getButtons().put(a, new Rectangle(x, startY, width, height));
            if(render.isHovered(a)) {
                UIService.drawBox(g2, STROKE, x, startY,
                        width, height, ARC, hasBackground,
                        true, 255);
                uiService.drawTooltip(g2, a.getId().getDescription(), 16, ARC);
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

        if(BooleanService.canZoomIn) {
            int zoomWidth  = render.scale(RenderContext.BASE_WIDTH / 2);
            int zoomHeight = render.scale(RenderContext.BASE_HEIGHT / 2);
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
}