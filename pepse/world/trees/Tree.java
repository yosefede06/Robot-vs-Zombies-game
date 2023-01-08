package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.WindowController;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.SegmentCompletion;
import pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

/**
 * A class that creates the tree of the game. It represents a static tree that throws leaves.
 */
public class Tree {
    static final Color TREE_COLOR = new Color(100, 50, 20);
    static final Color LEAVES_COLOR = new Color(50, 200, 30);
    static final Random random = new Random();
    private static final String TAG_TREE = "tree";
    private static final double PROBABILITY_OF_TREE_APPEAREANCE = 0.05;
    private static final double PROBABILITY_OF_LEAF_APPEAREANCE = 0.45;
    private static final float SCALE_TREE_TOP_SIZE = 1.5f;
    private static final int MAXIMUM_TREE_BLOCKS_HEIGHT = 18;
    private static final int MINIMUM_TREE_BLOCKS_HEIGHT = 6;
    private final GameObjectCollection gameObjects;
    private final int treeLayer;
    private final Function<Float, Float> getTreeHeight;
    private final int seed;
    private final WindowController windowController;
    private final ArrayList<String> seedArray;
    private final Runnable updateTable;
    private float lastCheck = -1f;

    /**
     * Constructor of Tree class that initializes all the parameters as fields of the class.
     * @param gameObjects Game objects to add.
     * @param treeLayer Layer to locate the object in the game.
     * @param getTreeHeight Function that returns the height of the terrain on a specific column.
     * @param seed Seed that will be passed to the random functions (all program runs with same seed,
     *             will output the same heights and positions for random calculations)
     */
    public Tree(GameObjectCollection gameObjects,
                int treeLayer,
                Function<Float,Float> getTreeHeight,
                int seed,
                WindowController windowController,
                ArrayList<String> seedArray,
                Runnable updateTable) {
        this.gameObjects = gameObjects;
        this.treeLayer = treeLayer;
        this.getTreeHeight = getTreeHeight;
        this.seed = seed;
        this.windowController = windowController;
        this.seedArray = seedArray;
        this.updateTable = updateTable;
    }

    /**
     * Build the trees of the game on the range (x1, x2) on x axis.
     * @param minX The minimal x ground.
     * @param maxX the maximal x ground
     */
    public void createInRange(int minX, int maxX) {
        SegmentCompletion segmentCompletion = new SegmentCompletion(minX, maxX, Block.SIZE);
        // checks for no trees
        if(minX == 0 && maxX == 0) {return; }
        // set the start x position tree
        int startX = segmentCompletion.calculateStartX();
        // set the total number of columns
        int numBlocksCol = segmentCompletion.calculateNumberOfCols(startX);
        // Iterates over columns, checks if it should plant a tree, if that's the case, adds it to the game.
        for(int col = 0; col < numBlocksCol; col++) {
            // Calculates the x current position.
            int currX = startX + col * Block.SIZE;
            // Set the seed to the random function which is used for the tree's appeareance and height.
            random.setSeed(Objects.hash(currX, seed));
            // Adds a new tree to the game if it should appear.
            if(checkIfTree()) {
                createTree(currX);
            }
        }
    }

    /**
     * Creates a new tree and adds it to the game.
     * @param currX current column.
     */
    private void createTree(int currX) {
        int startTerrainInColumn = (int) Math.floor(getTreeHeight.apply((float) currX) / Block.SIZE) * Block.SIZE;
        int treeBlocks = treeBlocksRandomHeight();
        for(int j = 0; j < treeBlocks; j++) {
            Vector2 currPosition = new Vector2(currX, startTerrainInColumn - ((j + 1) * Block.SIZE));
            Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR));
            Block blockTree = new Block(currPosition, renderable);
            gameObjects.addGameObject(blockTree, treeLayer);
            blockTree.setTag(TAG_TREE);
        }
        addLeaves(currX, startTerrainInColumn - treeBlocks * Block.SIZE, treeBlocks);
    }

    /**
     * Add all the leaves on the top of the tree.
     * @param treeTopX Top tree x coordinate
     * @param treeTopY Top tree y coordinate
     */
    private void addLeaves(int treeTopX, int treeTopY, int treeBlocksSize) {
        int topTreeSize = Math.round(treeBlocksSize / SCALE_TREE_TOP_SIZE);
        for(int i = -topTreeSize / 2; i <= topTreeSize / 2; i++) {
            for(int j = -topTreeSize / 2; j <= topTreeSize / 2; j++) {
                if(checkIfLeaf()) {
                    addLeaf(new Vector2(i * Block.SIZE + treeTopX, j * Block.SIZE + treeTopY));
                }
            }
        }
    }

    /**
     * Add a new single leaf.
     * @param position Vector2 leaf's position.
     */
    private void addLeaf(Vector2 position) {
        Leaf leaf = new StickyNoteLeaf(position,
                random,
                new RectangleRenderable(ColorSupplier.approximateColor(LEAVES_COLOR)),
                windowController, seedArray, updateTable);
        gameObjects.addGameObject(leaf, treeLayer + 1);
    }

    /**
     * Private method that calculates the number of blocks needed to build a tree.
     * @return int number of blocks.
     */
    private int treeBlocksRandomHeight() {
        return random.nextInt(MAXIMUM_TREE_BLOCKS_HEIGHT - MINIMUM_TREE_BLOCKS_HEIGHT)
                + MINIMUM_TREE_BLOCKS_HEIGHT;
    }

    /**
     * Private method that checks if we encounter a tree in a column.
     * @return int true if there is a tree, false otherwise.
     */
    private boolean checkIfTree() {
        float a = (float) random.nextDouble();
        if(lastCheck == a) {
            return false;
        }
        lastCheck = a;
        return random.nextDouble() >= 1 - PROBABILITY_OF_TREE_APPEAREANCE;
    }

    /**
     * Private method that checks if we encounter a leaf in a block.
     * @return int true if there is a tree, false otherwise.
     */
    private boolean checkIfLeaf() {
        return random.nextDouble() >= 1 - PROBABILITY_OF_LEAF_APPEAREANCE;
    }
}
