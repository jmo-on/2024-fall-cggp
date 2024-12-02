package mygame.core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public abstract class BaseGameState extends AbstractAppState {
    protected EventBus eventBus;
    protected SimpleApplication app;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.eventBus = EventBus.getInstance();
        initializeState();
    }
    
    protected abstract void initializeState();
    
    @Override
    public void cleanup() {
        cleanupState();
        super.cleanup();
    }
    
    protected abstract void cleanupState();
} 