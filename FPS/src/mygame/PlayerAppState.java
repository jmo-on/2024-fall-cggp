// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.input.controls.*;
import com.jme3.input.KeyInput;
import com.jme3.math.Vector3f;

/**
 * PlayerAppState
 * Player control for the game
 */
public class PlayerAppState extends AbstractAppState implements ActionListener {

    private SimpleApplication app;
    private CharacterControl playerControl;
    private Vector3f walkDirection = new Vector3f();
    private boolean left, right, forward, backward;

    /**
     * Constructor
     * @param app Application (SimpleApplication)
     */
    public PlayerAppState(SimpleApplication app) {
        this.app = app;
    }

    /**
     * Initialize
     * @param stateManager AppStateManager
     * @param app Application (SimpleApplication)
     * @return
     */
    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Initialize player control
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);
        playerControl = new CharacterControl(capsuleShape, 0.05f);
        playerControl.setJumpSpeed(20);
        playerControl.setFallSpeed(30);
        playerControl.setGravity(30);
        playerControl.setPhysicsLocation(new Vector3f(0, 1.8f, 0));

        // Add to physics space
        this.app.getStateManager().getState(BulletAppState.class)
                .getPhysicsSpace().add(playerControl);

        // Set up keys
        setupKeys();
    }

    /**
     * Setup Keys
     * @return
     */
    private void setupKeys() {
        app.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        app.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        app.getInputManager().addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));

        app.getInputManager().addListener(this, "Left", "Right", "Forward", "Backward", "Jump");
    }

    /**
     * Update
     * @param tpf Time per frame
     * @return
     */
    @Override
    public void update(float tpf) {
        Vector3f camDir = app.getCamera().getDirection().clone().multLocal(0.1f);
        Vector3f camLeft = app.getCamera().getLeft().clone().multLocal(0.05f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (forward) {
            walkDirection.addLocal(camDir);
        }
        if (backward) {
            walkDirection.addLocal(camDir.negate());
        }
        playerControl.setWalkDirection(walkDirection);
        app.getCamera().setLocation(playerControl.getPhysicsLocation());
    }

    /**
     * On Action
     * @param binding Action binding
     * @param isPressed Whether the action is pressed
     * @param tpf Time per frame
     * @return
     */
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        switch (binding) {
            case "Left":
                left = isPressed;
                break;
            case "Right":
                right = isPressed;
                break;
            case "Forward":
                forward = isPressed;
                break;
            case "Backward":
                backward = isPressed;
                break;
            case "Jump":
                if (isPressed) {
                    playerControl.jump();
                }
                break;
        }
    }
}
