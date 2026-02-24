package org.lud.engine.render.menu;

import org.lud.engine.enums.GameState;
import org.lud.engine.util.Colors;
import org.lud.engine.interfaces.State;
import org.lud.engine.interfaces.UI;
import org.lud.engine.render.Colorblindness;
import org.lud.engine.render.RenderContext;
import org.lud.engine.service.GameService;
import org.lud.engine.service.UIService;

import java.awt.*;

public class Checkmate implements UI {
    private final UIService uiService;
    private final GameService gameService;
    private final RenderContext render;
    private final int totalWidth;

    public Checkmate(UIService uiService, GameService gameService,
                     RenderContext render, int totalWidth) {
        this.uiService = uiService;
        this.gameService = gameService;
        this.render = render;
        this.totalWidth = totalWidth;
    }

    private int getCenterX(int containerWidth, int contentWidth) {
        return (containerWidth - contentWidth)/2;
    }

    @Override
    public void drawMenu(Graphics2D g2) {
        draw(g2);
    }

    @Override
    public boolean canDraw(State state) {
        return state == GameState.CHECKMATE;
    }

    public void draw(Graphics2D g2) {
        if(gameService.getState() != GameState.CHECKMATE
                || gameService.getState() != GameState.VICTORY) {
            return;
        }

        g2.setFont(UIService.getFont(UIService.getMENU_FONT()));
        FontMetrics fm = g2.getFontMetrics();

        int headerY = render.getOffsetY() + render.scale(200);
        int headerWidth = fm.stringWidth("CHECKMATE");

        g2.setColor(Colorblindness.filter(Colors.getForeground()));
        String text = gameService.getState() == GameState.CHECKMATE ?
                "CHECKMATE" : "STALEMATE";
        g2.drawString(text, getCenterX(totalWidth, headerWidth), headerY);
    }
}