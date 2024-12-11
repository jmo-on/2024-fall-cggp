// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;

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

        // Initialize background music
        stateManager.attach(new MusicAppState());

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
        healthBar = new HealthBar(assetManager, maxHealth);

        // collision detection between bullet and target
        CollisionListener collisionListener = new CollisionListener(this);
        bulletAppState.getPhysicsSpace().addCollisionListener(collisionListener);
        
        // Set up sky and fog (desert style) 
        //SkyandFog fogManager = new SkyandFog(this);
        //fogManager.setupSky();
        //fogManager.setupFog(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f), 5, 0.1f);
        
        // Fog (forest style)
        SkyandFog skyandFog = new SkyandFog(this);
        skyandFog.setupFog(new ColorRGBA(0.6f, 0.8f, 0.7f, 1.0f), 60, 0.4f);
        skyandFog.setupSky();
        
        // Initialize ShadowManager
        //ShadowManager shadowManager = new ShadowManager(this);
        //shadowManager.enableShadows(RenderQueue.ShadowMode.CastAndReceive, targetNode);
        //shadowManager.enableShadows(RenderQueue.ShadowMode.Receive, groundNode);
        

        // Add pause menu state
        stateManager.attach(new PauseMenuState(this));

        // Add water
        stateManager.attach(new WaterAppState());

        // Delete the default exit mapping
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        
        // Add new exit mapping as key p
        inputManager.addMapping("Exit", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean pressed, float tpf) {
                if (name.equals("Exit") && !pressed) {
                    stop();
                }
            }
        }, "Exit");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
    }
}
