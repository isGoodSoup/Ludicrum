package org.lud.engine.entities;

import org.lud.engine.enums.Turn;
import org.lud.engine.enums.TypeID;
import org.lud.engine.interfaces.GoldGeneral;

import java.util.List;

public class Gold extends Piece implements GoldGeneral {

    public Gold(Turn color, int col, int row) {
        super(color, col, row);
        this.typeID = TypeID.GOLD;
    }

    @Override
    public boolean canMove(int targetCol, int targetRow, List<Piece> board) {
        return canMoveLikeGold(this, targetCol, targetRow, board);
    }

    @Override
    public Piece copy() {
        Gold p = new Gold(getColor(), getCol(), getRow());
        p.setHasMoved(hasMoved());
        return p;
    }
}
