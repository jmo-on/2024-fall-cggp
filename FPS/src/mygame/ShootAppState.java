package mygame;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.input.MouseInput;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Box;
import com.jme3.font.BitmapText;

public class ShootAppState extends AbstractAppState implements ActionListener {
    private SimpleApplication app;
    private GunAppState gunAppState;
    private Node guiNode;
    
    // Ammo and reload parameters
    private int currentAmmo = 30;
    private final int maxAmmo = 30;
    private boolean isReloading = false;
    private float reloadTime = 2.0f; // Reload takes 2 seconds
    private float currentReloadTime = 0f;
    
    // UI elements
    private Geometry reloadBar;
    private Geometry reloadBarBackground;
    private BitmapText ammoText;

    public ShootAppState(SimpleApplication app) {
        this.app = app;
        this.guiNode = app.getGuiNode();
    }

    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        
        // Setup input mappings
        this.app.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        this.app.getInputManager().addMapping("Reload", new KeyTrigger(KeyInput.KEY_R));
        this.app.getInputManager().addListener(this, "Shoot", "Reload");
        
        // Get GunAppState
        gunAppState = stateManager.getState(GunAppState.class);
        if (gunAppState == null) {
            System.out.println("GunAppState not found!");
        }
        
        // Setup UI elements
        setupReloadUI();
        setupAmmoCounter();
    }

    private void setupReloadUI() {
        // Background bar (gray)
        Box barBg = new Box(100, 10, 0);
        reloadBarBackground = new Geometry("ReloadBarBg", barBg);
        Material matBg = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matBg.setColor("Color", ColorRGBA.Gray);
        reloadBarBackground.setMaterial(matBg);
        
        // Progress bar (green)
        Box bar = new Box(100, 10, 0);
        reloadBar = new Geometry("ReloadBar", bar);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        reloadBar.setMaterial(mat);
        
        // Position the bars at the bottom center of the screen
        reloadBarBackground.setLocalTranslation(app.getCamera().getWidth() / 2, 50, 0);
        reloadBar.setLocalTranslation(app.getCamera().getWidth() / 2, 50, 0);
        
        // Initially hide the reload bars
        reloadBar.scale(0, 1, 1);
        reloadBarBackground.setCullHint(Geometry.CullHint.Always);
        reloadBar.setCullHint(Geometry.CullHint.Always);
        
        guiNode.attachChild(reloadBarBackground);
        guiNode.attachChild(reloadBar);
    }

    private void setupAmmoCounter() {
        ammoText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        ammoText.setSize(20);
        ammoText.setColor(ColorRGBA.White);
        ammoText.setLocalTranslation(20, 50, 0);
        updateAmmoText();
        guiNode.attachChild(ammoText);
    }

    private void updateAmmoText() {
        ammoText.setText("Ammo: " + currentAmmo + "/" + maxAmmo);
    }

    @Override
    public void update(float tpf) {
        if (isReloading) {
            currentReloadTime += tpf;
            float progress = currentReloadTime / reloadTime;
            
            if (progress >= 1.0f) {
                // Reload complete
                isReloading = false;
                currentAmmo = maxAmmo;
                reloadBarBackground.setCullHint(Geometry.CullHint.Always);
                reloadBar.setCullHint(Geometry.CullHint.Always);
                updateAmmoText();
            } else {
                // Update reload bar
                reloadBar.setLocalScale(progress, 1, 1);
            }
        }
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Shoot") && !isPressed && !isReloading) {
            if (currentAmmo > 0) {
                shoot();
                currentAmmo--;
                updateAmmoText();
            }
        } else if (binding.equals("Reload") && !isPressed && !isReloading && currentAmmo < maxAmmo) {
            startReload();
        }
    }

    private void startReload() {
        isReloading = true;
        currentReloadTime = 0f;
        reloadBarBackground.setCullHint(Geometry.CullHint.Never);
        reloadBar.setCullHint(Geometry.CullHint.Never);
        reloadBar.setLocalScale(0, 1, 1);
    }

    private void shoot() {
        Sphere sphere = new Sphere(8, 8, 0.1f);
        Geometry bullet = new Geometry("Bullet", sphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        bullet.setMaterial(mat);
        bullet.setLocalTranslation(app.getCamera().getLocation().add(app.getCamera().getDirection().mult(1)));
        bullet.addControl(new BulletControl(app, app.getCamera().getDirection()));
        app.getRootNode().attachChild(bullet);
        
        if (gunAppState != null) {
            gunAppState.playShootAnimation();
        }
    }
}