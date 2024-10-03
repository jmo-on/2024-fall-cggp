package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.app.SimpleApplication;

public class TargetFactory {

    public static Geometry makeTarget(String name, Vector3f loc, SimpleApplication app) {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry target = new Geometry(name, box);
        target.setLocalTranslation(loc);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.randomColor());
        target.setMaterial(mat);

        /** Add Target Control **/
        target.addControl(new TargetControl(app));

        return target;
    }
}
