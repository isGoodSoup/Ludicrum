package org.lud.engine.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
    private int x, y;
    private boolean wasPressed;
    private boolean wasJustPressed;

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

    public boolean wasPressed() {
        return wasPressed;
    }

    public boolean wasJustPressed() { return wasJustPressed; }

    public void setWasPressed(boolean wasPressed) {
        this.wasPressed = wasPressed;
    }

    public void reset() { wasJustPressed = false; }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if(!wasPressed) {
            wasJustPressed = true;
        }
        wasPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        wasPressed = false;
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        x = e.getX();
        y = e.getY();
    }
}
