// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.scene.control.AbstractControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;

public class BulletControl extends AbstractControl {

    private SimpleApplication app;
    private RigidBodyControl bulletPhys;
    private Vector3f initialVelocity;

    /**
     * Constructor
     * @param app Application (SimpleApplication)
     * @param direction Bullet direction
     */
    public BulletControl(SimpleApplication app, Vector3f direction) {
        this.app = app;
        this.initialVelocity = direction.mult(50);
        bulletPhys = new RigidBodyControl(1f);
    }

    /**
     * Set Spatial
     * @param spatial
     * @return
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            // Setup physics and initial velocity
            spatial.addControl(bulletPhys);
            app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(bulletPhys);
            // Now that the physics object is initialized, set the linear velocity
            bulletPhys.setLinearVelocity(initialVelocity);
        } else {
            // Spatial is detached, cleanup
            app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(bulletPhys);
        }
    }

    /**
     * Update
     * @param tpf
     * @return
     */
    @Override
    protected void controlUpdate(float tpf) {
        // TODO: Implement bullet-specific logic here (e.g., lifespan)
    }

    /**
     * Render
     * @param rm
     * @param vp
     * @return
     */
    @Override
    protected void controlRender(com.jme3.renderer.RenderManager rm, com.jme3.renderer.ViewPort vp) {
        // TODO: Rendering code (if needed)
    }
}
