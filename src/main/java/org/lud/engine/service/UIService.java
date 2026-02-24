package org.lud.engine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lud.engine.entities.Board;
import org.lud.engine.enums.Theme;
import org.lud.engine.util.Colors;
import org.lud.engine.input.Mouse;
import org.lud.engine.manager.MovesManager;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.MenuRender;
import org.lud.engine.render.RenderContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class UIService {
    private static Font font;
    private static BufferedImage logo;
    private static final int MENU_SPACING = 40;
    private static final int MENU_START_X = 100;
    private static final int MENU_START_Y = 160;
    private static final int MENU_FONT = 48;
    private static final int MOVES_CAP = 28;
    private static final int PADDING = 90;

    private final RenderContext render;

    private final transient BufferedImage YES;
    private final transient BufferedImage NO;

    private final PieceService pieceService;
    private final BoardService boardService;
    private final GameService gameService;
    private final ModelService modelService;
    private final TimerService timerService;
    private final Mouse mouse;
    private PromotionService promotionService;

    private static final Logger log = LoggerFactory.getLogger(UIService.class);

    public UIService(RenderContext render, PieceService pieceService,
                     BoardService boardService,
                     GameService gameService,
                     PromotionService promotionService,
                     ModelService modelService,
                     MovesManager movesManager, TimerService timerService, Mouse mouse) {
        this.render = render;
        this.pieceService = pieceService;
        this.boardService = boardService;
        this.gameService = gameService;
        this.modelService = modelService;
        this.timerService = timerService;
        this.mouse = mouse;
        this.promotionService = promotionService;
        logo = null;
        try {
            YES = getImage("/ticks/tick_yes");
            NO = getImage("/ticks/tick_no");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            font = Font.createFont(Font.TRUETYPE_FONT,
                    Objects.requireNonNull(Board.class.getResourceAsStream(
                            "/fonts/BoldPixels.ttf")));
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch(Exception e) {
            log.error(e.getMessage());
            font = new Font("Helvetica", Font.BOLD, 30);
        }
    }

    public static Font getFont(int size) {
        return font.deriveFont(Font.PLAIN, (float) size);
    }

    public static int getMENU_SPACING() {
        return MENU_SPACING;
    }

    public static int getMENU_START_Y() {
        return MENU_START_Y;
    }

    public static int getMENU_START_X() {
        return MENU_START_X;
    }

    public static int getMENU_FONT() {
        return MENU_FONT;
    }

    public BufferedImage getYES() {
        return YES;
    }

    public BufferedImage getNO() {
        return NO;
    }

    public static BufferedImage getLogo() {
        try {
            return switch(Colors.getTheme()) {
                case DEFAULT -> logo = getImage("/ui/logo/logo_final_v2-1");
                case BLACK -> logo = getImage("/ui/logo/logo_final_v2");
                case LEGACY -> logo = getImage("/ui/logo/logo_final_v2_creme");
                case OCEAN -> logo = getImage("/ui/logo/logo_final_v2_ocean");
                case FOREST -> logo = getImage("/ui/logo/logo_final_v2_forest");
                case LOGO -> logo = getImage("/ui/logo/logo_final_v2_logo");
                case FAIRY -> logo = getImage("/ui/logo/logo_final_v2_fairy");
            };
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMOVES_CAP() {
        return MOVES_CAP;
    }

    public static int getPADDING() {
        return PADDING;
    }

    public static BufferedImage getImage(String path) throws IOException {
        InputStream stream = UIService.class.getResourceAsStream(path + ".png");
        if (stream == null) {
            log.error("Resource not found: {}.png", path);
            return null;
        }
        return ImageIO.read(stream);
    }

    public void drawTimer(Graphics2D g2) {
        Color filtered = Colorblindness.filter(Theme.DEFAULT.getBackground());

        int boardX = render.getBoardRender().getBoardOriginX();
        int boardY = render.getBoardRender().getBoardOriginY();
        int boardWidth = Board.getSquare() * boardService.getBoard().getCol();

        g2.setFont(getFont(MENU_FONT));
        FontMetrics fm = g2.getFontMetrics();
        String time = timerService.getTimeString();
        int textWidth = fm.stringWidth(time);
        int textHeight = fm.getAscent() + fm.getDescent();

        int innerPadding = render.scale(30);
        int padding = render.scale(PADDING);

        int textX = boardX + (boardWidth - textWidth)/2;
        int textY = boardY - padding - fm.getDescent();

        int boxX = textX - innerPadding;
        int boxY = textY - fm.getAscent() - innerPadding;
        int boxWidth = textWidth + 2 * innerPadding;
        int boxHeight = textHeight + 2 * innerPadding;

        drawBox(g2, 4, boxX, boxY, boxWidth,
                boxHeight, MenuRender.getARC(),
                true, false, 255);
        g2.setColor(filtered);
        g2.drawString(time, textX, textY);
    }

    public void drawTick(Graphics2D g2, boolean isLegal) {
        if(!BooleanService.canShowTick) { return; }
        if(PieceService.getHeldPiece() == null) return;

        BufferedImage image = isLegal ? YES : NO;
        image = Colorblindness.filter(image);

        int size = render.scale(Board.getSquare());
        int boardX = render.getBoardRender().getBoardOriginX();
        int boardY = render.getBoardRender().getBoardOriginY();
        int boardWidth = Board.getSquare() * boardService.getBoard().getCol();

        int padding = render.scale(PADDING + 30);
        int tickX = boardX + (boardWidth - size)/2;
        int tickY = boardY - size - padding;

        g2.drawImage(image, tickX, tickY, size, size, null);
    }

    public static void drawBox(Graphics2D g2, int stroke, int x, int y, int width,
                               int height, int arc, boolean hasBackground, boolean isHighlighted,
                               int alpha) {
        if(hasBackground) {
            g2.setColor(Colorblindness.filter(Colors.SETTINGS));
            g2.fillRoundRect(x, y, width, height, arc, arc);
        }

        if(isHighlighted) {
            g2.setColor(Colorblindness.filter(Colors.getHighlight()));
        } else {
            g2.setColor(Colorblindness.filter(Colors.getEdge()));
        }

        g2.setStroke(new BasicStroke(stroke));
        g2.drawRoundRect(x, y, width, height, arc, arc);
    }

    public void drawTooltip(Graphics2D g2, String text, int padding, int arc,
                            boolean isMisplaced, int newX, int newY) {
        String[] lines = text.split("\n");
        FontMetrics fm = g2.getFontMetrics();

        int maxWidth = 0;
        for(String line : lines) {
            int lineWidth = fm.stringWidth(line);
            if(lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        int lineHeight = fm.getHeight();
        int totalTextHeight = lineHeight * lines.length;

        int boxX = 0;
        int boxY = 0;

        if(isMisplaced) {
            boxX = newX;
            boxY = newY;
        } else {
            boxX = mouse.getX();
            boxY = mouse.getY();
        }

        int boxWidth = maxWidth + padding * 2;
        int boxHeight = totalTextHeight + padding * 2;

        if (boxX + boxWidth > RenderContext.BASE_WIDTH) {
            boxX -= boxWidth;
        }
        if (boxY + boxHeight > RenderContext.BASE_HEIGHT) {
            boxY -= boxHeight;
        }

        drawBox(g2, 4, boxX, boxY, boxWidth, boxHeight,
                arc / 4, true, false, 180
        );

        g2.setColor(Colorblindness.filter(Color.WHITE));

        int textX = boxX + padding;
        int textY = boxY + padding + fm.getAscent();

        for (String line : lines) {
            g2.drawString(line, textX, textY);
            textY += lineHeight;
        }
    }

    public void drawButton(Graphics2D g2, int x, int y,
                           int width, int height, int arc, boolean isHighlighted) {
        g2.setColor(Colorblindness.filter(isHighlighted ?
                Colors.getEdge() : Colors.getForeground()));
        g2.fillRoundRect(x, y, width, height, arc, arc);
    }

    public void drawToggle(Graphics2D g2, BufferedImage image, int x, int y,
                           int width, int height) {
        g2.drawImage(image, x, y, width, height, null);
    }
}