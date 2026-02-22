package org.lud.engine.interfaces;

import org.lud.engine.entities.Piece;
import org.lud.engine.enums.Turn;
import org.lud.engine.records.Move;
import org.lud.engine.records.MoveScore;

import java.util.List;

public interface Ruleset {
    boolean isLegalMove(Piece p, int col, int row);
    int evaluateMove(Move move);
    List<MoveScore> getAllLegalMoves(Turn color);
}
