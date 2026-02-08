package org.chess.entities;

import org.chess.enums.Tint;
import org.chess.enums.Type;
import org.chess.gui.BoardPanel;

public class Bishop extends Piece {

	public Bishop(Tint color, int col, int row) {
		super(color, col, row);
		this.id = Type.BISHOP;
		if (color == Tint.WHITE) {
			image = getImage("/pieces/bishop");
		} else {
			image = getImage("/pieces/bishop-b");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, BoardPanel board) {
		if (!isWithinBoard(targetCol, targetRow)
				|| isSameSquare(targetCol, targetRow)) {
			return false;
		}
		int colDiff = targetCol - getPreCol();
		int rowDiff = targetRow - getPreRow();

		if (Math.abs(colDiff) != Math.abs(rowDiff)) {
			return false;
		}

		int colStep = (colDiff > 0) ? 1 : -1;
		int rowStep = (rowDiff > 0) ? 1 : -1;

		int c = getPreCol() + colStep;
		int r = getPreRow() + rowStep;
		while (c != targetCol && r != targetRow) {
			if (board.boardHasPieceAt(c, r)) { 
				return false;
			}
			c += colStep;
			r += rowStep;
		}
		return isValidSquare(targetCol, targetRow, board);
	}
}
