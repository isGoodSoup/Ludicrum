package org.lud.engine.events;

import org.lud.engine.entities.Piece;
import org.lud.engine.interfaces.GameEvent;

public record CastlingEvent(Piece king, Piece rook) implements GameEvent {}
