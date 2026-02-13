package org.chess.render;

import org.chess.entities.Achievement;
import org.chess.enums.Achievements;
import org.chess.service.GUIService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class AchievementSprites {
    private static final Map<Achievements, BufferedImage> SPRITES =
            new EnumMap<>(Achievements.class);
    private final GUIService guiService;
    private static BufferedImage defaultSprite;

    public AchievementSprites(GUIService guiService) {
        this.guiService = guiService;
        loadSprites();
    }

    private void loadSprites() {
        try {
            defaultSprite = guiService.getImage("/achievements/a00_128x128");
        } catch (IOException e) {
            System.err.println("Default achievement sprite missing!");
            defaultSprite = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        }

        for (Achievements type : Achievements.values()) {
            String path = "/achievements/" + type.getFile();
            load(type, path);
        }
    }

    private void load(Achievements type, String path) {
        try {
            SPRITES.put(type, guiService.getImage(path));
        } catch (IOException e) {
            System.out.println("Missing achievement sprite: " + path);
            SPRITES.put(type, defaultSprite);
        }
    }

    public static BufferedImage getSprite(Achievement achievement) {
        BufferedImage sprite = SPRITES.getOrDefault(achievement.getId(), defaultSprite);
        if (sprite == null) {
            sprite = defaultSprite;
        }

        if (!achievement.isUnlocked()) {
            sprite = AchievementLock.filter(sprite);
        }
        return sprite;
    }

    public static BufferedImage getDefaultSprite() {
        return defaultSprite;
    }
}

