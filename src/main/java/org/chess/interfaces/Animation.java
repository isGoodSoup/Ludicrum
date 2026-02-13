package org.chess.interfaces;

import org.chess.entities.Piece;

import java.awt.*;

public interface Animation {
    void update(double delta);
    void render(Graphics2D g2);
    boolean isFinished();
    default boolean affects(Piece piece) {
        return false;
    }
}
