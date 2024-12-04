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
            System.out.println("Collision detected:");
            System.out.println("NodeA: " + event.getNodeA().getName());
            System.out.println("NodeB: " + event.getNodeB().getName());

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
        } else {
            //System.err.println("Collision event has a null node: NodeA=" + event.getNodeA() + ", NodeB=" + event.getNodeB());
        }
    }  
}
