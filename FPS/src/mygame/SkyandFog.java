package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

public class SkyandFog {
    
    private final SimpleApplication app;
    private final FogFilter fogFilter;

    public SkyandFog(SimpleApplication app) {
        this.app = app;
        this.fogFilter = new FogFilter();
    }

    public void setupFog(ColorRGBA fogColor, float fogDistance, float fogDensity) {
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());

        // Configure the fog
        fogFilter.setFogColor(fogColor);
        fogFilter.setFogDistance(fogDistance);
        fogFilter.setFogDensity(fogDensity);

        // Add the fog filter to the post processor
        fpp.addFilter(fogFilter);

        // Attach the post processor to the viewport
        app.getViewPort().addProcessor(fpp);
    }

    public FogFilter getFogFilter() {
        return fogFilter;
    }
    
    public void setupSky() {
    Spatial sky = SkyFactory.createSky(
        app.getAssetManager(),
        app.getAssetManager().loadTexture("Textures/Sky/right.png"),
        app.getAssetManager().loadTexture("Textures/Sky/left.png"),
        app.getAssetManager().loadTexture("Textures/Sky/top.png"),
        app.getAssetManager().loadTexture("Textures/Sky/bottom.png"),
        app.getAssetManager().loadTexture("Textures/Sky/front.png"),
        app.getAssetManager().loadTexture("Textures/Sky/back.png")
    );

    sky.setQueueBucket(RenderQueue.Bucket.Sky);
    sky.setCullHint(Spatial.CullHint.Never);

    app.getRootNode().attachChild(sky);

    System.out.println("Sky cube map applied.");
}

}