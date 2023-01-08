package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.util.HtmlTableGenerator;
import pepse.world.*;
import pepse.world.ui.BoardInfo;
import pepse.world.ui.deadUI.Kills;
import pepse.world.ui.energy.Energy;
import pepse.world.ui.lifeTime.LifeTime;
import pepse.world.ui.lives.GraphicLifeCounter;
import pepse.world.ui.lives.NumericLifeCounter;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;
import pepse.world.zombies.Zombies;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * This is a Java class called "PepseGameManager" that extends the "GameManager" class. The class has a number of
 * private static final variables, as well as some private variables. There is a main method and an "initializeGame"
 * method that overrides a method from the parent class. There are also some private variables that are used to store
 * objects such as a "skyObject", "nightObject", and "terrainObject". There is also an "imageReader" object,
 * an "inputListener" object, and a "windowController" object. Finally, there are some private variables for counters
 * and a map of data stored in a "dataTable" object.
 */

public class PepseGameManager extends GameManager {
    private static final String STRING_SEED = "Series";
    private static final int HEART_DIMENSIONS = 20;
    private static final float SCALE_TIME_SUN = 30;
    private static final Color SUN_AURA_COLOR = new Color(255, 255, 0, 20);
    private static final int SECONDS_PER_DAY = 86400;
    private static final float TIME_LIFE_GAME = 60;
    private static final int MAXIMUM_SEED = 1000;
    private int seed = 42;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND;
    private static final int LIFE_TIME_LAYER = Layer.UI;
    private static final int NUMERIC_LIFE_LAYER = Layer.UI;
    private static final int LAYER_HEART = Layer.UI;
    private static final int TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int TOP_TERRAIN_BLOCKS_LAYER = TERRAIN_LAYER - 1;
    private static final int TREE_LAYER = Layer.STATIC_OBJECTS + 1;
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 2;
    private static final int ZOMBIES_LAYER = Layer.STATIC_OBJECTS + 3;
    private static final int ZOMBIE_LAYER = Layer.STATIC_OBJECTS + 4;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int BULLET_LAYER = AVATAR_LAYER + 2;
    private static final float FRAME_OUT_WINDOW_INFINITY = 5 * Block.SIZE;
    private static final int MAX_ENERGY = 200;
    private static final int NUM_LIVES = 3;
    private static final int MAX_LIFE_TIME = 60;
    private static final int KILLS_LAYER = Layer.UI;
    private Vector2 windowDimensions;
    private GameObject skyObject;
    private GameObject nightObject;
    private GameObject sunObject;
    private GameObject sunHaloObject;
    private Terrain terrainObject;
    private Tree treeObject;
    private GameObject avatarObject;
    private ImageReader imageReader;
    private UserInputListener inputListener;
    private WindowController windowController;
    private Vector2 initialAvatarLocation;
    private float minCurrWindow;
    private float maxCurrWindow;
    private Zombies zombiesObject;
    private Counter livesCounter;
    private GraphicLifeCounter graphicLifeCounter;
    private NumericLifeCounter numericLifeCounter;
    private Counter lifeTimeCounter;
    private Counter killsCounter;
    private int MAX_KILLS = 10;
    private Map<String, ArrayList<String>> dataTable;
    private ArrayList<String> seedArray;
    private String strSeed;


    /**
     * Main static void that runs the program.
     * @param args args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * Overrides the initializeGame method which is called at the beginning of the game.
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = windowController.getWindowDimensions();
        minCurrWindow = 0 - FRAME_OUT_WINDOW_INFINITY;
        maxCurrWindow = windowDimensions.x() + FRAME_OUT_WINDOW_INFINITY;
        livesCounter = new Counter(NUM_LIVES);
        initializeSeedTable();
        initializeSky();
        initializeSun();
        initializeSunAura();
        initializeTerrain((int) minCurrWindow, (int) maxCurrWindow);
        initializeTrees((int) minCurrWindow, (int) maxCurrWindow);
        initializeBoardInfo();
        initializeLifeTime();
        initializeKills();
        initializeZombies((int) minCurrWindow, (int) maxCurrWindow);
        initializeAvatar();
        initializeNight();
        initializeInfiniteWorld();
        initializeNumericLifeCounter();
        initializeHearts();
    }

    /**
     * Initializes the seed table by reading from the "seed.html" file and storing the data in a map.
     * If the seed for this series does not exist in the map, it creates an entry for it.
     * The seed value is determined by the hashcode of the STRING_SEED variable.
     */
    private void initializeSeedTable() {
        int hash = STRING_SEED.hashCode();
        seed = Math.abs(hash) % MAXIMUM_SEED + 1;
        dataTable = HtmlTableGenerator.readTable("seed.html");
        seedArray = dataTable.get(STRING_SEED);
        if(seedArray == null) {
            seedArray = new ArrayList<String>();
            dataTable.put(STRING_SEED, seedArray);
        }
    }


    /**
     * Updates the modified data in the seed table by generating a new "seed.html" file.
     */
    private void updateSeedTable() {
        HtmlTableGenerator.generateTable(dataTable, "seed.html");
    }

