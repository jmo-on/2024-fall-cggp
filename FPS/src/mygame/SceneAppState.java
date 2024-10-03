package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.BulletAppState;

public class SceneAppState extends AbstractAppState {

    private SimpleApplication app;

    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;

        /** Create Floor **/
        Box floorBox = new Box(20, 0.1f, 20);
        Geometry floorGeom = new Geometry("Floor", floorBox);
        floorGeom.setLocalTranslation(0, -0.1f, 0);
        Material floorMat = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        floorMat.setColor("Color", ColorRGBA.DarkGray);
        floorGeom.setMaterial(floorMat);

        RigidBodyControl floorPhys = new RigidBodyControl(0.0f);
        floorGeom.addControl(floorPhys);
        this.app.getStateManager().getState(BulletAppState.class)
                .getPhysicsSpace().add(floorPhys);

        this.app.getRootNode().attachChild(floorGeom);

        /** Add Lighting **/
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -1f, -0.2f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        this.app.getRootNode().addLight(sun);
    }
}
