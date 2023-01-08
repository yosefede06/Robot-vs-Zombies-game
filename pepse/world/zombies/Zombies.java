package pepse.world.zombies;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.util.SegmentCompletion;
import pepse.world.Block;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;


/**
 * This class is responsible for generating and managing the zombies in the game. It uses a seed value and a
 * probability of zombie appearance to determine whether to create a new zombie at a given position. The
 * Zombies class also tracks the lives and kills of the player using the given Counter objects.
 */
public class Zombies {
    private static final double PROBABILITY_OF_ZOMBIE_APPEAREANCE = 0.1;
    private final ImageReader imageReader;
    private Counter livesCounter;
    private Counter killsCounter;
    private final GameObjectCollection gameObjects;
    private final int zombiesLayer;
    private final Function<Float, Float> getTreeHeight;
    private final int seed;
    private final Random random = new Random();


    /**
     * Constructs a new Zombies object.
     * @param gameObjects the collection of game objects
     * @param zombiesLayer the layer at which to add the zombies
     * @param getTreeHeight a function that returns the height of a tree at a given x-position
     * @param seed the seed value to use for generating the zombies
     * @param imageReader an image reader to use for reading images from disk
     * @param livesCounter a counter for tracking the lives of the player
     * @param killsCounter a counter for tracking the number of kills made by the player
     */
    public Zombies(GameObjectCollection gameObjects,
                   int zombiesLayer,
                   Function<Float,Float> getTreeHeight,
                   int seed,
                   ImageReader imageReader,
                   Counter livesCounter,
                   Counter killsCounter) {
        this.gameObjects = gameObjects;
        this.zombiesLayer = zombiesLayer;
        this.getTreeHeight = getTreeHeight;
        this.seed = seed;
        this.imageReader = imageReader;
        this.livesCounter = livesCounter;
        this.killsCounter = killsCounter;
    }

    /**
     * Build the zombies of the game on the range (x1, x2) on x axis.
     * @param minX The minimal x ground.
     * @param maxX the maximal x ground
     */
    public void createInRange(int minX, int maxX) {
        SegmentCompletion segmentCompletion = new SegmentCompletion(minX, maxX, Block.SIZE);
        // checks for no zombies
        if(minX == 0 && maxX == 0) {return; }
        // set the start x position for the zombies
        int startX = segmentCompletion.calculateStartX();
        // set the total number of columns
        int numBlocksCol = segmentCompletion.calculateNumberOfCols(startX);
        // Iterates over columns, checks if it should add a zombie, if that's the case, adds it to the game.
        for(int col = 0; col < numBlocksCol; col++) {
            // Calculates the x current position.
            int currX = startX + col * Block.SIZE;
            // Set the seed to the random function which is used for the zombie's appeareance and height.
            random.setSeed(Objects.hash(currX, seed));
            // Adds a new zombie to the game if it should appear.
            if(checkIfZombie()) {
                createZombie(currX);
            }
        }
    }

    /**
     * Creates a new Zombie object at the given x-position.
     * @param currX the x-position at which to create the Zombie
     */
    private void createZombie(int currX) {
        Zombie zombie = Zombie.create(gameObjects,
                zombiesLayer + 1,
                new Vector2(currX, getTreeHeight.apply((float) currX) - 3 * Block.SIZE),
                imageReader,
                random,
                livesCounter,
                killsCounter);
    }

    /**
     * Returns true if a Zombie should appear at the current position based on the probability of zombie
     * appearance.
     * @return true if a Zombie should appear, false otherwise
     */
    private boolean checkIfZombie() {
        return random.nextDouble() >= 1 - PROBABILITY_OF_ZOMBIE_APPEAREANCE;
    }
}
