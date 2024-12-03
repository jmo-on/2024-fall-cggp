package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.app.SimpleApplication;
import com.jme3.texture.Texture;
import com.jme3.asset.TextureKey;

/**
 * TargetFactory
 * Target factory for the game
 */
public class TargetFactory {

    /**
     * Make Target
     * @param name Target name
     * @param loc Target location
     * @param app Application (SimpleApplication)
     * @return Target Geometry
     */
    public static Geometry makeTarget(String name, Vector3f loc, SimpleApplication app) {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry target = new Geometry(name, box);
        target.setLocalTranslation(loc);

        // Create material with texture
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");

        // Load diffuse texture
        TextureKey diffuseKey = new TextureKey("Textures/targetMinecraft.png", false);
        Texture diffuseTex = app.getAssetManager().loadTexture(diffuseKey);
        mat.setTexture("DiffuseMap", diffuseTex);

        // Optionally, load normal map if available
        // TextureKey normalKey = new TextureKey("Textures/TargetTexture_normal.png", false);
        // Texture normalTex = app.getAssetManager().loadTexture(normalKey);
        // mat.setTexture("NormalMap", normalTex);

        // Set additional material properties
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 64f); // Controls the specular highlight

        target.setMaterial(mat);

        // Add target control
        int initialHealth = 100; //can be changed later
        TargetControl targetControl = new TargetControl(initialHealth);
        target.addControl(targetControl);
        // target.addControl(new TargetControl(app));

        return target;
    }
}
