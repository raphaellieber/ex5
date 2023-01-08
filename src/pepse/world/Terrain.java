package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.world.abstract_classes.ExtendableElement;

import java.awt.*;
import java.util.LinkedList;

public class Terrain extends ExtendableElement {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String NAME_TAG = "ground";
    private static final int TERRAIN_DEPTH = 20;
    private static final int BLOCK_SIZE = 30;

    private final GameObjectCollection gameObjects;
    private final int upperGroundLayer;
    private final int lowerGroundLayer;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noiseGenerator;

    private boolean firstCreationFlag;
    private final LinkedList<GameObject[]> blockColumList;


    /**
     * Constructor
     * @param gameObjects the collection of all game objects currently in the game
     * @param lowerGroundLayer represents the number of the ground layer
     * @param windowDimensions represents the dimensions of the window
     * @param seed represents a seed for the random creator
     */
    public Terrain(GameObjectCollection gameObjects, int upperGroundLayer, int lowerGroundLayer,
                   Vector2 windowDimensions, int seed) {
        super();
        super.setBlockSize(BLOCK_SIZE);

        this.gameObjects = gameObjects;
        this.upperGroundLayer = upperGroundLayer;
        this.lowerGroundLayer = lowerGroundLayer;
        this.groundHeightAtX0 = (float) 1/3 * windowDimensions.y();
        this.windowDimensions = windowDimensions;
        this.noiseGenerator = new NoiseGenerator(seed);

        this.firstCreationFlag = false; // default value
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
        return  (realColHeight / BLOCK_SIZE) * BLOCK_SIZE;
    }

    /**
     * A method that creates terrain between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */
    @Override
    public void createInRange(int minX, int maxX) {
        super.createInRange(minX, maxX);

        createInRangeHelper(super.getMinXToUpdate(), super.getMaxXToUpdate());
    }

    /**
     * A helper method that creates terrain from given minX to give maxX
     * @param minX represents the lower bound of the new part of terrain to create
     * @param maxX represents the upper bound of the new part of terrain to create
     */
    private void createInRangeHelper (int minX, int maxX) {
        for (int x = minX; x < maxX; x += BLOCK_SIZE) {
            // getting the height of the col
            int colHeight = (int) this.groundHeightAt(x);

            // creating a block array for a new col of blocks:
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
                if( n < 2) { gameObjects.addGameObject(block, upperGroundLayer); }
                else { gameObjects.addGameObject(block, lowerGroundLayer); }
            }

            // extending the terrain if needed
//            if (!(this.maxXOnTerrain == 0 & this.minXOnTerrain == 0)) { terrainExtension(blockArray); }
            if (this.firstCreationFlag) { terrainExtension(blockArray); }
            else { this.blockColumList.addLast(blockArray); }
        }
        this.firstCreationFlag = true;
    }

    /**
     * A method that deals with terrain changes
     * @param blockArray represents the array of blocks that was just created
     */
    private void terrainExtension(GameObject[] blockArray) {
        int tmp_layer = this.upperGroundLayer;
        GameObject[] removedBlockArray;

        // determining if going left or right:
        float firstBlockLoc = this.blockColumList.getFirst()[0].getCenter().x();
        float curBlocksLoc = blockArray[0].getCenter().x();
        boolean leftOrRight = curBlocksLoc > firstBlockLoc;

        if (leftOrRight) { // going right -> removing first
            this.blockColumList.addLast(blockArray);
            removedBlockArray = this.blockColumList.removeFirst();
//            this.minXOnTerrain += BLOCK_SIZE;
        }

        else{ // going left -> removing last
            this.blockColumList.addFirst(blockArray);
            removedBlockArray = this.blockColumList.removeLast();
//            this.maxXOnTerrain -= BLOCK_SIZE;
        }

        // removing objects
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            if (i > 1) { tmp_layer = this.lowerGroundLayer; }  //determining the layer of the block
            gameObjects.removeGameObject(removedBlockArray[i], tmp_layer);
        }

    }


}
