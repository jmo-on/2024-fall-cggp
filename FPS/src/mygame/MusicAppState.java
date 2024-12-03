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

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        setupBackgroundMusic();
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

    @Override
    public void cleanup() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            app.getRootNode().detachChild(backgroundMusic);
        }
        super.cleanup();
    }
}