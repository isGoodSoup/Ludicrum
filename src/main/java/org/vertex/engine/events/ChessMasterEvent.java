package org.vertex.engine.events;

import org.vertex.engine.entities.Achievement;
import org.vertex.engine.interfaces.GameEvent;

import java.util.List;

public record ChessMasterEvent(List<Achievement> achievements) implements GameEvent {}
