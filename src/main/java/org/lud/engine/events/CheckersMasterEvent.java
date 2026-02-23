package org.lud.engine.events;

import org.lud.engine.entities.Achievement;
import org.lud.engine.interfaces.GameEvent;

import java.util.List;

public record CheckersMasterEvent(List<Achievement> achievements) implements GameEvent {}
