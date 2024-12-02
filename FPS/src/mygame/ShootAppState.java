package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * ShootAppState
 * Shoot control for the game
 */
public class ShootAppState extends AbstractAppState implements ActionListener {

    private SimpleApplication app;
    private GunAppState gunAppState;

    public ShootAppState(SimpleApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;

        // Attach Shoot mapping
        this.app.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        this.app.getInputManager().addListener(this, "Shoot");

        // Retrieve GunAppState
        gunAppState = stateManager.getState(GunAppState.class);
        if (gunAppState == null) {
            System.out.println("GunAppState not found!");
        }
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Shoot") && !isPressed) {
            shoot();
        }
    }

    private void shoot() {
        // Create bullet geometry
        Sphere sphere = new Sphere(8, 8, 0.1f);
        Geometry bullet = new Geometry("Bullet", sphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        bullet.setMaterial(mat);

        // Set bullet position
        bullet.setLocalTranslation(app.getCamera().getLocation().add(app.getCamera().getDirection().mult(1)));

        // Add BulletControl
        bullet.addControl(new BulletControl(app, app.getCamera().getDirection()));
        app.getRootNode().attachChild(bullet);
        
        // Collision check
        for (Spatial spatial : app.getRootNode().getChildren()) {
            if (spatial.getControl(TargetControl.class) != null && bullet.getWorldBound().intersects(spatial.getWorldBound())) {
                TargetControl targetControl = spatial.getControl(TargetControl.class);
                targetControl.takeDamage(25); // take 25 damage, can be changed later
                bullet.removeFromParent(); // remove bullet after it hits
                break;
            }
        }

        // Play the gun shooting animation
        if (gunAppState != null) {
            gunAppState.playShootAnimation();
        }
    }
}
