package org.vertex.engine.events;

import org.vertex.engine.entities.Piece;
import org.vertex.engine.interfaces.GameEvent;

public record PromotionEvent(Piece piece) implements GameEvent {}