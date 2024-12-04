// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.ClipAction;
import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class GunAppState extends BaseAppState {
    private boolean isShooting = false;
    private double shootStartTime = 0;
    private double shootAnimationLength = 0;
    private String shootAnimationName = "Rig|Rig|VSK_Fire"; // Make sure this matches your animation name

    private SimpleApplication app;
    private Spatial gunModel;
    private Node gunNode;
    private AnimComposer animComposer;
    // private Action shootAction; // Not needed if using setCurrentAction with the action name

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;

        // Load the gun model
        gunModel = this.app.getAssetManager().loadModel("Models/an.j3o");

        // Migrate old AnimControl to AnimComposer if necessary
        AnimMigrationUtils.migrate(gunModel);

        // Retrieve the AnimComposer
        animComposer = gunModel.getControl(AnimComposer.class);
        if (animComposer == null) {
            System.out.println("AnimComposer not found on gun model!");
        } else {
            // Print available animations
            System.out.println("Available animations:");
            for (String animName : animComposer.getAnimClipsNames()) {
                System.out.println(animName);
            }   

            // Check if the shooting animation exists
            String shootAnimationName = "Rig|Rig|VSK_Fire";
            if (animComposer.getAnimClipsNames().contains(shootAnimationName)) {
                System.out.println("Shoot animation found: " + shootAnimationName);
            } else {
                System.out.println("Shoot animation '" + shootAnimationName + "' not found!");
            }
        }

        // Create a node to hold the gun model
        gunNode = new Node("GunNode");
        gunNode.attachChild(gunModel);

        // Attach gunNode to the rootNode
        this.app.getRootNode().attachChild(gunNode);
    }

    private void updateGunPosition() {
        Camera cam = app.getCamera();

        // Lower the gun position and adjust the offset
        Vector3f gunOffset = cam.getDirection().mult(0.7f)
            .add(cam.getLeft().mult(0.1f))
            .add(cam.getUp().mult(-1.15f)); // Increase this value to lower the gun further

        // Set the position of the gun based on camera location and offset
        gunNode.setLocalTranslation(cam.getLocation().add(gunOffset));

        // Tilt the gun slightly backward
        float pitchRadians = -0.2f; // Increased backward tilt
        Quaternion backwardTilt = new Quaternion().fromAngles(pitchRadians, 0, 0);
        

        // Apply the rotation
        gunNode.setLocalRotation(cam.getRotation().mult(backwardTilt));

        // Adjust scale if necessary
        gunNode.setLocalScale(3f); 
    }

    @Override
    public void update(float tpf) {
        updateGunPosition();
            if (isShooting) {
        shootStartTime += tpf;
        if (shootStartTime >= shootAnimationLength) {
            // Shooting animation finished
            isShooting = false;
            // Optionally, reset to an idle animation if you have one
            // animComposer.setCurrentAction("IdleAnimation");
            // Or set the current action to null to stop animations
            animComposer.removeCurrentAction(AnimComposer.DEFAULT_LAYER);
        }
    }

    }

    public void playShootAnimation() {
        if (animComposer != null) {
            if (animComposer.getAnimClipsNames().contains(shootAnimationName)) {
                // Set the current action to the shooting animation
                animComposer.setCurrentAction(shootAnimationName);

                // Initialize shooting state and timing
                isShooting = true;
                shootStartTime = 0;
                // Get the length of the shooting animation
                shootAnimationLength = animComposer.getAnimClip(shootAnimationName).getLength();
            } else {
                System.out.println("Shoot animation '" + shootAnimationName + "' not found!");
        }
    }
}

    @Override
    protected void cleanup(Application app) {
        if (gunNode != null) {
            gunNode.removeFromParent();
        }
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}
}

