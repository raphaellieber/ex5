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
import pepse.world.Avatar;
import pepse.world.AvatarsEnergyDisplay;
import pepse.world.Sky;
import pepse.world.Terrain;
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
    private static final int TERRAIN_LAYER = -150;
    private static final int TREE_LAYER = -149;
    private static final int LEAF_LAYER = -148;
    private static final int AVATAR_LAYER = 0;
    private static final int NIGHT_LAYER = 100;
    private static final int UI_LAYER = 200;

    private static final int TEXT_SIZE = 25;
    private static final int TERRAIN_FACTOR = 1000;

    private static final float DAY_CYCLE_LENGTH = 120;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);

    private final Vector2 windowDimensions;
    private Avatar avatar;
    private Tree tree;
    private Terrain terrain;

    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

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

    public void createWorldEnvironment(WindowController windowController) {
        // creating the sky
        GameObject sky = Sky.create(this.gameObjects(), windowController.getWindowDimensions(), SKY_LAYER);

        // creating the night
        GameObject night = Night.create(this.gameObjects(), NIGHT_LAYER, windowController.getWindowDimensions(),
                DAY_CYCLE_LENGTH / 2);

        // creating sun
        this.createSun(windowController);

        // creating random seed
        Random random = new Random();
        int seed = random.nextInt();

        // creating terrain
        this.terrain = new Terrain(this.gameObjects(), TERRAIN_LAYER, windowController.getWindowDimensions(), seed);
        this.terrain.createInRange(0,WINDOW_WIDTH);

        // creating TreeCreator
        this.tree = new Tree(terrain::groundHeightAt, gameObjects(), TREE_LAYER, LEAF_LAYER, seed);
        this.tree.createInRange(0,WINDOW_WIDTH);
    }

    private void setLayersCollisions() {
        this.gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER, true);
        if (this.tree.hasTrees()) {
            this.gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TREE_LAYER, true);
            this.gameObjects().layers().shouldLayersCollide(LEAF_LAYER, TERRAIN_LAYER, true);
        }
    }

    /**
     * A helper method that creates the energyDisplay
     */
    private void createEnergyDisplay() {
        Vector2 diam = Vector2.ONES.mult(TEXT_SIZE);
        AvatarsEnergyDisplay energyDisplay = new AvatarsEnergyDisplay(Vector2.ZERO, diam, this.avatar);
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

        // A lambda callback which sets the sunHalo center at the same place of the sun centering
        // each new frame (after deltaTime).
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));
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
        if (this.tree.treeAtX(x)) {x += 30;}  // makes sure the avatar won't start on a tree
        float y = this.terrain.groundHeightAt(x) - Avatar.AVATAR_DIMENSIONS.y();   // TODO -> Is it okay making a static variable public?
        Vector2 location = new Vector2(x, y);

        // creating avatar:
        this.avatar = Avatar.create(this.gameObjects(), AVATAR_LAYER, location, inputListener, imageReader);

        // creating infinite world
        setCamera(new Camera(avatar, windowDimensions.mult(0.5f).subtract(location), windowDimensions,
                windowDimensions));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // creating trees and terrain
        int minX = (int)this.avatar.getCenter().x() - TERRAIN_FACTOR;
        int maxX = (int)this.avatar.getCenter().x() + TERRAIN_FACTOR;
        this.terrain.createInRange(minX, maxX);
        this.tree.createInRange(minX, maxX);

        this.setLayersCollisions();
    }

    public static void main(String[] args) {
        new PepseGameManager(PEPSE_TAG, new Vector2(WINDOW_WIDTH,WINDOW_HEIGHT)).run();
    }
}
