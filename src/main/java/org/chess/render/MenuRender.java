package org.chess.render;

import org.chess.entities.Board;
import org.chess.enums.ColorblindType;
import org.chess.gui.Colors;
import org.chess.input.MenuInput;
import org.chess.input.Mouse;
import org.chess.input.MoveManager;
import org.chess.service.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MenuRender {
    public static final String[] optionsMenu = { "PLAY AGAINST", "RULES", "EXIT" };
    public static final String[] optionsMode = { "PLAYER", "AI" };
    public static final String[] optionsTweaks = { "RULES",
            "Dark Mode", "Promotion", "Training Mode", "Continue", "Castling",
            "En Passant", "Timer", "Stopwatch", "Chaos " +
            "Mode", "Testing", "Undo Moves", "Reset Table", "Colorblind Mode",
            "Themes"};
    private static final String ENABLE = "Enable ";
    private static final int OPTION_X = 100;
    private static final int OPTION_Y = 80;
    private final BufferedImage DARK_MODE_ON;
    private final BufferedImage DARK_MODE_OFF;
    private final BufferedImage DARK_MODE_ON_HIGHLIGHTED;
    private final BufferedImage DARK_MODE_OFF_HIGHLIGHTED;
    private final BufferedImage cbDARK_MODE_ON;
    private final BufferedImage cbDARK_MODE_OFF;
    private final BufferedImage cbDARK_MODE_ON_HIGHLIGHTED;
    private final BufferedImage cbDARK_MODE_OFF_HIGHLIGHTED;
    private final BufferedImage TOGGLE_ON;
    private final BufferedImage TOGGLE_OFF;
    private final BufferedImage TOGGLE_ON_HIGHLIGHTED;
    private final BufferedImage TOGGLE_OFF_HIGHLIGHTED;
    private final BufferedImage cbTOGGLE_ON;
    private final BufferedImage cbTOGGLE_OFF;
    private final BufferedImage cbTOGGLE_ON_HIGHLIGHTED;
    private final BufferedImage cbTOGGLE_OFF_HIGHLIGHTED;
    private static ColorblindType cb;
    private Rectangle yesButton;
    private Rectangle noButton;
    private int lastHoveredIndex = -1;
    private int exitSelection = 0;
    private final int OFFSET_X;
    private FontMetrics fontMetrics;
    private int currentPage = 1;

    private static RenderContext render;
    private final GameService gameService;
    private final BoardService boardService;
    private final MoveManager moveManager;
    private final GUIService guiService;
    private final Mouse mouse;
    private MenuInput menuInput;

    public MenuRender(RenderContext render, GUIService guiService,
                      GameService gameService,
                      BoardService boardService, MoveManager moveManager,
                      Mouse mouse) {
        MenuRender.render = render;
        this.guiService = guiService;
        this.gameService = gameService;
        this.boardService = boardService;
        this.moveManager = moveManager;
        this.mouse = mouse;
        this.menuInput = new MenuInput(render, this, guiService, gameService,
                boardService, moveManager, mouse);
        cb = ColorblindType.PROTANOPIA;

        try {
            DARK_MODE_ON = guiService.getImage("/ui/dark-mode_on");
            DARK_MODE_OFF = guiService.getImage("/ui/dark-mode_off");
            DARK_MODE_ON_HIGHLIGHTED = guiService.getImage("/ui/dark-mode_on-h");
            DARK_MODE_OFF_HIGHLIGHTED = guiService.getImage("/ui/dark-mode_off-h");
            TOGGLE_ON = guiService.getImage("/ui/toggle_on");
            TOGGLE_OFF = guiService.getImage("/ui/toggle_off");
            TOGGLE_ON_HIGHLIGHTED = guiService.getImage("/ui/toggle_on-h");
            TOGGLE_OFF_HIGHLIGHTED = guiService.getImage("/ui/toggle_off-h");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OFFSET_X = render.scale(RenderContext.BASE_WIDTH)/2 - 100
                + GUIService.getGRAPHICS_OFFSET() - 30;
        cbDARK_MODE_ON = Colorblindness.filter(DARK_MODE_ON);
        cbDARK_MODE_OFF = Colorblindness.filter(DARK_MODE_OFF);
        cbDARK_MODE_ON_HIGHLIGHTED = Colorblindness.filter(DARK_MODE_ON_HIGHLIGHTED);
        cbDARK_MODE_OFF_HIGHLIGHTED =
                Colorblindness.filter(DARK_MODE_OFF_HIGHLIGHTED);
        cbTOGGLE_ON = Colorblindness.filter(TOGGLE_ON);
        cbTOGGLE_OFF = Colorblindness.filter(TOGGLE_OFF);
        cbTOGGLE_ON_HIGHLIGHTED = Colorblindness.filter(TOGGLE_ON_HIGHLIGHTED);
        cbTOGGLE_OFF_HIGHLIGHTED = Colorblindness.filter(TOGGLE_OFF_HIGHLIGHTED);
    }

    public MenuInput getMenuInput() {
        return menuInput;
    }

    public static ColorblindType getCb() {
        return cb;
    }

    public static void setCb(ColorblindType cb) {
        MenuRender.cb = cb;
    }

    public int getOFFSET_X() {
        return OFFSET_X;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public Rectangle getYesButton() {
        return yesButton;
    }

    public Rectangle getNoButton() {
        return noButton;
    }

    public int getExitSelection() {
        return exitSelection;
    }

    public static int getOPTION_X() {
        return OPTION_X;
    }

    public static int getOPTION_Y() {
        return OPTION_Y;
    }

    public static String getENABLE() {
        return ENABLE;
    }
    
    private int getOptionsStartY() {
        return 100 + GUIService.getFontBold(32).getSize() + 8;
    }

    private boolean getOptionState(String option) {
        return switch(option) {
            case "Dark Mode" -> BooleanService.isDarkMode;
            case "Promotion" -> BooleanService.canPromote;
            case "Training Mode" -> BooleanService.canTrain;
            case "Continue" -> BooleanService.canContinue;
            case "Castling" -> BooleanService.canDoCastling;
            case "En Passant" -> BooleanService.canDoEnPassant;
            case "Timer" -> BooleanService.canTime;
            case "Stopwatch" -> BooleanService.canStopwatch;
            case "Testing" -> BooleanService.canDoTest;
            case "Chaos Mode" -> BooleanService.canDoChaos;
            case "Undo Moves" -> BooleanService.canUndoMoves;
            case "Reset Table" -> BooleanService.canResetTable;
            case "Colorblind Mode" -> BooleanService.canBeColorblind;
            case "Themes" -> BooleanService.canTheme;
            default -> false;
        };
    }

    public void toggleOption(String option) {
        switch(option) {
            case "Dark Mode" -> {
                BooleanService.isDarkMode ^= true;
                Colors.toggleDarkTheme();
            }
            case "Promotion" -> BooleanService.canPromote ^= true;
            case "Training Mode" -> BooleanService.canTrain ^= true;
            case "Continue" -> BooleanService.canContinue ^= true;
            case "Castling" -> BooleanService.canDoCastling ^= true;
            case "En Passant" -> BooleanService.canDoEnPassant ^= true;
            case "Timer" -> {
                BooleanService.canTime ^= true;
                BooleanService.canStopwatch = false;
            }
            case "Stopwatch" -> {
                BooleanService.canStopwatch ^= true;
                BooleanService.canTime = false;
            }
            case "Testing" -> BooleanService.canDoTest ^= true;
            case "Chaos Mode" -> BooleanService.canDoChaos ^= true;
            case "Undo Moves" -> BooleanService.canUndoMoves ^= true;
            case "Reset Table" -> BooleanService.canResetTable ^= true;
            case "Colorblind Mode" -> BooleanService.canBeColorblind ^= true;
            case "Themes" -> BooleanService.canTheme ^= true;
        }
    }

    private void drawToggle(Graphics2D g2, BufferedImage image, int x, int y,
                            int width, int height) {
        g2.drawImage(image, x, y, width, height, null);
    }

    private static void drawLogo(Graphics2D g2) {
        if(GUIService.getLogo() == null) { return; }
        BufferedImage img = BooleanService.isDarkMode ?
                GUIService.getLogo_v2() : Colorblindness.filter(GUIService.getLogo());
        int boardWidth = Board.getSquare() * 8;
        int boardCenterX = render.getOffsetX() + render.scale(
                GUIService.getEXTRA_WIDTH()) * 2 + boardWidth/2;
        int logoWidth = GUIService.getLogo().getWidth()/2;
        int logoHeight = GUIService.getLogo().getHeight()/2;
        int x = boardCenterX - logoWidth / 2;
        int y = render.getOffsetY() + render.scale(RenderContext.BASE_HEIGHT) / 7;
        g2.drawImage(img, x, y, logoWidth, logoHeight, null);
    }

    public void drawGraphics(Graphics2D g2, String[] options) {
        int boardWidth = render.scale(Board.getSquare() * 8);
        int totalWidth = boardWidth + 2 * render.scale(GUIService.getEXTRA_WIDTH());

        g2.setColor(Colorblindness.filter(GUIService.getNewBackground()));
        g2.fillRect(0, 0, totalWidth, render.scale(RenderContext.BASE_HEIGHT));

        g2.setFont(GUIService.getFontBold(GUIService.getMENU_FONT()));
        drawLogo(g2);

        int startY = render.scale(RenderContext.BASE_HEIGHT) / 2 + render.scale(GUIService.getMENU_START_Y());
        int spacing = render.scale(GUIService.getMENU_SPACING());

        for(int i = 0; i < options.length; i++) {
            String optionText = options[i];
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(optionText);

            int x = render.getOffsetX() + (totalWidth - textWidth) / 2 + render.scale(GUIService.getGRAPHICS_OFFSET());
            int y = render.getOffsetY() + startY + i * spacing;

            Rectangle hitbox = new Rectangle(
                    render.unscaleX(OFFSET_X),
                    render.unscaleY(y),
                    render.unscaleX(render.scale(200)),
                    render.unscaleY(render.scale(40))
            );
            boolean isHovered = hitbox.contains(mouse.getX(), mouse.getY());
            boolean isSelected = (i == moveManager.getSelectedIndexY());

            Color foreground = Colorblindness.filter(GUIService.getNewForeground());
            Color textColor = isSelected ? Color.YELLOW : (isHovered ? Color.WHITE : foreground);

            g2.setColor(textColor);
            g2.drawString(optionText, x, y);

            if(isHovered && lastHoveredIndex != i) {
                guiService.getFx().play(BooleanService.getRandom(1, 2));
                lastHoveredIndex = i;
            }
        }
    }

    public void drawOptionsMenu(Graphics2D g2, String[] options) {
        int boardWidth = render.scale(Board.getSquare() * 8);
        int totalWidth = boardWidth + 2 * render.scale(GUIService.getEXTRA_WIDTH());

        g2.setColor(BooleanService.canBeColorblind || BooleanService.isDarkMode
                ? Colorblindness.filter(GUIService.getNewBackground())
                : GUIService.getNewBackground());
        g2.fillRect(0, 0, totalWidth, render.scale(RenderContext.BASE_HEIGHT));
        menuInput.updatePage();
        g2.setFont(GUIService.getFontBold(32));
        fontMetrics = g2.getFontMetrics();

        int startY = render.scale(OPTION_Y); // scaled Y
        int centerX = totalWidth / 2;

        int lineHeight = render.scale(fontMetrics.getHeight() + 4);
        int itemsPerPage = 8;

        int startIndex = (currentPage - 1) * itemsPerPage + 1;
        int endIndex = Math.min(startIndex + itemsPerPage, optionsTweaks.length);

        for (int i = startIndex; i < endIndex; i++) {
            String enabledOption = ENABLE + options[i];

            g2.setFont(GUIService.getFontBold(24));

            int toggleWidth = render.scale(cbTOGGLE_ON.getWidth() / 2);
            int toggleHeight = render.scale(cbTOGGLE_ON.getHeight() / 2);

            int toggleX = centerX + render.scale(200);
            int toggleY = startY - toggleHeight + render.scale(16);

            Rectangle toggleHitbox = new Rectangle(
                    render.unscaleX(toggleX),
                    render.unscaleY(toggleY),
                    render.unscaleX(toggleWidth),
                    render.unscaleY(toggleHeight)
            );

            boolean isHovered = toggleHitbox.contains(mouse.getX(), mouse.getY());
            boolean isSelected = (i == moveManager.getSelectedIndexY());
            boolean isEnabled = getOptionState(options[i]);

            g2.setColor(Colorblindness.filter(GUIService.getNewForeground()));
            g2.drawString(enabledOption, render.getOffsetX()
                    + render.scale(OPTION_X), render.getOffsetY() + startY);

            BufferedImage toggleImage;
            if (options[i].equals("Dark Mode")) {
                toggleImage = isEnabled
                        ? (isSelected || isHovered ? cbDARK_MODE_ON_HIGHLIGHTED : cbDARK_MODE_ON)
                        : (isSelected || isHovered ? cbDARK_MODE_OFF_HIGHLIGHTED : cbDARK_MODE_OFF);
            } else {
                toggleImage = isEnabled
                        ? (isSelected || isHovered ? cbTOGGLE_ON_HIGHLIGHTED : cbTOGGLE_ON)
                        : (isSelected || isHovered ? cbTOGGLE_OFF_HIGHLIGHTED : cbTOGGLE_OFF);
            }
            drawToggle(g2, toggleImage, render.getOffsetX()
                    + toggleX, render.getOffsetY() + toggleY, toggleWidth,
                    toggleHeight);
            startY += lineHeight;
        }
    }

    public BufferedImage getSprite(int i) {
        return switch (i) {
            case 0 -> DARK_MODE_ON;
            case 1 -> DARK_MODE_OFF;
            case 2 -> DARK_MODE_ON_HIGHLIGHTED;
            case 3 -> DARK_MODE_OFF_HIGHLIGHTED;
            case 4 -> cbDARK_MODE_ON;
            case 5 -> cbDARK_MODE_OFF;
            case 6 -> cbDARK_MODE_ON_HIGHLIGHTED;
            case 7 -> cbDARK_MODE_OFF_HIGHLIGHTED;
            case 8 -> TOGGLE_ON;
            case 9 -> TOGGLE_OFF;
            case 10 -> TOGGLE_ON_HIGHLIGHTED;
            case 11 -> TOGGLE_OFF_HIGHLIGHTED;
            case 12 -> cbTOGGLE_ON;
            case 13 -> cbTOGGLE_OFF;
            case 14 -> cbTOGGLE_ON_HIGHLIGHTED;
            case 15 -> cbTOGGLE_OFF_HIGHLIGHTED;
            default -> null;
        };
    }
}
