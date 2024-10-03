// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.Spatial;
import com.jme3.app.SimpleApplication;

public class TargetControl extends AbstractControl {

    private SimpleApplication app;

    /**
     * Constructor
     * @param app Application (SimpleApplication)
     */
    public TargetControl(SimpleApplication app) {
        this.app = app;
    }

    /**
     * Update
     * @param tpf Time per frame
     * @return
     */
    @Override
    protected void controlUpdate(float tpf) {
        // TODO: Implement target-specific logic here (if any)
    }

    /**
     * Render
     * @param rm RenderManager
     * @param vp ViewPort
     * @return
     */
    @Override
    protected void controlRender(com.jme3.renderer.RenderManager rm, com.jme3.renderer.ViewPort vp) {
        // TODO: Rendering code (if needed)
    }
}
