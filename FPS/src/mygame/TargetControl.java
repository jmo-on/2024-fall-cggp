// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.audio.AudioNode;

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
    private float shootTimer = 0f;
    private final float EASY_SHOOT_INTERVAL = 2.0f;    // Slower shooting in easy mode
    private final float MEDIUM_SHOOT_INTERVAL = 1.5f;  // Current medium speed
    private final float HARD_SHOOT_INTERVAL = 0.2f;    // Much faster shooting in hard mode
    private float shootInterval = MEDIUM_SHOOT_INTERVAL;
    private static final float BULLET_SPEED = 20f; // Faster bullets
    private static final float MAX_BULLET_DISTANCE = 50f; // Maximum distance before cleanup
    private float gameStartDelay = 5f;
    private boolean canShoot = false;
    private AudioNode shootSound;
    
    public TargetControl(SimpleApplication app, int initialHealth) {
        this.app = app;
        this.health = initialHealth;
        this.maxHealth = initialHealth;
        this.eventBus = EventBus.getInstance();
        eventBus.subscribe("DAMAGE", this);
        this.moveDirection = getRandomDirection();
        
        // Initialize shooting sound
        shootSound = new AudioNode(app.getAssetManager(), "Sounds/laser_gunshot.wav", false);
        shootSound.setPositional(true);
        shootSound.setLooping(false);
        shootSound.setVolume(0.5f);
        app.getRootNode().attachChild(shootSound);
    }
    
    private Vector3f getRandomDirection() {
        Random random = new Random();
        float x = (random.nextFloat() * 2 - 1);
        float y = (random.nextFloat() * 2 - 1);
        float z = (random.nextFloat() * 2 - 1);
        return new Vector3f(x, y, z).normalizeLocal();
    }
    
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            initialY = spatial.getLocalTranslation().y; // Store initial Y position
            spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);  
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
            // Update game start delay
            if (!canShoot) {
                gameStartDelay -= tpf;
                if (gameStartDelay <= 0) {
                    canShoot = true;
                    System.out.println("Target can now shoot!");
                }
            }

            // Only shoot if delay has passed
            if (canShoot) {
                Vector3f playerPos = app.getCamera().getLocation();
                float distanceToPlayer = spatial.getWorldTranslation().distance(playerPos);
                
                if (distanceToPlayer < MAX_BULLET_DISTANCE) {
                    shootTimer += tpf;
                    if (shootTimer >= shootInterval) {
                        shootAtPlayer();
                        shootTimer = 0;
                    }
                }
            }

            // Get current position
            Vector3f currentPos = spatial.getLocalTranslation();

            // Calculate movement along all axes
            Vector3f movement = moveDirection.mult(moveSpeed * tpf);
            Vector3f newPos = currentPos.add(movement);

            // Keep targets within bounds
            float bound = 18f;  // Scene boundary
            newPos.x = Math.max(-bound, Math.min(bound, newPos.x));
            newPos.z = Math.max(-bound, Math.min(bound, newPos.z));
            
            // Allow vertical movement only in HARD mode
            if (moveSpeed >= 4f) {  // Only in HARD mode
                newPos.y = Math.max(1f, Math.min(6f, newPos.y));  // Height range
            } else {
                newPos.y = initialY;  // Keep constant in other modes
            }

            // Update spatial position
            spatial.setLocalTranslation(newPos);

            // Change direction periodically
            timeSinceDirectionChange += tpf;
            if (timeSinceDirectionChange >= directionChangeInterval) {
                moveDirection = getRandomDirection();
                timeSinceDirectionChange = 0;
            }

            // Bounce back if hitting the boundaries
            if (Math.abs(newPos.x) > bound || Math.abs(newPos.z) > bound || newPos.y <= 1f || newPos.y >= 6f) {
                moveDirection = moveDirection.negate();
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
        // Set shooting interval based on speed/mode
        if (speed == 0) {  // EASY mode
            this.shootInterval = EASY_SHOOT_INTERVAL;
        } else if (speed >= 4f) {  // HARD mode
            this.shootInterval = HARD_SHOOT_INTERVAL;
        } else {  // MEDIUM mode
            this.shootInterval = MEDIUM_SHOOT_INTERVAL;
        }
    }

    private void shootAtPlayer() {
        // Create bullet
        Sphere sphere = new Sphere(8, 8, 0.2f);
        Geometry bullet = new Geometry("EnemyBullet", sphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        bullet.setMaterial(mat);

        // Position bullet and sound at target location
        Vector3f spawnPos = spatial.getWorldTranslation().clone();
        bullet.setLocalTranslation(spawnPos);
        shootSound.setLocalTranslation(spawnPos);

        // Play shooting sound
        shootSound.playInstance();

        // Calculate direction to player
        Vector3f playerPos = app.getCamera().getLocation();
        Vector3f direction = playerPos.subtract(spawnPos).normalizeLocal();

        // Add bullet control with the calculated direction
        bullet.addControl(new EnemyBulletControl(app, direction, BULLET_SPEED));
        app.getRootNode().attachChild(bullet);
    }

    public void resetShootingDelay() {
        gameStartDelay = 5f;
        canShoot = false;
    }

    public void cleanup() {
        if (shootSound != null) {
            shootSound.removeFromParent();
        }
    }
}