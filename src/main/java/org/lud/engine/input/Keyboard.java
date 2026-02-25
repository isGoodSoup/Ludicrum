package org.lud.engine.input;

import org.lud.engine.service.BooleanService;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Keyboard implements KeyListener {
    private final Map<Integer, Boolean> keyStates;
    private final Map<Integer, Boolean> keyProcessed;
    private final Map<Integer, Boolean> keyProcessedCombo;
    private StringBuilder textBuffer = new StringBuilder();

    private boolean anyKeyPressedThisFrame = false;

    public Keyboard() {
        this.keyStates = new HashMap<>();
        this.keyProcessed = new HashMap<>();
        this.keyProcessedCombo = new HashMap<>();
    }

    public String consumeText() {
        String text = textBuffer.toString();
        textBuffer.setLength(0);
        return text;
    }

    public String getCurrentText() {
        return textBuffer.toString();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(!BooleanService.canType) { return; }
        char c = e.getKeyChar();
        if(!Character.isISOControl(c)) {
            textBuffer.append(c);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyStates.put(e.getKeyCode(), true);
        keyProcessed.putIfAbsent(e.getKeyCode(), false);
        if(BooleanService.canType) {
            if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !textBuffer.isEmpty()) {
                textBuffer.deleteCharAt(textBuffer.length() - 1);
            }
        }
        anyKeyPressedThisFrame = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyStates.put(e.getKeyCode(), false);
        keyProcessed.put(e.getKeyCode(), false);
    }

    public boolean wasPressed() {
        boolean result = anyKeyPressedThisFrame;
        anyKeyPressedThisFrame = false;
        return result;
    }

    private boolean wasKeyPressed(int keyCode) {
        if(keyProcessedCombo.getOrDefault(keyCode, false)) {
            return false;
        }

        boolean down = keyStates.getOrDefault(keyCode, false);
        boolean processed = keyProcessed.getOrDefault(keyCode, false);
        if(down && !processed) {
            keyProcessed.put(keyCode, true);
            return true;
        }
        return false;
    }

    public boolean wasCancelPressed() { return wasKeyPressed(KeyEvent.VK_C); }
    public boolean wasSelectPressed() { return wasEnterPressed() || wasSpacePressed(); }
    public boolean wasTabPressed() { return wasKeyPressed(KeyEvent.VK_TAB); }
    public boolean wasUpPressed() { return wasKeyPressed(KeyEvent.VK_UP); }
    public boolean wasLeftPressed() { return wasKeyPressed(KeyEvent.VK_LEFT); }
    public boolean wasDownPressed() { return wasKeyPressed(KeyEvent.VK_DOWN); }
    public boolean wasRightPressed() { return wasKeyPressed(KeyEvent.VK_RIGHT); }
    public boolean wasEnterPressed() { return wasKeyPressed(KeyEvent.VK_ENTER); }
    public boolean wasSpacePressed() { return wasKeyPressed(KeyEvent.VK_SPACE); }
    public boolean wasMPressed() { return wasKeyPressed(KeyEvent.VK_M); }
    public boolean wasHPressed() { return wasKeyPressed(KeyEvent.VK_H); }
    public boolean wasOnePressed() { return wasKeyPressed(KeyEvent.VK_1); }
    public boolean wasTwoPressed() { return wasKeyPressed(KeyEvent.VK_2); }
    public boolean wasThreePressed() { return wasKeyPressed(KeyEvent.VK_3); }
    public boolean wasF11Pressed() { return wasKeyPressed(KeyEvent.VK_F11); }

    private boolean isKeyDown(int keyCode) {
        return keyStates.getOrDefault(keyCode, false);
    }

    public boolean isEscapeDown() { return isKeyDown(KeyEvent.VK_ESCAPE); }

    public boolean isComboPressed(int modifierKey, int triggerKey) {
        boolean down = isKeyDown(modifierKey) && isKeyDown(triggerKey);
        boolean processed = keyProcessedCombo.getOrDefault(triggerKey, false);

        if(down && !processed) {
            keyProcessedCombo.put(triggerKey, true);
            return true;
        }

        if(!isKeyDown(modifierKey) || !isKeyDown(triggerKey)) {
            keyProcessedCombo.put(triggerKey, false);
        }

        return false;
    }
}
