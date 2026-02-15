package org.vertex.engine.rulesets;

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
        List<MoveScore> moves = new ArrayList<>();
        for (Piece p : pieceService.getPieces()) {
            if (p.getColor() != color) continue;

            for(int dr = -2; dr <= 2; dr++) {
                for(int dc = -2; dc <= 2; dc++) {
                    int newRow = p.getRow() + dr;
                    int newCol = p.getCol() + dc;
                    if (isLegalMove(p, newCol, newRow)) {
                        moves.add(new MoveScore(new Move(p, p.getCol(),
                                p.getRow(), newCol, newRow, p.getColor(), p.getOtherPiece()),
                                evaluateMove(new Move(p, p.getCol(),
                                        p.getRow(), newCol, newRow,
                                        p.getColor(), p.getOtherPiece()))));
                    }
                }
            }
        }
        return moves;
    }
}
