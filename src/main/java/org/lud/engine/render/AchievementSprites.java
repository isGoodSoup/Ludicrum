package org.lud.engine.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lud.engine.entities.Achievement;
import org.lud.engine.enums.Achievements;
import org.lud.engine.service.UIService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class AchievementSprites {
    private static final Map<Achievements, BufferedImage> SPRITES =
            new EnumMap<>(Achievements.class);
    private static BufferedImage defaultSprite;

    private static final Logger log =
            LoggerFactory.getLogger(AchievementSprites.class);

    public AchievementSprites() {
        loadSprites();
    }

    private void loadSprites() {
        log.info("Loading achievement sprites...");
        try {
            defaultSprite = UIService.getImage("/achievements/a00_256x");
        } catch (IOException e) {
            log.error("Default achievement sprite missing!");
            defaultSprite = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        }

        for (Achievements type : Achievements.values()) {
            String path = "/achievements/" + type.getFile();
            load(type, path);
        }
    }

    private void load(Achievements type, String path) {
        try {
            SPRITES.put(type, UIService.getImage(path));
        } catch (IOException e) {
            log.error("Missing achievement sprite: {}", path);
            SPRITES.put(type, defaultSprite);
        }
    }

    public static BufferedImage getSprite(Achievement achievement) {
        BufferedImage sprite = SPRITES.getOrDefault(achievement.getId(), defaultSprite);
        if (sprite == null) {
            sprite = defaultSprite;
        }

        if (!achievement.isUnlocked()) {
            sprite = AchievementLock.filter(sprite, achievement.isUnlocked());
        }
        return sprite;
    }

    public static BufferedImage getDefaultSprite() {
        return defaultSprite;
    }
}

