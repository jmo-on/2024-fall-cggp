package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    private void setupCrosshairs() {
        setDisplayStatView(false);
        guiNode.detachAllChildren();
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // Crosshairs
        ch.setLocalTranslation(
            settings.getWidth() / 2 - ch.getLineWidth() / 2,
            settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    @Override
    public void simpleInitApp() {
        /** Initialize Physics **/
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        /** Initialize Scene **/
        stateManager.attach(new SceneAppState());

        /** Initialize Player **/
        stateManager.attach(new PlayerAppState(this));

        /** Initialize Targets **/
        stateManager.attach(new TargetAppState(this));

        /** Initialize Shooting **/
        stateManager.attach(new ShootAppState(this));
        
        /** Setup Crosshairs **/
        setupCrosshairs();

        //stateManager.attach(new CameraAppState(this));

        //inputManager.setCursorVisible(false);

        /** Adjust Camera **/
        cam.setLocation(new com.jme3.math.Vector3f(0, 1.8f, 0));
        //flyCam.setEnabled(false);
    }
}
