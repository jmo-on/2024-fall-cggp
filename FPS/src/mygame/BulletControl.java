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

    public BulletControl(SimpleApplication app, Vector3f direction) {
        this.app = app;
        this.initialVelocity = direction.mult(50);
        bulletPhys = new RigidBodyControl(1f);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            spatial.addControl(bulletPhys);
            app.getStateManager()
                    .getState(BulletAppState.class).getPhysicsSpace().add(bulletPhys);

            // Now that the physics object is initialized, set the linear velocity
            bulletPhys.setLinearVelocity(initialVelocity);
        } else {
            // Spatial is detached, cleanup
            app.getStateManager()
                    .getState(BulletAppState.class).getPhysicsSpace().remove(bulletPhys);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        // Implement bullet-specific logic here (e.g., lifespan)
    }

    @Override
    protected void controlRender(com.jme3.renderer.RenderManager rm, com.jme3.renderer.ViewPort vp) {
        // Rendering code (if needed)
    }
}
