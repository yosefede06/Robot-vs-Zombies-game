package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;
import pepse.util.SegmentCompletion;

import java.awt.*;
import java.util.List;

/**
 * A class that produces all the necessary land blocks and will allow other objects to know what the height of the
 * ground is at a given X coordinate.
 */
public class Terrain {
    private static final int MIN_BLOCKS_THRESHOLD = 3;
    private final GameObjectCollection gameObjects;
    private final static int X_NORMALIZE = 600;
    private final static int BLOCKS_TO_ADD_AFTER_NOISE = 10;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 12;
    private static final String TAG_GROUND = "ground";
    private final int seed;
    private int groundLayer;
    private Vector2 windowDimensions;
    private final float groundHeightAtX0;

    /**
     * Constructor of the Terrain
     * @param gameObjects Game objects to add.
     * @param groundLayer Layer to locate the object in the game.
     * @param windowDimensions Game's window dimensions.
     * @param seed set value that will be used tu calculate the groundHeightAt method of the class.
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = windowDimensions.y()*((float) 2 / 3);
        this.seed = seed;
    }

    /**
     * Generate a procedurally generated terrain in a video game, where the ground is represented by a series of blocks.
     * The Perlin noise generator is used to create a smooth, random variation in the height of the ground, and
     * the threshold ensures that there is always a minimum number of blocks present.
     * @param x x position of the column.
     * @return Returns the height of the column.
     */
    public float groundHeightAt(float x){
        // Create a new Perlin noise generator using the seed value
        PerlinNoise perlin = new PerlinNoise(seed);
        // Generate noise value for the given x coordinate using the perlin noise generator
        double perlinNoiseY = perlin.noise(x / X_NORMALIZE);
        // Calculate the position for a block on the ground using the window height and the perlin noise value
        float pos = (float) (windowDimensions.y() * (1 - perlinNoiseY) - (Block.SIZE * BLOCKS_TO_ADD_AFTER_NOISE));
        // Return the block position after applying the threshold function
        return addThreshold(pos);
    }

    /**
     * Threshold that makes sure always will be a floor of MIN_BLOCKS_THRESHOLD.
     * @param pos desire block position.
     * @return New block position.
     */
    private float addThreshold(float pos) {
        // Return the minimum of the input position and the position of the
        // ground minus the size of a minimum number of blocks
        return Math.min(pos, windowDimensions.y() - (MIN_BLOCKS_THRESHOLD + 1) * Block.SIZE);
    }

    /**
     * Build the ground of the game on the range (x1, x2) on x axis.
     * @param minX The minimal x ground.
     * @param maxX the maximal x ground
     */
    public void createInRange(int minX, int maxX) {
        // Create a new object to track the completion of terrain segments
        SegmentCompletion segmentCompletion = new SegmentCompletion(minX, maxX, Block.SIZE);
        // If there are no blocks to create, return
        if(minX == 0 && maxX == 0) {return;}
        // Calculate the starting x-position for the ground
        int startX = segmentCompletion.calculateStartX();
        // Calculate the total number of columns of blocks to create
        int numBlocksCol = segmentCompletion.calculateNumberOfCols(startX);
        // Get the maximum height of the window
        int maxHeight = (int) windowDimensions.y();
        // Initialize variables to track the current column and x-coordinate
        int currColumn = 0;
        int currCounter = startX;
        // Loop through each column of blocks
        for(int col = 0; col < numBlocksCol; col++) {
            // Calculate the y-coordinate for the ground at the current x-coordinate
            int coord = (int) Math.floor(groundHeightAt(currCounter) / Block.SIZE) * Block.SIZE;
            // Calculate the number of blocks to create in the current column
            int numBlocksInCol = TERRAIN_DEPTH + (maxHeight - coord) / Block.SIZE;
            // Calculate the x-coordinate for the current column
            int currX = startX + currColumn * Block.SIZE;
            // Loop through each block in the current column
            for(int i = 0; i < numBlocksInCol; i++) {
                // Create a new block at the current position
                Vector2 currPosition = new Vector2(currX, coord + i * Block.SIZE);
                Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(currPosition, renderable);
                // Add the block to the game objects with a specific layer based on its position
                if(i == 0 || i == 1) {
                    gameObjects.addGameObject(block, groundLayer - 1);
                }
                else {
                    gameObjects.addGameObject(block, groundLayer);
                }
                // Set the block's tag to "ground"
                block.setTag(TAG_GROUND);
            }
            // Update the current x-coordinate and column for the next iteration
            currCounter += Block.SIZE;
            currColumn++;
        }
    }



}

