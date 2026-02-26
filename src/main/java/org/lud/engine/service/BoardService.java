package org.lud.engine.service;

import org.lud.engine.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lud.engine.enums.GameState;
import org.lud.engine.enums.Games;
import org.lud.engine.enums.Time;
import org.lud.engine.enums.Turn;
import org.lud.engine.manager.MovesManager;
import org.lud.engine.records.Save;

import java.util.*;

@SuppressWarnings("ALL")
public class BoardService {
    private static Piece[][] boardState;
    private Board board;
    private String[][] squares;
    private final Map<List<Integer>, List<Integer>> columns;

    private final PieceService pieceService;
    private GameService gameService;
    private static MovesManager movesManager;

    private ServiceFactory service;
    private static final Logger log = LoggerFactory.getLogger(BoardService.class);

    public BoardService(PieceService pieceService, MovesManager movesManager) {
        this.board = new Board();
        this.pieceService = pieceService;
        BoardService.movesManager = movesManager;
        this.columns = new HashMap<>();
    }

    public ServiceFactory getService() {
        return service;
    }

    public void setService(ServiceFactory service) {
        this.service = service;
    }

    public GameService getGameService() {
        return gameService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public static Piece[][] getBoardState() {
        return boardState;
    }

    public static MovesManager getMovesManager() {
        return movesManager;
    }

    public void prepBoard() {
        Games game = GameService.getGame();
        if (game == null) {
            throw new IllegalStateException("Game must be selected before starting the board");
        }
        board.setSize(game);
        boardState = new Piece[board.getRow()][board.getCol()];
        precomputeSquares();
    }

    private void precomputeSquares() {
        int rows = board.getRow();
        int cols = board.getCol();
        squares = new String[rows][cols];

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                char file = (char) ('a' + c);
                char rank = (char) ('8' - r);
                squares[r][c] = "" + file + rank;
            }
        }
    }

    public void restoreSprites(Save save) {
        List<Piece> loadedPieces = save.pieces();
        Piece[][] boardArray = new Piece[board.getRow()][board.getCol()];
        for(Piece p : loadedPieces) {
            if(p == null) continue;
            int col = p.getCol();
            int row = p.getRow();

            if(row >= board.getRow() || col >= board.getCol()) {
                log.warn("Skipping piece at invalid position: {} {}", row, col);
                continue;
            }

            p.setPreCol(p.getPreCol());
            p.setPreRow(p.getPreRow());
            p.setX(col * Board.getSquare());
            p.setY(row * Board.getSquare());
            pieceService.getSprite(p);
            boardArray[row][col] = p;
        }
        board.setPieces(boardArray);
        pieceService.getPieces().clear();
        pieceService.getPieces().addAll(loadedPieces);
    }

    private void clearBoardState() {
        boardState = new Piece[board.getRow()][board.getCol()];
        pieceService.getPieces().clear();
    }

    public String getSquareNameAt(int col, int row) {
        int rankIndex = board.getRow() - 1 - row;
        char fileChar = (char) ('a' + col);
        return "" + fileChar + (rankIndex + 1);
    }

    public void startBoard() {
        if(GameService.getGame() == Games.SANDBOX) { setPiecesSandbox(); }
        if(GameService.getGame() == Games.CHAOS) { setPiecesChaos(); }
        else { setPieces(); }

        if(!(GameService.getGame() == Games.SANDBOX)) {
            if(BooleanService.canStopwatch) {
                TimerService.setTime(Time.STOPWATCH);
                BooleanService.canTime = false;
                getService().getTimerService().reset();
                getService().getTimerService().start();
            }

            if(BooleanService.canTime) {
                TimerService.setTime(Time.TIMER);
                BooleanService.canStopwatch = false;
                getService().getTimerService().reset();
                getService().getTimerService().start();
            }
        }
    }

    public void resetBoard() {
        if(BooleanService.canResetTable) {
            service.getBoardService().clearBoardState();
            service.getGameService().startNewGame(GameService.getGame());
            service.getSound().playFX(0);
        }
    }

    public void setPieces() {
        Games game = GameService.getGame();

        if(game != GameService.getGame()) {
            log.debug("Current game: {}", GameService.getGame());
        }

        if(game == null) {
            throw new IllegalStateException("Game type not set before board initialization.");
        }

        switch(game) {
            case CHESS -> {
                List<Piece> pieces = pieceService.getPieces();
                pieces.clear();
                clearBoardState();

                for(int col = 0; col < 8; col++) {
                    Pawn whitePawn = new Pawn(Turn.LIGHT, col, 6);
                    Pawn blackPawn = new Pawn(Turn.DARK, col, 1);
                    pieces.add(whitePawn);
                    pieces.add(blackPawn);
                }

                Rook wR1 = new Rook(Turn.LIGHT, 0, 7);
                Rook wR2 = new Rook(Turn.LIGHT, 7, 7);
                Rook bR1 = new Rook(Turn.DARK, 0, 0);
                Rook bR2 = new Rook(Turn.DARK, 7, 0);
                pieces.addAll(List.of(wR1, wR2, bR1, bR2));

                Knight wN1 = new Knight(Turn.LIGHT, 1, 7);
                Knight wN2 = new Knight(Turn.LIGHT, 6, 7);
                Knight bN1 = new Knight(Turn.DARK, 1, 0);
                Knight bN2 = new Knight(Turn.DARK, 6, 0);
                pieces.addAll(List.of(wN1, wN2, bN1, bN2));

                Bishop wB1 = new Bishop(Turn.LIGHT, 2, 7);
                Bishop wB2 = new Bishop(Turn.LIGHT, 5, 7);
                Bishop bB1 = new Bishop(Turn.DARK, 2, 0);
                Bishop bB2 = new Bishop(Turn.DARK, 5, 0);
                pieces.addAll(List.of(wB1, wB2, bB1, bB2));

                Queen wQ = new Queen(Turn.LIGHT, 3, 7);
                Queen bQ = new Queen(Turn.DARK, 3, 0);
                pieces.add(wQ);
                pieces.add(bQ);

                King wK = new King(Turn.LIGHT, 4, 7);
                King bK = new King(Turn.DARK, 4, 0);
                pieces.add(wK);
                pieces.add(bK);

                for(Piece p : pieces) {
                    boardState[p.getRow()][p.getCol()] = p;
                    int squareSize = Board.getSquare();
                    p.setX(p.getCol() * squareSize);
                    p.setY(p.getRow() * squareSize);
                }

                board.setPieces(boardState);
                service.getGameService().setCurrentTurn(Turn.LIGHT);
                PieceService.nullThisPiece();
            }
            case CHECKERS -> {
                List<Piece> pieces = pieceService.getPieces();
                pieces.clear();
                clearBoardState();

                for (int row = 0; row <= 2; row++) {
                    for (int col = 0; col < 8; col++) {
                        if ((row + col) % 2 != 0) {
                            Checker black = new Checker(Turn.DARK, col, row);
                            pieces.add(black);
                            boardState[row][col] = black;
                        }
                    }
                }

                for (int row = 5; row <= 7; row++) {
                    for (int col = 0; col < 8; col++) {
                        if ((row + col) % 2 != 0) {
                            Checker white = new Checker(Turn.LIGHT, col, row);
                            pieces.add(white);
                            boardState[row][col] = white;
                        }
                    }
                }

                int squareSize = Board.getSquare();
                for (Piece p : pieces) {
                    p.setX(p.getCol() * squareSize);
                    p.setY(p.getRow() * squareSize);
                }

                board.setPieces(boardState);
                service.getGameService().setCurrentTurn(Turn.LIGHT);
                PieceService.nullThisPiece();
            }
            case SHOGI -> {
                List<Piece> pieces = pieceService.getPieces();
                pieces.clear();
                clearBoardState();

                for(int col = 0; col < 9; col++) {
                    Pawn lightPawn = new Pawn(Turn.LIGHT, col, 6);
                    Pawn darkPawn  = new Pawn(Turn.DARK, col, 2);
                    pieces.add(lightPawn);
                    pieces.add(darkPawn);
                }

                pieces.addAll(List.of(
                        new Lance(Turn.LIGHT, 0, 8),
                        new Knight(Turn.LIGHT, 1, 8),
                        new Silver(Turn.LIGHT, 2, 8),
                        new Gold(Turn.LIGHT, 3, 8),
                        new King(Turn.LIGHT, 4, 8),
                        new Gold(Turn.LIGHT, 5, 8),
                        new Silver(Turn.LIGHT, 6, 8),
                        new Knight(Turn.LIGHT, 7, 8),
                        new Lance(Turn.LIGHT, 8, 8)
                ));

                pieces.add(new Bishop(Turn.LIGHT, 1, 7));
                pieces.add(new Rook(Turn.LIGHT, 7, 7));

                pieces.addAll(List.of(
                        new Lance(Turn.DARK, 0, 0),
                        new Knight(Turn.DARK, 1, 0),
                        new Silver(Turn.DARK, 2, 0),
                        new Gold(Turn.DARK, 3, 0),
                        new King(Turn.DARK, 4, 0),
                        new Gold(Turn.DARK, 5, 0),
                        new Silver(Turn.DARK, 6, 0),
                        new Knight(Turn.DARK, 7, 0),
                        new Lance(Turn.DARK, 8, 0)
                ));

                pieces.add(new Bishop(Turn.DARK, 7, 1));
                pieces.add(new Rook(Turn.DARK, 1, 1));

                for(Piece p : pieces) {
                    boardState[p.getRow()][p.getCol()] = p;
                    int squareSize = Board.getSquare();
                    p.setX(p.getCol() * squareSize);
                    p.setY(p.getRow() * squareSize);
                }

                board.setPieces(boardState);
                service.getGameService().setCurrentTurn(Turn.LIGHT);
                PieceService.nullThisPiece();
            }
            case CHAOS -> {}
        }
    }

    public void setPiecesChaos() {
        if(!(GameService.getGame() == Games.CHAOS)) {
            return;
        }

        List<Piece> pieces = pieceService.getPieces();
        columns.clear();
        pieces.clear();
        clearBoardState();

        List<Integer> f = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7));
        List<Integer> b = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7));
        Collections.shuffle(f);
        Collections.shuffle(b);
        columns.put(f, b);

        for(Map.Entry<List<Integer>, List<Integer>> entry :
                columns.entrySet()) {
            List<Integer> front = entry.getKey();
            List<Integer> back = entry.getValue();

            for(int col : front) {
                pieces.add(pieceService.getRandomPiece(Turn.LIGHT, col, 6));
                pieces.add(pieceService.getRandomPiece(Turn.DARK, col, 1));
            }

            for(int col : back) {
                if(col == 4) { continue; }
                pieces.add(pieceService.getRandomPiece(Turn.LIGHT, col, 7));
                pieces.add(pieceService.getRandomPiece(Turn.DARK, col, 0));
            }
        }
        pieces.add(new King(Turn.LIGHT, 4, 7));
        pieces.add(new King(Turn.DARK, 4, 0));
    }

    public void setPiecesSandbox() {
        clearBoardState();
        pieceService.getPieces().clear();
        boardState = new Piece[board.getRow()][board.getCol()];
    }

    public void toggleSandboxMode() {
        GameService gameService = this.service.getGameService();

        if(GameService.getGame() != Games.SANDBOX) {
            gameService.setGame(Games.SANDBOX);
            gameService.setState(GameState.BOARD);
            BooleanService.canType = false;
            setPiecesSandbox();
        } else {
            finalizeSandbox();
            gameService.setGame(Games.CHESS);
            gameService.setState(GameState.BOARD);
            service.getMovesManager().setSelectedPiece(null);
            service.getPieceService().setHoveredPieceKeyboard(null);
            service.getGameService().setCurrentTurn(Turn.LIGHT);
        }
    }

    public void finalizeSandbox() {
        Piece[][] state = new Piece[board.getRow()][board.getCol()];
        for(Piece p : pieceService.getPieces()) {
            if(p != null) {
                state[p.getRow()][p.getCol()] = p;
                p.setPickedUp(false);
            }
        }
        boardState = state;
    }
}
