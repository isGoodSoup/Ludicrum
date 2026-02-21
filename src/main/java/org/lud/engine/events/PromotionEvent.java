package org.lud.engine.events;

import org.lud.engine.entities.Piece;
import org.lud.engine.interfaces.GameEvent;

public record PromotionEvent(Piece piece) implements GameEvent {}