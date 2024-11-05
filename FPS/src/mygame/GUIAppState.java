package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapText;
import com.jme3.font.BitmapFont;
import com.jme3.app.SimpleApplication;
import com.jme3.app.Application;

public class GUIAppState extends AbstractAppState {

    private Main app;
    private BitmapText hudText;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (Main) app;

        // Access guiFont via the public getter
        BitmapFont guiFont = this.app.getGuiFont();

        // Create a new BitmapText object
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setText("Score: 0");
        hudText.setLocalTranslation(10, this.app.getCamera().getHeight() - 10, 0);

        // Attach to guiNode
        this.app.getGuiNode().attachChild(hudText);
    }

    public void updateScore(int score) {
        hudText.setText("Score: " + score);
    }
}
