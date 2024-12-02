// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;

/**
 * Main
 */
public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;
    private HealthBar healthBar;
    private float currentHealth = 100;
    private final float maxHealth = 100;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    public BitmapFont getGuiFont() {
        return guiFont;
    }

    
    /**
     * Setup Crosshairs
     * @return
     */
    private void setupCrosshairs() {
        setDisplayStatView(false);
        guiNode.detachAllChildren();
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // Crosshairs
        ch.setLocalTranslation(
            settings.getWidth() / 2 - ch.getLineWidth() / 2,
            settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    /**
     * Simple Init App
     * @return
     */
    @Override
    public void simpleInitApp() {
        // Initialize physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Initialize scene
        stateManager.attach(new SceneAppState());

        // Initialize player
        stateManager.attach(new PlayerAppState(this));

        // Initialize targets
        stateManager.attach(new TargetAppState(this));

        // Initialize shooting
        stateManager.attach(new ShootAppState(this));
        
        // Initialize lighting
        stateManager.attach(new LightingAppState());

        // Load models
        //stateManager.attach(new ModelLoaderAppState());

        // Load animated models
       // stateManager.attach(new AnimatedModelAppState());

       stateManager.attach(new GunAppState()); // Attach GunAppState

        // Initialize the guiFont
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        // Initialize GUI
        stateManager.attach(new GUIAppState());
    
        // Setup crosshairs
        setupCrosshairs();

        // Adjust camera
        cam.setLocation(new com.jme3.math.Vector3f(0, 1.8f, 0));
        
        // Set up health bar with inital heath and max health
        healthBar = new HealthBar(this, maxHealth);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        currentHealth -= tpf * 2; // decrease health for example
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        
        // Update health bar
        healthBar.updateHealth(currentHealth, maxHealth);
    }
}
