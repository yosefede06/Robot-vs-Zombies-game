package pepse.world.ui.lives;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import danogl.util.Counter;

import java.awt.*;

/**
 * This class is the numeric life counter that have the player of the game. It corresponds to the number of
 * heart that the player has.
 */
public class NumericLifeCounter extends GameObject {
    private static final int NUMERIC_COUNTER_DIMENSIONS = 20;
    private static final Vector2 INITIAL_POSITION = new Vector2(130,47);
    private final Counter livesCounter;

    /**
     * The constructor of the textual representation object of how many strikes are left in the game.
     * @param livesCounter  The counter of how many lives are left right now.
     * @param topLeftCorner the top left corner of the position of the text object
     * @param dimensions the size of the text object
     */
    public NumericLifeCounter(Counter livesCounter, Vector2 topLeftCorner,
                              Vector2 dimensions) {
        super(topLeftCorner,dimensions,null);
        this.livesCounter = livesCounter;
    }

    /**
     * The create method is a static factory method for creating and adding a new NumericLifeCounter
     * object to a collection of GameObjects
     * @param livesCounter a Counter object that tracks the player's lives
     * @param gameObjects  a GameObjectCollection object where the new NumericLifeCounter object will be added
     * @param layer an int representing the layer on which the object should be added to the gameObjects
     *              collection
     * @return  NumericLifeCounter object.
     */
    static public NumericLifeCounter create(Counter livesCounter, GameObjectCollection gameObjects, int layer) {
        Vector2 vectorNumericDimensions = new Vector2(NUMERIC_COUNTER_DIMENSIONS, NUMERIC_COUNTER_DIMENSIONS);
        NumericLifeCounter numericLifeCounter = new NumericLifeCounter(livesCounter,
                INITIAL_POSITION,
                vectorNumericDimensions);
        numericLifeCounter.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(numericLifeCounter, layer);
        return numericLifeCounter;
    }

    /**
     * This method is overwritten from GameObject.
     * It sets the string value of the text object to the number of current lives left.
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
        TextRenderable textRenderable = new TextRenderable(String.format("%d", livesCounter.value()));
        if(livesCounter.value() == 3 || livesCounter.value() == 4) textRenderable.setColor(Color.green);
        if(livesCounter.value() == 2) textRenderable.setColor(Color.yellow);
        if(livesCounter.value() == 1) textRenderable.setColor(Color.red);
        this.renderer().setRenderable(textRenderable);
    }
}