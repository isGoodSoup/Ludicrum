package org.vertex.engine.entities;

import org.vertex.engine.interfaces.Clickable;
import org.vertex.engine.service.GameService;

public class Button implements Clickable {
    private int x;
    private int y;
    private int width;
    private int height;
    private Runnable action;

    public Button(int x, int y, int width, int height, Runnable action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.action = action;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void onClick(GameService gameService) {
        action.run();
    }
}
