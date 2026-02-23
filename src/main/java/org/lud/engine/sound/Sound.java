package org.lud.engine.sound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lud.engine.service.BooleanService;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {
    private final transient Clip[] clips = new Clip[30];
    private final transient URL[] soundURL = new URL[30];
    private final transient FloatControl[] controls = new FloatControl[30];
    private static final float[] VOLUME_LEVELS = {-80f, -40f, -32f, -24f, -18f, -12f, -8f, -4f, -2f, 0f};
    private int volumeScale = 2;

    private static final Logger log = LoggerFactory.getLogger(Sound.class);

    public Sound() {
        setSound(0, "piece-fx");
        setSound(1, "menu");
        setSound(2, "menu-select_1");
        setSound(3, "menu-select_2");
        setSound(4, "pages");
        setSound(5, "reveal");
        setSound(6, "checkmate");
        setSound(7, "main-theme");
        log.info("Loading sound FX...");
        preload();
    }

    public Clip[] getClips() {
        return clips;
    }

    public URL[] getSoundURL() {
        return soundURL;
    }

    public FloatControl[] getControls() {
        return controls;
    }

    private void setSound(int i, String name) {
        String path = "/fx/";
        soundURL[i] = getClass().getResource(path + name + ".wav");
    }

    private void preload() {
        for(int i = 0; i < soundURL.length; i++) {
            try {
                if(soundURL[i] == null) { continue; }
                AudioInputStream ais = AudioSystem.
                        getAudioInputStream(soundURL[i]);
                Clip c = AudioSystem.getClip();
                c.open(ais);
                clips[i] = c;
                controls[i] = (FloatControl)c
                        .getControl(FloatControl.Type.MASTER_GAIN);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public void play(int i) {
        Clip c = clips[i];
        if(c == null) { return; }
        if(c.isRunning()) { c.stop(); }
        c.setFramePosition(0);
        c.start();
    }

    public void loop(int i) {
        Clip c = clips[i];
        if(c == null) { return; }
        if(c.isRunning()) { c.stop(); }
        c.setFramePosition(0);
        c.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(int i) {
        Clip c = clips[i];
        if(c != null && c.isRunning()) { c.stop(); }
    }

    public void volumeUp() {
        if(volumeScale < VOLUME_LEVELS.length - 1) {
            volumeScale++;
            updateVolume();
            log.debug("Volume scale increased to {}", volumeScale);
        }
    }

    public void volumeDown() {
        if(volumeScale > 0) {
            volumeScale--;
            updateVolume();
            log.debug("Volume scale decreased to {}", volumeScale);
        }
    }

    private void updateVolume() {
        float dB = VOLUME_LEVELS[volumeScale];
        for(FloatControl c : controls) {
            if(c != null) {
                float min = c.getMinimum();
                float max = c.getMaximum();

                float clamped = Math.max(min, Math.min(max, dB));
                c.setValue(clamped);
            }
        }
    }

    public synchronized void playFX(int index) {
        if(!BooleanService.canPlayFX) { return; }
        Clip clip = clips[index];
        if(clip.isRunning()) { clip.stop(); }
        clip.setFramePosition(0);
        clip.start();
    }

    public synchronized void playMusic() {
        if(!BooleanService.canPlayMusic) { return; }
        Clip c = clips[7];
        if(c == null) { return; }

        if(!c.isRunning()) {
            c.setFramePosition(0);
            c.loop(Clip.LOOP_CONTINUOUSLY);
        }

        updateVolume();
    }
}