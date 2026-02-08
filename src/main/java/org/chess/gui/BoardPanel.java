package org.chess.gui;

import org.chess.Move;
import org.chess.entities.*;
import org.chess.enums.GameState;
import org.chess.enums.PlayState;
import org.chess.enums.Tint;
import org.chess.enums.Type;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BoardPanel extends JPanel implements Runnable {
	@Serial
    private static final long serialVersionUID = -5189356863277669172L;
	private static int WIDTH;
	private static int HEIGHT;
	private final int FPS = 60;
	private Thread thread;

	private final Board board;
	private final Mouse mouse;
    private static final Random random = new Random();
	private Tint currentTurn = Tint.WHITE;
    private int aiTurn;

	private GameState state;
	private PlayState mode;

	private final List<Piece> pieces = new ArrayList<>();
	private final List<Piece> promoted = new ArrayList<>();
    private final List<Piece> snapshot = new ArrayList<>(pieces);
    private final List<Move> legalMoves = new ArrayList<>();
    private final List<Integer> moveScores = new ArrayList<>();

	private Piece currentPiece;
    private Piece castlingPiece;
	private Piece promotingPawn;
	private Piece checkingPiece;
    private int dragOffsetX;
	private int dragOffsetY;
    private Tint promotionColor;

	private final BufferedImage yes;
	private final BufferedImage no;
	private final BufferedImage whitePawn, blackPawn;
	private final BufferedImage whiteRook, blackRook;
	private final BufferedImage whiteKnight, blackKnight;
	private final BufferedImage whiteBishop, blackBishop;
	private final BufferedImage whiteQueen, blackQueen;
	private final BufferedImage whiteKing, blackKing;

	private boolean canMove;
	private boolean validSquare;
	private boolean isPromoted;
	private boolean isDragging;
	private boolean isLegalPreview;
    private boolean isAIPlaying;
	private boolean isGameOver;

	private static final String[] optionsMenu = { "New Game", "Exit" };
	private static final String[] optionsMode = { "Player", "AI" };
	private static final int MENU_SPACING = 50;
	private static final int MENU_START_Y = 80;
    private static final int MENU_FONT = 32;

	public BoardPanel() {
		super();
		this.board = new Board();
		this.mouse = new Mouse();
		this.state = GameState.MENU;
        this.mode = null;
		WIDTH = Board.getSquare() * 8;
		HEIGHT = Board.getSquare() * 8;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		setPieces();

		try {
			yes = getImage("/ticks/tick_yes");
			no = getImage("/ticks/tick_no");
			whitePawn = getImage("/pieces/pawn-h");
			blackPawn = getImage("/pieces/pawn-bh");
			whiteRook = getImage("/pieces/rook-h");
			blackRook = getImage("/pieces/rook-bh");
			whiteKnight = getImage("/pieces/knight-h");
			blackKnight = getImage("/pieces/knight-bh");
			whiteBishop = getImage("/pieces/bishop-h");
			blackBishop = getImage("/pieces/bishop-bh");
			whiteQueen = getImage("/pieces/queen-h");
			blackQueen = getImage("/pieces/queen-bh");
			whiteKing = getImage("/pieces/king-h");
			blackKing = getImage("/pieces/king-bh");
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	public Thread getThread() {
		return thread;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public int getFPS() {
		return FPS;
	}

	public Tint getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(Tint currentTurn) {
		this.currentTurn = currentTurn;
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public Board getBoard() {
		return board;
	}

	public boolean isCanMove() {
		return canMove;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public boolean isValidSquare() {
		return validSquare;
	}

	public void setValidSquare(boolean validSquare) {
		this.validSquare = validSquare;
	}

	public Piece getCastlingPiece() {
		return castlingPiece;
	}

	public void setCastlingPiece(Piece castlingPiece) {
		this.castlingPiece = castlingPiece;
	}

	public List<Piece> getPromoted() {
		return promoted;
	}

	public boolean isPromoted() {
		return isPromoted;
	}

	public void setPromoted(boolean isPromoted) {
		this.isPromoted = isPromoted;
	}

	public Piece getPromotingPawn() {
		return promotingPawn;
	}

	public void setPromotingPawn(Piece promotingPawn) {
		this.promotingPawn = promotingPawn;
	}

	public Tint getPromotionColor() {
		return promotionColor;
	}

	public void setPromotionColor(Tint promotionColor) {
		this.promotionColor = promotionColor;
	}

	public Piece getCheckingPiece() {
		return checkingPiece;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

    public static Font getFont(int size) {
        return new Font("Roboto", Font.BOLD, size);
    }

	public GameState getState() {
		return state;
	}

    private void startBoard() {
        pieces.clear();
        setPieces();
        currentTurn = Tint.WHITE;
        isPromoted = false;
        isGameOver = false;
        currentPiece = null;
        state = GameState.BOARD;
    }

    private Rectangle getHitbox(int y) {
        Rectangle hitbox = new Rectangle(
                WIDTH/2 - 100,
                y - 30,
                200,
                40
        );
        return hitbox;
    }

	public void launch() {
		thread = new Thread(this);
		thread.start();
	}

	public void setPieces() {
		for(int col = 0; col < 8; col++) {
			pieces.add(new Pawn(Tint.WHITE, col, 6));
			pieces.add(new Pawn(Tint.BLACK, col, 1));
		}
		pieces.add(new Rook(Tint.WHITE, 0, 7));
		pieces.add(new Rook(Tint.WHITE, 7, 7));
		pieces.add(new Rook(Tint.BLACK, 0, 0));
		pieces.add(new Rook(Tint.BLACK, 7, 0));
		pieces.add(new Knight(Tint.WHITE, 1, 7));
		pieces.add(new Knight(Tint.WHITE, 6, 7));
		pieces.add(new Knight(Tint.BLACK, 1, 0));
		pieces.add(new Knight(Tint.BLACK, 6, 0));
		pieces.add(new Bishop(Tint.WHITE, 2, 7));
		pieces.add(new Bishop(Tint.WHITE, 5, 7));
		pieces.add(new Bishop(Tint.BLACK, 2, 0));
		pieces.add(new Bishop(Tint.BLACK, 5, 0));
		pieces.add(new Queen(Tint.WHITE, 3, 7));
		pieces.add(new Queen(Tint.BLACK, 3, 0));
		pieces.add(new King(Tint.WHITE, 4, 7));
		pieces.add(new King(Tint.BLACK, 4, 0));
	}

	private Piece getHoveredPiece() {
		int hoverCol = mouse.getX() / Board.getSquare();
		int hoverRow = mouse.getY() / Board.getSquare();

		for(Piece p : pieces) {
			if(p.getCol() == hoverCol && p.getRow() ==
					hoverRow && p != currentPiece) {
                if(isAIPlaying && p.getColor() != Tint.WHITE) {
                    return null;
                }
				return p;
			}
		}
		return null;
	}

	private BufferedImage getHoverSprite(Piece p) {
		if(p instanceof Pawn) return (p.getColor() == Tint.WHITE) ?
				whitePawn : blackPawn;
		if(p instanceof Rook) return (p.getColor() == Tint.WHITE) ?
				whiteRook : blackRook;
		if(p instanceof Knight) return (p.getColor() == Tint.WHITE) ?
				whiteKnight : blackKnight;
		if(p instanceof Bishop) return (p.getColor() == Tint.WHITE) ?
				whiteBishop : blackBishop;
		if(p instanceof Queen) return (p.getColor() == Tint.WHITE) ?
				whiteQueen : blackQueen;
		if(p instanceof King) return (p.getColor() == Tint.WHITE) ?
				whiteKing : blackKing;
		return null;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		switch(state) {
			case MENU -> drawMenu(g2);
			case MODE -> drawModes(g2);
			case BOARD -> drawBoard(g2);
		}
	}

    private void drawPromotions(Graphics2D g2) {
        if(!isPromoted) {
            return;
        }

        int size = Board.getSquare();
        int totalWidth = size * 4;
        int startX = (WIDTH - totalWidth) / 2;
        int startY = (HEIGHT - size) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        Type[] options = { Type.QUEEN, Type.ROOK, Type.BISHOP, Type.KNIGHT };

        int hoverIndex = -1;
        for(int i = 0; i < options.length; i++) {
            int x0 = startX + i * size;
            int x1 = x0 + size;
            int y1 = startY + size;

            if(mouse.getX() >= x0 && mouse.getX() <= x1 &&
                    mouse.getY() >= startY && mouse.getY() <= y1) {
                hoverIndex = i;
                break;
            }
        }

        for(int i = 0; i < options.length; i++) {
            Piece temp;
            switch (options[i]) {
                case QUEEN -> temp = new Queen(promotionColor, 0, 0);
                case ROOK -> temp = new Rook(promotionColor, 0, 0);
                case BISHOP -> temp = new Bishop(promotionColor, 0, 0);
                case KNIGHT -> temp = new Knight(promotionColor, 0, 0);
                default -> { continue; }
            }

            int x = startX + i * size;

            temp.setX(x);
            temp.setY(startY);

            if(i == hoverIndex) {
                temp.setScale(temp.getScale() + 0.5f);
            } else {
                temp.setScale(temp.getDEFAULT_SCALE());
            }

            temp.draw(g2);
        }
    }

    public BufferedImage getImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(
                getClass().getResourceAsStream(path + ".png")));
    }

    private void drawTick(Graphics2D g2, boolean isLegal) {
        double scale = currentPiece.getScale();
        int size = (int) (Board.getSquare() * scale);
        int x = currentPiece.getX() - size / 2;
        int y = currentPiece.getY() - size / 2;
        BufferedImage image = isLegal ? yes : no;
        g2.drawImage(image, x, y, size, size, null);
    }

    private void drawMenu(Graphics2D g2) {
        g2.setColor(new Color(0,0,0,255));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setFont(getFont(MENU_FONT));
        g2.setColor(Color.WHITE);

        int startY = HEIGHT/2 + 80;
        int spacing = 50;

        for(int i = 0; i < optionsMenu.length; i++) {
            int textWidth = g2.getFontMetrics().stringWidth(optionsMenu[i]);
            int x = (WIDTH - textWidth)/2;
            int y = startY + i * spacing;
            boolean isHovered = getHitbox(y).contains(mouse.getX(),
                    mouse.getY());
            g2.setColor(isHovered ? Color.YELLOW : Color.WHITE);
            g2.drawString(optionsMenu[i], x, y);
        }
    }

    private void drawModes(Graphics2D g2) {
        g2.setColor(new Color(0,0,0,255));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setFont(getFont(MENU_FONT));
        g2.setColor(Color.WHITE);

        int startY = HEIGHT/2 + MENU_START_Y;
        int spacing = MENU_SPACING;

        for(int i = 0; i < optionsMode.length; i++) {
            int textWidth = g2.getFontMetrics().stringWidth(optionsMode[i]);
            int x = (WIDTH - textWidth)/2;
            int y = startY + i * spacing;
            boolean isHovered = getHitbox(y).contains(mouse.getX(),
                    mouse.getY());
            g2.setColor(isHovered ? Color.YELLOW : Color.WHITE);
            g2.drawString(optionsMode[i], x, y);
        }
    }

    private void drawBoard(Graphics2D g2) {
        board.draw(g2);
        Piece hovered = getHoveredPiece();
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        for(Piece p : pieces) {
            if(p != currentPiece) {
                if(p == hovered) {
                    BufferedImage hoverImage = getHoverSprite(p);
                    int size = (int)(Board.getSquare() * p.getScale());
                    g2.drawImage(hoverImage, p.getX(), p.getY(), size, size, null);
                } else {
                    p.draw(g2);
                }
            }
        }

        if(currentPiece != null) {
            currentPiece.draw(g2);
        }

        if(currentPiece != null && isDragging) {
            drawTick(g2, isLegalPreview);
        }
        drawPromotions(g2);
    }

	@Override
	public void run() {
		double drawInterval = (double) 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (thread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;

			if(delta >= 1) {
				update();
                if(!isAIPlaying || currentTurn == Tint.WHITE) {
                    repaint();
                }
                delta--;
			}
		}
	}

	private void update() {
        switch(state) {
            case MENU -> {
                handleMenuInput();
                return;
            }
            case MODE -> {
                setPlayState();
                return;
            }
            default -> getGame();
        }

        switch(mode) {
            case PLAYER -> isAIPlaying = false;
            case AI -> isAIPlaying = true;
        }
	}

    private void getGame() {
        if(state != GameState.BOARD) {
            return;
        }

        if(isAIPlaying && currentTurn == Tint.BLACK) {
            return;
        }

        int hoverCol = mouse.getX() / Board.getSquare();
        int hoverRow = mouse.getY() / Board.getSquare();

        if(isPromoted) {
            promotion();
            mouse.setClicked(false);
            return;
        }

        if(mouse.isPressed() && !isDragging && currentPiece == null) {
            for(Piece p : pieces) {
                if(p.getColor() == currentTurn &&
                        p.getCol() == hoverCol &&
                        p.getRow() == hoverRow) {
                    currentPiece = p;
                    currentPiece.setScale(currentPiece.getDEFAULT_SCALE() + 0.5f);
                    isDragging = true;
                    dragOffsetX = mouse.getX() - p.getX();
                    dragOffsetY = mouse.getY() - p.getY();
                    currentPiece.setPreCol(p.getCol());
                    currentPiece.setPreRow(p.getRow());
                    break;
                }
            }
        }

        if(isDragging && mouse.isPressed() && currentPiece != null) {
            currentPiece.setX(mouse.getX() - dragOffsetX);
            currentPiece.setY(mouse.getY() - dragOffsetY);

            int targetCol = mouse.getX() / Board.getSquare();
            int targetRow = mouse.getY() / Board.getSquare();

            isLegalPreview =
                    currentPiece.canMove(targetCol, targetRow, this) &&
                            !wouldLeaveKingInCheck(currentPiece, targetCol, targetRow);
        }

        if(isDragging && mouse.isClicked() && currentPiece != null) {
            isDragging = false;

            int targetCol = mouse.getX() / Board.getSquare();
            int targetRow = mouse.getY() / Board.getSquare();

            if(isLegalPreview) {
                Piece capturedPiece = null;
                for(Piece p : pieces) {
                    if(p != currentPiece &&
                            p.getCol() == targetCol &&
                            p.getRow() == targetRow) {
                        capturedPiece = p;
                        break;
                    }
                }

                if(capturedPiece != null) {
                    pieces.remove(capturedPiece);
                }

                if(currentPiece instanceof King) {
                    int colDiff = targetCol - currentPiece.getCol();

                    if(Math.abs(colDiff) == 2 && currentPiece.hasMoved()) {
                        int step = (colDiff > 0) ? 1 : -1;
                        int rookStartCol = (colDiff > 0) ? 7 : 0;
                        int rookTargetCol = (colDiff > 0) ? 5 : 3;

                        if(isKingInCheck(currentPiece.getColor()) ||
                                wouldLeaveKingInCheck(currentPiece,
                                        currentPiece.getCol() + step,
                                        currentPiece.getRow())) {

                            currentPiece.updatePos();
                            currentPiece = null;
                            return;
                        }

                        boolean pathClear = true;
                        for(int c = currentPiece.getCol() + step; c != rookStartCol; c += step) {
                            if(boardHasPieceAt(c, currentPiece.getRow())) {
                                pathClear = false;
                                break;
                            }
                        }

                        if(pathClear) {
                            for(Piece p : pieces) {
                                if(p instanceof Rook &&
                                        p.getCol() == rookStartCol &&
                                        p.getRow() == currentPiece.getRow() &&
                                        p.hasMoved()) {

                                    p.setCol(rookTargetCol);
                                    p.updatePos();
                                    p.setHasMoved(true);
                                    break;
                                }
                            }
                        }
                    }
                }

                currentPiece.setCol(targetCol);
                currentPiece.setRow(targetRow);
                currentPiece.updatePos();
                currentPiece.setHasMoved(true);

                if(currentPiece instanceof Pawn) {
                    int oldRow = currentPiece.getPreRow();
                    int movedSquares = Math.abs(targetRow - oldRow);

                    if(capturedPiece == null && Math.abs(targetCol -
                            currentPiece.getPreCol()) == 1) {
                        int dir = (currentPiece.getColor() == Tint.WHITE) ? -1 : 1;
                        if(targetRow - oldRow == dir) {
                            for(Piece p : pieces) {
                                if(p instanceof Pawn &&
                                        p.getColor() != currentPiece.getColor() &&
                                        p.getCol() == targetCol &&
                                        p.getRow() == oldRow &&
                                        p.isTwoStepsAhead()) {
                                    pieces.remove(p);
                                    break;
                                }
                            }
                        }
                    }
                    currentPiece.setTwoStepsAhead(movedSquares == 2);
                }

                for(Piece p : pieces) {
                    if(p instanceof Pawn && p.getColor() != currentPiece.getColor()) {
                        p.resetEnPassant();
                    }
                }

                if(canPromote()) {
                    isPromoted = true;
                    promotionColor = currentPiece.getColor();
                } else {
                    currentTurn = (currentTurn == Tint.WHITE)
                            ? Tint.BLACK : Tint.WHITE;

                    if(currentPiece != null) {
                        currentPiece.setScale(currentPiece.getDEFAULT_SCALE());
                        currentPiece = null;
                    }

                    if(isAIPlaying && currentTurn == Tint.BLACK && !isDragging && !isPromoted) {
                        Move move = getAiTurn();
                        if(move != null) {
                            executeMove(move);
                        }
                        return;
                    }
                    isKingInCheck(currentTurn);
                }

            } else {
                currentPiece.updatePos();
            }
            if(currentPiece != null) {
                currentPiece.setScale(currentPiece.getDEFAULT_SCALE());
                currentPiece = null;
            }
        }
    }

	private boolean canPromote() {
		if(currentPiece.getId() == Type.PAWN) {
			if((currentPiece.getColor() == Tint.WHITE && currentPiece.getRow() == 0)
					|| (currentPiece.getColor() == Tint.BLACK && currentPiece.getRow() == 7)) {
				promotingPawn = currentPiece;
				promoted.clear();
				promoted.add(new Rook(currentPiece.getColor(), 9, 2));
				promoted.add(new Knight(currentPiece.getColor(), 9, 3));
				promoted.add(new Bishop(currentPiece.getColor(), 9, 4));
				promoted.add(new Queen(currentPiece.getColor(), 9, 5));
				return true;
			}
		}
		return false;
	}

    private void promotion() {
        if(!isPromoted || !mouse.isClicked()) {
            return;
        }

        int size = Board.getSquare();
        int totalWidth = size * 4;
        int startX = (WIDTH - totalWidth) / 2;
        int startY = (HEIGHT - size) / 2;

        Type[] options = {
                Type.QUEEN,
                Type.ROOK,
                Type.BISHOP,
                Type.KNIGHT
        };

        for(int i = 0; i < options.length; i++) {
            int x0 = startX + i * size;
            int x1 = x0 + size;
            int y1 = startY + size;

            if(mouse.getX() >= x0 && mouse.getX() <= x1 &&
                    mouse.getY() >= startY && mouse.getY() <= y1) {
                pieces.remove(promotingPawn);
                Piece promotedPiece = switch(options[i]) {
                    case QUEEN -> new Queen(promotingPawn.getColor(),
                            promotingPawn.getCol(), promotingPawn.getRow());
                    case ROOK -> new Rook(promotingPawn.getColor(),
                            promotingPawn.getCol(), promotingPawn.getRow());
                    case BISHOP -> new Bishop(promotingPawn.getColor(),
                            promotingPawn.getCol(), promotingPawn.getRow());
                    case KNIGHT -> new Knight(promotingPawn.getColor(),
                            promotingPawn.getCol(), promotingPawn.getRow());
                    default -> throw new IllegalStateException("Unexpected promotion type");
                };

                pieces.add(promotedPiece);

                promotingPawn = null;
                isPromoted = false;
                currentTurn = (currentTurn == Tint.WHITE) ? Tint.BLACK : Tint.WHITE;
                mouse.setClicked(false);
                break;
            }
        }
    }

    private boolean isKingInCheck(Tint kingColor) {
		Piece king = getKing(kingColor);

		for(Piece p : pieces) {
			if(p.getColor() != kingColor) {
				if(p.canMove(king.getCol(), king.getRow(), this)) {
					checkingPiece = p;
					return true;
				}
			}
		}

		checkingPiece = null;
		return false;
	}

	private Piece getKing(Tint color) {
		for(Piece p : pieces) {
			if(p instanceof King && p.getColor() == color) {
				return p;
			}
		}
		throw new IllegalStateException("King not found for color: " + color);
	}

	private boolean wouldLeaveKingInCheck(Piece piece, int targetCol, int targetRow) {
		int oldCol = piece.getCol();
		int oldRow = piece.getRow();

		Piece captured = null;
		for(Piece p : pieces) {
			if(p != piece && p.getCol() == targetCol && p.getRow() == targetRow) {
				captured = p;
				break;
			}
		}

        if(captured != null) {
			snapshot.remove(captured);
		}

		piece.setCol(targetCol);
		piece.setRow(targetRow);
        boolean inCheck = isKingInCheckSimulated(piece.getColor(), snapshot);
		piece.setCol(oldCol);
		piece.setRow(oldRow);
		return inCheck;
	}

    private boolean isKingInCheckSimulated(Tint kingColor, List<Piece> snapshot) {
        Piece king = null;
        for(Piece p : snapshot) {
            if(p instanceof King && p.getColor() == kingColor) {
                king = p;
                break;
            }
        }

        for(Piece p : snapshot) {
            if(p.getColor() != kingColor) {
                assert king != null;
                if(p.canMove(king.getCol(), king.getRow(), this)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPieceThreatened(Piece piece) {
        for(Piece enemy : pieces) {
            if(enemy.getColor() == piece.getColor()) continue;
            if(enemy.canMove(piece.getCol(), piece.getRow(), this)) {
                return true;
            }
        }
        return false;
    }

    private Move getAiTurn() {
        legalMoves.clear();
        moveScores.clear();

        for(Piece p : pieces) {
            if(p.getColor() != currentTurn) {
                continue;
            }

            for(int col = 0; col < 8; col++) {
                for(int row = 0; row < 8; row++) {
                    if(p.canMove(col, row, this)
                            && !wouldLeaveKingInCheck(p, col, row)) {
                        Move move = new Move(p, col, row);
                        int score = evaluateMove(move);
                        legalMoves.add(new Move(p, col, row));
                        moveScores.add(score);
                    }
                }
            }
        }

        if(legalMoves.isEmpty()) {
            return null;
        }

        int maxScore = Integer.MIN_VALUE;
        int bestIndex = 0;
        for(int i = 0; i < moveScores.size(); i++) {
            if(moveScores.get(i) > maxScore) {
                maxScore = moveScores.get(i);
                bestIndex = i;
            }
        }
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }

    private int evaluateMove(Move move) {
        int score = 0;
        Piece p = move.piece();

        for(Piece enemy : pieces) {
            if(enemy.getCol() == move.targetCol() && enemy.getRow() == move.targetRow()) {
                score += getPieceValue(enemy);
                break;
            }
        }

        int oldCol = p.getCol();
        int oldRow = p.getRow();
        p.setCol(move.targetCol());
        p.setRow(move.targetRow());

        if(isPieceThreatened(p)) {
            score -= getPieceValue(p);
        }

        p.setCol(oldCol);
        p.setRow(oldRow);
        return score;
    }

    private int getPieceValue(Piece p) {
        return switch(p.getId()) {
            case PAWN -> 10;
            case KNIGHT, BISHOP -> 30;
            case ROOK -> 50;
            case QUEEN -> 90;
            case KING -> 900;
        };
    }

    private void executeMove(Move move) {
        Piece p = move.piece();
        p.setPreCol(p.getCol());
        p.setPreRow(p.getRow());

        Piece captured = null;
        for(Piece other : pieces) {
            if(other != p &&
                    other.getCol() == move.targetCol() &&
                    other.getRow() == move.targetRow()) {
                captured = other;
                break;
            }
        }

        if(captured != null) {
            pieces.remove(captured);
        }

        p.setCol(move.targetCol());
        p.setRow(move.targetRow());
        p.updatePos();
        p.setHasMoved(true);
        currentPiece = null;
        isDragging = false;
        isLegalPreview = false;
        currentTurn = Tint.WHITE;
    }

    public boolean boardHasPieceAt(int col, int row) {
		for(Piece p : pieces) {
			if(p.getCol() == col && p.getRow() == row)
				return true;
		}
		return false;
	}

    private void handleMenuInput() {
		if(!mouse.isClicked()) {
			return;
		}
		int startY = HEIGHT/2 + MENU_START_Y;
		int spacing = MENU_SPACING;

        for(int i = 0; i < optionsMenu.length; i++) {
            int y = startY + i * spacing;
            boolean isHovered = getHitbox(y).contains(mouse.getX(),
                    mouse.getY());
            if(isHovered) {
                switch(i) {
                    case 0 -> startNewGame();
                    case 1 -> System.exit(0);
                }
                mouse.setClicked(false);
                break;
            }
        }
	}

	private void startNewGame() {
		this.state = GameState.MODE;
	}

	private void setPlayState() {
        if(!mouse.isClicked()) {
            return;
        }

		if(state != GameState.MODE) {
			return;
		}

		int startY = HEIGHT/2 + MENU_START_Y;
		int spacing = MENU_SPACING;

		for(int i = 0; i < optionsMode.length; i++) {
			int y = startY + i * spacing;
            boolean isHovered = getHitbox(y).contains(mouse.getX(),
                    mouse.getY());
            if(isHovered) {
				switch(i) {
					case 0 -> mode = PlayState.PLAYER;
					case 1 -> mode = PlayState.AI;
				}
				mouse.setClicked(false);
                startBoard();
				return;
			}
		}
	}
}
