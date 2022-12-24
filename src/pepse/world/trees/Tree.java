package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;
import java.util.Vector;
import java.util.function.Function;

public class Tree {

    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    private static final int TRUNK_BLOCK_SIZE = 30;
    private static final int LEAF_BLOCK_SIZE = 30;

    private static final int TREE_CHANCE = 15;
    private static final int MAX_TREE_HEIGHT = 10;
    private static final int MIN_TREE_HEIGHT = 7;

    private static final int DEFAULT_VALUE = -999;



    private final Function<Integer, Float> groundHeightAt;
    private final GameObjectCollection gameObjects;
    private final int treeLayer;
    private final int leafLayer;
    private final int groundLayer;
    private boolean treeCreated;
    private int xOfLastTree;
    private final Random random;

    public Tree (Function<Integer, Float> groundHeightAt, GameObjectCollection gameObjects, int treeLayer,
                 int leafLayer, int groundLayer, int seed) {

        this.treeCreated = false;
        this.groundHeightAt = groundHeightAt;
        this.gameObjects = gameObjects;
        this.treeLayer = treeLayer;
        this.leafLayer = leafLayer;
        this.groundLayer = groundLayer;

        this.xOfLastTree = DEFAULT_VALUE;
        this.random = new Random(seed);
    }

    public void createInRange(int minX, int maxX) {

        // calculating the location of normalized minX and maxX:
        int LastBlockLocation = Math.ceilDivExact(maxX, TRUNK_BLOCK_SIZE) * TRUNK_BLOCK_SIZE;
        int firstBlockLocation = (minX / TRUNK_BLOCK_SIZE) * TRUNK_BLOCK_SIZE;
        if (minX < 0) { firstBlockLocation -= TRUNK_BLOCK_SIZE; }

        // Creating trees:
        for (int i = firstBlockLocation; i < LastBlockLocation; i += TRUNK_BLOCK_SIZE) { createTree(i); }

        if (this.treeCreated) {
            gameObjects.layers().shouldLayersCollide(this.leafLayer, this.groundLayer, true);
        }
    }

    private void createTree(int x) {

        // Tossing tilted coin with 1/TREE_CHANCE chance of creating a tree:
        Random rand = new Random();
        int tiltedCoinToss = rand.nextInt(TREE_CHANCE);

        if (tiltedCoinToss == 0 && this.xOfLastTree != x-TRUNK_BLOCK_SIZE) {
            // Calculating the location of left top corner of first block
            float groundHeightAtX = (groundHeightAt.apply(x));

            // creating the tree
            int trunkHeight = createTrunk(x, groundHeightAtX);
            createTreeTop(x, trunkHeight);

            this.xOfLastTree = x;  // keeping the x of last tree

            if (!this.treeCreated) { this.treeCreated = true; }  // flag that at least one tree was created

        }
    }

    private int createTrunk(int topLeftX, float topLeftY) {
        int treeHeight = this.random.nextInt(MIN_TREE_HEIGHT, MAX_TREE_HEIGHT);

        for (int i = 0; i < treeHeight; i++) {
            // creating render-ability for all the blocks of the trunk:
            RectangleRenderable rectangleRenderable = new
                    RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR));

            // creating a trunk block, tagging it and adding into gameObjects:
            topLeftY -= TRUNK_BLOCK_SIZE;
            Vector2 leftTopCorner = new Vector2(topLeftX, topLeftY);
            GameObject trunkBlock = new GameObject(leftTopCorner, Vector2.ONES.mult(TRUNK_BLOCK_SIZE),
                    rectangleRenderable);
            trunkBlock.setTag("tree_trunk");
            this.gameObjects.addGameObject(trunkBlock, this.treeLayer);
        }
        // returning the y top left corner of the trunk
        return (int)topLeftY;
    }

    private void createTreeTop(int treeX, int trunkHeight) {

        // determining tree top diameter:
        int treeTopDiam = 0;

        if (this.random.nextInt(2) == 1){ treeTopDiam = 5; }
        else {treeTopDiam = 3;}

        // creating tree top from the bottom to the top:
        int maxTopTreeHeight = ((treeTopDiam/2) * LEAF_BLOCK_SIZE) + trunkHeight;
        int startX = treeX - (treeTopDiam/2) * LEAF_BLOCK_SIZE;
        int endX = startX + treeTopDiam * LEAF_BLOCK_SIZE;

        for (int x = startX; x < endX; x += LEAF_BLOCK_SIZE) {
            for (int y = 0; y < treeTopDiam  ; y++) {

                // creating render-ability for all leaf blocks:
                RectangleRenderable rectangleRenderable = new
                        RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR));

                // creating a leaf block, tagging it and adding into gameObjects:
                int curHeight = maxTopTreeHeight - (y * LEAF_BLOCK_SIZE);
                Vector2 leftTopCorner = new Vector2(x, curHeight);
                Leaf leaf = new Leaf(leftTopCorner, Vector2.ONES.mult(LEAF_BLOCK_SIZE), rectangleRenderable);

                leaf.setTag("tree_top");
                this.gameObjects.addGameObject(leaf, this.leafLayer);
            }
        }
    }


}
