package org.vertex.engine.rulesets;

import org.vertex.engine.entities.Board;
import org.vertex.engine.entities.Checker;
import org.vertex.engine.entities.Piece;
import org.vertex.engine.enums.Tint;
import org.vertex.engine.interfaces.Ruleset;
import org.vertex.engine.records.Move;
import org.vertex.engine.records.MoveScore;
import org.vertex.engine.service.BoardService;
import org.vertex.engine.service.PieceService;

import java.util.ArrayList;
import java.util.List;

public class CheckersRuleset implements Ruleset {
    private final PieceService pieceService;
    private final BoardService boardService;

    private static final int[][] OFFSETS = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1},   // single moves
            {2, 2}, {2, -2}, {-2, 2}, {-2, -2}    // jumps
    };

    public CheckersRuleset(PieceService pieceService, BoardService boardService) {
        this.pieceService = pieceService;
        this.boardService = boardService;
    }

    @Override
    public boolean isLegalMove(Piece p, int targetCol, int targetRow) {
        if (!PieceService.isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        if (PieceService.getPieceAt(targetCol, targetRow,
                pieceService.getPieces()) != null) {
            return false;
        }

        return p.canMove(targetCol, targetRow, pieceService.getPieces());
    }

    @Override
    public int evaluateMove(Move move) {
        Piece p = move.piece();
        int score = 1;
        if (Math.abs(move.targetRow() - p.getRow()) == 2) {
            score += 3;
        }

        if (move.targetRow() == (p.getColor() == Tint.LIGHT ? 0 : 7)
                && p instanceof Checker c && !c.isKing()) {
            score += 5;
        }
        return score;
    }

    @Override
    public List<MoveScore> getAllLegalMoves(Tint color) {
        List<MoveScore> allMoves = new ArrayList<>();
        List<MoveScore> captureMoves = new ArrayList<>();
        for (Piece p : pieceService.getPieces()) {
            if (p.getColor() != color) continue;
            for (int[] offset : OFFSETS) {
                int newRow = p.getRow() + offset[0];
                int newCol = p.getCol() + offset[1];
                if (isLegalMove(p, newCol, newRow)) {
                    MoveScore ms = new MoveScore(new Move
                            (p, p.getCol(), p.getRow(), newCol, newRow, p.getColor(),
                                    p.getOtherPiece()), evaluateMove(new Move(p,
                            p.getCol(), p.getRow(), newCol, newRow, p.getColor(),
                            p.getOtherPiece())));
                    if (Math.abs(offset[0]) == 2) captureMoves.add(ms);
                    else allMoves.add(ms);
                }
            }
        }
        return captureMoves.isEmpty() ? allMoves : captureMoves;
    }

    public void executeMove(Move move) {
        Piece p = move.piece();
        int dRow = move.targetRow() - p.getRow();
        int dCol = move.targetCol() - p.getCol();

        if (Math.abs(dRow) == 2 && Math.abs(dCol) == 2) {
            int capturedRow = p.getRow() + dRow / 2;
            int capturedCol = p.getCol() + dCol / 2;
            Piece captured = PieceService.getPieceAt(capturedCol, capturedRow, pieceService.getPieces());
            if (captured != null) pieceService.removePiece(captured);
        }

        p.setRow(move.targetRow());
        p.setCol(move.targetCol());
        p.setX(move.targetCol() * Board.getSquare());
        p.setY(move.targetRow() * Board.getSquare());

        if (p instanceof Checker c) {
            if ((c.getColor() == Tint.LIGHT && c.getRow() == 0) || (c.getColor() == Tint.DARK && c.getRow() == 7)) {
                c.promoteToKing();
            }
        }
    }
}