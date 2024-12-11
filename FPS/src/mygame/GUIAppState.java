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

public class GUIAppState extends AbstractAppState {

    private Main app;
    private BitmapText hudText;
    private BitmapText gameCompleteText;
    private int score = 0;
    private static final int WIN_SCORE = 10;
    private boolean gameCompleted = false;

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
    }

    @Override
    public void cleanup() {
        if (hudText != null) {
            app.getGuiNode().detachChild(hudText);
        }
        if (gameCompleteText != null) {
            app.getGuiNode().detachChild(gameCompleteText);
        }
        super.cleanup();
    }
}
