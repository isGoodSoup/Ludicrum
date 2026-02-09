package org.chess.service;

import org.chess.entities.Board;
import org.chess.entities.Piece;

public class AnimationService {
    private static long moveStartTime;
    private static int moveStartX, moveStartY;
    private static int moveTargetX, moveTargetY;
    private static Piece animatingPiece;

    public AnimationService() {}

    public long getMoveStartTime() {
        return moveStartTime;
    }

    public int getMoveStartX() {
        return moveStartX;
    }

    public int getMoveStartY() {
        return moveStartY;
    }

    public int getMoveTargetX() {
        return moveTargetX;
    }

    public int getMoveTargetY() {
        return moveTargetY;
    }

    public Piece getAnimatingPiece() {
        return animatingPiece;
    }

    public static void startMoveAnimation(Piece p, int targetCol,
                                         int targetRow) {
        moveStartTime = System.currentTimeMillis();
        moveStartX = p.getX();
        moveStartY = p.getY();
        moveTargetX = targetCol * Board.getSquare();
        moveTargetY = targetRow * Board.getSquare();
        animatingPiece = p;
    }

    public static void animateMove() {
        if (animatingPiece != null) {
            long elapsedTime = System.currentTimeMillis() - moveStartTime;
            double moveDuration = 500;
            double progress = Math.min(elapsedTime / moveDuration, 1.0);

            int currentX = (int) (moveStartX + (moveTargetX - moveStartX) * progress);
            int currentY = (int) (moveStartY + (moveTargetY - moveStartY) * progress);

            animatingPiece.setX(currentX);
            animatingPiece.setY(currentY);

            if (progress >= 1.0) {
                animatingPiece.setCol(moveTargetX / Board.getSquare());
                animatingPiece.setRow(moveTargetY / Board.getSquare());
                PieceService.updatePos(animatingPiece);
                animatingPiece = null;
            }
        }
    }
}
