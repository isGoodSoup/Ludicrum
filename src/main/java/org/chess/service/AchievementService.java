package org.chess.service;

import org.chess.entities.Achievement;
import org.chess.enums.Achievements;

import java.util.*;

public class AchievementService {
    private Map<Achievements, Achievement> achievements;
    private List<Achievement> achievementList;

    public AchievementService() {
        achievements = new HashMap<>();
        for(Achievements type : Achievements.values()) {
            achievements.put(type, new Achievement(type));
        }
        achievementList = new ArrayList<>(achievements.values());
    }

    public void unlockAchievement(Achievements type) {
        Achievement achievement = achievements.get(type);
        if(achievement != null && !achievement.isUnlocked()) {
            unlock(achievement);
            System.out.println("Achievement Unlocked: " + type.getTitle());
        }
    }

    public void unlockAllAchievements() {
        for(Map.Entry<Achievements, Achievement> a
                : achievements.entrySet()) {
            a.getValue().setUnlocked(true);
        }
    }

    public void lockAllAchievements() {
        for(Map.Entry<Achievements, Achievement> a
                : achievements.entrySet()) {
            a.getValue().setUnlocked(false);
        }
    }

    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }

    public List<Achievement> getAchievementList() {
        achievementList.sort(Comparator.comparingInt(a -> a.getId().ordinal()));
        return achievementList;
    }

    public Collection<Achievement> getUnlockedAchievements() {
        return achievements.values()
                .stream()
                .filter(Achievement::isUnlocked)
                .toList();
    }

    private void unlock(Achievement achievement) {
        achievement.setUnlocked(true);
    }
}
