package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.font.BitmapText;

public class ShootAppState extends AbstractAppState implements ActionListener {
    private SimpleApplication app;
    private GunAppState gunAppState;
    private AudioNode shootSound;
    private int currentAmmo = 30;
    private static final int MAX_AMMO = 30;
    private boolean isReloading = false;
    private PauseMenuState pauseMenuState;
    private boolean enabled = true;
    private BitmapText ammoText;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ShootAppState(SimpleApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        
        // Setup input
        this.app.getInputManager().addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        this.app.getInputManager().addMapping("Reload", new KeyTrigger(KeyInput.KEY_R));
        this.app.getInputManager().addListener(this, "Shoot", "Reload");
        
        // Get other states
        gunAppState = stateManager.getState(GunAppState.class);
        pauseMenuState = stateManager.getState(PauseMenuState.class);

        // Setup sounds
        setupSounds();
        
        // Setup ammo display
        setupAmmoDisplay();
    }

    private void setupSounds() {
        try {
            shootSound = new AudioNode(app.getAssetManager(), "Sounds/gunshot.wav", DataType.Buffer);
            shootSound.setPositional(false);
            shootSound.setLooping(false);
            shootSound.setVolume(2);
            app.getRootNode().attachChild(shootSound);
        } catch (Exception e) {
            System.err.println("Error loading sound files: " + e.getMessage());
        }
    }

    private void setupAmmoDisplay() {
        ammoText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        ammoText.setSize(30);
        ammoText.setColor(ColorRGBA.White);
        updateAmmoDisplay();
        
        // Position ammo text above and slightly right of the crosshair
        ammoText.setLocalTranslation(app.getCamera().getWidth() - ammoText.getLineWidth() - 10, 30, 0);
        app.getGuiNode().attachChild(ammoText);
    }

    private void updateAmmoDisplay() {
        if (ammoText != null) {
            ammoText.setText("Ammo: " + currentAmmo + "/" + MAX_AMMO);
        }
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (!enabled) {
            return;
        }
        
        if (pauseMenuState != null && pauseMenuState.isEnabled()) {
            return;
        }

        if (binding.equals("Shoot") && !isPressed && !isReloading) {
            if (currentAmmo > 0) {
                shoot();
                currentAmmo--;
                updateAmmoDisplay();
            }
        } else if (binding.equals("Reload") && !isPressed) {
            if (!isReloading && currentAmmo < MAX_AMMO) {
                reload();
            }
        }
    }

    private void shoot() {
        // Play sound
        if (shootSound != null) {
            shootSound.playInstance();
        }

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
            if (spatial.getControl(TargetControl.class) != null && 
                bullet.getWorldBound().intersects(spatial.getWorldBound())) {
                TargetControl targetControl = spatial.getControl(TargetControl.class);
                targetControl.takeDamage(25);
                bullet.removeFromParent();
                break;
            }
        }
        
        // Play the gun shooting animation
        if (gunAppState != null) {
            gunAppState.playShootAnimation();
        }
    }

    private void reload() {
        if (isReloading || currentAmmo >= MAX_AMMO) {
            return;
        }
        
        isReloading = true;
        System.out.println("Reloading..."); // Debug print
        
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 second reload time
                // Schedule the GUI update on the main JME thread
                app.enqueue(() -> {
                    currentAmmo = MAX_AMMO;
                    isReloading = false;
                    updateAmmoDisplay();
                    System.out.println("Reload complete"); // Debug print
                    return null;
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (shootSound != null) {
            app.getRootNode().detachChild(shootSound);
        }
        if (ammoText != null) {
            app.getGuiNode().detachChild(ammoText);
        }
    }
}