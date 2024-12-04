// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class ParticleEffects {

    public static ParticleEmitter createBurstEffect(SimpleApplication app) {
        ParticleEmitter burstEmitter = new ParticleEmitter("Burst", Type.Triangle, 5);

        // Load material
        Material burstMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        burstMat.setTexture("Texture", app.getAssetManager().loadTexture("Effects/flash.png"));
        burstEmitter.setMaterial(burstMat);

        // Set burst properties
        burstEmitter.setImagesX(2); // X-axis animation frames
        burstEmitter.setImagesY(2); // Y-axis animation frames
        burstEmitter.setSelectRandomImage(true);

        // Configure colors
        burstEmitter.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1f)); // Opaque orange
        burstEmitter.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));  // Transparent orange

        // Configure size
        burstEmitter.setStartSize(0.1f); // Small at start
        burstEmitter.setEndSize(3.0f);  // Expands rapidly

        // Configure lifetime
        burstEmitter.setGravity(0, 0, 0); // No gravity effect
        burstEmitter.setLowLife(0.5f);
        burstEmitter.setHighLife(2.0f);

        // Configure motion
        burstEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 5f, 0)); // Upward
        burstEmitter.getParticleInfluencer().setVelocityVariation(1f); // 360-degree variation

        // Configure shape
        burstEmitter.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.5f)); // Emit within a sphere

        return burstEmitter;
    }
}