package org.chess;

import org.chess.entities.Piece;

public record Move(Piece piece, int targetCol, int targetRow) {}
