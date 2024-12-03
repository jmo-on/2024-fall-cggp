/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

/**
 *
 * @author withk
 */
public class CollisionListener implements PhysicsCollisionListener {
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() != null && event.getNodeB() != null) {
            System.out.println("Collision detected:");
            System.out.println("NodeA: " + event.getNodeA().getName());
            System.out.println("NodeB: " + event.getNodeB().getName());

            if ((event.getNodeA().getName().equals("Bullet") && event.getNodeB().getName().startsWith("Target")) ||
            (event.getNodeB().getName().equals("Bullet") && event.getNodeA().getName().startsWith("Target"))) {

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
