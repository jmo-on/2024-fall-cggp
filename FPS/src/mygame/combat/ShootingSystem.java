package mygame.combat;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.Queue;
import java.util.LinkedList;
import mygame.BulletControl;

import mygame.core.BaseGameState;
import mygame.GameEvent;

public class ShootingSystem extends BaseGameState implements ActionListener {
    private static final float MAX_BULLET_LIFETIME = 5.0f;
    private Node bulletNode;
    private Queue<Geometry> bulletPool;
    private static final int INITIAL_POOL_SIZE = 20;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Shoot") && !isPressed) {
            shoot();
        }
    }

    private void setupInputs() {
        app.getInputManager().addMapping("Shoot", 
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(this, "Shoot");
    }
    
    @Override
    protected void initializeState() {
        bulletNode = new Node("BulletNode");
        app.getRootNode().attachChild(bulletNode);
        
        bulletPool = new LinkedList<>();
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            bulletPool.offer(createBullet());
        }
        
        setupInputs();
    }
    
    private Geometry createBullet() {
        Sphere sphere = new Sphere(8, 8, 0.1f);
        Geometry bullet = new Geometry("Bullet", sphere);
        Material mat = new Material(app.getAssetManager(), 
            "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        bullet.setMaterial(mat);
        return bullet;
    }
    
    private void shoot() {
        Geometry bullet = bulletPool.poll();
        if (bullet == null) {
            bullet = createBullet();
        }
        
        bullet.setLocalTranslation(app.getCamera().getLocation());
        bulletNode.attachChild(bullet);
        
        BulletControl bulletControl = new BulletControl(
            (SimpleApplication) app,  // Cast app to SimpleApplication
            app.getCamera().getDirection()
        );
        bullet.addControl(bulletControl);
        
        GameEvent shootEvent = new GameEvent("WEAPON_FIRED");
        shootEvent.addData("bullet", bullet);
        eventBus.publish(shootEvent);
    }
    
    @Override
    protected void cleanupState() {
        bulletNode.removeFromParent();
        bulletPool.clear();
    }
} 