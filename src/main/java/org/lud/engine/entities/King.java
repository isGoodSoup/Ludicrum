package org.lud.engine.entities;

import org.lud.engine.enums.Turn;
import org.lud.engine.enums.TypeID;
import org.lud.engine.service.BooleanService;
import org.lud.engine.service.GameService;
import org.lud.engine.service.PieceService;

import java.util.List;

public class King extends Piece {

	public King(Turn color, int col, int row) {
		super(color, col, row);
		this.typeID = TypeID.KING;
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, List<Piece> board) {
		if(!isWithinBoard(targetCol, targetRow)) { return false; }
		switch(GameService.getGame()) {
			case CHESS -> {
				int colDiff = Math.abs(targetCol - getCol());
				int rowDiff = Math.abs(targetRow - getRow());

				if((colDiff + rowDiff == 1) || (colDiff * rowDiff == 1)) {
					return isValidSquare(this, targetCol, targetRow, board);
				}

				if(BooleanService.canDoMoves) {
					if(rowDiff == 0 && colDiff == 2 && !hasMoved()) {
						int rookCol = (targetCol > getCol()) ? 7 : 0;
						Piece rook = PieceService.getPieceAt(rookCol, getRow(), board);
						if(rook instanceof Rook && !rook.hasMoved()) {
							int step = (targetCol > getCol()) ? 1 : -1;
							for(int c = getCol() + step; c != rookCol; c += step) {
								if(PieceService.getPieceAt(c, getRow(), board) != null) {
									return false;
								}
							}
							return true;
						}
					}
				}
			}
			case CHECKERS -> {
				int colDiff = Math.abs(targetCol - getCol());
				int rowDiff = Math.abs(targetRow - getRow());

				if(colDiff == 1 && rowDiff == 1) {
					return isColliding(targetCol, targetRow, board) == null;
				}

				if(colDiff == 2 && rowDiff == 2) {
					int midCol = (getCol() + targetCol) / 2;
					int midRow = (getRow() + targetRow) / 2;
					Piece middlePiece = isColliding(midCol, midRow, board);
					if(middlePiece != null && middlePiece.getColor() != getColor()) {
						return true;
					}
				}
			}
            case SHOGI -> {
				int colDiff = Math.abs(targetCol - getCol());
				int rowDiff = Math.abs(targetRow - getRow());

				if((colDiff + rowDiff == 1) || (colDiff * rowDiff == 1)) {
					return isValidSquare(this, targetCol, targetRow, board);
				}
            }
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
