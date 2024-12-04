// Yongjae Lee
// Jin Hong Moon
// Kerry Wang
package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;

public class AnimatedModelAppState extends ModelLoaderAppState {

    private AnimComposer animComposer;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Load the animated model
        Spatial animatedModel = this.app.getAssetManager().loadModel("Models/AnimatedModel.j3o");

        // Get the AnimComposer from the model
        animComposer = animatedModel.getControl(AnimComposer.class);
        // Play an animation
        if (animComposer != null) {
            animComposer.setCurrentAction("Walk");
        }

        // Attach to scene
        this.app.getRootNode().attachChild(animatedModel);
    }
}
