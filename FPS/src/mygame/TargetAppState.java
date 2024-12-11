// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import java.util.Random;
import mygame.core.EventListener;
import mygame.core.EventBus;
import mygame.GameEvent;

/**
 * TargetAppState
 * Target control for the game
 */
public class TargetAppState extends AbstractAppState {

    private SimpleApplication app;
    private Node targetNode;
    private final int TARGET_COUNT = 5; // Number of targets to maintain
    private final float SPAWN_AREA = 20f; // Size of spawn area
    private Random random = new Random();
    private GameMode currentMode = GameMode.MEDIUM; // Default game mode
    private final float EASY_HEIGHT = 1.7f;  // Changed from 3f to 1.7f
    private final float MEDIUM_SPEED = 2f;  // Movement speed for medium mode
    private final float HARD_SPEED = 4f;   // Movement speed for hard mode
    private BulletAppState bulletAppState;

    /**
     * Constructor
     * @param app Application (SimpleApplication)
     */
    public TargetAppState(SimpleApplication app) {
        this.app = app;
    }

    /**
     * Initialize
     * @param stateManager AppStateManager
     * @param app Application (SimpleApplication)
     * @return
     */
    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        bulletAppState = stateManager.getState(BulletAppState.class);
        if (bulletAppState == null) {
            bulletAppState = new BulletAppState();
            stateManager.attach(bulletAppState);
        }
        
        targetNode = new Node("Targets");
        this.app.getRootNode().attachChild(targetNode);
        
        // Subscribe to target destruction events
        EventBus.getInstance().subscribe("TARGET_DESTROYED", new EventListener() {
            @Override
            public void onEvent(GameEvent event) {
                // Only spawn new targets if the state is enabled
                if (isEnabled() && targetNode.getChildren().size() < TARGET_COUNT) {
                    spawnNewTarget();
                }
            }
        });
        
        // Initialize initial targets
        for (int i = 0; i < TARGET_COUNT; i++) {
            spawnNewTarget();
        }
    }

    public void setGameMode(GameMode mode) {
        this.currentMode = mode;
        resetTargets();
    }

    public void resetTargets() {
        // Remove all existing targets
        targetNode.detachAllChildren();
        
        // Spawn new targets based on game mode
        for (int i = 0; i < TARGET_COUNT; i++) {
            spawnNewTarget();
        }
    }

    private void spawnNewTarget() {
        float x = 0, z = 0, y = 1.7f;
        int targetCount = targetNode.getChildren().size();  // Get current number of targets
        
        switch (currentMode) {
            case EASY:
                // Line up targets horizontally
                x = (targetCount - TARGET_COUNT/2) * 4f; // Space them 4 units apart
                z = 0;
                y = EASY_HEIGHT;
                break;
                
            case MEDIUM:
                // Random positions as before
                x = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                z = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                y = 3f;
                break;
                
            case HARD:
                // Same as medium but will move faster
                x = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                z = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                y = 3f;
                break;
        }
        
      
        Vector3f position = new Vector3f(x, y, z);
        Node target = TargetFactory.makeTarget("Target" + System.currentTimeMillis(), 
                                             position, 
                                             this.app);
        
        // Set shadow mode for the target node and its children
        target.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        // Set movement speed based on mode
        TargetControl targetControl = target.getControl(TargetControl.class);
        if (targetControl != null) {
            switch (currentMode) {
                case EASY:
                    targetControl.setMoveSpeed(0);  // No movement
                    break;
                case MEDIUM:
                    targetControl.setMoveSpeed(MEDIUM_SPEED);
                    break;
                case HARD:
                    targetControl.setMoveSpeed(HARD_SPEED);
                    break;
            }
        }
        
        targetNode.attachChild(target);
        
        // Add physics
        RigidBodyControl physicsControl = target.getControl(RigidBodyControl.class);
        if (physicsControl != null) {
            bulletAppState.getPhysicsSpace().add(physicsControl);
        }
    }

    public Node getTargetNode() {
        return targetNode;
    }
}
