package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
//import pepse.util.ClassNoise;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String BLOCK_TAG = "ground";
    private static final int TERRAIN_DEPTH = 20;
    public static final int BLOCK_SIZE = 30;

    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noiseGenerator;

    private final LinkedList<GameObject[]> blockColumList;


    /**
     * Constructor
     * @param gameObjects the collection of all game objects currently in the game
     * @param groundLayer represents the number of the ground layer
     * @param windowDimensions represents the dimensions of the window
     * @param seed represents a seed for the random creator
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = (float) 1/3 * windowDimensions.y();
        this.windowDimensions = windowDimensions;
        this.noiseGenerator = new NoiseGenerator(seed);
        this.blockColumList = new LinkedList<>();
    }

    public int getBlockSize() {return BLOCK_SIZE;}

    public LinkedList<GameObject[]> getBlockColumList() {
        return blockColumList;
    }

    /**
     * Creates the terrain that should be initialized at the beginning of the Game using minX and maxX as
     * left and right extremities, respectively.
     * @param minX: the x coordinate of the left extremity.
     * @param maxX: the x coordinate of the right extremity.
     */
    public void creatInRange(int minX, int maxX) {
        for (int i = minX; i <= maxX; i += BLOCK_SIZE) {
            GameObject[] blockArray = createBlockArray(i);
            blockColumList.addLast(blockArray);
        }
    }

    /**
     * A method that returns the real height of the ground at a certain x
     * @param x represents the spot on the terrain we want to know the height of
     * @return float which represents the height of the ground at x
     */
    public float groundHeightAt(float x) {
        x /= BLOCK_SIZE; // normalizing x to a running index
        int generatedNoise = (int) (this.noiseGenerator.noise(x) * this.groundHeightAtX0);
        int realColHeight = (int) (this.windowDimensions.y() - this.groundHeightAtX0 + generatedNoise);
        // returning normalized col height:
        return (realColHeight / BLOCK_SIZE) * BLOCK_SIZE;
    }

    /**
     * Creates an array of GameObjects which
     * @param positionX
     * @return
     */
    private GameObject[] createBlockArray(float positionX) {
        int columnHeight = (int) groundHeightAt(positionX);
        GameObject[] blockArray = new GameObject[columnHeight];
        for (int i = 0; i < columnHeight / BLOCK_SIZE; i++) {
            RectangleRenderable rectangleRenderable = new
                    RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
            GameObject block = new Block(Vector2.ZERO, rectangleRenderable);
            block.setCenter(new Vector2(positionX, columnHeight + i * BLOCK_SIZE));
            blockArray[i] = block;
            block.setTag(BLOCK_TAG);
            gameObjects.addGameObject(block, groundLayer);
        }
        return blockArray;
    }

    private void removeBlockArray(GameObject[] blockArray, int layer) {
        for(GameObject block : blockArray)
            gameObjects.removeGameObject(block, layer);
    }

    public void extendRight() {
        float lastBlockPositionX = blockColumList.getLast()[0].getCenter().x();
        float newBlockPositionX = lastBlockPositionX + BLOCK_SIZE;
        GameObject[] blockArray = createBlockArray(newBlockPositionX);
        blockColumList.addLast(blockArray);
        removeBlockArray(blockColumList.removeFirst(), groundLayer);
    }

    public void extendLeft() {
        float firstBlockPositionX = blockColumList.getFirst()[0].getCenter().x();
        float newBlockPositionX = firstBlockPositionX - BLOCK_SIZE;
        GameObject[] blockArray = createBlockArray(newBlockPositionX);
        blockColumList.addFirst(blockArray);
        removeBlockArray(blockColumList.removeLast(), groundLayer);
    }
}
