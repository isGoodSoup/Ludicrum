package org.vertex.engine.entities;

import org.vertex.engine.enums.Tint;
import org.vertex.engine.enums.TypeID;

import java.util.List;

public class Lance extends Piece {

    public Lance(Tint color, int col, int row) {
        super(color, col, row);
        this.typeID = TypeID.LANCE;
    }

    @Override
    public boolean canMove(int targetCol, int targetRow, List<Piece> board) {
        return false;
    }

    @Override
    public Piece copy() {
        return null;
    }
}
