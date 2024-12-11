// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.scene.Spatial;
import com.jme3.effect.ParticleEmitter;
import mygame.core.EventBus;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.BulletAppState;

/**
 *
 * @author withk
 */
public class CollisionListener implements PhysicsCollisionListener {
    private final SimpleApplication app;
    
    public CollisionListener(SimpleApplication app) {
        this.app = app;
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() != null && event.getNodeB() != null) {
            String nodeAName = event.getNodeA().getName();
            String nodeBName = event.getNodeB().getName();
            
            System.out.println("Collision detected between:");
            System.out.println("NodeA: " + nodeAName + " (Type: " + event.getObjectA().getClass().getName() + ")");
            System.out.println("NodeB: " + nodeBName + " (Type: " + event.getObjectB().getClass().getName() + ")");

            // Remove enemy bullets when they hit terrain
            if ((nodeAName != null && nodeAName.equals("EnemyBullet") && nodeBName != null && nodeBName.equals("terrain")) ||
                (nodeBName != null && nodeBName.equals("EnemyBullet") && nodeAName != null && nodeAName.equals("terrain"))) {
                
                // Get the bullet node
                Spatial bullet = nodeAName.equals("EnemyBullet") ? event.getNodeA() : event.getNodeB();
                
                // Clean up physics before removing
                RigidBodyControl bulletPhys = bullet.getControl(RigidBodyControl.class);
                if (bulletPhys != null) {
                    app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(bulletPhys);
                }
                bullet.removeFromParent();
            }

            // Check for enemy bullet hitting player
            if ((nodeAName != null && nodeAName.equals("EnemyBullet") && 
                event.getObjectB() instanceof CharacterControl) ||
                (nodeBName != null && nodeBName.equals("EnemyBullet") && 
                event.getObjectA() instanceof CharacterControl)) {
                
                System.out.println("Enemy bullet hit player!");
                
                // Remove the bullet
                Spatial bullet = nodeAName != null && nodeAName.equals("EnemyBullet") ? 
                            event.getNodeA() : event.getNodeB();
                            
                // Clean up physics before removing
                RigidBodyControl bulletPhys = bullet.getControl(RigidBodyControl.class);
                if (bulletPhys != null) {
                    app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(bulletPhys);
                }
                bullet.removeFromParent();

                // Trigger player damage event
                GameEvent damageEvent = new GameEvent("PLAYER_DAMAGED");
                EventBus.getInstance().publish(damageEvent);
                System.out.println("Player hit! Damage event published.");
            }

            if ((event.getNodeA().getName().equals("Bullet") && event.getNodeB().getName().startsWith("Target")) ||
            (event.getNodeB().getName().equals("Bullet") && event.getNodeA().getName().startsWith("Target"))) {

                Spatial target = event.getNodeA().getName().startsWith("Target") ? event.getNodeA() : event.getNodeB();
                Spatial bullet = event.getNodeA().getName().equals("Bullet") ? event.getNodeA() : event.getNodeB();
            
                ParticleEmitter burstEffect = ParticleEffects.createBurstEffect(app);
                burstEffect.setLocalTranslation(target.getWorldTranslation());
                app.getRootNode().attachChild(burstEffect);
                burstEffect.emitAllParticles();
            
                app.enqueue(() -> {
                    float[] elapsedTime = {0f}; // Using an array to allow modification inside lambda
                    app.getStateManager().attach(new AbstractAppState() {
                        @Override
                        public void update(float tpf) {
                            elapsedTime[0] += tpf; // Increment time
                            if (elapsedTime[0] >= 1.0f) { // Check if 2 seconds passed
                                burstEffect.killAllParticles();
                                burstEffect.removeFromParent();
                                app.getStateManager().detach(this); // Remove this state
                            }
                        }
                    });
                });
                
                TargetControl targetControl = event.getNodeA().getControl(TargetControl.class);
                if (targetControl == null) {
                    targetControl = event.getNodeB().getControl(TargetControl.class);
                }

                if (targetControl != null) {
                    System.out.println("TargetControl found, applying damage.");
                    targetControl.takeDamage(25);
                } else {
                    System.err.println("TargetControl not found on collision nodes.");
                }

                // Remove bullet
                if (event.getNodeA().getName().equals("Bullet")) {
                    event.getNodeA().removeFromParent();
                } else if (event.getNodeB().getName().equals("Bullet")) {
                    event.getNodeB().removeFromParent();
                }
            }

            // Check for enemy bullet hitting player
            boolean isEnemyBulletA = event.getNodeA() != null && event.getNodeA().getName().equals("EnemyBullet");
            boolean isEnemyBulletB = event.getNodeB() != null && event.getNodeB().getName().equals("EnemyBullet");
            boolean isPlayerA = event.getObjectA() != null && event.getObjectA().getClass().getName().contains("CharacterControl");
            boolean isPlayerB = event.getObjectB() != null && event.getObjectB().getClass().getName().contains("CharacterControl");

            if ((isEnemyBulletA && isPlayerB) || (isEnemyBulletB && isPlayerA)) {
                System.out.println("Enemy bullet hit player!");
                // Remove the bullet
                Spatial bullet = isEnemyBulletA ? event.getNodeA() : event.getNodeB();
                bullet.removeFromParent();

                // Trigger player damage event
                GameEvent damageEvent = new GameEvent("PLAYER_DAMAGED");
                EventBus.getInstance().publish(damageEvent);
                System.out.println("Player hit! Damage event published.");
            }
        } else {
            //System.err.println("Collision event has a null node: NodeA=" + event.getNodeA() + ", NodeB=" + event.getNodeB());
        }
    }  
}
