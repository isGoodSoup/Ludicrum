package org.lud.engine.service;

import org.lud.engine.entities.*;
import org.lud.engine.enums.Turn;

import java.util.Random;

public class BooleanService {
    public static boolean isAIMoving;
    public static boolean canMove;
    public static boolean isValidSquare;
    public static boolean isLegal;
    public static boolean isDragging;
    public static boolean isCheckmate;
    public static boolean isStalemate;
    public static boolean isPromotionActive;
    public static boolean isFullscreen;
    public static boolean isExitActive;
    public static boolean isMovesActive;
    public static boolean isTurnLocked;
    public static boolean isDraw;
    public static boolean canType;
    public static boolean canPlayFX;
    public static boolean canPlayMusic;
    public static boolean isMovingVolume;

    public static boolean canUseMouse;
    public static boolean canUseKeyboard;

    public static boolean canDoMoves;
    public static boolean canUndoMoves;
    public static boolean canDoAchievements;
    public static boolean canPromote;
    public static boolean canSave;
    public static boolean canAIPlay;
    public static boolean canDoHard;
    public static boolean canTime;
    public static boolean canStopwatch;
    public static boolean canResetTable;
    public static boolean canBeColorblind;
    public static boolean canTheme;
    public static boolean canShowTick;
    public static boolean canToggleHelp;
    public static boolean canDoAuto;
    public static boolean cannotAutoCommit;
    public static boolean canAnimateLogo;

    private static final Random random = new Random();

    public static void defaultToggles() {
        canUseMouse = true;
        canUseKeyboard = false;
        canDoAchievements = true;
        canSave = false;
        isTurnLocked = false;
        canDoAuto = true;
        cannotAutoCommit = false;
        isMovingVolume = false;
        canPlayFX = true;
        canPlayMusic = false;
        canUndoMoves = true;
        canDoMoves = true;
        canAIPlay = true;
        canPromote = true;
        canShowTick = true;
        canResetTable = true;
        canStopwatch = true;
        canTheme = false;
        isFullscreen = true;
        isCheckmate = false;
        isExitActive = false;
        canType = false;
        canToggleHelp = false;
        isDragging = false;
        canAnimateLogo = true;
    }

    public static boolean getBoolean() {
        return random.nextBoolean();
    }

    public static int getRandom(int i, int i1) {
        return random.nextInt(i, i1);
    }

    public static int getRandom(int i) {
        return random.nextInt(i);
    }

    public static Piece getRandomPiece(Piece pawn, Turn color) {
        int index = getRandom(0, 3);
        switch(index) {
            case 0 -> pawn = new Rook(color, pawn.getCol(), pawn.getRow());
            case 1 -> pawn = new Bishop(color, pawn.getCol(), pawn.getRow());
            case 2 -> pawn = new Knight(color, pawn.getCol(), pawn.getRow());
            case 3 -> pawn = new Queen(color, pawn.getCol(), pawn.getRow());
        }
        return pawn;
    }
}