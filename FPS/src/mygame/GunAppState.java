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
    private String shootAnimationName = "Rig|Rig|VSK_Fire";
    private SimpleApplication app;
    private Spatial gunModel;
    private Node gunNode;
    private AnimComposer animComposer;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        gunModel = this.app.getAssetManager().loadModel("Models/an.j3o");
        AnimMigrationUtils.migrate(gunModel);
        animComposer = gunModel.getControl(AnimComposer.class);
        if (animComposer == null) {
            System.out.println("AnimComposer not found on gun model!");
        } else {
            System.out.println("Available animations:");
            for (String animName : animComposer.getAnimClipsNames()) {
                System.out.println(animName);
            }   
            if (animComposer.getAnimClipsNames().contains(shootAnimationName)) {
                System.out.println("Shoot animation found: " + shootAnimationName);
            } else {
                System.out.println("Shoot animation '" + shootAnimationName + "' not found!");
            }
        }
        gunNode = new Node("GunNode");
        gunNode.attachChild(gunModel);
        this.app.getRootNode().attachChild(gunNode);
    }

    private void updateGunPosition() {
        Camera cam = app.getCamera();
        // Adjusted offset values to move the gun more forward and visible
        Vector3f gunOffset = cam.getDirection().mult(1.4f)
            .add(cam.getLeft().mult(0.15f))    // Slightly increased to move gun more right
            .add(cam.getUp().mult(-1.04f));
            
        gunNode.setLocalTranslation(cam.getLocation().add(gunOffset));
        
        // Create combined rotation for both backward tilt and right tilt
        float pitchRadians = -0.15f;  // Backward tilt
        float rollRadians = 0.055f;     // Right tilt (positive value tilts right)
        Quaternion combinedTilt = new Quaternion().fromAngles(pitchRadians, 0, rollRadians);
        
        gunNode.setLocalRotation(cam.getRotation().mult(combinedTilt));
        gunNode.setLocalScale(3f);
    }

    @Override
    public void update(float tpf) {
        updateGunPosition();
        if (isShooting) {
            shootStartTime += tpf;
            if (shootStartTime >= shootAnimationLength) {
                isShooting = false;
                animComposer.removeCurrentAction(AnimComposer.DEFAULT_LAYER);
            }
        }
    }

    public void playShootAnimation() {
        if (animComposer != null) {
            if (animComposer.getAnimClipsNames().contains(shootAnimationName)) {
                animComposer.setCurrentAction(shootAnimationName);
                isShooting = true;
                shootStartTime = 0;
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