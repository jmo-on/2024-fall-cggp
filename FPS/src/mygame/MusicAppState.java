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
    private float musicVolume = 0.5f;
    private AudioNode footstepSound;
    private boolean isFootstepPlaying = false;
    private float footstepVolume = 0.3f;

    // Shooting sound nodes
    private AudioNode gunshotSound;
    private AudioNode reloadSound;
    private AudioNode emptyGunSound;
    
    // Volume levels
    private float gunshotVolume = 0.4f;
    private float reloadVolume = 0.5f;
    private float emptyGunVolume = 0.3f;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        setupBackgroundMusic();
        setupSoundEffects();
    }

    private void setupBackgroundMusic() {
        try {
            backgroundMusic = new AudioNode(app.getAssetManager(), "Sounds/background_music.wav", DataType.Stream);
            backgroundMusic.setLooping(true);
            backgroundMusic.setPositional(false);
            backgroundMusic.setVolume(musicVolume);
            app.getRootNode().attachChild(backgroundMusic);
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    private void setupSoundEffects() {
        try {
            // Footstep sound
            footstepSound = new AudioNode(app.getAssetManager(), "Sounds/footstep.wav", DataType.Buffer);
            footstepSound.setPositional(false);
            footstepSound.setLooping(false);
            footstepSound.setVolume(footstepVolume);
            app.getRootNode().attachChild(footstepSound);

            // Gunshot sound
            gunshotSound = new AudioNode(app.getAssetManager(), "Sounds/gunshot.wav", DataType.Buffer);
            gunshotSound.setPositional(false);
            gunshotSound.setLooping(false);
            gunshotSound.setVolume(gunshotVolume);
            app.getRootNode().attachChild(gunshotSound);

            // Single reload sound
            reloadSound = new AudioNode(app.getAssetManager(), "Sounds/reload.wav", DataType.Buffer);
            reloadSound.setPositional(false);
            reloadSound.setLooping(false);
            reloadSound.setVolume(reloadVolume);
            app.getRootNode().attachChild(reloadSound);

            // Empty gun click sound
            emptyGunSound = new AudioNode(app.getAssetManager(), "Sounds/empty_gun.wav", DataType.Buffer);
            emptyGunSound.setPositional(false);
            emptyGunSound.setLooping(false);
            emptyGunSound.setVolume(emptyGunVolume);
            app.getRootNode().attachChild(emptyGunSound);

        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }

    // Existing music methods
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
            musicVolume = Math.max(0f, Math.min(1f, volume));
            backgroundMusic.setVolume(musicVolume);
        }
    }

    // Footstep methods
    public void playFootstep() {
        if (footstepSound != null && !isFootstepPlaying) {
            footstepSound.playInstance();
            isFootstepPlaying = true;
            new Thread(() -> {
                try {
                    Thread.sleep(400);
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

    // Gun sound methods
    public void playGunshot() {
        if (gunshotSound != null) {
            gunshotSound.playInstance();
        }
    }

    public void playReload() {
        if (reloadSound != null) {
            reloadSound.playInstance();
        }
    }

    public void playEmptyGun() {
        if (emptyGunSound != null) {
            emptyGunSound.playInstance();
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
        if (gunshotSound != null) {
            gunshotSound.stop();
            app.getRootNode().detachChild(gunshotSound);
        }
        if (reloadSound != null) {
            reloadSound.stop();
            app.getRootNode().detachChild(reloadSound);
        }
        if (emptyGunSound != null) {
            emptyGunSound.stop();
            app.getRootNode().detachChild(emptyGunSound);
        }
        super.cleanup();
    }
}