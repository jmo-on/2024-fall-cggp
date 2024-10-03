// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.input.controls.*;
import com.jme3.input.MouseInput;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

public class ShootAppState extends AbstractAppState implements ActionListener {

    private SimpleApplication app;

    /**
     * Constructor
     * @param app Application (SimpleApplication)
     */
    public ShootAppState(SimpleApplication app) {
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

        // Set up shoot control
        this.app.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        this.app.getInputManager().addListener(this, "Shoot");
    }

    /**
     * On Action
     * @param binding Action binding
     * @param isPressed Whether the action is pressed
     * @param tpf Time per frame
     * @return
     */
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Shoot") && !isPressed) {
            shoot();
        }
    }

    /**
     * Shoot
     * @return
     */
    private void shoot() {
        // Create a bullet geometry
        Sphere sphere = new Sphere(8, 8, 0.1f);
        Geometry bullet = new Geometry("Bullet", sphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        bullet.setMaterial(mat);

        // Position the bullet
        bullet.setLocalTranslation(app.getCamera().getLocation().add(app.getCamera().getDirection().mult(1)));

        // Add bullet control
        bullet.addControl(new BulletControl(app, app.getCamera().getDirection()));

        // Attach bullet to scene
        app.getRootNode().attachChild(bullet);
    }
}
