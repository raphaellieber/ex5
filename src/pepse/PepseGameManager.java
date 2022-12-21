package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;

public class PepseGameManager extends GameManager {
    private static final String PEPSE = "PEPSE";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;
    private final Vector2 windowDimensions;

    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        GameObject sky = Sky.create(this.gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
    }

    public static void main(String[] args) {
        new PepseGameManager(PEPSE, new Vector2(WINDOW_WIDTH,WINDOW_HEIGHT)).run();
    }

}
