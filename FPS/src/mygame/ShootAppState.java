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

    public ShootAppState(SimpleApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        /** Set up Shoot Control **/
        this.app.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        this.app.getInputManager().addListener(this, "Shoot");
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Shoot") && !isPressed) {
            shoot();
        }
    }

    private void shoot() {
        /** Create a Bullet Geometry **/
        Sphere sphere = new Sphere(8, 8, 0.1f);
        Geometry bullet = new Geometry("Bullet", sphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        bullet.setMaterial(mat);

        /** Position the Bullet **/
        bullet.setLocalTranslation(app.getCamera().getLocation().add(app.getCamera().getDirection().mult(1)));

        /** Add Bullet Control **/
        bullet.addControl(new BulletControl(app, app.getCamera().getDirection()));

        /** Attach Bullet to Scene **/
        app.getRootNode().attachChild(bullet);
    }
}
