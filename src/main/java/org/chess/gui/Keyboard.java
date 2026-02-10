package org.chess.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
    private boolean wPressedEvent = false;
    private boolean aPressedEvent = false;
    private boolean sPressedEvent = false;
    private boolean dPressedEvent = false;
    private boolean bPressedEvent = false;
    private boolean zPressedEvent = false;
    private boolean spacePressedEvent = false;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_W) {
            wPressedEvent = true;
        }

        if(key == KeyEvent.VK_A) {
            aPressedEvent = true;
        }

        if(key == KeyEvent.VK_S) {
            sPressedEvent = true;
        }

        if(key == KeyEvent.VK_D) {
            dPressedEvent = true;
        }

        if(key == KeyEvent.VK_SPACE) {
            bPressedEvent = true;
        }

        if(key == KeyEvent.VK_B) {
            spacePressedEvent = true;
        }

        if(key == KeyEvent.VK_Z) {
            zPressedEvent = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public boolean wasWPressed() {
        if(wPressedEvent) {
            wPressedEvent = false;
            return true;
        }
        return false;
    }

    public boolean wasAPressed() {
        if(aPressedEvent) {
            aPressedEvent = false;
            return true;
        }
        return false;
    }

    public boolean wasSPressed() {
        if(sPressedEvent) {
            sPressedEvent = false;
            return true;
        }
        return false;
    }

    public boolean wasDPressed() {
        if(dPressedEvent) {
            dPressedEvent = false;
            return true;
        }
        return false;
    }

    public boolean wasSpacePressed() {
        if(spacePressedEvent) {
            spacePressedEvent = false;
            return true;
        }
        return false;
    }

    public boolean wasZPressed() {
        if(zPressedEvent) {
            zPressedEvent = false;
            return true;
        }
        return false;
    }

    public boolean wasBPressed() {
        if(bPressedEvent) {
            bPressedEvent = false;
            return true;
        }
        return false;
    }
}
