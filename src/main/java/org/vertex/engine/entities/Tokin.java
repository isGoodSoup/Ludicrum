package org.vertex.engine.entities;

import org.vertex.engine.enums.Tint;
import org.vertex.engine.enums.TypeID;

import java.util.List;

public class Tokin extends Piece {

    public Tokin(Tint color, int col, int row) {
        super(color, col, row);
        this.typeID = TypeID.TOKIN;
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
