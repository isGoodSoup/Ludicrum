package org.lud.engine.service;

import org.lud.engine.entities.Piece;
import org.lud.engine.enums.Games;
import org.lud.engine.enums.Tint;
import org.lud.engine.interfaces.Ruleset;
import org.lud.engine.records.Move;
import org.lud.engine.records.MoveScore;
import org.lud.engine.rulesets.CheckersRuleset;
import org.lud.engine.rulesets.ChessRuleset;
import org.lud.engine.rulesets.ShogiRuleset;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

public class ModelService {
    private final PieceService pieceService;
    private final AnimationService animationService;
    private final PromotionService promotionService;
    private BoardService boardService;
    private Ruleset rule;

    public ModelService(PieceService pieceService,
                        AnimationService animationService,
                        PromotionService promotionService) {
        this.pieceService = pieceService;
        this.animationService = animationService;
        this.promotionService = promotionService;
    }

    public Ruleset getRule() {
        return rule;
    }

    public void setRule(Ruleset rule) {
        this.rule = rule;
    }

    public void setBoardService(BoardService boardService) {
        this.boardService = boardService;
    }

    public Ruleset createRuleSet(Games type) {
        return switch(type) {
            case CHESS -> new ChessRuleset(pieceService, boardService);
            case CHECKERS -> new CheckersRuleset(pieceService, boardService);
            case SHOGI -> new ShogiRuleset(pieceService, boardService);
            default -> throw new IllegalStateException("No ruleset?");
        };
    }

    public Move getAiTurn() {
        if(rule == null) {
            throw new IllegalStateException("Invalid ruleset: not set or null");
        }
        List<MoveScore> moves = rule.getAllLegalMoves(boardService.getService().getGameService().getCurrentTurn());
        if(moves.isEmpty()) { return null; }
        moves.sort(Comparator.comparingInt(MoveScore::score).reversed());
        return moves.getFirst().move();
    }

    public void executeMove(Move move) {
        if(move == null) { return; }
        Piece p = move.piece();
        if(p.getColor() == Tint.DARK) {
            animationService.startMove(p, move.targetCol(), move.targetRow());
            boardService.getService().getSound().playFX(0);
        }
        BoardService.getMovesManager()
                .attemptMove(move.piece(), move.targetCol(), move.targetRow());
        BoardService.getMovesManager().commitMove();
    }

    public void triggerAIMove() {
        if(!BooleanService.canAIPlay ||
                boardService.getService().getGameService().getCurrentTurn() != Tint.DARK ||
                BooleanService.isAIMoving) return;
        new Thread(() -> {
            Move AIMove = getAiTurn();
            if(AIMove != null) {
                SwingUtilities.invokeLater(() -> {
                    executeMove(AIMove);
                    BooleanService.isAIMoving = false;
                });
            } else {
                BooleanService.isAIMoving = false;
            }
        }).start();
    }
}