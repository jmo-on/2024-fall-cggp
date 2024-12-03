package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class SceneAppState extends AbstractAppState {
    private SimpleApplication app;
    private TerrainQuad terrain;
    private Node rootNode;
    private BulletAppState bulletAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        
        bulletAppState = stateManager.getState(BulletAppState.class);
        if (bulletAppState == null) {
            bulletAppState = new BulletAppState();
            stateManager.attach(bulletAppState);
        }
        bulletAppState.setDebugEnabled(true);
        
        createTerrain();
        addLighting();
        initializePhysics();
    }

    private void createTerrain() {
        try {
            // Create a flatter terrain with the peak around player spawn height
            AbstractHeightMap heightmap = new HillHeightMap(513, 200, 1.5f, 2.5f, (byte) 3);
            heightmap.load();
            
            terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
            
            Material matTerrain = new Material(app.getAssetManager(), "Common/MatDefs/Terrain/Terrain.j3md");
            
            // Load grass texture
            Texture grass = app.getAssetManager().loadTexture("Textures/Terrain/grass.png");
            grass.setWrap(WrapMode.Repeat);
            matTerrain.setTexture("Tex1", grass);
            matTerrain.setFloat("Tex1Scale", 32f);
            
            terrain.setMaterial(matTerrain);
            
            // Position terrain to match player spawn height (1.8f)
            terrain.setLocalTranslation(0, 0, 0);
            // Keep terrain relatively flat but large
            terrain.setLocalScale(3f, 1f, 3f);
            
            rootNode.attachChild(terrain);
            
        } catch (Exception e) {
            System.err.println("Error creating terrain: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializePhysics() {
        if (terrain != null) {
            float[] heightmap = terrain.getHeightMap();
            HeightfieldCollisionShape terrainShape = new HeightfieldCollisionShape(heightmap, terrain.getLocalScale());
            RigidBodyControl terrainPhysics = new RigidBodyControl(terrainShape, 0);
            terrain.addControl(terrainPhysics);
            terrainPhysics.setPhysicsLocation(terrain.getLocalTranslation());
            bulletAppState.getPhysicsSpace().add(terrainPhysics);
        }
    }

    private void addLighting() {
        // Main sun
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(1.5f));
        rootNode.addLight(sun);

        // Secondary light
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f(0.5f, -0.5f, 0.5f).normalizeLocal());
        sun2.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(sun2);

        // Ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(ambient);
    }

    @Override
    public void cleanup() {
        if (terrain != null) {
            rootNode.detachChild(terrain);
        }
        super.cleanup();
    }
}