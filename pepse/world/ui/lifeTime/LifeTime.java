package pepse.world.ui.lifeTime;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;


/**
 * The LifeTime class is a subclass of GameObject that represents a visual label for the player's lifetime bar
 */
public class LifeTime extends GameObject {
    private static final Vector2 ENERGY_LABEL_POSITION = new Vector2(30, 125);
    private static final Vector2 LABEL_ENERGY_SIZE = new Vector2(144, 40);
    private final static Vector2 BAR_SIZE = new Vector2(54 * 1.85f, 10 * 1.85f);
    private final static String BAR_LABEL_IMAGE = "assets/ui/life-label.png";
    private final static int ADD_TO_X = 33;
    private final static int ADD_TO_Y = 12;

    /**
     *  Constructor of LifeTime
     * @param topLeftCorner a Vector2 object representing the position of the top-left corner of the object
     * @param dimensions a Vector2 object representing the dimensions of the object
     * @param renderable a Renderable object representing the visual representation of the object
     */
    public LifeTime(Vector2 topLeftCorner,
                  Vector2 dimensions,
                  Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * The create method is a static factory method for creating and adding a LifeTime object and
     * a BarLifeTime object to a collection of GameObjects.
     * @param gameObjects a GameObjectCollection object where the new LifeTime and BarLifeTime objects will
     *      *                    be added
     * @param imageReader an ImageReader object used to read the image files
     * @param layer an int representing the layer on which the objects should be added to the gameObjects
     *      *             collection
     * @param lifeTimeCounter a Counter object that tracks the player's lifeTime
     * @param maxEnergy  an int representing the maximum energy that the bar should represent
     * @return LifeTime object
     */
    public static LifeTime create(GameObjectCollection gameObjects,
                                ImageReader imageReader,
                                int layer,
                                Counter lifeTimeCounter,
                                int maxEnergy) {
        LifeTime lifeTimeLabel = new LifeTime(ENERGY_LABEL_POSITION,
                LABEL_ENERGY_SIZE,
                imageReader.readImage(BAR_LABEL_IMAGE, true));
        gameObjects.addGameObject(lifeTimeLabel, layer);
        lifeTimeLabel.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        // create bar inside energy label
        BarLifeTime.create(gameObjects,
                imageReader,
                layer + 1,
                lifeTimeCounter,
                maxEnergy,
                new Vector2(ADD_TO_X + ENERGY_LABEL_POSITION.x(), ENERGY_LABEL_POSITION.y() + ADD_TO_Y),
                BAR_SIZE);
        return lifeTimeLabel;
    }
}

/**
 * The BarEnergy class is a subclass of GameObject that represents a bar that visually displays the player's
 * time left.
 */
class BarLifeTime extends GameObject {
    private final static String BAR_ENERGY_IMAGE = "assets/ui/life-bar.png";
    private final Vector2 initialPosition;
    private final Vector2 initialDimension;
    private final Counter lifeTimeCounter;
    private final int maxEnergy;
    private final static int ADD_TO_INITIAL_X = 3;
    private final static int FACTOR_X = 10;

    /**
     * Constructor of BarLifeTime
     * @param topLeftCorner a Vector2 object representing the position of the top-left corner of the object
     * @param dimensions a Vector2 object representing the dimensions of the object
     * @param renderable a Renderable object representing the visual representation of the object
     * @param lifeTimeCounter a Counter object that tracks the player's time
     * @param maxEnergy an int representing the maximum amount of energy that the bar should represent
     */
    public BarLifeTime(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                     Counter lifeTimeCounter,
                     int maxEnergy) {
        super(topLeftCorner, dimensions, renderable);
        this.lifeTimeCounter = lifeTimeCounter;
        this.maxEnergy = maxEnergy;
        lifeTimeCounter.decrement();
        initialPosition = topLeftCorner;
        initialDimension = dimensions;
    }
    /**
     * The create method is a static factory method for creating and adding a new BarLifeTime object to
     * a collection of GameObjects.
     * @param gameObjects  a GameObjectCollection object where the new BarLifeTime object will be added
     * @param imageReader an ImageReader object used to read the image file for the bar
     * @param layer an int representing the layer on which the object should be added to the gameObjects
     *             collection
     * @param energyCounter a Counter object that tracks the player's energy
     * @param maxEnergy an int representing the maximum amount of energy that the bar should represent
     * @param topLeftCorner a Vector2 object representing the position of the top-left corner of the object
     * @param barDimension a Vector2 object representing the dimensions of the object
     * @return BarLifeTime object
     */
    public static BarLifeTime create(GameObjectCollection gameObjects,
                                   ImageReader imageReader,
                                   int layer, Counter energyCounter,
                                   int maxEnergy,
                                   Vector2 topLeftCorner,
                                   Vector2 barDimension) {
        BarLifeTime barLifeTime = new BarLifeTime(topLeftCorner,
                barDimension,
                imageReader.readImage(BAR_ENERGY_IMAGE, true),
                energyCounter, maxEnergy);
        gameObjects.addGameObject(barLifeTime, layer);
        barLifeTime.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return barLifeTime;
    }

    /**
     * The update method is used to update the dimensions and position of the bar based on the current
     * amount of energy.
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
     * Private method for updating the dimensions and position of the bar based on the current amount of
     * time left.
     */
    private void reRenderBar() {
        Vector2 newBarDimension = new Vector2(lifeTimeCounter.value() * initialDimension.x() / maxEnergy,
                getDimensions().y());
        setDimensions(newBarDimension);
        Vector2 newPositionCalc = new Vector2(initialPosition.x() + ADD_TO_INITIAL_X +
                FACTOR_X * (float) ((maxEnergy - lifeTimeCounter.value()) / maxEnergy),
                initialPosition.y());
        setTopLeftCorner(newPositionCalc);
    }

}