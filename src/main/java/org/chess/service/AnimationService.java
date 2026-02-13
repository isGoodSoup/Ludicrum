package org.chess.service;

import org.chess.animations.MoveAnimation;
import org.chess.animations.ToastAnimation;
import org.chess.entities.Board;
import org.chess.entities.Piece;
import org.chess.interfaces.Animation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimationService {
    private final List<Animation> animations = new ArrayList<>();
    private static final int MAX_TOASTS = 4;

    public void startMove(Piece piece, int targetCol, int targetRow) {
        animations.add(new MoveAnimation(piece, piece.getX(), piece.getY(),
                targetCol * Board.getSquare(), targetRow * Board.getSquare(),
                1200));
    }

    public void add(Animation animation) {
        if (animation instanceof ToastAnimation) {
            long currentToasts = animations.stream().filter(a -> a instanceof ToastAnimation).count();
            if (currentToasts >= MAX_TOASTS) return;
        }
        animations.add(animation);
    }

    public void update(double delta) {
        Iterator<Animation> it = animations.iterator();
        while(it.hasNext()) {
            Animation anim = it.next();
            anim.update(delta);
            if(anim.isFinished()) it.remove();
        }
    }

    public void render(Graphics2D g2) {
        for (Animation anim : animations) anim.render(g2);
    }

    public boolean isAnimating(Piece piece) {
        return animations.stream().anyMatch(a -> a.affects(piece));
    }

    public List<Animation> getAnimations() {
        return animations;
    }
}