    /**
     * Initializes the board info by creating a new BoardInfo object and adding it to the gameObjects list.
     */
    private void initializeBoardInfo() {
        BoardInfo.create(gameObjects(), imageReader, KILLS_LAYER);
    }

    /**
     * Initializes the kills counter and creates a new Kills object, adding it to the gameObjects list.
     */
    private void initializeKills() {
        killsCounter = new Counter(0);
        Kills.create(gameObjects(), imageReader, KILLS_LAYER, killsCounter, MAX_KILLS);
    }

    /**
     * Initializes the life time counter and creates a new LifeTime object, adding it to the gameObjects list.
     * Then, creates a transition that decrements the life time counter every second until it reaches zero.
     */
    private void initializeLifeTime() {
        lifeTimeCounter = new Counter(MAX_LIFE_TIME);
        GameObject lifeTime = LifeTime.create(gameObjects(),
                imageReader,
                LIFE_TIME_LAYER,
                lifeTimeCounter,
                MAX_LIFE_TIME);
        // Simulates a timer of TIME_LIFE_GAME seconds.
        new Transition<>(lifeTime,
                (t) -> {
                if(TIME_LIFE_GAME - Math.ceil(t) < lifeTimeCounter.value()) {lifeTimeCounter.decrement();}
                },
                0f,
                TIME_LIFE_GAME,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TIME_LIFE_GAME,
                Transition.TransitionType.TRANSITION_ONCE,
                null);
    }

