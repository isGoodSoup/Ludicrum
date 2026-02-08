package org.chess.entities;

import org.chess.gui.BoardPanel;

import java.awt.*;

public class Board {
	private final int COL = 8;
	private final int ROW = 8;
	private static final int SQUARE = 64;
	private static final int HALF_SQUARE = SQUARE / 2;
	private static final Color EVEN = new Color(210, 165, 125);
	private static final Color ODD = new Color(175, 115, 70);
	private static final int PADDING = 4;
	private final String[][] squares = new String[ROW][COL];

	public int getCOL() {
		return COL;
	}

	public int getROW() {
		return ROW;
	}

	public static int getSquare() {
		return SQUARE;
	}

	public static int getHalfSquare() {
		return HALF_SQUARE;
	}

	public Board() {
		precomputeSquares();
	}

	private void precomputeSquares() {
		for (int row = 0; row < ROW; row++) {
			for (int col = 0; col < COL; col++) {
				squares[row][col] = getSquareName(col, row);
			}
		}
	}

	public void draw(Graphics2D g2) {
		String[] letters = {"A","B","C","D","E","F","G","H"};
		for(int row = 0; row < ROW; row++) {
			for(int col = 0; col < COL; col++) {
				boolean isEven = (row + col) % 2 == 0;
				g2.setColor(isEven ? EVEN : ODD);
				g2.fillRect(col * SQUARE, row * SQUARE, SQUARE, SQUARE);
				g2.setFont(BoardPanel.getFont(12));
				g2.setColor(isEven ? ODD : EVEN);
				g2.drawString(getSquareName(col, row),
						col * SQUARE + PADDING,
						row * SQUARE + SQUARE - PADDING);
			}
		}
	}

	public static String getSquareName(int col, int row) {
		char file = (char) ('A' + col);
		int rank = 8 - row;
		return "" + file + rank;
	}

	public String getSquareNameAt(int col, int row) {
		return squares[row][col];
	}
}
