// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;

public class MusicAppState extends AbstractAppState {
    private SimpleApplication app;
    private AudioNode backgroundMusic;
    private float musicVolume = 0.5f; // Adjustable volume level
    private AudioNode footstepSound;
    private boolean isFootstepPlaying = false;
    private float footstepVolume = 0.3f;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        setupBackgroundMusic();
        setupSoundEffects();
    }

    private void setupBackgroundMusic() {
        try {
            // Create audio node for background music
            backgroundMusic = new AudioNode(app.getAssetManager(), "Sounds/background_music.wav", DataType.Stream);
            backgroundMusic.setLooping(true);  // Make it loop continuously
            backgroundMusic.setPositional(false);  // Make it non-positional (plays at same volume everywhere)
            backgroundMusic.setVolume(musicVolume);
            app.getRootNode().attachChild(backgroundMusic);
            
            // Start playing immediately
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    private void setupSoundEffects() {
        try {
            // Create audio node for footsteps
            footstepSound = new AudioNode(app.getAssetManager(), "Sounds/footstep.wav", DataType.Buffer);
            footstepSound.setPositional(false);
            footstepSound.setLooping(false);
            footstepSound.setVolume(footstepVolume);
            app.getRootNode().attachChild(footstepSound);
        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }

    // Methods to control music
    public void pauseMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }

    public void playMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }

    public void setVolume(float volume) {
        if (backgroundMusic != null) {
            musicVolume = Math.max(0f, Math.min(1f, volume)); // Clamp between 0 and 1
            backgroundMusic.setVolume(musicVolume);
        }
    }

    public void playFootstep() {
        if (footstepSound != null && !isFootstepPlaying) {
            footstepSound.playInstance();
            isFootstepPlaying = true;
            // Reset flag after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(400); // 0.4 seconds
                    isFootstepPlaying = false;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    public void stopFootstep() {
        if (footstepSound != null) {
            footstepSound.stop();
            isFootstepPlaying = false;
        }
    }

    @Override
    public void cleanup() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            app.getRootNode().detachChild(backgroundMusic);
        }
        if (footstepSound != null) {
            footstepSound.stop();
            app.getRootNode().detachChild(footstepSound);
        }
        super.cleanup();
    }
}