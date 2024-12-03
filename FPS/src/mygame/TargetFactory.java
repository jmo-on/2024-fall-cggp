package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.app.SimpleApplication;
import com.jme3.texture.Texture;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

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
    public static Node makeTarget(String name, Vector3f loc, SimpleApplication app) {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry targetGeometry = new Geometry(name, box);
        targetGeometry.setLocalTranslation(loc);

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

        targetGeometry.setMaterial(mat);
        
        // set shadow mode
        targetGeometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        // create target node to hold geometry and health bar
        Node targetNode = new Node(name);
        targetNode.setLocalTranslation(loc);
        targetGeometry.setLocalTranslation(0, 0.5f, 0);
        targetNode.attachChild(targetGeometry);
        
        // add health bar
        HealthBar healthBar = new HealthBar(app.getAssetManager(), 100);
        healthBar.setLocalTranslation(0, 1.1f, 0);
        targetNode.attachChild(healthBar);
        
        // Add target control
        TargetControl targetControl = new TargetControl(100);
        targetControl.setHealthBar(healthBar);
        targetNode.addControl(targetControl);
        
        RigidBodyControl physicsControl = new RigidBodyControl(0.0f); // Static object
        targetNode.addControl(physicsControl);
        System.out.println("TargetControl added to target: " + name);


        return targetNode;
    }
}
