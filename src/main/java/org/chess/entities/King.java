package org.chess.entities;

import org.chess.enums.Tint;
import org.chess.enums.Type;
import org.chess.service.PieceService;

import java.util.List;

public class King extends Piece {

	public King(Tint color, int col, int row) {
		super(color, col, row);
		this.id = Type.KING;
		if(color == Tint.WHITE) {
			image = PieceService.getImage("/pieces/king");
		} else {
			image = PieceService.getImage("/pieces/king-b");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, List<Piece> board) {
		if(!isWithinBoard(targetCol, targetRow)) { return false; }
		int colDiff = Math.abs(targetCol - getCol());
		int rowDiff = Math.abs(targetRow - getRow());
		if((colDiff + rowDiff == 1) || (colDiff * rowDiff == 1)) {
			return isValidSquare(targetCol, targetRow, board);
		}
		return false;
	}

	@Override
	public Piece copy() {
		King p = new King(getColor(), getCol(), getRow());
		p.setHasMoved(hasMoved());
		return p;
	}
}
