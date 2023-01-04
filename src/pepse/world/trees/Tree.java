package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class Tree {

    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    private static final int TRUNK_BLOCK_SIZE = 30;
    private static final int LEAF_BLOCK_SIZE = 30;

    private static final int TREE_CHANCE = 10;
    private static final int MAX_TREE_HEIGHT = 12;
    private static final int MIN_TREE_HEIGHT = 7;

    private static final int MAX_TREE_TOP_DIAM_BLOCKS = 5;
    private static final int MIN_TREE_TOP_DIAM_BLOCKS = 3;

    private static final int DEFAULT_VALUE = 600;
    private static final String TREE_TRUNK = "tree_trunk";
    private static final String TREE_TOP = "tree_top";
    private static final int TREE_CONDITION = 1;


    private final Function<Integer, Float> groundHeightAtFunc;
    private final GameObjectCollection gameObjects;
    private final int treeLayer;
    private final int leafLayer;
    private final Random random;
    private int minXOnTerrain;
    private int maxXOnTerrain;

    //    private final ArrayList<Integer> treeXLocations;
    private final TreeMap<Integer, ArrayList<GameObject>> treeTreeMap;

    private int seed;
//    private final int creationFactor;



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
        this.seed = seed;
//        this.creationFactor = creationFactor;

        this.minXOnTerrain = DEFAULT_VALUE; // default value
        this.maxXOnTerrain = DEFAULT_VALUE; // default value

        this.random = new Random(seed);

        this.treeTreeMap = new TreeMap<Integer, ArrayList<GameObject>>();
    }


    /**
     * A method that creates trees between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */


    /**
     * A method that creates the trunk of the tree with bottom left corner given at (leftX, leftY)
     * @param leftX represents the X of the bottom left corner
     * @param leftY represents the Y of the bottom left corner
     * @return returns the height of the tree
     */

    private void createTrunk(int positionX, float groundHeight, ArrayList<GameObject> treeObject,
                             Random random) {
        int trunkHeightBlocks = MIN_TREE_HEIGHT + random.nextInt(MAX_TREE_HEIGHT - MIN_TREE_HEIGHT);
        float trunkBlockCenterY = groundHeight - TRUNK_BLOCK_SIZE;
        for (int i = 0; i < trunkHeightBlocks; i++) {
            RectangleRenderable rectangleRenderable = new
                    RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR));
            GameObject trunkBlock = new GameObject(Vector2.ZERO, Vector2.ONES.mult(TRUNK_BLOCK_SIZE),
                    rectangleRenderable);
            trunkBlock.setCenter(new Vector2(positionX, trunkBlockCenterY));
            System.out.println("center");
            trunkBlock.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            trunkBlock.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
            trunkBlock.setTag(TREE_TRUNK);
            this.gameObjects.addGameObject(trunkBlock, this.treeLayer);
            treeObject.add(trunkBlock);
            trunkBlockCenterY -= TRUNK_BLOCK_SIZE;
        }
    }

    /**
     * A method that creates the tree top at a given X and given tree height with change of 1/2 to be with
     * diameter size of 5 and 1/2 chance to be with diameter size of 7
     * @param treeX represents the X where the tree at
     * @param trunkHeight represents the height of the tree
     */

    private void createTreeTop(int positionX, int groundHeightAtX, ArrayList<GameObject> treeObjects, Random random) {
        int trunkHeight = (int) groundHeightAtX - treeObjects.size() * TRUNK_BLOCK_SIZE;
        int treeTopDiameterBlocks = random.nextBoolean() ? MIN_TREE_TOP_DIAM_BLOCKS : MAX_TREE_TOP_DIAM_BLOCKS;
        int treeTopMaxHeight = trunkHeight +((treeTopDiameterBlocks / 2) * LEAF_BLOCK_SIZE);
        int treeTopMinHeight = trunkHeight - ((treeTopDiameterBlocks / 2) * LEAF_BLOCK_SIZE);
        int leftEdgeX = positionX - (treeTopDiameterBlocks / 2) * LEAF_BLOCK_SIZE;
        int rightEdge = leftEdgeX + treeTopDiameterBlocks * LEAF_BLOCK_SIZE;

        for (int x = leftEdgeX; x < rightEdge; x += LEAF_BLOCK_SIZE) {
            for (int y = treeTopMinHeight; y < treeTopMaxHeight ; y += LEAF_BLOCK_SIZE) {
                RectangleRenderable rectangleRenderable = new
                        RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR));
                Vector2 leafCenter = new Vector2(x, y);
                Leaf leaf = new Leaf(Vector2.ZERO, Vector2.ONES.mult(LEAF_BLOCK_SIZE), rectangleRenderable);
                leaf.setCenter(leafCenter);
                leaf.setTag(TREE_TOP);
                this.gameObjects.addGameObject(leaf, this.leafLayer);
                treeObjects.add(leaf);
            }

        }
    }

    /**
     * A helper method that creates trees between minX and maxX
     * @param minX represents the lower bound
     * @param maxX represents the top bound
     */




    private boolean treeCondition(Map.Entry<Integer, ArrayList<GameObject>> extremityEntry, int newPositionX,
                                  Random random) {
        if(extremityEntry == null || extremityEntry.getValue().size() == 0) {
            //TODO: try to use the same random for all tree elements.
            int tiltedCoinToss = random.nextInt(TREE_CHANCE);
            if (tiltedCoinToss == TREE_CONDITION)
                return true;
        }
        return false;
    }


    /**
     * A helper method that creates one tree with a change: 1/TREE_CHANCE at a given x
     * @param x represents the given x
     */

    private void createTree(int positionX, ArrayList<GameObject> treeObjects, Random random) {
        float groundHeightAtX = (groundHeightAtFunc.apply(positionX));
        createTrunk(positionX, groundHeightAtX, treeObjects, random);
        int trunkHeight = (int) groundHeightAtX - treeObjects.size() * TRUNK_BLOCK_SIZE;
//        createTreeTop(positionX, trunkHeight, treeObjects);

        createTreeTop(positionX, (int) groundHeightAtX, treeObjects, random);
    }

    public void createInRange(int minX, int maxX) {
        for (int i = minX; i < maxX; i += TRUNK_BLOCK_SIZE) {
            Map.Entry<Integer, ArrayList<GameObject>> lastEntry = treeTreeMap.lastEntry();
            ArrayList<GameObject> treeObjects = new ArrayList<>();
            Random random = new Random(Objects.hash(i, this.seed));
            if(treeCondition(lastEntry, i, random))
                createTree(i, treeObjects, random);
            treeTreeMap.put(i, treeObjects);
        }
    }

    public void extendRight() {
        Map.Entry<Integer, ArrayList<GameObject>> lastEntry = treeTreeMap.lastEntry();
        int nextPositionX = lastEntry.getKey() + TRUNK_BLOCK_SIZE;
        ArrayList<GameObject> treeObjects = new ArrayList<>();
        Random random = new Random(Objects.hash(nextPositionX, this.seed));
        if(treeCondition(lastEntry, nextPositionX, random))
            createTree(nextPositionX, treeObjects, random);
        treeTreeMap.put(nextPositionX, treeObjects);
        removeTreeObjects(treeTreeMap.pollFirstEntry().getValue());
    }

    public void extendLeft() {
        Map.Entry<Integer, ArrayList<GameObject>> firstEntry = treeTreeMap.firstEntry();
        int nextPositionX = firstEntry.getKey() - TRUNK_BLOCK_SIZE;
        ArrayList<GameObject> treeObjects = new ArrayList<>();
        Random random = new Random(Objects.hash(nextPositionX, this.seed));
        if(treeCondition(firstEntry, nextPositionX, random))
            createTree(nextPositionX, treeObjects, random);
        treeTreeMap.put(nextPositionX, treeObjects);
        removeTreeObjects(treeTreeMap.pollLastEntry().getValue());

    }

    /**
     * a helper method that clears the trees that are out of the range
     * @param minX
     * @param maxX
     */



    private void removeTreeObjects(ArrayList<GameObject> treeObjects) {
        if(treeObjects != null)
            for(GameObject treeObject : treeObjects) {
                int objectLayer;
                if(treeObject.getClass() == Leaf.class)
                    objectLayer = leafLayer;
                else objectLayer = treeLayer;
                this.gameObjects.removeGameObject(treeObject, objectLayer);
            }
    }






    /**
     * A method that returns True if there is a tree at a given x and false otherwise
     * @param x represents the given x
     * @return True if there is a tree at x, false otherwise
     */
    public boolean treeAtX(int x){
        return this.treeTreeMap.containsKey(x);
    }

    /**
     * A method that checks if there are trees
     * @return true if there is, false otherwise
     */
    public boolean hasTrees() {
        return !this.treeTreeMap.isEmpty();
    }

}
