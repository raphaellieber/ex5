package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

public class Tree {

    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    private static final int TRUNK_BLOCK_SIZE = 30;
    private static final int LEAF_BLOCK_SIZE = 30;

    private static final int TREE_CHANCE = 30;
    private static final int MAX_TREE_HEIGHT = 12;
    private static final int MIN_TREE_HEIGHT = 7;

//    private static final int DEFAULT_VALUE = -999;

    private final Function<Integer, Float> groundHeightAtFunc;
    private final GameObjectCollection gameObjects;
    private final int treeLayer;
    private final int leafLayer;
//    private final int groundLayer;
    private final Random random;

    public final ArrayList<Integer> treeXLocations;
    private int minXOnTerrain;
    private int maxXOnTerrain;

    /**
     * Constructor
     * @param groundHeightAt represents a function that gets a float number and returns the height of the
     *                      ground at that number
     * @param gameObjects the collection of all game objects currently in the game
     * @param treeLayer represents the layer of the trees
     * @param leafLayer represents the layer of the leafs
     * @param seed represents a seed for the random creator
     */
    public Tree (Function<Integer, Float> groundHeightAt, GameObjectCollection gameObjects, int treeLayer,
                 int leafLayer, int seed) {

        this.groundHeightAtFunc = groundHeightAt;
        this.gameObjects = gameObjects;
        this.treeLayer = treeLayer;
        this.leafLayer = leafLayer;
//        this.groundLayer = groundLayer;

        this.minXOnTerrain = 0 ; // default value
        this.maxXOnTerrain = 0; // default value

        this.random = new Random(seed);

        this.treeXLocations = new ArrayList<>();
    }

    /**
     * A method that creates trees between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */
    public void createInRange(int minX, int maxX) {
//            minX.......|(this.min)...................(this.max)|......maxX
        if (minX <= this.minXOnTerrain && maxX >= this.maxXOnTerrain) {
            createInRangeHelper(minX, this.minXOnTerrain);
            createInRangeHelper(this.maxXOnTerrain, maxX);
            this.minXOnTerrain = minX;
            this.maxXOnTerrain = maxX;
        }

        //   |(this.min)......minX...................(this.max)|......maxX
        else if (minX > this.minXOnTerrain && minX < this.maxXOnTerrain && maxX > this.maxXOnTerrain) {
            createInRangeHelper(this.maxXOnTerrain, maxX);
            this.maxXOnTerrain = maxX;
        }

        //   minX.......|(this.min)...................maxX......(this.max)|
        else if (maxX < this.maxXOnTerrain && maxX > this.minXOnTerrain && minX < this.minXOnTerrain) {
            createInRangeHelper(minX,this.minXOnTerrain);
            this.minXOnTerrain = minX;
        }
    }


    /**
     * A helper method that creates trees between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */
    private void createInRangeHelper(int minX, int maxX){

        // calculating the location of normalized minX and maxX:
        int LastBlockLocation = (int) (Math.ceil(maxX / TRUNK_BLOCK_SIZE) * TRUNK_BLOCK_SIZE);
        int firstBlockLocation = (minX / TRUNK_BLOCK_SIZE) * TRUNK_BLOCK_SIZE;
        if (minX < 0) { firstBlockLocation -= TRUNK_BLOCK_SIZE; }

        // Creating trees:
        for (int i = firstBlockLocation; i < LastBlockLocation; i += TRUNK_BLOCK_SIZE) { createTree(i); }

    }

    /**
     * A helper method that creates one tree with a change: 1/TREE_CHANCE at a given x
     * @param x represents the given x
     */
    private void createTree(int x) {

        // Tossing tilted coin with 1/TREE_CHANCE chance of creating a tree:
        Random rand = new Random();
        int tiltedCoinToss = rand.nextInt(TREE_CHANCE);

//        if (tiltedCoinToss == 0 && this.xOfLastTree != x-TRUNK_BLOCK_SIZE) {

        if (tiltedCoinToss == 0 && !this.treeXLocations.contains(x+30)
                                && !this.treeXLocations.contains(x-30)
                                && !this.treeXLocations.contains(x)) {
            // Calculating the location of left top corner of first block
            float groundHeightAtX = (groundHeightAtFunc.apply(x));

            // creating the tree
            int trunkHeight = createTrunk(x, groundHeightAtX);
            createTreeTop(x, trunkHeight);

            this.treeXLocations.add(x); // adding x into the tree locations collection
        }
    }

    /**
     * A method that creates the trunk of the tree with bottom left corner given at (leftX, leftY)
     * @param LeftX represents the X of the bottom left corner
     * @param LeftY represents the Y of the bottom left corner
     * @return returns the height of the tree
     */
    private int createTrunk(int LeftX, float LeftY) {
        int treeHeight = MIN_TREE_HEIGHT + this.random.nextInt(MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);

        for (int i = 0; i < treeHeight; i++) {
            // creating render-ability for all the blocks of the trunk:
            RectangleRenderable rectangleRenderable = new
                    RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR));

            // creating a trunk block, tagging it and adding into gameObjects:
            LeftY -= TRUNK_BLOCK_SIZE;
            Vector2 leftTopCorner = new Vector2(LeftX, LeftY);
            GameObject trunkBlock = new GameObject(leftTopCorner, Vector2.ONES.mult(TRUNK_BLOCK_SIZE),
                    rectangleRenderable);
            trunkBlock.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            trunkBlock.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
            trunkBlock.setTag("tree_trunk");
            this.gameObjects.addGameObject(trunkBlock, this.treeLayer);
        }
        // returning the y top left corner of the trunk
        return (int)LeftY;
    }

    /**
     * A method that creates the tree top at a given X and given tree height with change of 1/2 to be with
     * diameter size of 5 and 1/2 chance to be with diameter size of 7
     * @param treeX represents the X where the tree at
     * @param trunkHeight represents the height of the tree
     */
    private void createTreeTop(int treeX, int trunkHeight) {

        // determining tree top diameter:
        int treeTopDiam;

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

    /**
     * A method that returns True if there is a tree at a given x and false otherwise
     * @param x represents the given x
     * @return True if there is a tree at x, false otherwise
     */
    public boolean treeAtX(int x){
        return this.treeXLocations.contains(x);
    }

    /**
     * A method that checks if there are trees
     * @return true if there is, false otherwise
     */
    public boolean hasTrees() {
        return !this.treeXLocations.isEmpty();
    }

}
