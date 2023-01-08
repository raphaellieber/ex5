package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

public class PepseGameManager extends GameManager {

    private static final String PEPSE_TAG = "PEPSE";

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;

    private static final int SKY_LAYER = -200;
    private static final int SUN_LAYER = -199;
    private static final int SUN_HALO_LAYER = -198;
    private static final int UPPER_TERRAIN_LAYER = -151;
    private static final int LOWER_TERRAIN_LAYER = -150;
    private static final int TREE_LAYER = -149;
    private static final int LEAF_LAYER = -148;
    private static final int AVATAR_LAYER = 0;
    private static final int NIGHT_LAYER = 100;
    private static final int UI_LAYER = 200;

    private static final int TEXT_SIZE = 25;
    private static final int CREATION_FACTOR = 700;
    private static final int DEFAULT_START = -200 ;
    private static final int DEFAULT_END = WINDOW_WIDTH + 100;

    private static final float DAY_CYCLE_LENGTH = 120;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);

    private final Vector2 windowDimensions;
    private Avatar avatar;
    private Tree tree;
    private Terrain terrain;

    /**
     * A constructor for the PEPSE game, which calls its super (GameManager)'s constructor
     * @param windowTitle the title of the window
     * @param windowDimensions a 2D vector representing the height and width of the window
     */
    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

    /**
     * This method initializes a new game. It creates all game objects, sets their values and initial
     * positions, and allows the start of a game.
     *
     * @param imageReader Contains a single method readImage, which reads an image from the disk.
     *                      See its documentation for help.
     * @param soundReader Contains a method readSound, which reads a wav file from
     *                      disk. See its documentation for help.
     * @param inputListener Contains a method isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful self-explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(80);

        // creating the world objects (sky, night, sun, terrain, and trees)
        this.createWorldEnvironment(windowController);

        // creating Avatar
        this.createAvatar(imageReader, inputListener);

        // creating Energy Display:
        this.createEnergyDisplay();

        // setting collisions between layers:
        this.setLayersCollisions();
    }

    /**
     * A helper method that creates the world environment: sky, sun, sun halo, terrain and trees
     * @param windowController Contains an array of helpful, self-explanatory methods concerning the window.
     */
    private void createWorldEnvironment(WindowController windowController) {
        // creating the sky
        GameObject sky = Sky.create(this.gameObjects(), windowController.getWindowDimensions(), SKY_LAYER);

        // creating the night.
        // The night takes half the time of the day cycle, hence it receives DAY_CYCLE_LENGTH / 2 as cycle length.
        GameObject night = Night.create(this.gameObjects(), NIGHT_LAYER,
                windowController.getWindowDimensions(), DAY_CYCLE_LENGTH / 2);

        // creating sun and sun halo
        this.createSun(windowController);

        // creating random seed
        Random random = new Random();
        int seed = random.nextInt();
        ColorSupplier.setSeed(seed); // setting the seed of the color creator


        // creating terrain
        this.terrain = new Terrain(this.gameObjects(), UPPER_TERRAIN_LAYER, LOWER_TERRAIN_LAYER,
                windowController.getWindowDimensions(), seed);

        // The range should be bigger than WINDOW_WIDTH/2 + creation factor
        this.terrain.createInRange(DEFAULT_START, DEFAULT_END);


        // creating TreeCreator
        this.tree = new Tree(terrain::groundHeightAt, gameObjects(), TREE_LAYER, LEAF_LAYER, seed);
        this.tree.createInRange(DEFAULT_START, DEFAULT_END);
    }

    /**
     * A method that sets collisions between layers.
     */
    private void setLayersCollisions() {
        this.gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, UPPER_TERRAIN_LAYER, true);
        if (this.tree.hasTrees()) {
            this.gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TREE_LAYER, true);
            this.gameObjects().layers().shouldLayersCollide(LEAF_LAYER, UPPER_TERRAIN_LAYER, true);
        }
    }

    /**
     * A helper method that creates the energyDisplay
     */
    private void createEnergyDisplay() {
        Vector2 diam = Vector2.ONES.mult(TEXT_SIZE);
        EnergyDisplay energyDisplay = new EnergyDisplay(Vector2.ZERO, diam, this.avatar);
        energyDisplay.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.gameObjects().addGameObject(energyDisplay, UI_LAYER);
    }

    /**
     * A helper function that creates the sun and everything connected to it, including the sun halo
     * @param windowController a controller used to control the window and its attributes
     */
    private void createSun(WindowController windowController) {
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER,
                windowController.getWindowDimensions(), DAY_CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(gameObjects(), SUN_HALO_LAYER,
                sun, SUN_HALO_COLOR);
    }

    /**
     * A helper function that creates the avatar: the function sets the camera to follow the avatar
     * and creates "infinite world", the function sets the physics of the avatar as well
     * @param imageReader an object used to read images from the disc and render them.
     * @param inputListener a listener capable of reading user keyboard inputs
     */
    private void createAvatar(ImageReader imageReader, UserInputListener inputListener) {

        // setting avatars location:
        int x = WINDOW_WIDTH / 2;
        while (this.tree.hasTreeAtX(x)) {x += 60;}  // makes sure the avatar won't start on a tree
        float y = this.terrain.groundHeightAt(x) - Avatar.AVATAR_DIMENSIONS.y();
        Vector2 location = new Vector2(x, y);

        // creating avatar:
        this.avatar = Avatar.create(this.gameObjects(), AVATAR_LAYER, location, inputListener, imageReader);

        // creating infinite world
        setCamera(new Camera(avatar, windowDimensions.mult(0.5f).subtract(location), windowDimensions,
                windowDimensions));
    }

    /**
     * An override for the super's method update.
     * the method creates terrain and trees in the range:
     * [Avatars.center - CREATION_FACTOR, Avatars.center + CREATION_FACTOR]
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // defining the limits where terrain and tree content should be present.
        // The CREATION_FACTOR takes a large enough margin so that the range exceeds the window dimension.
        int minX = (int)this.avatar.getCenter().x() - CREATION_FACTOR;
        int maxX = (int)this.avatar.getCenter().x() + CREATION_FACTOR;

        // creating trees and terrain in the defined range where they are missing.
        this.terrain.createInRange(minX, maxX);
        this.tree.createInRange(minX, maxX);

        this.setLayersCollisions();
    }

    public static void main(String[] args) {
        new PepseGameManager(PEPSE_TAG, new Vector2(WINDOW_WIDTH,WINDOW_HEIGHT)).run();
    }
}
