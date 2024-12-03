package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import mygame.core.EventListener;
import mygame.core.EventBus;
import mygame.GameEvent;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Spatial;
import java.util.Random;
import com.jme3.math.Quaternion;

public class TargetControl extends AbstractControl implements EventListener {
    private EventBus eventBus;
    private HealthBar healthBar;
    private final float maxHealth;
    private float health;
    private SimpleApplication app;
    private Vector3f moveDirection;
    private float moveSpeed = 1f; // Slower speed
    private float timeSinceDirectionChange = 0f;
    private float directionChangeInterval = 3f;
    private float initialY; // Store initial Y position
    
    public TargetControl(SimpleApplication app, int initialHealth) {
        this.app = app;
        this.health = initialHealth;
        this.maxHealth = initialHealth;
        this.eventBus = EventBus.getInstance();
        eventBus.subscribe("DAMAGE", this);
        this.moveDirection = getRandomDirection();
    }
    
    private Vector3f getRandomDirection() {
        Random random = new Random();
        float x = (random.nextFloat() * 2 - 1);
        float z = (random.nextFloat() * 2 - 1);
        return new Vector3f(x, 0, z).normalizeLocal();
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            initialY = spatial.getLocalTranslation().y; // Store initial Y position
        }
    }
    
    @Override
    public void onEvent(GameEvent event) {
        if (event.getType().equals("DAMAGE")) {
            int damage = (Integer) event.getData("amount");
            takeDamage(damage);
        }
    }
    
    public void setHealthBar(HealthBar healthBar) {
        this.healthBar = healthBar;
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Damage applied: " + damage + ", Remaining health: " + health);

        if(healthBar != null) {
            healthBar.decreaseHealth(damage); 
            System.out.println("Health bar updated. Current health: " + healthBar.getCurrentHealth());
        }
        
        if (health <= 0) {
            handleDeath();
        }
        
        /*GameEvent damageEvent = new GameEvent("TARGET_DAMAGED");
        damageEvent.addData("target", spatial);
        damageEvent.addData("remainingHealth", health);
        eventBus.publish(damageEvent);
        */
        
    }
    
    private void handleDeath() {
        GameEvent deathEvent = new GameEvent("TARGET_DESTROYED");
        deathEvent.addData("target", spatial);
        eventBus.publish(deathEvent);
        
        // Remove from physics space if it has physics
        RigidBodyControl physicsControl = spatial.getControl(RigidBodyControl.class);
        if (physicsControl != null) {
            app.getStateManager().getState(BulletAppState.class)
                .getPhysicsSpace().remove(physicsControl);
        }
        
        spatial.removeFromParent();
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            // Get current position
            Vector3f currentPos = spatial.getLocalTranslation();
            
            // Calculate movement
            Vector3f movement = moveDirection.mult(moveSpeed * tpf);
            Vector3f newPos = currentPos.add(movement);
            newPos.y = initialY;  // Keep Y position constant
            
            // Update position
            spatial.setLocalTranslation(newPos);
            
            // Change direction periodically
            timeSinceDirectionChange += tpf;
            if (timeSinceDirectionChange >= directionChangeInterval) {
                moveDirection = getRandomDirection();
                timeSinceDirectionChange = 0;
            }
            
            // Keep within bounds
            float bound = 18f;
            if (Math.abs(newPos.x) > bound || Math.abs(newPos.z) > bound) {
                moveDirection = moveDirection.negate();
            }
            
            // Update physics position
            RigidBodyControl physicsControl = spatial.getControl(RigidBodyControl.class);
            if (physicsControl != null) {
                physicsControl.setPhysicsLocation(newPos);
            }
            
            // Make health bar face the camera (only in X-Z plane)
            if (healthBar != null) {
                // Get camera position and target position
                Vector3f cameraPos = app.getCamera().getLocation();
                Vector3f targetPos = spatial.getLocalTranslation();
                
                // Calculate direction from target to camera
                Vector3f direction = cameraPos.subtract(targetPos);
                direction.y = 0; // Zero out the Y component to keep health bar upright
                direction.normalizeLocal();
                
                // Calculate the angle between the direction and the Z axis
                float angle = (float) Math.atan2(direction.x, direction.z);
                
                // Create rotation quaternion (only around Y axis)
                Quaternion rotation = new Quaternion();
                rotation.fromAngles(0, angle, 0);
                
                // Apply rotation to health bar
                healthBar.setLocalRotation(rotation);
            }
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Render logic here
    }
    
    public void setMoveSpeed(float speed) {
        this.moveSpeed = speed;
    }
}