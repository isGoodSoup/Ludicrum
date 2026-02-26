package org.lud.engine.service;

import org.lud.engine.input.*;
import org.lud.engine.util.Colors;
import org.lud.engine.core.GameFrame;
import org.lud.engine.interfaces.UI;
import org.lud.engine.manager.EventBus;
import org.lud.engine.manager.MovesManager;
import org.lud.engine.manager.SaveManager;
import org.lud.engine.render.MenuRender;
import org.lud.engine.render.RenderContext;
import org.lud.engine.render.menu.*;
import org.lud.engine.sound.Sound;

import java.util.List;

@SuppressWarnings("ALL")
public class ServiceFactory {
    private final Intro intro;
    private final RenderContext render;
    private final PieceService piece;
    private final BoardService board;
    private final Coordinator coordinator;
    private final Keyboard keyboard;
    private final KeyboardInput key;
    private final Mouse mouse;
    private final MouseInput mouseInput;
    private final Sound sound;
    private final UIService ui;
    private final GameService gs;
    private final PromotionService promotion;
    private final MovesManager movesManager;
    private final SaveManager saveManager;
    private final ModelService model;
    private final AnimationService animation;
    private final TimerService timer;
    private final AchievementService achievement;
    private final EventBus eventBus;

    private final MainMenu mainMenu;
    private final SettingsMenu settingsMenu;
    private final AchievementsMenu achievementsMenu;
    private final Checkmate checkmate;
    private final PromotionMenu promotionMenu;
    private final SandboxMenu sandboxMenu;
    private final TooltipMenu tooltipMenu;
    private final VolumeMenu volumeMenu;

    public ServiceFactory(RenderContext render, GameFrame gameFrame,
                          Intro intro) {
        this.render = render;
        this.intro = intro;
        this.eventBus = new EventBus();
        this.coordinator = new Coordinator();
        this.keyboard = new Keyboard();
        this.key = new KeyboardInput();
        this.key.setGameFrame(gameFrame);
        this.render.setKeyUI(key);
        this.mouse = new Mouse();
        this.mouseInput = new MouseInput(mouse, this);
        this.render.setMouse(mouse);
        this.render.setMouseInput(mouseInput);
        this.sound = new Sound();
        this.key.setService(this);
        this.animation = new AnimationService();
        this.piece = new PieceService(eventBus);
        this.promotion = new PromotionService(piece, eventBus);
        this.model = new ModelService(piece, animation);
        this.movesManager = new MovesManager();
        this.promotion.setMovesManager(movesManager);
        this.piece.setMoveManager(movesManager);
        this.render.setMovesManager(movesManager);
        this.saveManager = new SaveManager();
        this.gs = new GameService(null, saveManager);
        this.board = new BoardService(piece, movesManager);
        this.piece.setGameService(gs);
        this.board.setGameService(gs);
        this.promotion.setGameService(gs);
        this.key.setBoardService(board);
        this.gs.setBoardService(board);
        this.piece.setBoardService(board);
        this.board.setService(this);
        this.model.setBoardService(board);
        this.gs.setServiceFactory(this);
        this.gs.setSaveManager(saveManager);
        this.timer = new TimerService();
        this.ui = new UIService(render, piece, board, gs, promotion,
                model, movesManager, timer, mouse);
        this.achievement = new AchievementService(eventBus);
        this.achievement.setService(this);
        this.achievement.setAnimationService(animation);
        this.achievement.setSaveManager(saveManager);

        this.render.getMenuRender().setGameService(gs);
        this.render.getMenuRender().init();

        List<UI> menus = render.getMenuRender().getMenus();
        this.mainMenu = new MainMenu(render, gs, ui, key, mouse);
        this.settingsMenu = new SettingsMenu(render, ui, gs, key, MenuRender.OPTION_BUTTONS);
        this.achievementsMenu = new AchievementsMenu(render, key, achievement, gs, mouse);
        this.checkmate = new Checkmate(gs, render, RenderContext.BASE_WIDTH);
        this.promotionMenu = new PromotionMenu(render, piece, promotion);
        this.sandboxMenu = new SandboxMenu(render, board);
        this.tooltipMenu = new TooltipMenu(render, piece, board, ui, mouse);
        this.volumeMenu = new VolumeMenu(render, sound);
        menus.add(mainMenu);
        menus.add(settingsMenu);
        menus.add(achievementsMenu);
        menus.add(checkmate);
        menus.add(promotionMenu);
        menus.add(sandboxMenu);
        menus.add(tooltipMenu);
        menus.add(volumeMenu);

        this.render.getBoardRender().setBoardService(board);
        this.render.getBoardRender().setPieceService(piece);
        this.render.getBoardRender().setUIService(ui);
        this.render.getBoardRender().setPromotionService(promotion);
        this.render.getBoardRender().setGameService(gs);
        this.render.getBoardRender().setMouse(mouse);
        this.render.getBoardRender().setMouseInput(mouseInput);
        this.render.getMovesRender().setBoardService(board);
        this.render.getMovesRender().setUIService(ui);
        this.render.getMovesRender().setMovesManager(movesManager);
        this.render.getControlsRender().setService(this);
        this.movesManager.init(this, eventBus);
        this.mouseInput.init();
        this.achievement.init();
        Colors.setService(this);
    }

    public Intro getIntro() {
        return intro;
    }

    public RenderContext getRender() {
        return render;
    }

    public PieceService getPieceService() {
        return piece;
    }

    public BoardService getBoardService() {
        return board;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public KeyboardInput getKeyboardInput() {
        return key;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public UIService getUIService() {
        return ui;
    }

    public Sound getSound() {
        return sound;
    }

    public PromotionService getPromotionService() {
        return promotion;
    }

    public MovesManager getMovesManager() {
        return movesManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public ModelService getModelService() {
        return model;
    }

    public GameService getGameService() {
        return gs;
    }

    public AnimationService getAnimationService() {
        return animation;
    }

    public TimerService getTimerService() { return timer; }

    public AchievementService getAchievementService() {
        return achievement;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }

    public AchievementsMenu getAchievementsMenu() {
        return achievementsMenu;
    }

    public Checkmate getCheckmate() {
        return checkmate;
    }

    public PromotionMenu getPromotionMenu() {
        return promotionMenu;
    }

    public SandboxMenu getSandboxMenu() {
        return sandboxMenu;
    }

    public TooltipMenu getTooltipMenu() {
        return tooltipMenu;
    }

    public VolumeMenu getVolumeMenu() {
        return volumeMenu;
    }
}
