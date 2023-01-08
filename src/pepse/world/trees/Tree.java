package pepse.world.trees;


import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.abstract_classes.ExtendableElement;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class Tree extends ExtendableElement {

    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    private static final int TRUNK_BLOCK_SIZE = 30;
    private static final int LEAF_BLOCK_SIZE = 30;

    private static final int TREE_CHANCE = 10;
    private static final int MAX_TREE_HEIGHT = 12;
    private static final int MIN_TREE_HEIGHT = 6;

    private static final int MAX_TREE_TOP_DIAM = 5;
    private static final int MIN_TREE_TOP_DIAM = 3;

    private static final String TREE_TRUNK = "tree_trunk";
    private static final String TREE_TOP = "tree_top";


    private final Function<Integer, Float> groundHeightAtFunc;
    private final GameObjectCollection gameObjects;
    private final int treeLayer;
    private final int leafLayer;
    private int minXOnTerrain;
    private int maxXOnTerrain;
    private boolean firstCreationFlag;

    private final HashMap<Integer, ArrayList<GameObject>> treeMap;
    private final int seed;

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
        super();
        super.setBlockSize(TRUNK_BLOCK_SIZE);

        this.groundHeightAtFunc = groundHeightAt;
        this.gameObjects = gameObjects;
        this.treeLayer = treeLayer;
        this.leafLayer = leafLayer;
        this.seed = seed;

        this.minXOnTerrain = 0; // default value
        this.maxXOnTerrain = 0; // default value
        this.firstCreationFlag = false; // default value

        this.treeMap = new HashMap<>();
    }

    /**
     * A method that creates trees between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */
    @Override
    public void createInRange(int minX, int maxX) {
        super.createInRange(minX, maxX);
        this.minXOnTerrain = super.getMinXCreated();
        this.maxXOnTerrain = super.getMaxXCreated();

        createInRangeHelper(super.getMinXToUpdate(), super.getMaxXToUpdate());

        if (this.firstCreationFlag) { clearTrees(); }
        this.firstCreationFlag = true;
    }

    /**
     * A helper method that creates trees between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */
    private void createInRangeHelper(int minX, int maxX){
        // Creating trees:
        for (int i = minX; i < maxX; i += TRUNK_BLOCK_SIZE) { createTree(i); }
    }

    /**
     * a helper method that clears the trees that are out of the range
     */
    private void clearTrees() {
        ArrayList<Integer> treeToRemoveXLoc = new ArrayList<>();

        // going through all the trees we have:
        for (Map.Entry<Integer,ArrayList<GameObject>> entry: this.treeMap.entrySet()) {

            // checking the boundaries of the trees
            // if out of boundaries, removes the objects of the current tree
            if (entry.getKey() > this.maxXOnTerrain | entry.getKey() < this.minXOnTerrain){
                for (GameObject obj: entry.getValue()){
                    if (obj.getTag().equals(TREE_TOP)) {
                        this.gameObjects.removeGameObject(obj, this.leafLayer);
                    }
                    else { this.gameObjects.removeGameObject(obj, this.treeLayer); }
                }
                treeToRemoveXLoc.add(entry.getKey());
            }
        }
        for (Integer x: treeToRemoveXLoc) { this.treeMap.remove(x);}
    }

    /**
     * A helper method that creates one tree with a change: 1/TREE_CHANCE at a given x
     * @param x represents the given x
     */
    private void createTree(int x) {

        // Tossing tilted coin with 1/TREE_CHANCE chance of creating a tree:
        Random rand = new Random(Objects.hash(x, this.seed));
        int tiltedCoinToss = rand.nextInt(TREE_CHANCE);

        if (tiltedCoinToss == 1 && !this.treeMap.containsKey(x+30)
                                && !this.treeMap.containsKey(x-30)
                                && !this.treeMap.containsKey(x)) {

            // Calculating the location of left top corner of first block
            float groundHeightAtX = (groundHeightAtFunc.apply(x));

            // will hold all the objects of the current tree;
            ArrayList<GameObject> treeObjects = new ArrayList<>();

            // creating the tree
            int trunkHeight = createTrunk(x, groundHeightAtX, treeObjects);
            createTreeTop(x, trunkHeight, treeObjects);

//            this.treeXLocations.add(x); // adding x into the tree locations collection
            this.treeMap.put(x, treeObjects);
        }
    }

    /**
     * A method that creates the trunk of the tree with bottom left corner given at (leftX, leftY)
     * @param leftX represents the X of the bottom left corner
     * @param leftY represents the Y of the bottom left corner
     * @return returns the height of the tree
     */
    private int createTrunk(int leftX, float leftY, ArrayList<GameObject> treeObject) {
        Random rand = new Random(Objects.hash(leftX, this.seed));
        int treeHeight = MIN_TREE_HEIGHT + rand.nextInt(MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);

        for (int i = 0; i < treeHeight; i++) {
            // creating render-ability for all the blocks of the trunk:
            RectangleRenderable rectangleRenderable = new
                    RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR));

            // creating a trunk block, tagging it and adding into gameObjects:
            leftY -= TRUNK_BLOCK_SIZE;
            Vector2 leftTopCorner = new Vector2(leftX, leftY);
            GameObject trunkBlock = new GameObject(leftTopCorner, Vector2.ONES.mult(TRUNK_BLOCK_SIZE),
                    rectangleRenderable);

            // setting physics
            trunkBlock.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            trunkBlock.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

            trunkBlock.setTag(TREE_TRUNK);
            this.gameObjects.addGameObject(trunkBlock, this.treeLayer);
            treeObject.add(trunkBlock);
        }
        // returning the y top left corner of the trunk
        return (int)leftY;
    }

    /**
     * A method that creates the tree top at a given X and given tree height with change of 1/2 to be with
     * diameter size of 5 and 1/2 chance to be with diameter size of 7
     * @param treeX represents the X where the tree at
     * @param trunkHeight represents the height of the tree
     */
    private void createTreeTop(int treeX, int trunkHeight, ArrayList<GameObject> treeObject) {

        // determining tree top diameter:
        int treeTopDiam;

        Random rand = new Random(Objects.hash(treeX, this.seed));
        if (rand.nextInt(2) == 1){ treeTopDiam = MAX_TREE_TOP_DIAM; }
        else {treeTopDiam = MIN_TREE_TOP_DIAM;}

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

                leaf.setTag(TREE_TOP);
                this.gameObjects.addGameObject(leaf, this.leafLayer);
                treeObject.add(leaf);
            }
        }
    }

    /**
     * A method that returns True if there is a tree at a given x and false otherwise
     * @param x represents the given x
     * @return True if there is a tree at x, false otherwise
     */
    public boolean hasTreeAtX(int x){
        return this.treeMap.containsKey(x);
    }

    /**
     * A method that checks if there are trees
     * @return true if there is, false otherwise
     */
    public boolean hasTrees() {
        return !this.treeMap.isEmpty();
    }

}
