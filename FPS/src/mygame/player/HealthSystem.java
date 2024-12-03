package mygame.player;

import mygame.core.BaseGameState;
import mygame.core.EventListener;
import mygame.GameEvent;
import mygame.HealthBar;

public class HealthSystem extends BaseGameState implements EventListener {
    private float currentHealth;
    private float maxHealth;
    private HealthBar healthBar;
    private boolean isRegenerating;
    private float regenRate = 1.0f;
    private float regenDelay = 5.0f;
    private float timeSinceLastDamage;
    
    @Override
    protected void initializeState() {
        currentHealth = 100;
        maxHealth = 100;
        healthBar = new HealthBar(app.getAssetManager(), maxHealth);
        healthBar.setLocalTranslation(10, 10, 0);
        app.getGuiNode().attachChild(healthBar);
        
        eventBus.subscribe("DAMAGE_TAKEN", this);
        eventBus.subscribe("HEALTH_PICKUP", this);
    }
    
    @Override
    public void update(float tpf) {
        if (isRegenerating) {
            timeSinceLastDamage += tpf;
            if (timeSinceLastDamage >= regenDelay) {
                regenerateHealth(tpf);
            }
        }
    }
    
    private void regenerateHealth(float tpf) {
        if (currentHealth < maxHealth) {
            currentHealth = Math.min(maxHealth, currentHealth + (regenRate * tpf));
            GameEvent event = new GameEvent("HEALTH_CHANGED");
            event.addData("currentHealth", currentHealth);
            eventBus.publish(event);
        }
    }
    
    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case "DAMAGE_TAKEN":
                float damage = (Float) event.getData("amount");
                takeDamage(damage);
                break;
            case "HEALTH_PICKUP":
                float amount = (Float) event.getData("amount");
                heal(amount);
                break;
        }
    }
    
    private void takeDamage(float damage) {
        currentHealth = Math.max(0, currentHealth - damage);
        timeSinceLastDamage = 0;
        isRegenerating = true;
        
        if (currentHealth <= 0) {
            eventBus.publish(new GameEvent("PLAYER_DIED"));
        }
    }
    
    private void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }
    
    @Override
    protected void cleanupState() {
        eventBus.unsubscribe("DAMAGE_TAKEN", this);
        eventBus.unsubscribe("HEALTH_PICKUP", this);
    }
} 