package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import mygame.core.EventListener;
import mygame.core.EventBus;
import mygame.GameEvent;

public class TargetControl extends AbstractControl implements EventListener {
    private EventBus eventBus;
    private HealthBar healthBar;
    private final float maxHealth;
    private float health;
    
    public TargetControl(int initialHealth) {
        this.health = initialHealth;
        this.maxHealth = initialHealth;
        //this.healthBar = healthBar;
        this.eventBus = EventBus.getInstance();
        eventBus.subscribe("DAMAGE", this);
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
            spatial.removeFromParent();
            System.out.println("Target destroyed.");
            //handleDeath();
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
        
        spatial.removeFromParent();
    }
    
    
    
    @Override
    protected void controlUpdate(float tpf) {
        // Update logic here
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Render logic here
    }
}