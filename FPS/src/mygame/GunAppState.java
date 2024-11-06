package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class GunAppState extends BaseAppState {

    private SimpleApplication app;
    private Spatial gunModel;
    private Node gunNode;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;

        // Load the gun model
        gunModel = this.app.getAssetManager().loadModel("Models/scene.j3o");

        // If necessary, apply a material
        // Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        // gunModel.setMaterial(mat);

        // Create a node to hold the gun model
        gunNode = new Node("GunNode");
        gunNode.attachChild(gunModel);

        // Attach gunNode to the rootNode
        this.app.getRootNode().attachChild(gunNode);
    }

    private void updateGunPosition() {
        Camera cam = app.getCamera();
        // Adjust these values to position the gun correctly
        Vector3f gunOffset = cam.getDirection().mult(0.5f)
                         .add(cam.getLeft().mult(-0.2f))
                         .add(cam.getUp().mult(-0.2f));
        gunNode.setLocalTranslation(cam.getLocation().add(gunOffset));
        gunNode.setLocalRotation(cam.getRotation());
        gunNode.setLocalScale(0.5f); // Adjust scale as needed
    }

    @Override
    public void update(float tpf) {
        updateGunPosition();
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

