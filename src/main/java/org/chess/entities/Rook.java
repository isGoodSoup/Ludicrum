package org.chess.entities;

import org.chess.enums.Tint;
import org.chess.enums.Type;
import org.chess.gui.BoardPanel;

public class Rook extends Piece {

	public Rook(Tint color, int col, int row) {
		super(color, col, row);
		this.id = Type.ROOK;
		if(color == Tint.WHITE) {
			image = getImage("/pieces/rook");
		} else {
			image = getImage("/pieces/rook-b");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, BoardPanel board) {
	    if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
	        if(targetCol == getPreCol() || targetRow == getPreRow()) {
                return isValidSquare(targetCol, targetRow, board)
                        && !isPieceOnTheWay(targetCol, targetRow, board);
	        }
	    }
	    return false;
	}
}
