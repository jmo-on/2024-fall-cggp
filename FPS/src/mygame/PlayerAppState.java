package mygame;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.input.controls.*;
import com.jme3.input.KeyInput;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class PlayerAppState extends AbstractAppState implements ActionListener {
    private SimpleApplication app;
    private CharacterControl playerControl;
    private Vector3f walkDirection = new Vector3f();
    private boolean left, right, forward, backward, isRunning;
    private MusicAppState musicAppState;
    private float footstepTimer = 0;
    private static final float FOOTSTEP_INTERVAL = 0.4f;
    private static final float RUNNING_FOOTSTEP_INTERVAL = 0.25f; // Faster footsteps while running
    private static final float WALK_SPEED = 0.1f;
    private static final float RUN_SPEED = 0.2f; // Twice as fast when running

    public PlayerAppState(SimpleApplication app) {
        this.app = app;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.musicAppState = stateManager.getState(MusicAppState.class);
        
        // Create player physics
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);
        playerControl = new CharacterControl(capsuleShape, 0.05f);
        
        // Create a Node for the player and set its name
        Node playerNode = new Node("Player");
        playerNode.addControl(playerControl);
        
        playerControl.setJumpSpeed(20);
        playerControl.setFallSpeed(30);
        playerControl.setGravity(30);
        playerControl.setPhysicsLocation(new Vector3f(0, 3f, 0));
        
        // Add to physics space
        BulletAppState bulletAppState = this.app.getStateManager().getState(BulletAppState.class);
        bulletAppState.getPhysicsSpace().add(playerControl);
        
        // Add to scene
        this.app.getRootNode().attachChild(playerNode);
        
        setupKeys();
    }

    private void setupKeys() {
        app.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        app.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        app.getInputManager().addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        app.getInputManager().addMapping("Run", new KeyTrigger(KeyInput.KEY_LSHIFT)); // Add run mapping

        app.getInputManager().addListener(this, "Left", "Right", "Forward", "Backward", "Jump", "Run");
    }

    @Override
    public void update(float tpf) {
        float currentSpeed = isRunning ? RUN_SPEED : WALK_SPEED;
        float currentFootstepInterval = isRunning ? RUNNING_FOOTSTEP_INTERVAL : FOOTSTEP_INTERVAL;

        Vector3f camDir = app.getCamera().getDirection().clone().multLocal(currentSpeed);
        Vector3f camLeft = app.getCamera().getLeft().clone().multLocal(currentSpeed / 2); // Keep side movement slower

        walkDirection.set(0, 0, 0);
        if (left) walkDirection.addLocal(camLeft);
        if (right) walkDirection.addLocal(camLeft.negate());
        if (forward) walkDirection.addLocal(camDir);
        if (backward) walkDirection.addLocal(camDir.negate());

        playerControl.setWalkDirection(walkDirection);
        app.getCamera().setLocation(playerControl.getPhysicsLocation());

        // Handle footstep sounds with adjusted interval for running
        if ((left || right || forward || backward) && playerControl.onGround()) {
            footstepTimer += tpf;
            if (footstepTimer >= currentFootstepInterval) {
                if (musicAppState != null) {
                    musicAppState.playFootstep();
                }
                footstepTimer = 0;
            }
        } else {
            if (musicAppState != null) {
                musicAppState.stopFootstep();
            }
            footstepTimer = currentFootstepInterval;
        }
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        switch (binding) {
            case "Left": left = isPressed; break;
            case "Right": right = isPressed; break;
            case "Forward": forward = isPressed; break;
            case "Backward": backward = isPressed; break;
            case "Jump": if (isPressed) playerControl.jump(); break;
            case "Run": isRunning = isPressed; break;
        }
    }
}