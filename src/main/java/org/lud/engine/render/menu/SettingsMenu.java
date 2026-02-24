package org.lud.engine.render.menu;

import org.lud.engine.entities.Button;
import org.lud.engine.entities.ButtonSprite;
import org.lud.engine.enums.GameSettings;
import org.lud.engine.enums.GameState;
import org.lud.engine.enums.Theme;
import org.lud.engine.util.Colors;
import org.lud.engine.input.KeyboardInput;
import org.lud.engine.input.Mouse;
import org.lud.engine.input.MouseInput;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.MenuRender;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.GameService;
import org.lud.engine.service.Localization;
import org.lud.engine.service.UIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class SettingsMenu implements UI {
    private static final int ARC = 32;
    private static final int STROKE = 6;
    private static final int OPTION_Y = 160;
    private static final Logger log = LoggerFactory.getLogger(SettingsMenu.class);

    private final RenderContext render;
    private final UIService uiService;
    private final GameService gameService;
    private final KeyboardInput keyUI;
    private final Mouse mouse;
    private final MouseInput mouseInput;

    private final BufferedImage toggleOn;
    private final BufferedImage toggleOff;
    private final BufferedImage toggleOnHighlighted;
    private final BufferedImage toggleOffHighlighted;
    private final BufferedImage hardModeOn;
    private final BufferedImage hardModeOnHighlighted;

    private Button nextButton;
    private Button prevButton;
    private Button backButton;

    public SettingsMenu(RenderContext render, UIService uiService, GameService gs,
                        KeyboardInput keyUI, Mouse mouse, MouseInput mouseInput,
                        BufferedImage... images) {

        this.render = render;
        this.uiService = uiService;
        this.gameService = gs;
        this.keyUI = keyUI;
        this.mouse = mouse;
        this.mouseInput = mouseInput;

        this.toggleOn = images[0];
        this.toggleOff = images[1];
        this.toggleOnHighlighted = images[2];
        this.toggleOffHighlighted = images[3];
        this.hardModeOn = images[4];
        this.hardModeOnHighlighted = images[5];
    }

    private int getTotalWidth() {
        return render.scale(RenderContext.BASE_WIDTH);
    }

    private int getCenterX(int containerWidth, int elementWidth) {
        return render.getOffsetX() + (containerWidth - elementWidth)/2;
    }

    @Override
    public void drawMenu(Graphics2D g2) {
        initButtons();
        draw(g2, MenuRender.SETTINGS_MENU);
    }

    @Override
    public boolean canDraw(State state) {
        return state == GameState.SETTINGS;
    }

    public void draw(Graphics2D g2, GameSettings[] options) {
        clearToggles();

        final String SETTINGS = Localization.lang.t("settings.header");
        final String ENABLE = Localization.lang.t("settings.enable");

        int totalWidth = getTotalWidth();
        g2.setColor(Colorblindness.filter(Colors.getBackground()));
        g2.fillRect(0, 0, totalWidth,
                render.scale(RenderContext.BASE_HEIGHT));
        int x = 32;
        int y = 32;

        g2.setFont(UIService.getFont(UIService.fontSize()[4]));

        int headerY = render.getOffsetY() + render.scale(OPTION_Y);
        int headerWidth = g2.getFontMetrics().stringWidth(SETTINGS);

        g2.setColor(Colorblindness.filter(
                Colors.getTheme() == Theme.DEFAULT
                        ? Color.WHITE
                        : Colors.getForeground()));

        g2.drawString(SETTINGS.toUpperCase(),
                getCenterX(totalWidth, headerWidth),
                headerY);

        int startY = headerY + render.scale(100);
        int lineHeight = g2.getFontMetrics().getHeight()
                + render.scale(32);

        int itemsPerPage = 8;
        int startIndex = keyUI.getCurrentPage() * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage,
                options.length);

        int gap = render.scale(100);
        int maxRowWidth = 0;

        for(int i = startIndex; i < endIndex; i++) {
            String label = ENABLE + " " + options[i].getLabel();
            int textWidth = g2.getFontMetrics().stringWidth(label.toUpperCase());
            int toggleWidth = render.scale(toggleOn.getWidth()/2);
            int rowWidth = textWidth + gap + toggleWidth;
            if(rowWidth > maxRowWidth)
                maxRowWidth = rowWidth;
        }

        for(int i = startIndex; i < endIndex; i++) {
            GameSettings option = options[i];
            int relativeIndex = i - startIndex;
            boolean isSelected =
                    relativeIndex == keyUI.getSelectedIndexY();
            String label = ENABLE + " " + option.getLabel();
            int textWidth = g2.getFontMetrics().stringWidth(label.toUpperCase());
            int toggleWidth = render.scale(toggleOn.getWidth());
            int toggleHeight = render.scale(toggleOn.getHeight());
            int blockX = getCenterX(totalWidth, maxRowWidth);
            int textX = blockX;
            int toggleX = blockX + maxRowWidth - toggleWidth;
            int toggleY = startY - toggleHeight + 15;

            g2.drawString(label.toUpperCase(), textX,
                    render.getOffsetY() + startY);

            Rectangle toggleHitbox = new Rectangle(
                    render.getOffsetX() + toggleX,
                    render.getOffsetY() + toggleY,
                    toggleWidth,
                    toggleHeight
            );

            render.getMenuRender().addButton(option, toggleHitbox);
            boolean isEnabled = option.get();
            boolean isHovered = toggleHitbox.contains(
                    mouse.getX(), mouse.getY());

            BufferedImage toggleImage = drawToggle(
                    option, isEnabled, isSelected, isHovered);

            uiService.drawToggle(g2,
                    toggleImage,
                    render.getOffsetX() + toggleX,
                    render.getOffsetY() + toggleY,
                    toggleWidth,
                    toggleHeight);
            startY += lineHeight;
        }
        drawButtons(g2);
    }

    private BufferedImage drawToggle(GameSettings option, boolean isEnabled,
                                     boolean isSelected, boolean isHovered) {
        if(option == GameSettings.HARD_MODE) {
            if(isEnabled) {
                return (isSelected || isHovered)
                        ? Colorblindness.filter(hardModeOnHighlighted)
                        : Colorblindness.filter(hardModeOn);
            } else {
                return (isSelected || isHovered)
                        ? Colorblindness.filter(toggleOffHighlighted)
                        : Colorblindness.filter(toggleOff);
            }
        }

        if(isEnabled) {
            return (isSelected || isHovered)
                    ? Colorblindness.filter(toggleOnHighlighted)
                    : Colorblindness.filter(toggleOn);
        } else {
            return (isSelected || isHovered)
                    ? Colorblindness.filter(toggleOffHighlighted)
                    : Colorblindness.filter(toggleOff);
        }
    }

    private void initButtons() {
        int baseY = render.scale(RenderContext.BASE_HEIGHT - 115);
        int x = 0, y = baseY;
        Map<Clickable, Rectangle> buttons = render.getMenuRender().getButtons();

        x = render.scale(50);
        backButton = createButton(backButton, x, y, getSprites()[0].getWidth(), getSprites()[0].getHeight(),
                () -> {
                    gameService.setState(GameState.MENU);
                    log.debug("Back to Menu");
                    render.getMenuRender().onClose();
                });

        x = getTotalWidth()/2 - render.scale(80);
        prevButton = createButton(prevButton, x, y, getSprites()[0].getWidth(), getSprites()[0].getHeight(),
                () -> {
                    int page = keyUI.getCurrentPage() - 1;
                    if (page < 0) {
                        page = 0;
                        log.debug("No previous page");
                    } else {
                        log.debug("Previous page");
                    }
                    keyUI.setCurrentPage(page);
                });

        x = getTotalWidth()/2;
        nextButton = createButton(nextButton, x, y, getSprites()[0].getWidth(), getSprites()[0].getHeight(),
                () -> {
                    int totalPages = (MenuRender.SETTINGS_MENU.length + KeyboardInput.getITEMS_PER_PAGE() - 1)
                            / KeyboardInput.getITEMS_PER_PAGE() - 1;

                    int page = keyUI.getCurrentPage() + 1;
                    if(page > totalPages) {
                        page = totalPages;
                        log.debug("No next page");
                    } else {
                        log.debug("Next page");
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

    public void clearToggles() {
        render.getMenuRender().getButtons().clear();
        initButtons();
    }
}