package org.chess.entities;

import org.chess.enums.Tint;
import org.chess.enums.Type;

import java.util.List;

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
	public boolean canMove(int targetCol, int targetRow, List<Piece> board) {
		if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			int moveValue = (getColor() == Tint.WHITE) ? -1 : 1;
			setOtherPiece(isColliding(targetCol, targetRow, board));

			if (targetCol == getPreCol() && targetRow == getPreRow() +
					moveValue && getOtherPiece() == null) {
				return true;
			}

			if (targetCol == getPreCol() && targetRow == getPreRow() +
					moveValue * 2 &&
					getOtherPiece() == null && !hasMoved()
					&& isPathClear(targetCol, targetRow, board)) {
				return true;
			}

			if (Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow()
					+ moveValue && getOtherPiece() != null &&
					getOtherPiece().getColor() != this.getColor()) {
				return true;
			}

			if (Math.abs(targetCol - getPreCol()) == 1 &&
					targetRow == getPreRow() + moveValue) {
				for (Piece p : board) {
					if (p instanceof Pawn && p.getColor() != this.getColor()
							&& p.getCol() == targetCol && p.getRow() == getPreRow()
							&& p.isTwoStepsAhead()) {
						setOtherPiece(p);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isPathClear(int targetCol, int targetRow, List<Piece> board) {
		int colDiff = targetCol - getCol();
		int rowDiff = targetRow - getRow();

		if (colDiff == 0) {
			int rowStep = Integer.signum(rowDiff);
			int r = getRow() + rowStep;

			while (r != targetRow) {
				if (getPieceAt(targetCol, r, board) != null) {
					return false;
				}
				r += rowStep;
			}
			return true;
		}
		return false;
	}

	@Override
	public void movePiece(Piece p, int newCol, int newRow) {
		super.movePiece(p, newCol, newRow);
        setTwoStepsAhead(Math.abs(newRow - getPreRow()) == 2);
		if (Math.abs(newRow - getPreRow()) == 2) {
			setHasMoved(true);
		}
	}

	@Override
	public Piece copy() {
		Pawn p = new Pawn(getColor(), getCol(), getRow());
		p.setHasMoved(hasMoved());
		p.setTwoStepsAhead(this.isTwoStepsAhead());
		return p;
	}
}
