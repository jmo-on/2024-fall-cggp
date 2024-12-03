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

/**
 * TargetAppState
 * Target control for the game
 */
public class TargetAppState extends AbstractAppState {

    private SimpleApplication app;
    private Node targetNode;

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

        // Initialize targets
        for (int i = 0; i < 5; i++) {
            Node target = TargetFactory.makeTarget("Target" + i, new Vector3f(i * 3 - 6, 0, -10), this.app);
            targetNode.attachChild(target);
            
            RigidBodyControl physicsControl = target.getControl(RigidBodyControl.class);
            if (physicsControl != null) {
                this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(physicsControl);
            }
        }
    }
}
