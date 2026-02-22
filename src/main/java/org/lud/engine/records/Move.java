package org.lud.engine.records;

import org.lud.engine.entities.Piece;
import org.lud.engine.enums.Turn;

public record Move(Piece piece, int fromCol, int fromRow, int targetCol, int targetRow,
                   Turn color, Piece captured, boolean wasPromoted, Turn currentTurn,
                   boolean hasMoved, int preCol, int preRow, boolean isTwoStepsAhead) {}