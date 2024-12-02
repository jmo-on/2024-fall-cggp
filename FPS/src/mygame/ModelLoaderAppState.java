package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;

public class ModelLoaderAppState extends AbstractAppState {

    protected SimpleApplication app;
    protected Spatial model;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
    }
}
