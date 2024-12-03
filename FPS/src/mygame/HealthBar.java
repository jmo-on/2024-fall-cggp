// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class HealthBar extends Node {

    private final float maxHealth;
    private float currentHealth;
    private Geometry healthBarBackground;
    private Geometry healthBarForeground;

    public HealthBar(AssetManager assetManager, float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;

        // Create the background (gray bar)
        Quad bgQuad = new Quad(1, 0.1f); // Adjust dimensions as needed
        healthBarBackground = new Geometry("HealthBarBG", bgQuad);
        Material bgMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMaterial.setColor("Color", ColorRGBA.Gray);
        healthBarBackground.setMaterial(bgMaterial);
        attachChild(healthBarBackground); // Attach to this Node

        // Create the foreground (green bar)
        Quad fgQuad = new Quad(1, 0.1f);
        healthBarForeground = new Geometry("HealthBarFG", fgQuad);
        Material fgMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fgMaterial.setColor("Color", ColorRGBA.Green);
        healthBarForeground.setMaterial(fgMaterial);
        attachChild(healthBarForeground); // Attach to this Node
    }

    // Decrease health and update the bar
    public void decreaseHealth(float amount) {
        currentHealth -= amount;
        if (currentHealth < 0) currentHealth = 0;

        // Scale the health bar foreground
        float healthPercentage = currentHealth / maxHealth;
        healthBarForeground.setLocalScale(healthPercentage, 1, 1);

        // Change color based on health
        if (healthPercentage < 0.2f) {
            healthBarForeground.getMaterial().setColor("Color", ColorRGBA.Red);
        }

        // If health is depleted, remove the parent node (the target)
        if (currentHealth <= 0 && getParent() != null) {
            getParent().removeFromParent();
        }
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }
}
