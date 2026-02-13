package org.chess.service;

import org.chess.animations.MoveAnimation;
import org.chess.entities.Board;
import org.chess.entities.Piece;
import org.chess.interfaces.Animation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimationService {
    private final List<Animation> animations = new ArrayList<>();

    public void startMove(Piece piece, int targetCol, int targetRow) {
        animations.add(new MoveAnimation(piece, piece.getX(), piece.getY(),
                targetCol * Board.getSquare(), targetRow * Board.getSquare()));
    }

    public void render(Graphics2D g2) {
        for (Animation anim : animations) {
            anim.render(g2);
        }
    }

    public void update(int delta) {
        Iterator<Animation> it = animations.iterator();
        while(it.hasNext()) {
            Animation anim = it.next();
            anim.update(delta);
            if(anim.isFinished()) {
                it.remove();
            }
        }
    }

    public boolean isAnimating(Piece piece) {
        return animations.stream().anyMatch(a -> a.affects(piece));
    }
}