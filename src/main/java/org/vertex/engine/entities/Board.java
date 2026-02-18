package org.vertex.engine.entities;

import org.vertex.engine.enums.Games;
import org.vertex.engine.service.GameService;

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
	private Piece[][] pieces = new Piece[ROW][COL];

	public Board() {
		COL = grids.get(GameService.getGame());
		ROW = grids.get(GameService.getGame());
	}

	public void init() {
		grids.put(Games.CHESS, 8);
		grids.put(Games.CHECKERS, 8);
		grids.put(Games.SHOGI, 9);
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
