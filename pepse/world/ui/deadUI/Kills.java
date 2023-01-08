package pepse.world.ui.deadUI;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * The Kills class is a subclass of GameObject, which represents an object in the game world.
 */
public class Kills extends GameObject {
    private static final Vector2 ENERGY_LABEL_POSITION = new Vector2(30, 170);
    private static final Vector2 LABEL_ENERGY_SIZE = new Vector2(144, 40);
    private final static Vector2 BAR_SIZE = new Vector2(54 * 1.85f, 10 * 1.85f);
    private final static String BAR_LABEL_IMAGE = "assets/ui/dead-label.png";
    private final static Vector2 BAR_KILLS_VECTOR = new Vector2(33 + ENERGY_LABEL_POSITION.x(),
            ENERGY_LABEL_POSITION.y() + 12);


    /**
     * Constructor of Kill
     * @param topLeftCorner a Vector2 object representing the position of the top-left corner of
     *                      the object
     * @param dimensions  the width and height of the object
     * @param renderable object representing the image or animation to be rendered for the object
     */
    public Kills(Vector2 topLeftCorner,
                Vector2 dimensions,
                Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * Creates an instance of Kills and adds it to a GameObjectCollection.
     * @param gameObjects the GameObjectCollection to which the Kills object should be added
     * @param imageReader  an ImageReader object used to read in image files
     * @param layer an integer representing the layer on which the Kills object should be
     *              added to the GameObjectCollection
     * @param energyCounter a Counter object representing the current number of energy
     * @param maxKills an integer representing the maximum number of kills the player can achieve
     * @return Kills object
     */
    public static Kills create(GameObjectCollection gameObjects,
                              ImageReader imageReader,
                              int layer,
                              Counter energyCounter,
                              int maxKills) {
        Kills killsLabel = new Kills(ENERGY_LABEL_POSITION,
                LABEL_ENERGY_SIZE,
                imageReader.readImage(BAR_LABEL_IMAGE, true));
        gameObjects.addGameObject(killsLabel, layer);
        killsLabel.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        // create bar inside energy label
        BarKills.create(gameObjects,
                imageReader,
                layer + 1,
                energyCounter,
                maxKills,
                BAR_KILLS_VECTOR,
                BAR_SIZE);
        return killsLabel;
    }
}

/**
 * The BarKills class is a subclass of GameObject that represents a bar that visually displays the player's
 * number of kills.
 */
class BarKills extends GameObject {
    private final static String BAR_ENERGY_IMAGE = "assets/ui/dead-bar.png";
    private final Vector2 initialPosition;
    private final Vector2 initialDimension;
    private final Counter killsCounter;
    private final int maxKills;
    private final static int ADD_TO_INITIAL_X = 3;
    private final static int FACTOR_X = 10;

    /**
     * Constructor of BarKills
     * @param topLeftCorner a Vector2 object representing the position of the top-left corner of
     *      *                      the object
     * @param dimensions a Vector2 object representing the dimensions of the object
     * @param renderable a Renderable object representing the visual representation of the object
     * @param killsCounter a Counter object that tracks the player's kills
     * @param maxKills an int representing the maximum number of kills the user need to achieve
     */
    public BarKills(Vector2 topLeftCorner,
                    Vector2 dimensions,
                    Renderable renderable,
                     Counter killsCounter,
                     int maxKills) {
        super(topLeftCorner, dimensions, renderable);
        this.killsCounter = killsCounter;
        this.maxKills = maxKills;
        initialPosition = topLeftCorner;
        initialDimension = dimensions;
    }

    /**
     * The create method is a static factory method for creating and adding a new BarKills object
     *  to a collection of GameObjects.
     * @param gameObjects a GameObjectCollection object where the new BarKills object will be added
     * @param imageReader an ImageReader object used to read the image file for the bar
     * @param layer  an int representing the layer on which the object should be added to the gameObjects
     *              collection
     * @param energyCounter a Counter object that tracks the player's energy
     * @param maxEnergy an int representing the maximum energy that the bar should represent
     * @param topLeftCorner a Vector2 object representing the position of the top-left corner of the object
     * @param barDimension  a Vector2 object representing the dimensions of the object
     * @return BarKills object
     */
    public static BarKills create(GameObjectCollection gameObjects,
                                   ImageReader imageReader,
                                   int layer, Counter energyCounter,
                                   int maxEnergy,
                                   Vector2 topLeftCorner,
                                   Vector2 barDimension) {
        BarKills barKills = new BarKills(topLeftCorner,
                barDimension,
                imageReader.readImage(BAR_ENERGY_IMAGE, true),
                energyCounter, maxEnergy);
        gameObjects.addGameObject(barKills, layer);
        barKills.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return barKills;
    }

    /**
     * The update method is used to update the dimensions and position of the bar based on the
     * current number of kills
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        reRenderBar();
    }

    /**
     *The reRenderBar method is a private method for updating the dimensions and position of
     * the bar based on the current number of kills.
     */
    private void reRenderBar() {
        Vector2 newBarDimension = new Vector2(killsCounter.value() * initialDimension.x() / maxKills,
                getDimensions().y());
        setDimensions(newBarDimension);
        Vector2 newPositionCalc = new Vector2(initialPosition.x() + ADD_TO_INITIAL_X +
                FACTOR_X * (float)((maxKills - killsCounter.value()) / maxKills),
                initialPosition.y());
        setTopLeftCorner(newPositionCalc);
    }

}

