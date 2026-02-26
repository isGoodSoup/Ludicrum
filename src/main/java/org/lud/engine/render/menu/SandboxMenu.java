package org.lud.engine.render.menu;

import org.lud.engine.entities.Board;
import org.lud.engine.enums.Games;
import org.lud.engine.util.Colors;
import org.lud.engine.input.Keyboard;
import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.BoardService;
import org.lud.engine.service.GameService;
import org.lud.engine.service.UIService;

import java.awt.*;

@SuppressWarnings("ALL")
public class SandboxMenu implements UI {
    private static final int ARC = 32;
    private static final int STROKE = 6;

    private final RenderContext render;
    private final BoardService boardService;

    public SandboxMenu(RenderContext render,
                       BoardService boardService) {
        this.render = render;
        this.boardService = boardService;
    }

    @Override
    public void drawMenu(Graphics2D g2) {
        draw(g2);
    }

    @Override
    public boolean canDraw(State state) {
        return state == Games.SANDBOX;
    }

    public void draw(Graphics2D g2) {
        if(GameService.getGame() != Games.SANDBOX) { return; }
        int boardX = render.getBoardRender().getBoardOriginX();
        int boardY = render.getBoardRender().getBoardOriginY();
        int boardWidth = Board.getSquare() * boardService.getBoard().getCol();
        int boardHeight = Board.getSquare() * boardService.getBoard().getRow();
        int boardBottom = boardY + boardHeight;

        g2.setFont(UIService.getFont(UIService.fontSize()[4]));
        FontMetrics fm = g2.getFontMetrics();
        Keyboard keyboard = boardService.getService().getKeyboard();
        String input = keyboard.getCurrentText();
        int textWidth = fm.stringWidth(input);
        int textHeight = fm.getAscent() + fm.getDescent();

        int innerPadding = render.scale(30);
        int padding = render.scale(90);

        int spacingBelowBoard = render.scale(60);

        int boxWidth = boardWidth;
        int boxHeight = textHeight + 2 * innerPadding;

        int boxX = boardX;
        int boxY = boardBottom + spacingBelowBoard;

        int textX = boxX + (boxWidth - textWidth)/2;
        int textY = boxY + (boxHeight + fm.getAscent() - fm.getDescent())/2;

        UIService.drawBox(g2, STROKE, boxX, boxY, boxWidth,
                boxHeight, ARC, true, false, 255);

        g2.setColor(Colorblindness.filter(Colors.getForeground()));
        g2.drawString(input, textX, textY);
    }
}