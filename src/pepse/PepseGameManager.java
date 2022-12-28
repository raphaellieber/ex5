package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

public class PepseGameManager extends GameManager {

    private static final String PEPSE = "PEPSE";

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;

    private static final int SKY_LAYER = -200;
    private static final int TERRAIN_LAYER = -150;
    private static final int TREE_LAYER = -149;
    private static final int LEAF_LAYER = -148;
    private static final int CYCLE_LENGTH = 40;
    private static final float DAY_CYCLE_LENGTH = 30;


    private final Vector2 windowDimensions;

    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {

        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        // creating the sky
        GameObject sky = Sky.create(gameObjects(), windowDimensions, SKY_LAYER);

        // creating the night
        GameObject night = Night.create(gameObjects(), Layer.FOREGROUND, windowDimensions,
                DAY_CYCLE_LENGTH / 2);

        // creating terrain
        Random random = new Random();
        int seed = random.nextInt();
        Terrain terrain = new Terrain(gameObjects(), TERRAIN_LAYER, windowDimensions, seed);
        terrain.createInRange(0,WINDOW_WIDTH);

        // creating TreeCreator
        Tree tree = new Tree(terrain::groundHeightAt, gameObjects(), TREE_LAYER, LEAF_LAYER,
                TERRAIN_LAYER, seed);
        tree.createInRange(0,WINDOW_WIDTH);

        // creating sun
        GameObject sun = Sun.create(gameObjects(), Layer.BACKGROUND + 1,
                windowController.getWindowDimensions(), DAY_CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(gameObjects(), Layer.BACKGROUND + 2,
                sun, new Color(255, 255, 0, 20));

        /**
         * A lambda callback which sets the sunHalo center at the same place of the sun center
         * in each new frame (after deltaTime).
         */
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));

        // creating Avatar
        Avatar avatar = Avatar.create(gameObjects(), Layer.DEFAULT, Vector2.ZERO, inputListener, imageReader);
    }

    public static void main(String[] args) {
        new PepseGameManager(PEPSE, new Vector2(WINDOW_WIDTH,WINDOW_HEIGHT)).run();
    }


}
