package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.app.Application;

public class LightingAppState extends AbstractAppState {

    private Main app;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (Main) app;

        // Add ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.5f));
        this.app.getRootNode().addLight(ambient);

        // Add directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        this.app.getRootNode().addLight(sun);
    }
}
