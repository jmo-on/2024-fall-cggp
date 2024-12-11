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
import com.jme3.scene.Spatial;
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
    private final float SPAWN_AREA = 40f; // Size of spawn area
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
        
        switch (currentMode) {
            case EASY:
                // Spread targets in a circular pattern
                float angle = (float) (2 * Math.PI * targetNode.getChildren().size() / TARGET_COUNT);
                x = (float) (Math.cos(angle) * SPAWN_AREA * 0.8);
                z = (float) (Math.sin(angle) * SPAWN_AREA * 0.8);
                y = EASY_HEIGHT;
                break;
                
            case MEDIUM:
                // Random positions with minimum distance check
                boolean validPosition;
                do {
                    x = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                    z = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                    y = 3f + random.nextFloat() * 2; // Varying heights
                    validPosition = checkMinimumDistance(x, z, 10f); // Ensure 10 units minimum distance
                } while (!validPosition);
                break;
                
            case HARD:
                // Similar to medium but with more vertical variation
                do {
                    x = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                    z = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
                    y = 2f + random.nextFloat() * 4; // More height variation
                    validPosition = checkMinimumDistance(x, z, 8f); // Slightly smaller minimum distance
                } while (!validPosition);
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

    // Add this helper method to check minimum distance between targets
    private boolean checkMinimumDistance(float x, float z, float minDistance) {
        Vector3f newPos = new Vector3f(x, 0, z);
        for (Spatial child : targetNode.getChildren()) {
            Vector3f childPos = child.getLocalTranslation();
            // Only check x and z distances
            float dx = childPos.x - x;
            float dz = childPos.z - z;
            float distance = (float) Math.sqrt(dx * dx + dz * dz);
            if (distance < minDistance) {
                return false;
            }
        }
        return true;
    }

    public Node getTargetNode() {
        return targetNode;
    }
}
