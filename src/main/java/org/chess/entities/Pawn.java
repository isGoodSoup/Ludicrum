package org.chess.entities;

import org.chess.enums.Tint;
import org.chess.enums.Type;
import org.chess.gui.BoardPanel;

public class Pawn extends Piece {

	public Pawn(Tint color, int col, int row) {
		super(color, col, row);
		this.id = Type.PAWN;
		if(color == Tint.WHITE) {
			image = getImage("/pieces/pawn");
		} else {
			image = getImage("/pieces/pawn-b");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, BoardPanel board) {
		if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			int moveValue = 0;
			if(getColor() == Tint.WHITE) {
				moveValue = -1;
			}
			
			if(getColor() == Tint.BLACK) {
				moveValue = 1;
			}

			setOtherPiece(isColliding(targetCol, targetRow, board));
			if(targetCol == getPreCol() && targetRow == getPreRow() + moveValue 
					&& getOtherPiece() == null) {
				return true;
			}

			if(targetCol == getPreCol() && targetRow == getPreRow() + moveValue * 2 
					&& getOtherPiece() == null
					&& hasMoved() && !isPieceOnTheWay(targetCol, targetRow,
					board)) {
				return true;
			}

			if(Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() + moveValue
					&& getOtherPiece() != null && getOtherPiece().getColor() != this.getColor()) {
				return true;
			}
			
			if(Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() + moveValue) {
				for (Piece p : board.getPieces()) {
					if(p.getCol() == targetCol && p.getRow() == getPreRow()
							&& p.isTwoStepsAhead()) {
						setOtherPiece(p);
						return true;
					}
				}
			}
		}
		return false;
	}
}
