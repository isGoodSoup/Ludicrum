package org.lud.engine.service;

import org.lud.engine.animations.ToastAnimation;
import org.lud.engine.entities.Achievement;
import org.lud.engine.entities.Piece;
import org.lud.engine.enums.Achievements;
import org.lud.engine.enums.Games;
import org.lud.engine.enums.Turn;
import org.lud.engine.events.*;
import org.lud.engine.manager.EventBus;
import org.lud.engine.manager.SaveManager;
import org.lud.engine.render.AchievementSprites;
import org.lud.engine.render.RenderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AchievementService {
    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);
    private Map<Achievements, Achievement> achievements;
    private List<Achievement> achievementList;
    private List<Achievement> sortedList;

    private final Map<Long, Integer> moveCount;
    private final Map<Long, Integer> winCount;
    private final Map<Long, Integer> promotionCount;
    private final Map<Long, Integer> checkCount;
    private final Map<Long, Integer> jumpCount;

    private Set<Long> unlockedIDs;
    private Set<Turn> kingsChecked;

    private int castlingCount = 0;
    private int opponentPieces = 0;
    private int piecesCounter = 0;
    private int lostPieces = 0;

    private AnimationService animationService;
    private SaveManager saveManager;
    private ServiceFactory service;
    private EventBus eventBus;

    private boolean isFirstCapture;
    private boolean isFirstToggle;
    private boolean isFirstWin;
    private boolean isQuickWin;

    public AchievementService(EventBus eventBus) {
        achievements = new HashMap<>();
        this.eventBus = eventBus;

        this.moveCount = new HashMap<>();
        this.winCount = new HashMap<>();
        this.promotionCount = new HashMap<>();
        this.checkCount = new HashMap<>();
        this.jumpCount = new HashMap<>();

        this.unlockedIDs = new HashSet<>();
        this.kingsChecked = new HashSet<>();

        this.isFirstCapture = true;
        this.isFirstToggle = true;
        this.isFirstWin = true;
        this.isQuickWin = true;

        for(Achievements type : Achievements.values()) {
            achievements.put(type, new Achievement(type));
        }
        achievementList = new ArrayList<>(achievements.values());
        getSortedAchievements();

        eventBus.register(TotalMovesEvent.class, this::onMove);
        eventBus.register(ToggleEvent.class, this::onToggle);
        eventBus.register(CaptureEvent.class, this::onCapture);
        eventBus.register(CheckEvent.class, this::onCheck);
        eventBus.register(CheckmateEvent.class, this::onCheckmate);
        eventBus.register(CastlingEvent.class, this:: onCastling);
        eventBus.register(PromotionEvent.class, this::onPromotion);
        eventBus.register(HardEvent.class, this::onHardGame);
        eventBus.register(StalemateEvent.class, this::onStalemate);
        eventBus.register(ChessMasterEvent.class, event -> {
            long chessStartId = 1001L;
            long chessEndId   = 1011L;
            long unlockedChess = event.achievements().stream()
                    .map(a -> a.getId().getId())
                    .filter(id -> id >= chessStartId && id <= chessEndId)
                    .count();
            long totalChess = getSortedAchievements().stream()
                    .map(a -> a.getId().getId())
                    .filter(id -> id >= chessStartId && id <= chessEndId)
                    .count();
            if(unlockedChess >= totalChess) {
                unlock(Achievements.MASTER_OF_NONE);
            }
        });

        eventBus.register(VictoryEvent.class, this::onVictory);
        eventBus.register(StrategistEvent.class, this::onStrategist);
        eventBus.register(JumpEvent.class, this::onJump);
        eventBus.register(CaptureEvent.class, this::onLostPiece);
        eventBus.register(CheckersMasterEvent.class, event -> {
            long checkersStartId = 2001L;
            long checkersEndId   = 2011L;
            long unlockedCheckers = event.achievements().stream()
                    .map(a -> a.getId().getId())
                    .filter(id -> id >= checkersStartId && id <= checkersEndId)
                    .count();
            long totalCheckers = getSortedAchievements().stream()
                    .map(a -> a.getId().getId())
                    .filter(id -> id >= checkersStartId && id <= checkersEndId)
                    .count();
            if(unlockedCheckers >= totalCheckers) {
                unlock(Achievements.KINGMAKER);
            }
        });

        eventBus.register(GrandmasterEvent.class, this::onGrandmaster);
    }

    public List<Achievement> init() {
        List<Achievement> loaded = saveManager.loadAchievements();
        setUnlockedAchievements(loaded);
        return loaded;
    }

    public ServiceFactory getService() {
        return service;
    }

    public void setService(ServiceFactory service) {
        this.service = service;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public void setSaveManager(SaveManager saveManager) {
        this.saveManager = saveManager;
    }

    public AnimationService getAnimationService() {
        return animationService;
    }

    public void setAnimationService(AnimationService animationService) {
        this.animationService = animationService;
    }

    public int getOpponentPieces() {
        return opponentPieces;
    }

    public void setOpponentPieces(int opponentPieces) {
        this.opponentPieces = opponentPieces;
    }

    public void addOpponentPieces() {
        this.opponentPieces += 1;
    }

    public int getPiecesCounter() {
        return piecesCounter;
    }

    public void setPiecesCounter(int piecesCounter) {
        this.piecesCounter = piecesCounter;
    }

    public void addPiecesCounter() {
        this.piecesCounter += 1;
    }

    public void unlock(Achievements type) {
        if(!BooleanService.canDoAchievements) { return; }
        Achievement achievement = achievements.get(type);

        if(unlockedIDs.contains(type.getId())) {
            return;
        }

        unlockedIDs.add(type.getId());
        if(achievement != null && !achievement.isUnlocked()) {
            achievement.setUnlocked(true);
            animationService.add(new ToastAnimation
                    (achievement.getId().getTitle(),
                            achievement.getId().getDescription(),
                            RenderContext.BASE_HEIGHT,
                            AchievementSprites.getSprite(achievement)));
           service.getSound().playFX(5);
           service.getGameService().autoSave();
           saveManager.saveAchievements(getUnlockedAchievements());
           log.info("Achievement Unlocked: {}", achievement.getId().getTitle());
           eventBus.fire(new ChessMasterEvent(getUnlockedAchievements()));
        }
        eventBus.fire(new GrandmasterEvent(Collections
                .unmodifiableList(getUnlockedAchievements())));
    }

    public boolean isUnlocked(Achievements type) {
        return achievements.get(type).isUnlocked();
    }

    public void lock(Achievements type) {
        Achievement achievement = achievements.get(type);
        if(achievement != null && achievement.isUnlocked()) {
            achievement.setUnlocked(false);
        }
    }

    public void unlockAllAchievements() {
        for(Map.Entry<Achievements, Achievement> a
                : achievements.entrySet()) {
            a.getValue().setUnlocked(true);
        }
    }

    public void lockAllAchievements() {
        for(Map.Entry<Achievements, Achievement> a
                : achievements.entrySet()) {
            a.getValue().setUnlocked(false);
        }
    }

    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }

    public List<Achievement> getAchievementList() {
        achievementList.sort(Comparator.comparingInt(a -> a.getId().ordinal()));
        return achievementList;
    }

    public List<Achievement> getUnlockedAchievements() {
        return achievements.values()
                .stream()
                .filter(Achievement::isUnlocked)
                .toList();
    }

    public void setUnlockedAchievements(List<Achievement> loadedAchievements) {
        unlockedIDs.clear();

        for (Achievement loaded : loadedAchievements) {
            Achievements type = loaded.getId();
            Achievement existing = achievements.get(type);

            if(existing != null) {
                existing.setUnlocked(true);
                unlockedIDs.add(type.getId());
            }
        }
    }

    public List<Achievement> getSortedAchievements() {
        Collection<Achievement> achievements = achievementList;
        sortedList = new ArrayList<>(achievements);
        sortedList.sort(
                Comparator.comparingInt(a -> a.getId().ordinal())
        );
        return sortedList;
    }

    private void onMove(TotalMovesEvent event) {
        Piece piece = event.piece();
        moveCount.merge(piece.getID(), 1, Integer::sum);

        if(GameService.getGame() == Games.CHESS && moveCount.get(piece.getID()) < 5 && isQuickWin) {
            unlock(Achievements.QUICK_WIN);
            isQuickWin = false;
        }

        if(GameService.getGame() == Games.CHECKERS && moveCount.get(piece.getID()) < 8 && isQuickWin) {
            unlock(Achievements.QUICK_START);
            isQuickWin = false;
        }
    }

    private void onCapture(CaptureEvent event) {
        Piece attacker = event.piece();
        Piece captured = event.captured();
        if(attacker.getColor() != Turn.LIGHT) { return; }

        if(GameService.getGame() == Games.CHESS && isFirstCapture) {
            unlock(Achievements.FIRST_CAPTURE);
            isFirstCapture = false;
        }

        if(GameService.getGame() == Games.CHECKERS && isFirstCapture) {
            unlock(Achievements.ROUND_CAPTURE);
            isFirstCapture = false;
        }
    }

    private void onLostPiece(CaptureEvent event) {
        Piece captured = event.captured();
        Piece attacker = event.piece();

        if (captured.getColor() == Turn.LIGHT) {
            lostPieces++;
        }
    }

    private void onToggle(ToggleEvent event) {
        if(isFirstToggle) {
            unlock(Achievements.SECRET_TOGGLE);
            isFirstToggle = false;
        }
    }

    private void onCastling(CastlingEvent event) {
        if(GameService.getGame() != Games.CHESS) { return; }
        castlingCount++;
        if(castlingCount == 10) {
            unlock(Achievements.CASTLING_MASTER);
        }
    }

    private void onCheck(CheckEvent event) {
        if(GameService.getGame() != Games.CHESS) { return; }
        Piece piece = event.piece();
        Piece king = event.king();
        checkCount.merge(piece.getID(), 1, Integer::sum);

        if(checkCount.get(piece.getID()) == 4) {
            unlock(Achievements.CHECK_OVER);
        }

        kingsChecked.add(king.getColor());
    }

    private void onCheckmate(CheckmateEvent event) {
        if(GameService.getGame() != Games.CHESS) { return; }
        Piece piece = event.piece();
        winCount.merge(piece.getID(), 1, Integer::sum);
        if(isFirstWin) {
            unlock(Achievements.CHECKMATE);
            isFirstWin = false;
        }

        if(winCount.get(piece.getID()) == 128) {
            unlock(Achievements.HEAVY_CROWN);
        }

        if(!kingsChecked.contains(Turn.LIGHT)) {
            unlock(Achievements.UNTOUCHABLE);
        }
    }

    private void onJump(JumpEvent event) {
        Piece p = event.piece();
        jumpCount.merge(p.getID(), 1, Integer::sum);

        if(jumpCount.get(p.getID()) == 2 && !isUnlocked(Achievements.DOUBLE_JUMP)) {
            unlock(Achievements.DOUBLE_JUMP);
            jumpCount.clear();
        }

        if(jumpCount.get(p.getID()) == 3 && !isUnlocked(Achievements.TRIPLE_JUMP)) {
            unlock(Achievements.TRIPLE_JUMP);
            jumpCount.clear();
        }
    }

    private void onVictory(VictoryEvent event) {
        if(GameService.getGame() != Games.CHECKERS) { return; }
        Piece p = event.piece();

        if(BooleanService.isDraw && !isUnlocked(Achievements.DRAW_MASTER)) {
            unlock(Achievements.DRAW_MASTER);
        }

        if(opponentPieces == 0) {
            unlock(Achievements.CLEAN_SWEEP);
        }

        if (lostPieces >= 4) {
            unlock(Achievements.COMEBACK);
            lostPieces = 0;
        }
    }

    private void onHardGame(HardEvent event) {
        if(GameService.getGame() != Games.CHESS) { return; }
        Piece piece = event.piece();
        unlock(Achievements.HARD_GAME);
    }

    private void onPromotion(PromotionEvent event) {
        Piece piece = event.piece();
        promotionCount.merge(piece.getID(), 1, Integer::sum);
        if(GameService.getGame() == Games.CHESS) {
            if(promotionCount.get(piece.getID()) == 4) {
                unlock(Achievements.KING_PROMOTER);
            }
        }

        if(GameService.getGame() == Games.CHECKERS) {
            if(promotionCount.get(piece.getID()) == 1
                    && !isUnlocked(Achievements.KING_ME)) {
                unlock(Achievements.KING_ME);
            }

            if(promotionCount.get(piece.getID()) == 4
                    && !isUnlocked(Achievements.KINGDOM_BUILDER)) {
                unlock(Achievements.KINGDOM_BUILDER);
            }
        }
    }

    private void onStrategist(StrategistEvent event) {
        if(GameService.getGame() != Games.CHECKERS) { return; }
        Piece p = event.piece();
        Turn color = event.color();

        if(piecesCounter == 12) {
            unlock(Achievements.STRATEGIST);
        }
    }

    private void onStalemate(StalemateEvent event) {
        Piece piece = event.piece();

        if(GameService.getGame() == Games.CHESS) {
            unlock(Achievements.ALL_PIECES);
        }

        if(GameService.getGame() == Games.CHECKERS) {
            unlock(Achievements.STALEMATE_SURVIVOR);
        }
    }

    private void onGrandmaster(GrandmasterEvent event) {
        List<Achievement> unlocked = event.achievementsList();

        long totalAchievements = achievementList.stream()
                .filter(a -> a.getId() != Achievements.GRANDMASTER)
                .count();

        if(unlocked.size() >= totalAchievements && !isUnlocked(Achievements.GRANDMASTER)) {
            unlock(Achievements.GRANDMASTER);
        }
    }
}
