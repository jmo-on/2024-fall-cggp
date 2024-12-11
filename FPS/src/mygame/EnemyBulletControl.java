package mygame;

import com.jme3.scene.control.AbstractControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;

public class EnemyBulletControl extends AbstractControl {
    private SimpleApplication app;
    private RigidBodyControl bulletPhys;
    private Vector3f initialVelocity;
    private BulletAppState bulletAppState;

    public EnemyBulletControl(SimpleApplication app, Vector3f direction, float speed) {
        this.app = app;
        this.initialVelocity = direction.mult(speed);
        this.bulletAppState = app.getStateManager().getState(BulletAppState.class);
        bulletPhys = new RigidBodyControl(1f);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            spatial.addControl(bulletPhys);
            bulletAppState.getPhysicsSpace().add(bulletPhys);
            bulletPhys.setLinearVelocity(initialVelocity);
        } else {
            // Clean up physics when spatial is removed
            if (bulletPhys != null) {
                bulletAppState.getPhysicsSpace().remove(bulletPhys);
                bulletPhys = null;
            }
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        // Remove bullet if it's fallen below the world
        if (spatial != null && spatial.getWorldTranslation().y < -10) {
            // Clean up physics before removing spatial
            if (bulletPhys != null) {
                bulletAppState.getPhysicsSpace().remove(bulletPhys);
                bulletPhys = null;
            }
            spatial.removeFromParent();
        }
    }

    @Override
    protected void controlRender(com.jme3.renderer.RenderManager rm, com.jme3.renderer.ViewPort vp) {
        // Not needed
    }
} 