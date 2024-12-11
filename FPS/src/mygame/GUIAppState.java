// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapText;
import com.jme3.font.BitmapFont;
import com.jme3.app.SimpleApplication;
import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import mygame.core.EventBus;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import com.jme3.scene.Spatial;

public class GUIAppState extends AbstractAppState {

    private Main app;
    private BitmapText hudText;
    private BitmapText gameCompleteText;
    private int score = 0;
    private static final int WIN_SCORE = 10;
    private boolean gameCompleted = false;
    private Node healthNode;
    private Picture[] hearts;
    private static final int MAX_HEALTH = 3;
    private int currentHealth = MAX_HEALTH;
    private boolean isInvincible = false;
    private float invincibilityTimer = 0f;
    private static final float INVINCIBILITY_DURATION = 2f;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (Main) app;

        // Access guiFont via the public getter
        BitmapFont guiFont = this.app.getGuiFont();

        // Create score text
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ColorRGBA forestGreen = new ColorRGBA(0.02f, 0.2f, 0.02f, 1f);
        hudText.setColor(forestGreen);
        hudText.setText("Targets Destroyed: 0 / " + WIN_SCORE);
        hudText.setLocalTranslation(10, this.app.getCamera().getHeight() - 10, 0);

        // Create game complete text (initially hidden)
        gameCompleteText = new BitmapText(guiFont, false);
        gameCompleteText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        gameCompleteText.setText("Game Complete!");
        gameCompleteText.setColor(ColorRGBA.Yellow);
        gameCompleteText.setLocalTranslation(
            (this.app.getCamera().getWidth() - gameCompleteText.getLineWidth()) / 2,
            this.app.getCamera().getHeight() / 2,
            0);
        gameCompleteText.setCullHint(com.jme3.scene.Spatial.CullHint.Always);

        // Attach to guiNode
        this.app.getGuiNode().attachChild(hudText);
        this.app.getGuiNode().attachChild(gameCompleteText);

        // Reset score before subscribing to events
        score = 0;
        
        // Subscribe to target destruction events
        EventBus.getInstance().subscribe("TARGET_DESTROYED", event -> {
            if (event.getData("target") != null) {  // Only count if target exists
                updateScore(score + 1);
            }
        });

        // Initialize health before setting up display
        currentHealth = MAX_HEALTH;  // Make sure it's 3 when we start
        System.out.println("Initial health set to: " + currentHealth);
        
        setupHealthDisplay();
        updateHealthDisplay();  // Make sure display matches initial health
        
        // Subscribe to player damage events
        EventBus.getInstance().subscribe("PLAYER_DAMAGED", event -> {
            decreaseHealth();
        });
    }

    public void updateScore(int newScore) {
        this.score = newScore;
        hudText.setText("Targets Destroyed: " + score + " / " + WIN_SCORE);

        if (score >= WIN_SCORE) {
            gameComplete();
        }
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    private void gameComplete() {
        gameCompleted = true;
        gameCompleteText.setCullHint(com.jme3.scene.Spatial.CullHint.Never);
        
        // Disable shooting and pause menu
        app.getStateManager().getState(ShootAppState.class).setEnabled(false);
        app.getStateManager().getState(TargetAppState.class).setEnabled(false);
        
        // Update pause menu text
        app.getStateManager().getState(PauseMenuState.class).updateMenuForGameEnd();
    }

    private void handleGameOver() {
        // Disable shooting and target states
        app.getStateManager().getState(ShootAppState.class).setEnabled(false);
        app.getStateManager().getState(TargetAppState.class).setEnabled(false);
        
        // Show game over text
        gameCompleteText.setText("Game Over!");
        gameCompleteText.setCullHint(Spatial.CullHint.Never);
        
        // Update pause menu text
        app.getStateManager().getState(PauseMenuState.class).updateMenuForGameEnd();
    }
    
    public void resetHealth() {
        System.out.println("Resetting health to MAX_HEALTH: " + MAX_HEALTH);
        currentHealth = MAX_HEALTH;
        updateHealthDisplay();
    }

    public void resetGame() {
        score = 0;
        gameCompleted = false;
        hudText.setText("Targets Destroyed: 0 / " + WIN_SCORE);
        gameCompleteText.setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        
        // Re-enable states
        app.getStateManager().getState(ShootAppState.class).setEnabled(true);
        app.getStateManager().getState(TargetAppState.class).setEnabled(true);
        
        // Reset targets
        app.getStateManager().getState(TargetAppState.class).resetTargets();
        resetHealth();
        
        // Hide game over text
        gameCompleteText.setCullHint(Spatial.CullHint.Always);

        // Reset shooting delay for all targets
        TargetAppState targetAppState = app.getStateManager().getState(TargetAppState.class);
        Node targetNode = targetAppState.getTargetNode();
        if (targetNode != null) {
            for (Spatial target : targetNode.getChildren()) {
                TargetControl targetControl = target.getControl(TargetControl.class);
                if (targetControl != null) {
                    targetControl.resetShootingDelay();
                }
            }
        }
    }

    @Override
    public void cleanup() {
        if (hudText != null) {
            app.getGuiNode().detachChild(hudText);
        }
        if (gameCompleteText != null) {
            app.getGuiNode().detachChild(gameCompleteText);
        }
        if (healthNode != null) {
            app.getGuiNode().detachChild(healthNode);
        }
        super.cleanup();
    }

    private void setupHealthDisplay() {
        System.out.println("Setting up health display with current health: " + currentHealth);
        healthNode = new Node("HealthDisplay");
        hearts = new Picture[MAX_HEALTH];
        
        // Create hearts
        for (int i = 0; i < MAX_HEALTH; i++) {
            hearts[i] = new Picture("Heart" + i);
            hearts[i].setImage(app.getAssetManager(), "Interface/heart.png", true);
            hearts[i].setWidth(40);
            hearts[i].setHeight(40);
            hearts[i].setPosition(
                app.getCamera().getWidth() - (MAX_HEALTH - i) * (40 + 10) - 10,
                app.getCamera().getHeight() - 50
            );
            healthNode.attachChild(hearts[i]);
        }
        
        app.getGuiNode().attachChild(healthNode);
        updateHealthDisplay();  // Make sure hearts are visible initially
    }
    
    public void decreaseHealth() {
        if (!isInvincible && currentHealth > 0) {
            System.out.println("decreaseHealth called. Current health before decrease: " + currentHealth);
            currentHealth--;
            System.out.println("Health decreased. New health: " + currentHealth);
            updateHealthDisplay();
            
            // Activate invincibility
            isInvincible = true;
            invincibilityTimer = 0f;
            System.out.println("Invincibility started");
            
            if (currentHealth <= 0) {
                System.out.println("Health reached zero, calling handleGameOver");
                handleGameOver();
            }
        }
    }
    
    private void updateHealthDisplay() {
        System.out.println("Updating health display. Current health: " + currentHealth);
        for (int i = 0; i < MAX_HEALTH; i++) {
            hearts[i].setCullHint(i < currentHealth ? Spatial.CullHint.Never : Spatial.CullHint.Always);
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (isInvincible) {
            invincibilityTimer += tpf;
            if (invincibilityTimer >= INVINCIBILITY_DURATION) {
                isInvincible = false;
                invincibilityTimer = 0f;
                System.out.println("Invincibility ended");
            }
        }
    }
}
