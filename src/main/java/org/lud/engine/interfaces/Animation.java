package org.lud.engine.interfaces;

import org.lud.engine.entities.Piece;

import java.awt.*;

public interface Animation {
    void update(double delta);
    void render(Graphics2D g2);
    boolean isFinished();
    default boolean affects(Piece piece) {
        return false;
    }
    boolean affects(Object obj);
}
