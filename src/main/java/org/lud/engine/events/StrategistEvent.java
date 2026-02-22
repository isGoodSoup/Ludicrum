package org.lud.engine.events;

import org.lud.engine.entities.Piece;
import org.lud.engine.enums.Turn;
import org.lud.engine.interfaces.GameEvent;

public record StrategistEvent(Piece piece, Turn color) implements GameEvent {}
