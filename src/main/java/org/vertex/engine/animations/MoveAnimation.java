package org.vertex.engine.animations;

import org.vertex.engine.entities.Piece;
import org.vertex.engine.interfaces.Animation;

public class MoveAnimation implements Animation {
    private final Piece piece;
    private final int startX, startY;
    private final int targetX, targetY;
    private final double speed;
    private double progress = 0.0;
    private final double distance;

    public MoveAnimation(Piece piece, int startX, int startY, int targetX, int targetY, double speed) {
        this.piece = piece;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;

        double dx = targetX - startX;
        double dy = targetY - startY;
        distance = Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public void update(double delta) {
        if (isFinished()) {
            return;
        }

        double moveAmount = speed * delta;
        progress += moveAmount/distance;
        if(progress > 1.0) {
            progress = 1.0;
        }

        int newX = (int)(startX + (targetX - startX) * progress);
        int newY = (int)(startY + (targetY - startY) * progress);
        piece.setX(newX);
        piece.setY(newY);
    }

    @Override
    public void render(java.awt.Graphics2D g2) {
    }

    @Override
    public boolean isFinished() {
        return progress >= 1.0;
    }

    @Override
    public boolean affects(Object obj) {
        return this.piece == obj;
    }
}
