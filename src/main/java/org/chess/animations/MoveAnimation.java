package org.chess.animations;

import org.chess.entities.Piece;
import org.chess.interfaces.Animation;

import java.awt.*;

public class MoveAnimation implements Animation {
    public Piece piece;
    public int startX, startY;
    public int targetX, targetY;
    public double progress;

    public MoveAnimation(Piece piece, int startX, int startY, int targetX, int targetY) {
        this.piece = piece;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.progress = 0.0;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public double getProgress() {
        return progress;
    }

    @Override
    public boolean affects(Piece piece) {
        return this.piece == piece;
    }

    @Override
    public void update(double delta) {
        progress = Math.min(1.0, progress + delta);
        piece.setX((int)(startX + (targetX - startX) * progress));
        piece.setY((int)(startY + (targetY - startY) * progress));
    }

    @Override
    public void render(Graphics2D g2) {

    }

    @Override
    public boolean isFinished() {
        return progress >= 1.0;
    }
}

