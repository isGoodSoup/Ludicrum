package org.chess.entities;

import org.chess.enums.Tint;
import org.chess.enums.Type;
import org.chess.gui.BoardPanel;

public class Queen extends Piece {

	public Queen(Tint color, int col, int row) {
		super(color, col, row);
		this.id = Type.QUEEN;
		if(color == Tint.WHITE) {
			image = getImage("/pieces/queen");
		} else {
			image = getImage("/pieces/queen-b");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, BoardPanel board) {
	    if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
	    	 if(targetCol == getCol() || targetRow == getRow()) {
	    		 if(isValidSquare(targetCol, targetRow, board)
						 && !isPieceOnTheWay(targetCol, targetRow, board)) {
	    			 return true;
	    		 }
	    	 }
			if(Math.abs(targetCol - getCol()) == Math.abs(targetRow - getRow())) {
                return isValidSquare(targetCol, targetRow, board)
						&& !isPieceOnTheWay(targetCol, targetRow, board);
			}
		}
	    return false;
	}
}
