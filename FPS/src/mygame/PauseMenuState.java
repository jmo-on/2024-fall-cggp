// Yongjae Lee
// Jin Hong Moon
// Kerry Wang

package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.font.BitmapText;

public class PauseMenuState extends AbstractAppState implements ActionListener {
    private SimpleApplication app;
    private Node guiNode;
    private boolean isEnabled = false;
    private GameMode selectedMode = GameMode.MEDIUM;
    private BitmapText[] menuItems;
    private int selectedItem = -1;

    public PauseMenuState(SimpleApplication app) {
        this.app = app;
        this.guiNode = new Node("Pause Menu");
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        setupKeys();
        createMenu();
    }

    private void setupKeys() {
        app.getInputManager().addMapping("Pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
        app.getInputManager().addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(this, "Pause", "Click");
    }

    private void createMenu() {
        // Create semi-transparent background
        BitmapText background = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        background.setText("");
        background.setSize(app.getCamera().getHeight());
        background.setColor(new ColorRGBA(0, 0, 0, 0.5f));
        background.setLocalTranslation(0, app.getCamera().getHeight(), 0);
        guiNode.attachChild(background);

        // Create menu items
        String[] items = {"Easy Mode", "Medium Mode", "Hard Mode", "Resume"};
        menuItems = new BitmapText[items.length];
        
        float spacing = 50f;
        float startY = app.getCamera().getHeight() * 0.7f;
        
        for (int i = 0; i < items.length; i++) {
            BitmapText text = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
            text.setText(items[i]);
            text.setSize(30);
            text.setColor(ColorRGBA.White);
            text.setLocalTranslation(
                (app.getCamera().getWidth() - text.getLineWidth()) / 2,
                startY - (i * spacing),
                0);
            menuItems[i] = text;
            guiNode.attachChild(text);
        }
    }

    public void updateMenuForGameEnd() {
        if (menuItems != null && menuItems.length > 0) {
            // Update the last menu item (Resume/Retry)
            menuItems[menuItems.length - 1].setText("Retry");
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Pause") && !isPressed) {
            togglePauseMenu();
        } else if (name.equals("Click") && !isPressed && isEnabled) {
            handleClick();
        }
    }

    private void handleClick() {
        Vector2f click = app.getInputManager().getCursorPosition();
        GUIAppState guiState = app.getStateManager().getState(GUIAppState.class);
        boolean isGameComplete = guiState != null && guiState.isGameCompleted();

        for (int i = 0; i < menuItems.length; i++) {
            BitmapText item = menuItems[i];
            if (isClickOnText(click, item)) {
                switch (i) {
                    case 0: // Easy Mode
                        setGameMode(GameMode.EASY);
                        if (isGameComplete) {
                            guiState.resetGame();
                        }
                        togglePauseMenu();
                        break;
                    case 1: // Medium Mode
                        setGameMode(GameMode.MEDIUM);
                        if (isGameComplete) {
                            guiState.resetGame();
                        }
                        togglePauseMenu();
                        break;
                    case 2: // Hard Mode
                        setGameMode(GameMode.HARD);
                        if (isGameComplete) {
                            guiState.resetGame();
                        }
                        togglePauseMenu();
                        break;
                    case 3: // Resume/Retry
                        if (isGameComplete) {
                            guiState.resetGame();
                        }
                        togglePauseMenu();
                        break;
                }
                break;
            }
        }
    }

    private boolean isClickOnText(Vector2f click, BitmapText text) {
        float x = text.getLocalTranslation().x;
        float y = text.getLocalTranslation().y;
        return click.x >= x && click.x <= x + text.getLineWidth() &&
               click.y >= y - text.getLineHeight() && click.y <= y;
    }

    private void togglePauseMenu() {
        isEnabled = !isEnabled;
        if (isEnabled) {
            app.getGuiNode().attachChild(guiNode);
            app.getInputManager().setCursorVisible(true);
            app.getFlyByCamera().setEnabled(false);
        } else {
            app.getGuiNode().detachChild(guiNode);
            app.getInputManager().setCursorVisible(false);
            app.getFlyByCamera().setEnabled(true);
        }
    }

    public void setGameMode(GameMode mode) {
        this.selectedMode = mode;
        TargetAppState targetState = app.getStateManager().getState(TargetAppState.class);
        if (targetState != null) {
            targetState.setGameMode(mode);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        app.getInputManager().deleteMapping("Pause");
        app.getInputManager().deleteMapping("Click");
        app.getInputManager().removeListener(this);
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}