    /**
     * Set the collisions according to respective layers
     */
    private void handleCollisions() {
        if(!gameObjects().isLayerEmpty(LEAF_LAYER)) {
            gameObjects().layers().shouldLayersCollide(LEAF_LAYER, TOP_TERRAIN_BLOCKS_LAYER, true);
        }
        if(!gameObjects().isLayerEmpty(ZOMBIE_LAYER)) {
            gameObjects().layers().shouldLayersCollide(ZOMBIE_LAYER, TOP_TERRAIN_BLOCKS_LAYER, true);
            gameObjects().layers().shouldLayersCollide(ZOMBIE_LAYER, AVATAR_LAYER, true);
            if(!gameObjects().isLayerEmpty(BULLET_LAYER)) {
                gameObjects().layers().shouldLayersCollide(ZOMBIE_LAYER, BULLET_LAYER, true);
                gameObjects().layers().shouldLayersCollide(TOP_TERRAIN_BLOCKS_LAYER, BULLET_LAYER, true);
                gameObjects().layers().shouldLayersCollide(TREE_LAYER, BULLET_LAYER, true);
            }
        }
        if(!gameObjects().isLayerEmpty(AVATAR_LAYER)) {
            if(!gameObjects().isLayerEmpty(TREE_LAYER)) {
                gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TREE_LAYER, true);
            }
            gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TOP_TERRAIN_BLOCKS_LAYER, true);
        }
    }

    /**
     * Responsible for initializing the zombies.
     */
    private void initializeZombies(int min, int max) {
        zombiesObject = new Zombies(gameObjects(), ZOMBIES_LAYER, terrainObject::groundHeightAt, seed,
                imageReader, livesCounter, killsCounter);
        zombiesObject.createInRange(min, max);
    }

    private void initializeInfiniteWorld() {
//        Vector2 deltaRelativeToObject = windowDimensions.mult(0.5f).add(initialAvatarLocation.mult(-1));
        setCamera(new Camera(avatarObject, Vector2.ZERO, windowDimensions, windowDimensions));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        handleInfiniteWorld();
        handleCollisions();
        checkEndGame();
    }

    private void checkEndGame() {
        String prompt = "";
        if(killsCounter.value() >= MAX_KILLS) {
            prompt = "You win!";
        }
        if (livesCounter.value() <= 0 || lifeTimeCounter.value() <= 0) {
            prompt = "You Lose!";
        }
        if(!prompt.isEmpty()) {
            prompt += " Play again?";
            if(windowController.openYesNoDialog(prompt)) {
                windowController.resetGame();
            }
            else {
                windowController.closeWindow();
            }
        }

    }


    private void handleInfiniteWorld() {
        float currAvatarPosition = avatarObject.getCenter().x();
        if(currAvatarPosition - minCurrWindow - Block.SIZE < windowDimensions.x() / 2  ||
                maxCurrWindow - currAvatarPosition - Block.SIZE < windowDimensions.x() / 2) {
            if(currAvatarPosition - minCurrWindow - Block.SIZE < windowDimensions.x() / 2) {
                initializeTerrain((int) minCurrWindow - Block.SIZE, (int) minCurrWindow);
                initializeTrees((int) minCurrWindow - Block.SIZE, (int) minCurrWindow);
                initializeZombies((int) minCurrWindow - Block.SIZE, (int) minCurrWindow);
                minCurrWindow -= Block.SIZE;
                maxCurrWindow -= Block.SIZE;
            }
            else {
                initializeTerrain((int) maxCurrWindow, (int) maxCurrWindow + Block.SIZE);
                initializeTrees((int) maxCurrWindow, (int) maxCurrWindow + Block.SIZE);
                initializeZombies((int) maxCurrWindow, (int) maxCurrWindow + Block.SIZE);
                minCurrWindow += 2 * Block.SIZE;
                maxCurrWindow += 2 * Block.SIZE;
            }
            deleteObjectsOutOfWindow();
        }
    }

    /**
     * Responsible for initializing the avatar (main character of the game).
     */
    private void initializeAvatar() {
        initialAvatarLocation = new Vector2(windowDimensions.x() / 2, windowDimensions.y() / 3);
        avatarObject = Avatar.create(gameObjects(),
                AVATAR_LAYER,
                initialAvatarLocation,
                inputListener,
                imageReader);
    }

    /**
     *
     */
    private void deleteObjectsOutOfWindow() {
        for (GameObject obj : gameObjects().objectsInLayer(TERRAIN_LAYER)) {
            if (obj.getCenter().x() < minCurrWindow || obj.getCenter().x() > maxCurrWindow) {
                gameObjects().removeGameObject(obj, TERRAIN_LAYER);
            }
        }
        for (GameObject obj : gameObjects().objectsInLayer(TREE_LAYER)) {
            if (obj.getCenter().x() < minCurrWindow || obj.getCenter().x() > maxCurrWindow) {
                gameObjects().removeGameObject(obj, TREE_LAYER);
            }
        }
        for (GameObject obj : gameObjects().objectsInLayer(AVATAR_LAYER)) {
            if (obj.getCenter().x() < minCurrWindow || obj.getCenter().x() > maxCurrWindow) {
                gameObjects().removeGameObject(obj, Layer.DEFAULT);
            }
        }
        for (GameObject obj : gameObjects().objectsInLayer(LEAF_LAYER)) {
            if (obj.getCenter().x() + 5 * Block.SIZE < minCurrWindow || obj.getCenter().x() - 5 * Block.SIZE > maxCurrWindow) {
                gameObjects().removeGameObject(obj, LEAF_LAYER);
            }
        }
        if(!gameObjects().isLayerEmpty(ZOMBIE_LAYER)) {
            for (GameObject obj : gameObjects().objectsInLayer(ZOMBIE_LAYER)) {
                if (obj.getCenter().x() < minCurrWindow || obj.getCenter().x() > maxCurrWindow) {
                    gameObjects().removeGameObject(obj, ZOMBIE_LAYER);
                }
            }
        }
        if(!gameObjects().isLayerEmpty(BULLET_LAYER)) {
            for (GameObject obj : gameObjects().objectsInLayer(BULLET_LAYER)) {
                if (obj.getCenter().x() < minCurrWindow || obj.getCenter().x() > maxCurrWindow) {
                    gameObjects().removeGameObject(obj, BULLET_LAYER);
                }
            }
        }

    }


    /**
     * Initializes the sky and night objects and adds them to the gameObjects list.
     */
    private void initializeSky() {
        skyObject = Sky.create(gameObjects(), windowDimensions, SKY_LAYER);
    }

    /**
     * Responsible for initializing the night.
     */
    private void initializeNight() {
        nightObject = Night.create(gameObjects(), NIGHT_LAYER, windowDimensions, SCALE_TIME_SUN);
    }

    /**
     * Initializes the terrain object and adds it to the gameObjects list.
     * The terrain is generated using the seed value.
     */
    private void initializeTerrain(int min, int max) {
        terrainObject = new Terrain(gameObjects(), TERRAIN_LAYER, windowDimensions, seed);
        terrainObject.createInRange(min, max);
    }

    /**
     * Responsible for initializing the trees.
     * @param min
     * @param max
     */
    private void initializeTrees(int min, int max) {
        treeObject = new Tree(gameObjects(),
                TREE_LAYER,
                terrainObject::groundHeightAt,
                seed, windowController,
                seedArray,
                this::updateSeedTable);
        treeObject.createInRange(min, max);
    }

    /**
     * Responsible for initializing the sun.
     */
    private void initializeSun() {
        sunObject = Sun.create(gameObjects(), SUN_LAYER, windowDimensions, SCALE_TIME_SUN);
    }

    /**
     * Responsible for initializing the sun aura.
     */
    private void initializeSunAura() {
        sunHaloObject = SunHalo.create(gameObjects(), SUN_HALO_LAYER, sunObject, SUN_AURA_COLOR);
    }

    /**
     * This function initializes the graphic lives counter that are heart that correspond to how many lives
     * left to the player.
     */
    private void initializeHearts() {
        graphicLifeCounter = GraphicLifeCounter.create(livesCounter,
                gameObjects(),
                imageReader,
                NUM_LIVES,
                LAYER_HEART);
    }

    /**
     * This function initializes the numeric life counter (3 in the beginning)
     */
    private void initializeNumericLifeCounter() {
        numericLifeCounter = NumericLifeCounter.create(livesCounter, gameObjects(), NUMERIC_LIFE_LAYER);
    }
}
