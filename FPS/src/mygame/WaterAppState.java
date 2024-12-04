// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.water.WaterFilter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;

public class WaterAppState extends AbstractAppState {
    private SimpleApplication app;
    private WaterFilter water;
    private Geometry waterGeom;
    private FilterPostProcessor fpp;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        
        createWater();
    }

    private void createWater() {
        // Create water filter
        fpp = new FilterPostProcessor(app.getAssetManager());
        app.getViewPort().addProcessor(fpp);

        // Create and configure water filter
        water = new WaterFilter(app.getRootNode(), new Vector3f(-0.5f, -1f, -0.5f));
        water.setWaterHeight(0.0f); // Water surface height
        water.setMaxAmplitude(0.3f); // Wave size
        water.setWaveScale(0.008f); // Wave scale
        water.setSpeed(0.7f); // Wave speed
        water.setShoreHardness(1.0f);
        water.setRefractionStrength(0.2f);
        water.setWaterTransparency(0.2f);
        water.setColorExtinction(new Vector3f(20, 20, 20));
        water.setWaterColor(new ColorRGBA(0.0f, 0.5f, 0.5f, 1.0f));
        water.setDeepWaterColor(new ColorRGBA(0.0f, 0.1f, 0.1f, 1.0f));
        
        // Create water surface geometry
        Quad quad = new Quad(40, 40); // Size of water surface
        waterGeom = new Geometry("water", quad);
        waterGeom.setLocalTranslation(-20, -0.5f, 20); // Position of water
        waterGeom.setLocalRotation(new com.jme3.math.Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        
        // Create water material
        Material waterMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        waterMat.setColor("Color", new ColorRGBA(0, 0.5f, 0.5f, 0.8f));
        waterGeom.setMaterial(waterMat);
        waterGeom.setQueueBucket(Bucket.Transparent);
        
        // Add everything to the scene
        app.getRootNode().attachChild(waterGeom);
        fpp.addFilter(water);
        
        // Add reflection scene
        createReflectionScene();
    }

    private void createReflectionScene() {
        /*// Load skybox texture
        Texture west = app.getAssetManager().loadTexture("Textures/DesertSky/front.png");
        Texture east = app.getAssetManager().loadTexture("Textures/DesertSky/right.png");
        Texture up = app.getAssetManager().loadTexture("Textures/DesertSky/top.png");
        Texture down = app.getAssetManager().loadTexture("Textures/DesertSky/bottom.png");
        Texture north = app.getAssetManager().loadTexture("Textures/DesertSky/left.png");
        Texture south = app.getAssetManager().loadTexture("Textures/DesertSky/back.png");

        app.getRootNode().attachChild(SkyFactory.createSky(app.getAssetManager(), west, east, north, south, up, down));
        */
        // Add light for better water appearance
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        app.getRootNode().addLight(sun);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (water != null && fpp != null) {
            fpp.removeFilter(water);
        }
        if (waterGeom != null) {
            app.getRootNode().detachChild(waterGeom);
        }
    }
}