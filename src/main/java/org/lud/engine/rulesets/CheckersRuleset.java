package org.lud.engine.rulesets;

import org.lud.engine.entities.Checker;
import org.lud.engine.entities.Piece;
import org.lud.engine.enums.Turn;
import org.lud.engine.interfaces.Ruleset;
import org.lud.engine.records.Move;
import org.lud.engine.records.MoveScore;
import org.lud.engine.service.PieceService;

import java.util.ArrayList;
import java.util.List;

public class CheckersRuleset implements Ruleset {
    private final PieceService pieceService;

    private static final int[][] OFFSETS = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1},
            {2, 2}, {2, -2}, {-2, 2}, {-2, -2}
    };

    public CheckersRuleset(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    @Override
    public boolean isLegalMove(Piece p, int targetCol, int targetRow) {
        if(!PieceService.isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        if(PieceService.getPieceAt(targetCol, targetRow,
                pieceService.getPieces()) != null) {
            return false;
        }

        return p.canMove(targetCol, targetRow, pieceService.getPieces());
    }

    @Override
    public int evaluateMove(Move move) {
        Piece p = move.piece();
        int score = 1;
        if(Math.abs(move.targetRow() - p.getRow()) == 2) {
            score += 3;
        }

        if(move.targetRow() == (p.getColor() == Turn.LIGHT ? 0 : 7)
                && p instanceof Checker c && !c.isKing()) {
            score += 5;
        }
        return score;
    }

    @Override
    public List<MoveScore> getAllLegalMoves(Turn color) {
        List<MoveScore> allMoves = new ArrayList<>();
        List<MoveScore> captureMoves = new ArrayList<>();
        for(Piece p : pieceService.getPieces()) {
            if(p.getColor() != color) continue;
            for(int[] offset : OFFSETS) {
                int newRow = p.getRow() + offset[0];
                int newCol = p.getCol() + offset[1];
                if(isLegalMove(p, newCol, newRow)) {
                    MoveScore ms = new MoveScore(new Move
                            (p, p.getCol(), p.getRow(), newCol, newRow, p.getColor(),
                                    p.getOtherPiece(), p.isPromoted(), p.getColor(), p.hasMoved(),
                                    p.getPreCol(), p.getPreRow(), p.isTwoStepsAhead()),
                            evaluateMove(new Move(p,
                            p.getCol(), p.getRow(), newCol, newRow, p.getColor(),
                            p.getOtherPiece(), p.isPromoted(), p.getColor(), p.hasMoved(),
                                    p.getPreCol(), p.getPreRow(), p.isTwoStepsAhead())));
                    if(Math.abs(offset[0]) == 2) captureMoves.add(ms);
                    else allMoves.add(ms);
                }
            }
        }
        return captureMoves.isEmpty() ? allMoves : captureMoves;
    }
}