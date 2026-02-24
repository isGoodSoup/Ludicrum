package org.lud.engine.manager;

import org.lud.engine.entities.*;
import org.lud.engine.enums.GameState;
import org.lud.engine.enums.Games;
import org.lud.engine.enums.Turn;
import org.lud.engine.events.*;
import org.lud.engine.records.Move;
import org.lud.engine.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MovesManager {
    private Piece selectedPiece;
    private ServiceFactory service;
    private List<Move> moves;

    private int currentPage = 1;

    private EventBus eventBus;
    private static final Logger log =
            LoggerFactory.getLogger(MovesManager.class);

    private boolean isCommiting = false;

    public MovesManager() {}

    public void init(ServiceFactory service, EventBus eventBus) {
        this.service = service;
        this.eventBus = eventBus;
        this.moves = new ArrayList<>();
    }

    public List<Move> getMoves() {
        return moves;
    }

    public ServiceFactory getService() {
        return service;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void attemptMove(Piece piece, int targetCol, int targetRow) {
        if(BooleanService.isCheckmate) { return; }
        boolean isHumanMove = isHumanTurn(service.getGameService().getCurrentTurn());

        if(isHumanMove) {
            service.getTimerService().resume();
        } else {
            service.getTimerService().pause();
        }

        for(Piece p : service.getPieceService().getPieces()) {
            if(p instanceof Pawn && p.getColor() == service.getGameService().getCurrentTurn()) {
                p.resetEnPassant();
            }
        }

        if(piece instanceof King) {
            int colDiff = targetCol - piece.getCol();
            if(Math.abs(colDiff) > 1) {
                int step = (colDiff > 0) ? 1 : -1;
                for(int c = piece.getCol(); c != targetCol + step; c += step) {
                    if(service.getPieceService()
                            .wouldLeaveKingInCheck(piece, c, piece.getRow())) {
                        PieceService.updatePos(piece);
                        return;
                    }
                }
            }
        }

        boolean isLegalLocal = piece.canMove(targetCol, targetRow,
                service.getPieceService().getPieces())
                && !service.getPieceService().wouldLeaveKingInCheck(
                piece, targetCol, targetRow);

        if(!isLegalLocal) {
            PieceService.updatePos(piece);
            return;
        }

        Piece captured = null;

        if(Math.abs(targetRow - piece.getRow()) == 2 && Math.abs(targetCol - piece.getCol()) == 2) {
            int capturedRow = (piece.getRow() + targetRow)/2;
            int capturedCol = (piece.getCol() + targetCol)/2;
            captured = PieceService.getPieceAt(capturedCol, capturedRow,
                    service.getPieceService().getPieces());
        } else {
            captured = PieceService.getPieceAt(targetCol, targetRow, service.getPieceService().getPieces());
        }

        if(captured != null) {
            service.getPieceService().removePiece(captured);
            eventBus.fire(new CaptureEvent(piece, captured));
        }

        if(piece instanceof Checker && captured != null) {
            boolean canJumpMore = true;
            while(canJumpMore) {
                canJumpMore = false;
                for(int dr = -2; dr <= 2; dr += 4) {
                    for(int dc = -2; dc <= 2; dc += 4) {
                        int newRow = piece.getRow() + dr;
                        int newCol = piece.getCol() + dc;
                        if(piece.canMove(newCol, newRow, service.getPieceService().getPieces())) {
                            int capturedRow = (piece.getRow() + newRow) / 2;
                            int capturedCol = (piece.getCol() + newCol) / 2;
                            Piece nextCaptured = PieceService.getPieceAt(capturedCol, capturedRow,
                                    service.getPieceService().getPieces());

                            if(nextCaptured != null && nextCaptured.getColor() != piece.getColor()) {
                                service.getPieceService().removePiece(nextCaptured);
                                eventBus.fire(new CaptureEvent(piece, nextCaptured));
                                PieceService.movePiece(piece, newCol, newRow);
                                moves.add(new Move(piece,
                                        piece.getRow() - dr,
                                        piece.getCol() - dc,
                                        newCol, newRow,
                                        piece.getColor(),
                                        nextCaptured,
                                        piece.isPromoted(),
                                        piece.getColor(),
                                        piece.hasMoved(),
                                        piece.getPreCol(),
                                        piece.getPreRow(),
                                        piece.isTwoStepsAhead()));
                                canJumpMore = true;
                                eventBus.fire(new JumpEvent(piece));
                            }
                        }
                    }
                }
            }
        }

        if(piece instanceof King) {
            executeCastling(piece, targetCol);
        }

        if(piece instanceof Pawn && isEnPassantMove(piece, targetCol,
                targetRow, service.getPieceService().getPieces())) {
            executeEnPassant(piece, captured, targetCol, targetRow);
        } else {
            moves.add(new Move(
                    piece,
                    piece.getRow(),
                    piece.getCol(),
                    targetCol, targetRow,
                    piece.getColor(),
                    captured,
                    piece.isPromoted(),
                    piece.getColor(),
                    piece.hasMoved(),
                    piece.getPreCol(),
                    piece.getPreRow(),
                    piece.isTwoStepsAhead()));
        }

        PieceService.movePiece(piece, targetCol, targetRow);
        piece.setHasMoved(true);

        if(service.getPromotionService().checkPromotion(piece)) {
            BooleanService.isPromotionActive = true;
            service.getPromotionService().setPromotionColor(piece.getColor());
            Piece promoted = service.getPromotionService().promote(piece);
            service.getPieceService().replacePiece(piece, promoted);
            log.info("Promoted piece");
            service.getKeyboardInput().setMoveX(promoted.getCol());
            service.getKeyboardInput().setMoveY(promoted.getRow());
            if(!(GameService.getGame() == Games.SANDBOX)) {
                selectedPiece = null;
            }
            service.getPieceService().setHoveredPieceKeyboard(promoted);
        }

        if(BooleanService.canDoAuto && !BooleanService.cannotAutoCommit) {
            commitMove(piece);
        }

        if(isAITurn()) {
            service.getModelService().triggerAIMove();
        }

        if(isHumanTurn(service.getGameService().getCurrentTurn())) {
            BooleanService.isTurnLocked = false;
        }
    }

    private boolean isHumanTurn(Turn turn) {
        return turn == Turn.LIGHT;
    }

    private boolean isAITurn() {
        return service.getGameService().getCurrentTurn() == Turn.DARK;
    }

    private boolean isCheckmate() {
        if(service.getPieceService().isKingInCheck(service.getGameService().getCurrentTurn())) {
            boolean hasEscapeMoves = false;
            for(Piece piece : service.getPieceService().getPieces()) {
                if(piece.getColor() == service.getGameService().getCurrentTurn()) {
                    for(int col = 0; col < service.getBoardService().getBoard().getCol(); col++) {
                        for(int row = 0; row < service.getBoardService().getBoard().getRow(); row++) {
                            if(piece.canMove(col, row, service.getPieceService().getPieces()) &&
                                    !service.getPieceService().wouldLeaveKingInCheck(piece, col, row)) {
                                hasEscapeMoves = true;
                                break;
                            }
                        }
                        if(hasEscapeMoves) {
                            break;
                        }
                    }
                }
                if(hasEscapeMoves) {
                    break;
                }
            }

            if(!hasEscapeMoves) {
                log.debug("Checkmate");
                BooleanService.isCheckmate = true;
                service.getTimerService().stop();
                BooleanService.canPlayMusic = false;
                service.getSound().playFX(6);
                new Timer(3000, (e)
                        -> service.getGameService().setState(GameState.CHECKMATE)).start();
                return true;
            }
        }
        return false;
    }

    private boolean isStalemate() {
        int kingCounter = 0;
        for(Piece p : service.getPieceService().getPieces()) {
            if(p instanceof King) {
                kingCounter++;
                if(kingCounter == 2 && service.getPieceService().getPieces().size() == 2) {
                    BooleanService.isStalemate = true;
                    log.debug("Stalemate");
                    service.getTimerService().stop();
                    BooleanService.canPlayMusic = false;
                    service.getGameService().setState(GameState.STALEMATE);
                }
            }
        }
        return false;
    }

    private boolean isVictory() {
        int opponentPieces = service.getAchievementService().getOpponentPieces();
        boolean hasLegalMove = false;
        for(Piece p : service.getPieceService().getPieces()) {
            if(p instanceof Checker && p.getColor() != service.getGameService().getCurrentTurn()) {
                opponentPieces++;
                for(int col = 0; col < service.getBoardService().getBoard().getCol(); col++) {
                    for(int row = 0; row < service.getBoardService().getBoard().getRow(); row++) {
                        if(p.canMove(col, row, service.getPieceService().getPieces())) {
                            hasLegalMove = true;
                            break;
                        }
                    }
                    if(hasLegalMove) { break; }
                }
                if(hasLegalMove) { break; }
            }
        }
        if(!hasLegalMove) {
            log.debug("Victory");
            BooleanService.isCheckmate = true;
            service.getTimerService().stop();
            BooleanService.canPlayMusic = false;
            service.getSound().playFX(6);
            new Timer(3000, (e)
                    -> service.getGameService().setState(GameState.VICTORY)).start();
            return true;
        }
        service.getAchievementService().setOpponentPieces(opponentPieces);
        return service.getAchievementService().getOpponentPieces() == 0 || !hasLegalMove;
    }

    private void executeCastling(Piece currentPiece, int targetCol) {
        if(!BooleanService.canDoMoves) { return; }
        if(!(currentPiece instanceof King)) { return; }
        int colDiff = targetCol - currentPiece.getCol();

        if(Math.abs(colDiff) == 2 && !currentPiece.hasMoved()) {
            int step = (colDiff > 0) ? 1 : -1;
            int rookStartCol = (colDiff > 0) ? 7 : 0;
            int rookTargetCol = (colDiff > 0) ? 5 : 3;

            boolean pathClear = true;
            for(int c = currentPiece.getCol() + step; c != rookStartCol; c += step) {
                if(PieceService.getPieceAt(c, currentPiece.getRow(),
                        service.getPieceService().getPieces()) != null) {
                    pathClear = false;
                    break;
                }
            }

            if(pathClear) {
                for(Piece p : service.getPieceService().getPieces()) {
                    if(p instanceof Rook &&
                            p.getCol() == rookStartCol &&
                            p.getRow() == currentPiece.getRow() &&
                            !p.hasMoved()) {

                        p.setCol(rookTargetCol);
                        PieceService.updatePos(p);
                        p.setHasMoved(true);
                        eventBus.fire(new CastlingEvent(currentPiece, p));
                        break;
                    }
                }
            }
        }
    }

    private void executeEnPassant(Piece currentPiece, Piece captured,
                                  int targetCol, int targetRow) {
        if(!BooleanService.canDoMoves) { return; }
        int oldRow = currentPiece.getPreRow();
        int movedSquares = Math.abs(targetRow - oldRow);

        if(captured == null && Math.abs(targetCol - currentPiece.getPreCol()) == 1) {
            int dir = (currentPiece.getColor() == Turn.LIGHT) ? -1 : 1;
            if(targetRow - oldRow == dir) {
                for(Piece p : service.getPieceService().getPieces()) {
                    if(p instanceof Pawn &&
                            p != null &&
                            p.getColor() != currentPiece.getColor() &&
                            p.getCol() == targetCol &&
                            p.getRow() == oldRow &&
                            p.isTwoStepsAhead()) {
                        captured = p;
                        service.getPieceService().removePiece(p);
                        Move newMove = getMoveEvent();
                        moves.add(newMove);
                        break;
                    }
                }
            }
        }

        currentPiece.setTwoStepsAhead(movedSquares == 2);
    }

    private Move getMoveEvent() {
        Move lastMove = moves.getLast();
        Move newMove = new Move(
                lastMove.piece(), lastMove.fromRow(), lastMove.fromCol(),
                lastMove.targetCol(), lastMove.targetRow(), lastMove.color(),
                lastMove.captured(), lastMove.wasPromoted(), lastMove.currentTurn(),
                lastMove.hasMoved(), lastMove.preCol(), lastMove.preRow(),
                lastMove.isTwoStepsAhead()
        );
        return newMove;
    }

    private boolean isEnPassantMove(Piece pawn, int targetCol, int targetRow,
                              List<Piece> board) {
        if(!(pawn instanceof Pawn)) return false;
        if(Math.abs(targetCol - pawn.getCol()) == 1 &&
                targetRow != pawn.getRow()) {
            for(Piece p : board) {
                if(p instanceof Pawn &&
                        p.getColor() != pawn.getColor() &&
                        p.getCol() == targetCol &&
                        p.getRow() == pawn.getRow() &&
                        p.isTwoStepsAhead()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean noLoses() {
        int piecesCounter = service.getAchievementService().getPiecesCounter();
        for(Piece p : service.getPieceService().getPieces()) {
            if(p instanceof Checker && p.getColor() == service.getGameService().getCurrentTurn()) {
                piecesCounter++;
            }
        }
        service.getAchievementService().setPiecesCounter(piecesCounter);
        return service.getAchievementService().getPiecesCounter() == 12;
    }

    public void commitMove(Piece piece) {
        if(isCommiting) { return; }
        isCommiting = true;
        BooleanService.cannotAutoCommit = true;
        service.getSound().playFX(0);

        if(GameService.getGame() == Games.CHESS) {
            if(isCheckmate()) {
                eventBus.fire(new CheckmateEvent(piece,
                        service.getPieceService().getKing(piece.getColor())));
                eventBus.fire(new TotalMovesEvent(piece));

                if(BooleanService.canDoHard) {
                    eventBus.fire(new HardEvent(piece));
                }
            } else if(isStalemate()) {
                eventBus.fire(new StalemateEvent(piece));
            }
        }

        if(GameService.getGame() == Games.CHECKERS) {
            if(isVictory()) {
                eventBus.fire(new VictoryEvent(piece));
            }
            if(isVictory() && noLoses()) {
                eventBus.fire(new StrategistEvent(piece, piece.getColor()));
            }
            if(isStalemate()) {
                eventBus.fire(new StalemateEvent(piece));
            }
        }

        Turn.nextTurn(service.getGameService());
        BooleanService.isTurnLocked = true;

        if(service.getGameService().getCurrentTurn() == Turn.DARK) {
            service.getModelService().triggerAIMove();
        }
        isCommiting = false;
        BooleanService.cannotAutoCommit = false;
    }

    public void cancelMove() {
        if(selectedPiece != null) {
            selectedPiece.setCol(selectedPiece.getPreCol());
            selectedPiece.setRow(selectedPiece.getPreRow());
            PieceService.updatePos(selectedPiece);

            selectedPiece = null;
            service.getPieceService().setHoveredPieceKeyboard(null);
            BooleanService.isLegal = true;
        }
    }

    public void undoLastMove() {
        if(!BooleanService.canUndoMoves || moves.isEmpty()) { return; }
        Turn turn = moves.getLast().currentTurn();
        if(!moves.isEmpty() && moves.getLast().currentTurn() == turn) {
            Piece[][] boardState = BoardService.getBoardState();
            Move lastMove = moves.getLast();
            Piece p = lastMove.piece();
            Piece captured = lastMove.captured();

            p.setCol(p.getPreCol());
            p.setRow(p.getPreRow());
            PieceService.updatePos(p, false);
            boardState[lastMove.fromRow()][lastMove.fromCol()] = p;

            if(captured != null) {
                captured.setCol(captured.getPreCol());
                captured.setRow(captured.getPreRow());
                service.getPieceService().addPiece(captured);
                PieceService.updatePos(p, false);
            }

            log.info("Move undone: {} <- from {} [{}{}]",
                    service.getBoardService().getSquareNameAt(lastMove.targetCol(), lastMove.targetRow()),
                    service.getBoardService().getSquareNameAt(lastMove.fromCol(), lastMove.fromRow()),
                    p.getTypeID(), captured != null ? " capturing " + captured.getTypeID() : "");
            moves.removeLast();
        }
    }
}
