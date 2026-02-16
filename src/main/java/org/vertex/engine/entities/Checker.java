package org.vertex.engine.entities;

import org.vertex.engine.enums.Tint;
import org.vertex.engine.enums.TypeID;

import java.util.List;

public class Checker extends Piece {
    private boolean isKing;

    public Checker(Tint color, int col, int row) {
        super(color, col, row);
        this.type = TypeID.CHECKER;
    }

    public boolean isKing() {
        return isKing;
    }

    public void promoteToKing() {
        isKing = true;
    }

    @Override
    public boolean canMove(int targetCol, int targetRow, List<Piece> board) {
        if(!isWithinBoard(targetCol, targetRow)) { return false; }

        int colDiff = targetCol - getCol();
        int rowDiff = targetRow - getRow();
        int absColDiff = Math.abs(colDiff);
        int absRowDiff = Math.abs(rowDiff);

        Piece target = isColliding(targetCol, targetRow, board);
        if (absColDiff == 1 && absRowDiff == 1) {
            if (isKing() || (getColor() == Tint.LIGHT && rowDiff == -1)
                    || (getColor() == Tint.DARK && rowDiff == 1)) {
                return target == null;
            }
        }

        if (absColDiff == 2 && absRowDiff == 2) {
            int midCol = getCol() + colDiff / 2;
            int midRow = getRow() + rowDiff / 2;
            Piece midPiece = isColliding(midCol, midRow, board);
            return midPiece != null && midPiece.getColor() != getColor() && target == null;
        }
        return false;
    }

    @Override
    public Piece copy() {
        Checker p = new Checker(getColor(), getCol(), getRow());
        p.setHasMoved(hasMoved());
        return p;
    }
}
