package org.vertex.engine.records;

import org.vertex.engine.entities.Piece;
import org.vertex.engine.enums.Tint;

public record Move(Piece piece, int fromCol, int fromRow, int targetCol,
                   int targetRow, Tint color, Piece captured) {}