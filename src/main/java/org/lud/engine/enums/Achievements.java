package org.lud.engine.enums;

import org.lud.engine.service.Localization;

public enum Achievements {
    FIRST_CAPTURE           (1001L, "a01_first_steps",            "achievement.first_steps.title",        "achievement.first_steps.desc"),
    SECRET_TOGGLE           (1002L, "a02_toggles",                "achievement.toggles.title",           "achievement.toggles.desc"),
    CHECKMATE               (1003L, "a03_checkmate",              "achievement.checkmate.title",         "achievement.checkmate.desc"),
    CASTLING_MASTER         (1004L, "a04_castling_master",        "achievement.castling_master.title",   "achievement.castling_master.desc"),
    KING_PROMOTER           (1005L, "a05_king_promoter",          "achievement.king_promoter.title",     "achievement.king_promoter.desc"),
    QUICK_WIN               (1006L, "a06_quick_win",              "achievement.quick_win.title",         "achievement.quick_win.desc"),
    CHECK_OVER              (1007L, "a07_check_over",             "achievement.check_over.title",        "achievement.check_over.desc"),
    HEAVY_CROWN             (1008L, "a08_heavy_crown",            "achievement.heavy_crown.title",       "achievement.heavy_crown.desc"),
    ALL_PIECES              (1009L, "a09_good_riddance",          "achievement.all_pieces.title",        "achievement.all_pieces.desc"),
    HARD_GAME               (1010L, "a10_that_was_easy",          "achievement.hard_game.title",         "achievement.hard_game.desc"),
    UNTOUCHABLE             (1011L, "a11_cant_touch_this",        "achievement.untouchable.title",       "achievement.untouchable.desc"),
    MASTER_OF_NONE          (1012L, "a12_master_of_none",         "achievement.master_of_none.title",    "achievement.master_of_none.desc"),

    ROUND_CAPTURE           (2001L, "a13_first_capture",          "achievement.round_capture.title",     "achievement.round_capture.desc"),
    QUICK_START             (2002L, "a14_quick_start",            "achievement.quick_start.title",       "achievement.quick_start.desc"),
    STRATEGIST              (2003L, "a15_strategist",             "achievement.strategist.title",        "achievement.strategist.desc"),
    DOUBLE_JUMP             (2004L, "a16_graysons",               "achievement.double_jump.title",       "achievement.double_jump.desc"),
    TRIPLE_JUMP             (2005L, "a17_triple_threat",          "achievement.triple_jump.title",       "achievement.triple_jump.desc"),
    KING_ME                 (2006L, "a18_king_me",                "achievement.king_me.title",           "achievement.king_me.desc"),
    KINGDOM_BUILDER         (2007L, "a19_kingdom_builder",        "achievement.kingdom_builder.title",   "achievement.kingdom_builder.desc"),
    COMEBACK                (2008L, "a20_comeback",               "achievement.comeback.title",          "achievement.comeback.desc"),
    DRAW_MASTER             (2009L, "a21_even_stevens",           "achievement.draw_master.title",       "achievement.draw_master.desc"),
    CLEAN_SWEEP             (2010L, "a22_lord_of_checkers",       "achievement.clean_sweep.title",       "achievement.clean_sweep.desc"),
    STALEMATE_SURVIVOR      (2011L, "a23_stalemate_survivor",     "achievement.stalemate_survivor.title","achievement.stalemate_survivor.desc"),
    KINGMAKER               (2012L, "a24_kingmaker",              "achievement.kingmaker.title",         "achievement.kingmaker.desc"),
    GRANDMASTER             (1024L, "axx_grandmaster",            "achievement.grandmaster.title",       "achievement.grandmaster.desc");

    private final long id;
    private final String file;
    private final String titleKey;
    private final String descKey;

    Achievements(long id, String file, String titleKey, String descKey) {
        this.id = id;
        this.file = file;
        this.titleKey = titleKey;
        this.descKey = descKey;
    }

    public long getId() { return id; }
    public String getFile() { return file; }

    public String getTitle() {
        return Localization.lang.t(titleKey).toUpperCase();
    }

    public String getDescription() {
        return Localization.lang.t(descKey);
    }
}