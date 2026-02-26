package org.lud.engine.interfaces;

import org.lud.engine.entities.Piece;
import org.lud.engine.service.BoardService;

import java.util.List;

public interface GameModule {
    String getId();
    void setup(BoardService boardService);
    void apply(RulesetManager rulesManager);
    List<Piece> setPieces();
}