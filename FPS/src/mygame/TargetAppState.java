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
        
        targetNode = new Node("Targets");
        this.app.getRootNode().attachChild(targetNode);
        
        // Subscribe to target destruction events
        EventBus.getInstance().subscribe("TARGET_DESTROYED", new EventListener() {
            @Override
            public void onEvent(GameEvent event) {
                // Check current target count
                if (targetNode.getChildren().size() < TARGET_COUNT) {
                    spawnNewTarget();
                }
            }
        });
        
        // Initialize initial targets
        for (int i = 0; i < TARGET_COUNT; i++) {
            spawnNewTarget();
        }
    }

    private void spawnNewTarget() {
        // Generate random position
        float x = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
        float z = (random.nextFloat() * 2 - 1) * SPAWN_AREA;
        float y = 3f; // Fixed height for all targets
        
        Vector3f randomPosition = new Vector3f(x, y, z);
        
        // Create new target
        Node target = TargetFactory.makeTarget("Target" + System.currentTimeMillis(), 
                                             randomPosition, 
                                             this.app);
        targetNode.attachChild(target);
        
        // Add physics
        RigidBodyControl physicsControl = target.getControl(RigidBodyControl.class);
        if (physicsControl != null) {
            BulletAppState bulletAppState = this.app.getStateManager().getState(BulletAppState.class);
            if (bulletAppState != null) {
                bulletAppState.getPhysicsSpace().add(physicsControl);
            }
        }
    }
}
