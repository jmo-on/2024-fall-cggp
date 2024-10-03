// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.input.controls.*;
import com.jme3.input.MouseInput;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;

public class CameraAppState extends AbstractAppState implements AnalogListener {

    private SimpleApplication app;
    private float rotationSpeed = 0.1f;
    private float maxVerticalAngle = FastMath.PI / 2 - 0.01f; // Slightly less than 90 degrees to prevent flipping

    // Angles to keep track of camera rotation
    private float yaw = 0;   // Rotation around the Y-axis (left/right)
    private float pitch = 0; // Rotation around the X-axis (up/down)

    /**
     * Constructor
     * @param app Application (SimpleApplication)
     */
    public CameraAppState(SimpleApplication app) {
        this.app = app;
    }

    /**
     * Initialize
     * @param stateManager AppStateManager
     * @param app
     * @return
     */
    @Override
    public void initialize(com.jme3.app.state.AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Set up mouse input mappings
        this.app.getInputManager().addMapping("MouseMoveLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        this.app.getInputManager().addMapping("MouseMoveRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        this.app.getInputManager().addMapping("MouseMoveUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        this.app.getInputManager().addMapping("MouseMoveDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        this.app.getInputManager().addListener(this, "MouseMoveLeft", "MouseMoveRight", "MouseMoveUp", "MouseMoveDown");
    }

    /**
     * On Analog
     * @param name
     * @param value
     * @param tpf
     * @return
     */
    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("MouseMoveLeft")) {
            yaw += value * rotationSpeed;
        } else if (name.equals("MouseMoveRight")) {
            yaw -= value * rotationSpeed;
        } else if (name.equals("MouseMoveUp")) {
            pitch += value * rotationSpeed;
            if (pitch > maxVerticalAngle) {
                pitch = maxVerticalAngle;
            }
        } else if (name.equals("MouseMoveDown")) {
            pitch -= value * rotationSpeed;
            if (pitch < -maxVerticalAngle) {
                pitch = -maxVerticalAngle;
            }
        }

        // Update camera rotation
        Quaternion rotation = new Quaternion();
        rotation.fromAngles(pitch, yaw, 0);
        app.getCamera().setRotation(rotation);
    }
}
