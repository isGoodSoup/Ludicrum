package org.chess.input;

import org.chess.entities.Board;
import org.chess.render.MenuRender;
import org.chess.service.BoardService;
import org.chess.service.GUIService;
import org.chess.service.GameService;

import java.awt.*;

public class MenuInput {
    private final GameService gameService;
    private final BoardService boardService;
    private final MoveManager moveManager;
    private final GUIService guiService;
    private final Mouse mouse;
    private final MenuRender menuRender;

    public MenuInput(MenuRender menuRender, GUIService guiService,
                     GameService gameService, BoardService boardService,
                     MoveManager moveManager, Mouse mouse) {
        this.gameService = gameService;
        this.boardService = boardService;
        this.moveManager = moveManager;
        this.guiService = guiService;
        this.mouse = mouse;
        this.menuRender = menuRender;
    }

    public boolean updatePage() {
        int itemsPerPage = 8;
        int totalPages = (MenuRender.optionsTweaks.length - 1 + itemsPerPage - 1) / itemsPerPage;
        int newPage = moveManager.getSelectedIndexX() / itemsPerPage + 1;
        newPage = Math.max(1, Math.min(newPage, totalPages));

        if(newPage != menuRender.getCurrentPage()) {
            menuRender.setCurrentPage(newPage);
            moveManager.setSelectedIndexY(0);
            return true;
        }
        return false;
    }

    public void previousPage() {
        int itemsPerPage = 8;
        int currentIndex = moveManager.getSelectedIndexX();

        if(currentIndex >= itemsPerPage) {
            moveManager.setSelectedIndexX(currentIndex - itemsPerPage);
        }
        updatePage();
    }

    public void nextPage() {
        int itemsPerPage = 8;
        int currentIndex = moveManager.getSelectedIndexX();

        if(currentIndex + itemsPerPage < MenuRender.optionsTweaks.length) {
            moveManager.setSelectedIndexX(currentIndex + itemsPerPage);
        }
        updatePage();
    }


    public void handleOptionsInput() {
        if(!mouse.wasPressed()) { return; }

        int lineHeight = menuRender.getFontMetrics().getHeight() + 4;
        int y = MenuRender.getOPTION_Y() + lineHeight;

        int boardWidth = Board.getSquare() * 8;
        int totalWidth = boardWidth + 2 * GUIService.getEXTRA_WIDTH();
        int centerX = totalWidth / 2;
        int toggleWidth = menuRender.getSprite(0).getWidth() / 2;
        int toggleHeight = menuRender.getSprite(0).getHeight() / 2;
        int toggleX = centerX + 200;

        for(int i = 1; i < MenuRender.optionsTweaks.length; i++) {
            String option = MenuRender.optionsTweaks[i];
            String enabledOption = MenuRender.getENABLE() + option;
            int textX = MenuRender.getOPTION_X();
            int textY = y;
            int textWidth = menuRender.getFontMetrics().stringWidth(enabledOption);

            Rectangle toggleHitbox = new Rectangle(
                    toggleX,
                    textY - toggleHeight + 16,
                    toggleWidth,
                    toggleHeight
            );

            if(toggleHitbox.contains(mouse.getX(), mouse.getY())) {
                guiService.getFx().play(0);
                menuRender.toggleOption(option);
                break;
            }
            y += lineHeight;
        }
    }

    public void handleMenuInput() {
        if(!mouse.wasPressed()) { return; }

        int startY = GUIService.getHEIGHT()/2 + GUIService.getMENU_START_Y();
        int spacing = GUIService.getMENU_SPACING();

        for(int i = 0; i < MenuRender.optionsMenu.length; i++) {
            int y = startY + i * spacing;
            boolean isHovered =
                    GUIService.getHITBOX(menuRender.getOFFSET_X(), y,
                            200, 40).contains(mouse.getX(),
                            mouse.getY());

            if(isHovered) {
                guiService.getFx().play(0);
                switch (i) {
                    case 0 -> gameService.startNewGame();
                    case 1 -> gameService.optionsMenu();
                    case 2 -> System.exit(0);
                }
                break;
            }
        }
    }
}
