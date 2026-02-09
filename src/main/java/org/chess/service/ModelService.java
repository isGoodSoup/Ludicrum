package org.chess.service;

import org.chess.entities.Piece;
import org.chess.enums.Tint;
import org.chess.records.Move;
import org.chess.records.MoveScore;

import java.util.ArrayList;
import java.util.List;

public class ModelService {
    private final PieceService pieceService;
    private final AnimationService animationService;

    public ModelService(PieceService pieceService,
                        AnimationService animationService) {
        this.pieceService = pieceService;
        this.animationService = animationService;
    }

    public Move getAiTurn() {
        List<MoveScore> moves = getAllLegalMoves(GameService.getCurrentTurn());
        if(moves.isEmpty()) { return null; }
        moves.sort((a,b) -> Integer.compare(b.score(), a.score()));
        Move bestMove = moves.getFirst().move();

        Piece p = bestMove.piece();
        if (p.getColor() == Tint.BLACK) {
            AnimationService.startMoveAnimation(p, bestMove.targetCol(),
                    bestMove.targetRow());
        }
        return bestMove;
    }

    private List<MoveScore> getAllLegalMoves(Tint color) {
        List<MoveScore> moves = new ArrayList<>();
        for (Piece p : PieceService.getPieces()) {
            if (p.getColor() != color) { continue; }
            for (int col = 0; col < 8; col++) {
                for (int row = 0; row < 8; row++) {
                    if (!isLegalMove(p, col, row)) { continue; }
                    Move move = new Move(p, col, row);
                    moves.add(new MoveScore(move, evaluateMove(move)));
                }
            }
        }
        return moves;
    }

    private boolean isLegalMove(Piece p, int col, int row) {
        Piece target = PieceService.getPieceAt(col, row,
                PieceService.getPieces());
        if (!p.canMove(col, row, PieceService.getPieces())) {
            return false;
        }

        if (target != null && target.getColor() == p.getColor()) {
            return false;
        }
        return !pieceService.wouldLeaveKingInCheck(p, col, row);
    }

    private int evaluateMove(Move move) {
        int score = 0;
        Piece p = move.piece();

        for(Piece enemy : PieceService.getPieces()) {
            if(enemy.getCol() == move.targetCol() && enemy.getRow() == move.targetRow()) {
                score += pieceService.getPieceValue(enemy);
                break;
            }
        }

        int oldCol = p.getCol();
        int oldRow = p.getRow();
        p.setCol(move.targetCol());
        p.setRow(move.targetRow());

        if(pieceService.isPieceThreatened(p)) {
            score -= pieceService.getPieceValue(p);
        }

        p.setCol(oldCol);
        p.setRow(oldRow);
        return score;
    }

    public void executeMove(Move move) {
        Piece p = move.piece();
        p.setPreCol(p.getCol());
        p.setPreRow(p.getRow());

        Piece captured = PieceService.getPieceAt(move.targetCol(),
                move.targetRow(),
                PieceService.getPieces());
        if (captured != null) {
            pieceService.removePiece(captured);
        }

        if (p.getColor() == Tint.BLACK) {
            AnimationService.startMoveAnimation(p, move.targetCol(),
                    move.targetRow());
        }

        PieceService.movePiece(p, move.targetCol(),
                move.targetRow());
        p.setHasMoved(true);

        PieceService.nullThisPiece();
        BooleanService.isDragging = false;
        BooleanService.isLegal = false;
        GameService.setCurrentTurn(Tint.WHITE);
    }
}
