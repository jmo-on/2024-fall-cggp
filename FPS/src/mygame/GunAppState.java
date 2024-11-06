package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class GunAppState extends BaseAppState {

    private SimpleApplication app;
    private Spatial gunModel;
    private Node gunNode;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;

        // Load the gun model
        gunModel = this.app.getAssetManager().loadModel("Models/scene.j3o");

        if (gunModel == null) {
            System.err.println("Failed to load gun model!");
            return;
        } else {
            System.out.println("Gun model loaded successfully.");
        }

        // Apply a simple material for testing
        Material testMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        testMat.setColor("Color", ColorRGBA.Green);
        gunModel.setMaterial(testMat);

        // Disable culling
        gunModel.setCullHint(Spatial.CullHint.Never);

        // Initialize and start animation
        AnimComposer animComposer = gunModel.getControl(AnimComposer.class);
        if (animComposer != null) {
            System.out.println("Available animations: " + animComposer.getAnimClipsNames());
            animComposer.setCurrentAction("YourAnimationName"); // Replace with actual animation name
        } else {
            System.out.println("No AnimComposer found on the gun model.");
        }

        // Create a node to hold the gun model
        gunNode = new Node("GunNode");
        gunNode.attachChild(gunModel);

        // Attach gunNode to the root node
        this.app.getRootNode().attachChild(gunNode);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        // Update gunNode position to be in front of the camera
        Camera cam = this.app.getCamera();
        Vector3f camLocation = cam.getLocation();
        Quaternion camRotation = cam.getRotation();

        // Adjust the offset vector as needed
        Vector3f offset = camRotation.mult(new Vector3f(0.0f, -0.2f, -0.5f));
        gunNode.setLocalTranslation(camLocation.add(offset));

        // Rotate the gun to match the camera's rotation
        gunNode.setLocalRotation(camRotation);

        // Log the gun's position
        System.out.println("Gun position: " + gunNode.getWorldTranslation());
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
