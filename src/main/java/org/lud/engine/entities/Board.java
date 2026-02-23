package org.lud.engine.entities;

import org.lud.engine.enums.Games;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Board implements Serializable {
	private int COL;
	private int ROW;
	private static final int SQUARE = 64;
	private static final int HALF_SQUARE = SQUARE/2;
	private static final int PADDING = 8;
	private Map<Games, Integer> grids = new HashMap<>();
	private Piece[][] pieces;

	public Board() {
		init();
	}

	public void init() {
		grids.put(Games.CHESS, 8);
		grids.put(Games.CHECKERS, 8);
		grids.put(Games.SHOGI, 9);
		grids.put(Games.SANDBOX, 9);
		grids.put(Games.CHAOS, 8);
	}

	public void setSize(Games game) {
		Integer size = grids.get(game);
		if (size == null) throw new IllegalStateException("Grid not defined for game: " + game);
		COL = size;
		ROW = size;
		pieces = new Piece[ROW][COL];
	}

	public Map<Games, Integer> getGrids() {
		return grids;
	}

	public void setGrids(Map<Games, Integer> grids) {
		this.grids = grids;
	}

	public int getCol() {
		return COL;
	}

	public int getRow() {
		return ROW;
	}

	public void setCol(int COL) {
		this.COL = COL;
	}

	public void setRow(int ROW) {
		this.ROW = ROW;
	}

	public static int getSquare() {
		return SQUARE;
	}

	public static int getHalfSquare() {
		return HALF_SQUARE;
	}

	public static int getPadding() {
		return PADDING;
	}

	public Piece[][] getPieces() {
		return pieces;
	}

	public void setPieces(Piece[][] pieces) {
		this.pieces = pieces;
	}
}
