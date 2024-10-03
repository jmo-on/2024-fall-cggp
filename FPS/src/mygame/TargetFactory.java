// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.app.SimpleApplication;

public class TargetFactory {

    /**
     * Make Target
     * @param name Target name
     * @param loc Target location
     * @param app Application (SimpleApplication)
     * @return Target Geometry
     */
    public static Geometry makeTarget(String name, Vector3f loc, SimpleApplication app) {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry target = new Geometry(name, box);
        target.setLocalTranslation(loc);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.randomColor());
        target.setMaterial(mat);

        // Add target control
        target.addControl(new TargetControl(app));

        return target;
    }
}
