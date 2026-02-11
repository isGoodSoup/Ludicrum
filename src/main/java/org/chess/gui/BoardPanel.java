package org.chess.gui;

import org.chess.enums.ColorblindType;
import org.chess.enums.GameState;
import org.chess.enums.PlayState;
import org.chess.input.Keyboard;
import org.chess.input.MoveManager;
import org.chess.render.MenuRender;
import org.chess.service.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serial;

public class BoardPanel extends JPanel implements Runnable {
	@Serial
    private static final long serialVersionUID = -5189356863277669172L;
	private final int FPS = 60;
	private Thread thread;
    private long lastUpTime = 0;
    private long lastDownTime = 0;
    private long lastLeftTime = 0;
    private long lastRightTime = 0;
    private final long repeatDelay = 150;

    private static ServiceFactory service;

	public BoardPanel() {
        super();
        service = new ServiceFactory();
        GameService.setState(GameState.MENU);
        BooleanService.defaultToggles();
        final int WIDTH = GUIService.getWIDTH();
        final int HEIGHT = GUIService.getHEIGHT();
        MenuRender.drawRandomBackground(BooleanService.getBoolean());
        setPreferredSize(new Dimension(WIDTH +
                GUIService.getEXTRA_WIDTH(), HEIGHT));
        setBackground(GUIService.getNewBackground());
        addMouseMotionListener(service.getMouseService());
        addMouseListener(service.getMouseService());
        addKeyListener(service.getKeyboard());
        setFocusable(true);
        requestFocusInWindow();
	}

    public void launch() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(thread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                service.getAnimationService().update();
                service.getMouseService().update();
                repaint();
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch(GameService.getState()) {
            case MENU -> service.getGuiService().getMenuRender().drawGraphics(g2,
                    MenuRender.optionsMenu);
            case MODE -> service.getGuiService().getMenuRender().drawGraphics(g2,
                    MenuRender.optionsMode);
            case RULES -> service.getGuiService().getMenuRender()
                    .drawOptionsMenu(g2, MenuRender.optionsTweaks);
            case BOARD -> {
                service.getGuiService().getBoardRender().drawBoard(g2);
                service.getGuiService().getMovesRender().drawMoves(g2);
            }
        }

        if(service.getTimerService().isActive()) {
            service.getGuiService().drawTimer(g2);
            service.getGuiService().drawTick(g2, BooleanService.isLegal);
        }
    }

    private void update() {
        checkKeyboard();
        service.getTimerService().update();
        service.getBoardService().resetBoard();

        switch(GameService.getState()) {
            case MENU -> {
                service.getGuiService().getMenuRender().getMenuInput().handleMenuInput();
                return;
            }
            case MODE -> {
                GameService.setMode();
                return;
            }
            case RULES -> {
                service.getGuiService().getMenuRender().getMenuInput().handleOptionsInput();
                return;
            }
            default -> service.getBoardService().getGame();
        }

        PlayState mode = GameService.getMode();
        if(mode != null) {
            switch(mode) {
                case PLAYER -> BooleanService.isAIPlaying = false;
                case AI -> BooleanService.isAIPlaying = true;
            }
        }
    }

    private void checkKeyboard() {
        long now = System.currentTimeMillis();
        MoveManager move = BoardService.getManager();
        Keyboard keyboard = service.getKeyboard();
        GameState state = GameService.getState();

        if((state == GameState.RULES || state == GameState.MODE)
                && keyboard.wasBPressed()) {
            GameService.setState(GameState.MENU);
            service.getGuiService().getFx().playFX(3);
        }

        if(BooleanService.canBeColorblind) {
            if(keyboard.wasOnePressed()) { MenuRender.setCb(ColorblindType.PROTANOPIA); }
            if(keyboard.wasTwoPressed()) { MenuRender.setCb(ColorblindType.DEUTERANOPIA); }
            if(keyboard.wasThreePressed()) { MenuRender.setCb(ColorblindType.TRITANOPIA); }
        }

        switch(state) {
            case MENU -> {
                if(keyboard.wasSelectPressed()) { move.activate(GameState.MENU); }
                if(keyboard.isUpDown() && now - lastUpTime >= repeatDelay) {
                    move.moveUp(MenuRender.optionsMenu);
                    lastUpTime = now;
                }
                if(keyboard.isDownDown() && now - lastDownTime >= repeatDelay) {
                    move.moveDown(MenuRender.optionsMenu);
                    lastDownTime = now;
                }
            }
            case MODE -> {
                if(keyboard.wasSelectPressed()) { move.activate(GameState.MODE); }
                if(keyboard.isUpDown() && now - lastUpTime >= repeatDelay) {
                    move.moveUp(MenuRender.optionsMode);
                    lastUpTime = now;
                }
                if(keyboard.isDownDown() && now - lastDownTime >= repeatDelay) {
                    move.moveDown(MenuRender.optionsMode);
                    lastDownTime = now;
                }
            }
            case RULES -> {
                if(keyboard.wasSelectPressed()) { move.activate(GameState.RULES); }
                if(keyboard.isUpDown() && now - lastUpTime >= repeatDelay) {
                    move.moveUp(MenuRender.optionsTweaks);
                    lastUpTime = now;
                }
                if(keyboard.isDownDown() && now - lastDownTime >= repeatDelay) {
                    move.moveDown(MenuRender.optionsTweaks);
                    lastDownTime = now;
                }
                if(keyboard.isLeftDown() && now - lastDownTime >= repeatDelay) {
                    move.moveLeft(MenuRender.optionsTweaks);
                    lastDownTime = now;
                }
                if(keyboard.isRightDown() && now - lastDownTime >= repeatDelay) {
                    move.moveRight(MenuRender.optionsTweaks);
                    lastDownTime = now;
                }
            }
            case BOARD -> {
                if(keyboard.wasSelectPressed()) { move.activate(GameState.BOARD); }
                if(keyboard.isUpDown() && now - lastUpTime >= repeatDelay) {
                    move.moveUp();
                    move.updateKeyboardHover();
                    lastUpTime = now;
                }
                if(keyboard.isDownDown() && now - lastDownTime >= repeatDelay) {
                    move.moveDown();
                    move.updateKeyboardHover();
                    lastDownTime = now;
                }
                if(keyboard.isLeftDown() && now - lastLeftTime >= repeatDelay) {
                    move.moveLeft();
                    move.updateKeyboardHover();
                    lastLeftTime = now;
                }
                if(keyboard.isRightDown() && now - lastRightTime >= repeatDelay) {
                    move.moveRight();
                    move.updateKeyboardHover();
                    lastRightTime = now;
                }
                if(keyboard.isComboPressed(KeyEvent.VK_CONTROL,
                        KeyEvent.VK_Z) && BooleanService.canUndoMoves) {
                    move.undoLastMove(move.getSelectedPiece());
                    lastRightTime = now;
                }
            }
        }

        if(keyboard.isComboPressed(KeyEvent.VK_CONTROL, KeyEvent.VK_Q)) {
            System.exit(0);
        }
    }
}
