package org.lud.engine.core;

import org.lud.engine.render.menu.Intro;
import org.lud.engine.service.Localization;
import org.lud.engine.util.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lud.engine.entities.Piece;
import org.lud.engine.enums.GameState;
import org.lud.engine.enums.Games;
import org.lud.engine.enums.PlayState;
import org.lud.engine.enums.Theme;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.BooleanService;
import org.lud.engine.service.GameService;
import org.lud.engine.service.ServiceFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serial;
import java.util.Locale;

public class BoardPanel extends JPanel implements Runnable {
	@Serial
    private static final long serialVersionUID = -5189356863277669172L;
    private static final Logger log = LoggerFactory.getLogger(BoardPanel.class);
    private final GameFrame gameFrame;
    private final Intro intro;
    private final RenderContext render;
    private final int FPS = 60;
	private Thread thread;

    private static ServiceFactory service;

    private boolean isFinished;

	public BoardPanel(GameFrame gameFrame) {
        super();
        log.info("Opening new session");
        this.gameFrame = gameFrame;
        this.render = new RenderContext();
        this.intro = new Intro();
        service = new ServiceFactory(render, gameFrame, intro);
        service.getGameService().setState(GameState.INTRO);
        Colors.setTheme(Theme.LEGACY);
        BooleanService.defaultToggles();
        Localization.lang.setLocale(Locale.forLanguageTag("en"));
        final int WIDTH = RenderContext.BASE_WIDTH;
        final int HEIGHT = RenderContext.BASE_HEIGHT;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Colorblindness.filter(Colors.getBackground()));
        addKeyListener(service.getKeyboard());
        addMouseListener(service.getMouse());
        addMouseMotionListener(service.getMouse());
        setFocusTraversalKeysEnabled(false);
        setFocusable(true);
        isFinished = false;
	}

    public void launch() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        Timer timer = new Timer(1000/FPS, e -> {
            update();
            updateAnimations(1.0/FPS);
            repaint();
        });
        timer.start();
    }

    public void updateAnimations(double delta) {
        service.getAnimationService().update(delta);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        render.updateTransform(getWidth(), getHeight());
        g2.translate(render.getOffsetX(), render.getOffsetY());
        g2.scale(render.getScale(), render.getScale());
        try {
            drawGame(g2);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        g2.dispose();
    }

    public void drawGame(Graphics2D g2) throws InterruptedException, IOException {
        switch (service.getGameService().getState()) {
            case MENU, SETTINGS, ACHIEVEMENTS, CHECKMATE, STALEMATE, VICTORY ->
                    service.getRender().getMenuRender().draw(g2);
            case INTRO -> service.getIntro().draw(g2);
            case BOARD -> {
                service.getRender().getBoardRender().drawBoard(g2);
                service.getRender().getMovesRender().drawMoves(g2);
                if (GameService.getGame() != Games.SANDBOX && service.getTimerService().isActive()) {
                    service.getUIService().drawTimer(g2);
                    Piece selected = service.getMovesManager().getSelectedPiece();
                    if (selected != null) {
                        service.getUIService().drawTick(g2, BooleanService.isLegal);
                    }
                }
                service.getRender().getMenuRender().draw(g2);
            }
        }
        renderAnimations(g2);
        if (BooleanService.canToggleHelp) {
            renderControls(g2);
        }
    }

    private void renderAnimations(Graphics2D g2) {
        service.getAnimationService().render(g2);
    }

    private void renderControls(Graphics2D g2) {
        service.getRender().getControlsRender().drawControlsHUD(g2);
    }

    private void update() {
        if(intro.isFinished() && !isFinished) {
            service.getGameService().setState(GameState.MENU);
            BooleanService.canPlayMusic = true;
            isFinished = true;
        }

        if(BooleanService.canPlayMusic) {
            service.getSound().playMusic();
        }

        if(!BooleanService.canPlayMusic) {
            service.getSound().stop(7);
        }

        service.getKeyboardInput().update();
        service.getMouseInput().update();
        if(!(GameService.getGame() == Games.SANDBOX)) {
            service.getTimerService().update();
        }
        PlayState mode = service.getGameService().getMode();
        if(mode != null) {
            switch(mode) {
                case PLAYER -> BooleanService.canAIPlay = false;
                case AI -> BooleanService.canAIPlay = true;
            }
        }
    }
}
