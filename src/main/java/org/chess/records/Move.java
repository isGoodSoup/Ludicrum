package org.chess.records;

import org.chess.entities.Piece;
import org.chess.enums.Tint;

public record Move(Piece piece, int fromCol, int fromRow, int targetCol,
                   int targetRow, Tint color) {}