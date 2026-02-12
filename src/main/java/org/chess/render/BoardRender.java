package org.chess.render;

import org.chess.entities.Board;
import org.chess.entities.Piece;
import org.chess.gui.Colors;
import org.chess.service.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardRender {
    private final RenderContext render;
    private final GUIService guiService;
    private final PieceService pieceService;
    private final BoardService boardService;
    private final PromotionService promotionService;

    public BoardRender(RenderContext render, GUIService guiService,
                       PieceService pieceService,
                       BoardService boardService,
                       PromotionService promotionService) {
        this.render = render;
        this.guiService = guiService;
        this.pieceService = pieceService;
        this.boardService = boardService;
        this.promotionService = promotionService;
    }

    public void drawBoard(Graphics2D g2) {
        Piece currentPiece = PieceService.getPiece();
        Piece hoveredPiece = pieceService.getHoveredPieceKeyboard();
        int hoverX = pieceService.getHoveredSquareX();
        int hoverY = pieceService.getHoveredSquareY();

        drawBaseBoard(g2);

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        if (hoverX >= 0 && hoverY >= 0) {
            g2.setColor(Colorblindness.filter(Colors.getODD()));
            int squareSize = render.scale(Board.getSquare());
            g2.fillRect(
                    render.getOffsetX() + render.scale(GUIService.getEXTRA_WIDTH()) + hoverX * squareSize,
                    render.getOffsetY() + hoverY * squareSize,
                    squareSize,
                    squareSize
            );
        }

        Piece selectedPiece = pieceService.getMoveManager() != null
                ? pieceService.getMoveManager().getSelectedPiece() : null;

        for (Piece p : pieceService.getPieces()) {
            if (p != currentPiece) {
                BufferedImage img = (p == hoveredPiece) ? hoveredPiece.getHovered() : p.getImage();
                drawPiece(g2, p, Colorblindness.filter(img));
            }
        }

        if (currentPiece != null) {
            if (!BooleanService.isDragging) {
                currentPiece.setScale(currentPiece.getDEFAULT_SCALE());
            }
            drawPiece(g2, currentPiece);
        }
    }

    public void drawBaseBoard(Graphics2D g2) {
        final int ROW = boardService.getBoard().getROW();
        final int COL = boardService.getBoard().getCOL();
        final int PADDING = render.scale(Board.getPadding());
        final int SQUARE = render.scale(Board.getSquare());

        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                boolean isEven = (row + col) % 2 == 0;
                g2.setColor(isEven ? Colors.getEVEN() : Colors.getODD());
                g2.fillRect(
                        render.getOffsetX() + render.scale(GUIService.getEXTRA_WIDTH()) + col * SQUARE,
                        render.getOffsetY() + row * SQUARE,
                        SQUARE,
                        SQUARE
                );

                g2.setFont(GUIService.getFont(24));
                g2.setColor(isEven ? Colors.getODD() : Colors.getEVEN());
                g2.drawString(
                        BoardService.getSquareName(col, row),
                        render.getOffsetX() + render.scale(GUIService.getEXTRA_WIDTH()) + col * SQUARE + PADDING,
                        render.getOffsetY() + row * SQUARE + SQUARE - PADDING
                );
            }
        }
    }

    public void drawPiece(Graphics2D g2, Piece piece) {
        drawPiece(g2, piece, null);
    }

    public void drawPiece(Graphics2D g2, Piece piece, BufferedImage override) {
        int square = render.scale(Board.getSquare());
        int drawSize = (int) (square * piece.getScale());
        int offset = (square - drawSize) / 2;

        g2.drawImage(
                override != null ? override : piece.getFilteredSprite(piece.getImage()),
                render.getOffsetX() + render.scale(piece.getX()) + offset + render.scale(GUIService.getEXTRA_WIDTH()),
                render.getOffsetY() + render.scale(piece.getY()) + offset,
                drawSize,
                drawSize,
                null
        );
    }
}