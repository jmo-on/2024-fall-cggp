// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class HealthBar {

    private SimpleApplication app;
    private Geometry healthBarBackground;
    private Geometry healthBarForeground;
    private float maxWidth;
    private Material healthMaterial;

    public HealthBar(SimpleApplication app, float maxHealth) {
        this.app = app;

        // Create and position the health bar background
        Quad bgQuad = new Quad(200, 20);
        healthBarBackground = new Geometry("HealthBarBG", bgQuad);
        Material bgMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bgMaterial.setColor("Color", ColorRGBA.Gray); // Background color
        healthBarBackground.setMaterial(bgMaterial);
        healthBarBackground.setLocalTranslation(10, app.getCamera().getHeight() - 30, 0);
        app.getGuiNode().attachChild(healthBarBackground);

        // Create and position the health bar foreground (the actual health)
        Quad fgQuad = new Quad(200, 20);
        healthBarForeground = new Geometry("HealthBarFG", fgQuad);
        healthMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        healthMaterial.setColor("Color", ColorRGBA.Green); // Initial health color
        healthBarForeground.setMaterial(healthMaterial);
        healthBarForeground.setLocalTranslation(10, app.getCamera().getHeight() - 30, 0);
        app.getGuiNode().attachChild(healthBarForeground);

        // Store the maximum width for scaling
        maxWidth = fgQuad.getWidth();
    }

    // Update the health bar width and color based on current health
    public void updateHealth(float currentHealth, float maxHealth) {
        float healthPercentage = currentHealth / maxHealth;
        healthBarForeground.setLocalScale(healthPercentage, 1, 1);

        // Change color if health is low (optional)
        if (healthPercentage < 0.2f) {
            healthMaterial.setColor("Color", ColorRGBA.Red);
        } else {
            healthMaterial.setColor("Color", ColorRGBA.Green);
        }
    }
}

