package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
//import pepse.util.ClassNoise;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String NAME_TAG = "ground";
    private static final int TERRAIN_DEPTH = 20;
    public static final int BLOCK_SIZE = 30;
//    public static final float ROUGHNESS = 1F;



    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noiseGenerator;

    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = (float)1/3 * windowDimensions.y();
        this.windowDimensions = windowDimensions;
        this.noiseGenerator = new NoiseGenerator(seed);
    }

    public float groundHeightAt(float x) {
        x /= BLOCK_SIZE; // normalizing x to an running index
        int generatedNoise = (int) (this.noiseGenerator.noise(x) * this.groundHeightAtX0);
        int realColHeight = (int) (this.windowDimensions.y() - this.groundHeightAtX0 + generatedNoise);

        // returning normalized col height:
        return (realColHeight / BLOCK_SIZE) * BLOCK_SIZE;
    }

    public void createInRange(int minX, int maxX) {

        // calculating the location of first block and last block in a row
        int LastBlockLocation = Math.ceilDivExact(maxX, BLOCK_SIZE) * BLOCK_SIZE;
        int firstBlockLocation = (minX / BLOCK_SIZE) * BLOCK_SIZE;
        if (minX < 0) { firstBlockLocation -= BLOCK_SIZE; }

        for (int x = firstBlockLocation; x <= LastBlockLocation; x += BLOCK_SIZE) {

            // getting the height of the col
            int colHeight = (int)this.groundHeightAt(x);

            for (int n = 0; n <= TERRAIN_DEPTH ; n++) {
                // creating renderer
                RectangleRenderable rectangleRenderable = new
                        RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));

                // calculating the left lower corner of each block
                Vector2 locationVector = new Vector2(x, colHeight + n * BLOCK_SIZE);

                // creating the block
                GameObject block = new Block(locationVector, rectangleRenderable);
                block.setTag(NAME_TAG);
                gameObjects.addGameObject(block, groundLayer);
            }
        }
    }
}
