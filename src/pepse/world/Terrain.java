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

public class Terrain {
    private static final boolean LEFT = false;
    private static final boolean RIGHT = true;
    private static final boolean EXTEND = true;
    private static final boolean NO_EXTEND = false;


    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String NAME_TAG = "ground";
    private static final int TERRAIN_DEPTH = 20;
    public static final int BLOCK_SIZE = 30;

    private final GameObjectCollection gameObjects;
    private final int upperGroundLayer;
    private final int lowerGroundLayer;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noiseGenerator;
    private int minXOnTerrain;
    private int maxXOnTerrain;
    private final LinkedList<GameObject[]> blockColumList;


    /**
     * Constructor
     * @param gameObjects the collection of all game objects currently in the game
     * @param lowerGroundLayer represents the number of the ground layer
     * @param windowDimensions represents the dimensions of the window
     * @param seed represents a seed for the random creator
     */
    public Terrain(GameObjectCollection gameObjects, int upperGroundLayer, int lowerGroundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.upperGroundLayer = upperGroundLayer;
        this.lowerGroundLayer = lowerGroundLayer;
        this.groundHeightAtX0 = (float) 1/3 * windowDimensions.y();
        this.windowDimensions = windowDimensions;
        this.noiseGenerator = new NoiseGenerator(seed);

        this.minXOnTerrain = (int) (windowDimensions.x()/2); // default value
        this.maxXOnTerrain = (int) (windowDimensions.x()/2); // default value

        this.blockColumList = new LinkedList<>();
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
     * A method that creates terrain between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */
    public void createInRange(int minX, int maxX) {
        // normalizing minX and maxX to a size that fits the block size
        int normalizedMaxX = (int) (Math.ceil(maxX / BLOCK_SIZE) * BLOCK_SIZE);
        int normalizedMinX = (minX / BLOCK_SIZE) * BLOCK_SIZE;
        if (minX < 0) { normalizedMinX -= BLOCK_SIZE; }

        //    minX.......|(this.min)...................(this.max)|......maxX
        if (normalizedMinX < this.minXOnTerrain && normalizedMaxX > this.maxXOnTerrain) {
            createInRangeHelper(normalizedMinX, this.minXOnTerrain, LEFT, NO_EXTEND);
            createInRangeHelper(this.maxXOnTerrain, normalizedMaxX, RIGHT, NO_EXTEND);
            this.minXOnTerrain = normalizedMinX ;
            this.maxXOnTerrain = normalizedMaxX;
        }

        //   |(this.min)......minX...................(this.max)|......maxX
        else if (normalizedMaxX > this.maxXOnTerrain) {
            createInRangeHelper(this.maxXOnTerrain + BLOCK_SIZE, normalizedMaxX, RIGHT, EXTEND);
            this.maxXOnTerrain = normalizedMaxX ;
        }

            //   minX.......|(this.min)...................maxX......(this.max)|
        else if (normalizedMinX < this.minXOnTerrain) {
            System.out.println(normalizedMaxX);
            System.out.println(normalizedMinX);
            System.out.println(this.maxXOnTerrain);
            System.out.println(this.minXOnTerrain);


            createInRangeHelper(normalizedMinX, this.minXOnTerrain - BLOCK_SIZE, LEFT, EXTEND);
            this.minXOnTerrain = normalizedMinX;
            System.out.println("creating left");
        }
    }

    private void createInRangeHelper ( int minX, int maxX, boolean LTR, boolean extension) {

        for (int x = minX; x <= maxX; x += BLOCK_SIZE) {

            // getting the height of the col
            int colHeight = (int) this.groundHeightAt(x);
            GameObject[] blockArray = new GameObject[TERRAIN_DEPTH];
            for (int n = 0; n < TERRAIN_DEPTH; n++) {
                // creating renderer
                RectangleRenderable rectangleRenderable = new
                        RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));

                // calculating the left lower corner of each block
                Vector2 locationVector = new Vector2(x, colHeight + n * BLOCK_SIZE);

                // creating the block
                GameObject block = new Block(locationVector, rectangleRenderable);
                block.setTag(NAME_TAG);
                blockArray[n] = block;  // saving the column of the current x to the linked-list

                // dividing the blocks into different layers
//                if(n < 2) { gameObjects.addGameObject(block, upperGroundLayer); }
//                else { gameObjects.addGameObject(block, lowerGroundLayer); }
                gameObjects.addGameObject(block, upperGroundLayer);
            }

            // extending the terrain if needed
            if (extension) { terrainExtension(LTR, blockArray); }
            else { this.blockColumList.addLast(blockArray); }

        }
    }

    private void terrainExtension(boolean LTR, GameObject[] blockArray) {
        int tmp_layer = this.upperGroundLayer;
        GameObject[] removedBlockArray;

        if (LTR) { // removing first
            this.blockColumList.addLast(blockArray);
            removedBlockArray = this.blockColumList.removeFirst();
            this.minXOnTerrain += BLOCK_SIZE;
        }

        else{ // removing last
            this.blockColumList.addFirst(blockArray);
            removedBlockArray = this.blockColumList.removeLast();
            this.maxXOnTerrain -= BLOCK_SIZE;
        }

        for (int i = 0; i < TERRAIN_DEPTH; i++) {
//            if (i > 2) { tmp_layer = this.lowerGroundLayer; }  //determining the layer of the block
            gameObjects.removeGameObject(removedBlockArray[i], tmp_layer);
        }

    }


}
