// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 *
 * @author withk
 */
public class ShadowManager{
    private DirectionalLight sun;
    private DirectionalLightShadowRenderer shadowRenderer;

    public ShadowManager(SimpleApplication app) {
        // init light
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1f, -1f, -1f).normalizeLocal()); // Adjust direction
        app.getRootNode().addLight(sun);
        
        // init shadow renderer
        shadowRenderer = new DirectionalLightShadowRenderer(app.getAssetManager(), 2048, 4);
        shadowRenderer.setLight(sun);
        app.getViewPort().addProcessor(shadowRenderer);
        
        // set default shadow mode for scene
        app.getRootNode().setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    public void enableShadows(RenderQueue.ShadowMode mode, com.jme3.scene.Spatial spatial) {
        spatial.setShadowMode(mode);
    }
}
