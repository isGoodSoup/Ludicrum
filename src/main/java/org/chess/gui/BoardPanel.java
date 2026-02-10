package org.chess.gui;

import org.chess.enums.GameState;
import org.chess.enums.PlayState;
import org.chess.render.MenuRender;
import org.chess.service.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class BoardPanel extends JPanel implements Runnable {
	@Serial
    private static final long serialVersionUID = -5189356863277669172L;
	private final int FPS = 60;
	private Thread thread;
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
                AnimationService.animateMove();
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
    }

    private void update() {
        checkKeyboard();
        switch(GameService.getState()) {
            case MENU -> {
                service.getGuiService().getMenuRender().handleMenuInput(true);
                return;
            }
            case MODE -> {
                GameService.setMode();
                return;
            }
            case RULES -> {
                service.getGuiService().getMenuRender().handleOptionsInput(true);
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
        MenuRender menu = service.getGuiService().getMenuRender();
        Keyboard keyboard = service.getKeyboard();
        GameState state = GameService.getState();

        // Global back button
        if((state == GameState.RULES || state == GameState.MODE) && keyboard.wasBPressed()) {
            GameService.setState(GameState.MENU);
        }

        // Handle input per game state
        switch(state) {
            case MENU -> {
                if(keyboard.wasUpPressed()) menu.moveUp(MenuRender.optionsMenu);
                if(keyboard.wasDownPressed()) menu.moveDown(MenuRender.optionsMenu);
                if(keyboard.wasSelectPressed()) menu.activate(GameState.MENU);
            }
            case MODE -> {
                if(keyboard.wasUpPressed()) menu.moveUp(MenuRender.optionsMode);
                if(keyboard.wasDownPressed()) menu.moveDown(MenuRender.optionsMode);
                if(keyboard.wasSelectPressed()) menu.activate(GameState.MODE);
            }
            case RULES -> {
                if(keyboard.wasUpPressed()) menu.moveUp(MenuRender.optionsTweaks);
                if(keyboard.wasDownPressed()) menu.moveDown(MenuRender.optionsTweaks);
                if(keyboard.wasSelectPressed()) menu.activate(GameState.RULES);
            }
            case BOARD -> {

            }
        }
    }
}
