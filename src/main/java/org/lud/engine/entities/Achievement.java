package org.lud.engine.entities;

import org.lud.engine.enums.Achievements;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.service.GameService;

public class Achievement implements Clickable {
    private Achievements id;
    private boolean isUnlocked;

    public Achievement(Achievements id) {
        this.id = id;
    }

    public Achievements getId() {
        return id;
    }

    public void setId(Achievements id) {
        this.id = id;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.isUnlocked = unlocked;
    }

    @Override
    public void onClick(GameService gameService) {}
}
