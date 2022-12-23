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
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.awt.*;

public class PepseGameManager extends GameManager {
    private static final String PEPSE = "PEPSE";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;
    private final Vector2 windowDimensions;
    private final int CYCLE_LENGTH = 5;

    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        GameObject sky = Sky.create(this.gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        GameObject sun = Sun.create(this.gameObjects(), Layer.BACKGROUND + 1,
                windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(this.gameObjects(), Layer.BACKGROUND + 2,
                sun, new Color(255, 255, 0, 20));
        /**
         * A lambda callback which sets the sunHalo center at the same place of the sun center
         * in each new frame (after deltaTime).
         */
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));
    }

    public static void main(String[] args) {
        new PepseGameManager(PEPSE, new Vector2(WINDOW_WIDTH,WINDOW_HEIGHT)).run();
    }

}
