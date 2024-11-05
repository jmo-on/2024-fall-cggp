package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.asset.TextureKey;
import com.jme3.scene.Spatial;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;

public class ModelLoaderAppState extends AbstractAppState {

    protected SimpleApplication app;
    protected Spatial model;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;

        // Load the model
        model = this.app.getAssetManager().loadModel("Models/YourModel.j3o");

        // Load textures
        TextureKey diffuseKey = new TextureKey("Textures/YourTexture_diffuse.png", false);
        Texture diffuseTex = this.app.getAssetManager().loadTexture(diffuseKey);

        TextureKey normalKey = new TextureKey("Textures/YourTexture_normal.png", false);
        Texture normalTex = this.app.getAssetManager().loadTexture(normalKey);

        // Create material and set textures
        Material mat = new Material(this.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", diffuseTex);
        mat.setTexture("NormalMap", normalTex);

        // Apply material to the model
        model.setMaterial(mat);

        // Attach the model to the scene
        this.app.getRootNode().attachChild(model);
    }
}
