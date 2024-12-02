package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import mygame.core.EventListener;
import mygame.core.EventBus;
import mygame.GameEvent;

public class TargetControl extends AbstractControl implements EventListener {
    private int health;
    private EventBus eventBus;
    
    public TargetControl(int initialHealth) {
        this.health = initialHealth;
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
    
    public void takeDamage(int damage) {
        health -= damage;
        
        GameEvent damageEvent = new GameEvent("TARGET_DAMAGED");
        damageEvent.addData("target", spatial);
        damageEvent.addData("remainingHealth", health);
        eventBus.publish(damageEvent);
        
        if (health <= 0) {
            handleDeath();
        }
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