package org.lud.engine.records;

import org.lud.engine.entities.Achievement;
import org.lud.engine.entities.Piece;
import org.lud.engine.enums.Games;
import org.lud.engine.enums.Turn;

import java.io.Serializable;
import java.util.List;

public record Save(Games game, String name, Turn player, List<Piece> pieces,
                   List<Achievement> achievements) implements Serializable {}
