package org.lud.engine.enums;

import org.lud.engine.service.Localization;

public enum TypeID {
    PAWN,
    PAWN_SHOGI,
    TOKIN,
    LANCE,
    SILVER,
    GOLD,
    KNIGHT,
    KNIGHT_SHOGI,
    BISHOP,
    BISHOP_SHOGI,
    ROOK,
    ROOK_SHOGI,
    QUEEN,
    KING,
    CHECKER;

    public String getLabel() {
        return Localization.lang.t("type." + this.name().toLowerCase());
    }
}