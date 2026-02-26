package org.lud.engine.service;

import org.lud.engine.entities.*;
import org.lud.engine.enums.Games;
import org.lud.engine.enums.Turn;
import org.lud.engine.events.PromotionEvent;
import org.lud.engine.manager.EventBus;
import org.lud.engine.manager.MovesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class PromotionService {
    private Turn promotionColor;
    private Piece promotingPawn;

    private final PieceService pieceService;
    private GameService gameService;
    private MovesManager movesManager;
    private final EventBus event;

    private boolean isPromoted;
    private static final Logger log = LoggerFactory.getLogger(PromotionService.class);

    public PromotionService(PieceService pieceService, EventBus event) {
        this.pieceService = pieceService;
        this.event = event;
    }

    public Turn getPromotionColor() {
        return promotionColor;
    }

    public void setPromotionColor(Turn promotionColor) {
        this.promotionColor = promotionColor;
    }

    public Piece getPromotingPawn() {
        return promotingPawn;
    }

    public void setPromotingPawn(Piece promotingPawn) {
        this.promotingPawn = promotingPawn;
    }

    public GameService getGameService() {
        return gameService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public MovesManager getMovesManager() {
        return movesManager;
    }

    public void setMovesManager(MovesManager movesManager) {
        this.movesManager = movesManager;
    }

    public boolean checkPromotion(Piece p) {
        if(GameService.getGame() == Games.SHOGI) {
            if(p instanceof Pawn || p instanceof Lance || p instanceof Silver || p instanceof Knight ||
                    p instanceof Bishop || p instanceof Rook) {
                return PieceService.isInPromotionZone(p.getColor(), p.getRow());
            }
        }

        if(p instanceof Pawn) {
            if((p.getColor() == Turn.LIGHT && p.getRow() == 0) ||
                    (p.getColor() == Turn.DARK && p.getRow() == 7)) {
                return true;
            }
        }

        if(p instanceof Checker) {
            if((p.getColor() == Turn.LIGHT && p.getRow() == 0) ||
                    (p.getColor() == Turn.DARK && p.getRow() == 7)) {
                return true;
            }
        }

        return false;
    }

    public Piece promote(Piece piece) {
        if(piece == null || !BooleanService.canPromote) { return piece; }
        if(!checkPromotion(piece)) { return piece; }
        if(piece.isPromotionMandatory()) {
            return autoPromote(piece);
        }
        log.info("Promotion: {}, {}", piece.getRow(), piece.getCol());

        Piece promotedPiece = getPiece(piece);
        pieceService.replacePiece(piece, promotedPiece);
        promotedPiece.setX(promotedPiece.getRow() * Board.getSquare());
        promotedPiece.setY(promotedPiece.getCol() * Board.getSquare());

        BoardService.getBoardState()
                [promotedPiece.getRow()][promotedPiece.getCol()] =
                promotedPiece;

        PieceService.nullThisPiece();
        pieceService.setHoveredPieceKeyboard(null);

        BooleanService.isPromotionActive = false;
        if(piece instanceof Pawn pawn) {
            event.fire(new PromotionEvent(pawn));
        }

        if(piece instanceof Checker checker) {
            event.fire(new PromotionEvent(checker));
        }
        return promotedPiece;
    }

    private Piece autoPromote(Piece piece) {
        if(GameService.getGame() == Games.SHOGI) {
            if(piece instanceof Pawn || piece instanceof Lance || piece instanceof Silver || piece instanceof Knight ||
                    piece instanceof Bishop || piece instanceof Rook) {
                event.fire(new PromotionEvent(piece));
            }
        }

        if(piece instanceof Pawn && GameService.getGame() == Games.SHOGI) {
            Tokin tokin = new Tokin(piece.getColor(), piece.getRow(), piece.getCol());
            piece.setPromoted(true);
            return tokin;
        } else if(piece instanceof Lance || piece instanceof Knight) {
            piece.setPromoted(true);
            return piece;
        } else if(piece instanceof Bishop || piece instanceof Rook) {
            pieceService.getSprite(piece);
            piece.setPromoted(true);
            return piece;
        }
        if(BooleanService.canDoAuto) { movesManager.commitMove(piece); }
        return piece;
    }

    public List<Piece> getPromotions(Piece p) {
        List<Piece> options = new ArrayList<>();
        int row = p.getRow();
        int col = p.getCol();
        Turn color = p.getColor();

        if(p instanceof Pawn) {
            if (GameService.getGame() == Games.SHOGI) {
                options.add(new Tokin(color, row, col));
            } else {
                options.add(new Queen(color, row, col));
                options.add(new Rook(color, row, col));
                options.add(new Bishop(color, row, col));
                options.add(new Knight(color, row, col));
            }
        }

        if(GameService.getGame() == Games.SHOGI) {
            if (p instanceof Lance || p instanceof Silver || p instanceof Knight) {
                options.add(new Gold(color, row, col));
            }
            if (p instanceof Bishop || p instanceof Rook) {
                Piece promoted = p.copy();
                promoted.setPromoted(true);
                options.add(promoted);
            }
        }
        return options;
    }

    private Piece getPiece(Piece p) {
        Piece promotedPiece = null;
        if(p instanceof Pawn) {
            if(GameService.getGame() == Games.SHOGI) {
                promotedPiece = new Tokin(p.getColor(), p.getRow(),
                        p.getCol());
            } else {
                p.setPromotionID(p.getPromotionID());
                return switch(p.getPromotionID()) {
                    case KNIGHT -> promotedPiece = new Knight(p.getColor(), p.getRow(), p.getCol());
                    case BISHOP -> promotedPiece = new Bishop(p.getColor(), p.getRow(), p.getCol());
                    case ROOK -> promotedPiece = new Rook(p.getColor(), p.getRow(), p.getCol());
                    case QUEEN -> promotedPiece = new Queen(p.getColor(), p.getRow(), p.getCol());
                    default -> throw new IllegalStateException("Unexpected value: " + p.getPromotionID());
                };
            }
        }
        if(p instanceof Checker) {
            promotedPiece = new King(p.getColor(), p.getRow(), p.getCol());
        }
        if(GameService.getGame() == Games.SHOGI) {
            if(p instanceof Lance || p instanceof Silver || p instanceof Knight ) {
                promotedPiece = new Gold(p.getColor(), p.getRow(), p.getCol());
            }
            if(p instanceof Bishop || p instanceof Rook) {
                promotedPiece = p;
                p.setPromoted(true);
            }
        }
        return promotedPiece;
    }
}
