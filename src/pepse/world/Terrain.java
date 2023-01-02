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

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String NAME_TAG = "ground";
    private static final int TERRAIN_DEPTH = 20;
    public static final int BLOCK_SIZE = 30;

    private final GameObjectCollection gameObjects;
    private int upperGroundLayer;
    private final int lowerGroundLayer;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noiseGenerator;
    private int minXOnTerrain;
    private int maxXOnTerrain;
    private LinkedList<GameObject[]> blockColumList;


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

        this.minXOnTerrain = (int) (windowDimensions.x()/4); // default value
//        this.minXOnTerrain = 350; // default value
        this.maxXOnTerrain = (int) (windowDimensions.x()/4); // default value
//        this.maxXOnTerrain = 650; // default value

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

        //    minX.......|(this.min)...................(this.max)|......maxX
        if (minX < this.minXOnTerrain && maxX > this.maxXOnTerrain) {
            createInRangeHelper(minX, this.minXOnTerrain, false, false);
            createInRangeHelper(this.maxXOnTerrain, maxX, true, false);
            this.minXOnTerrain = minX;
            this.maxXOnTerrain = maxX;
            System.out.println("In first");
            System.out.println("minX: " + minX);
            System.out.println("maxX: " + maxX);
            System.out.println("thisMinX: " + this.minXOnTerrain);
            System.out.println("thisMaxX: " + this.maxXOnTerrain);
        }

        //   |(this.min)......minX...................(this.max)|......maxX
        else if (minX > this.minXOnTerrain && minX < this.maxXOnTerrain && maxX > this.maxXOnTerrain) {
            createInRangeHelper(this.maxXOnTerrain, maxX, true, true);
//            this.minXOnTerrain = maxX - this.maxXOnTerrain;
            this.maxXOnTerrain = maxX;
            System.out.println("creating right");
        }

        //   minX.......|(this.min)...................maxX......(this.max)|
        else if (maxX < this.maxXOnTerrain && maxX > this.minXOnTerrain && minX < this.minXOnTerrain) {
            createInRangeHelper(minX,this.minXOnTerrain, false, true);
//            this.maxXOnTerrain = maxX - this.maxXOnTerrain;

            this.minXOnTerrain = minX;
            System.out.println("creating left");
        }
    }


    private void createInRangeHelper ( int minX, int maxX, boolean LTR, boolean extension) {
        // calculating the location of first block and last block in a row
        int LastBlockLocation = Math.ceilDivExact(maxX, BLOCK_SIZE) * BLOCK_SIZE;
        int firstBlockLocation = (minX / BLOCK_SIZE) * BLOCK_SIZE;
        if (minX < 0) {
            firstBlockLocation -= BLOCK_SIZE;
        }

        for (int x = firstBlockLocation; x <= LastBlockLocation; x += BLOCK_SIZE) {

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

//                if(n < 3) {
//                    gameObjects.addGameObject(block, upperGroundLayer);
//                }
//                else gameObjects.addGameObject(block, lowerGroundLayer);
                gameObjects.addGameObject(block, lowerGroundLayer);
            }
            if (extension) {

                if (LTR) { // removing first
                    this.blockColumList.addLast(blockArray);
                    GameObject[] temp = this.blockColumList.removeFirst();
                    for (GameObject obj : temp) { gameObjects.removeGameObject(obj, lowerGroundLayer); }
                    this.minXOnTerrain += BLOCK_SIZE;
                    System.out.println("removing first");
                } else { // removing last
                    this.blockColumList.addFirst(blockArray);
                    for (GameObject obj : this.blockColumList.removeLast()) { gameObjects.removeGameObject(obj, lowerGroundLayer);  }
                    this.maxXOnTerrain -= BLOCK_SIZE;

                    System.out.println("removing last");
                }
            }

        }
    }



}
