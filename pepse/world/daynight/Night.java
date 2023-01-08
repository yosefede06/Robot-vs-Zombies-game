package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class that produces the night effect of the game.
 */
public class Night {
    private static final Color NIGHT_COLOR =  Color.BLACK;
    private static final String TAG_NIGHT = "night";
    private static final Float INITIAL_OPAQUENESS = 0f;
    private static final Float MIDNIGHT_OPACITY = 0.5f;

    /**
     * The role of the method is to produce the above rectangle according to windowDimensions,
     * to combine it with the game itself in the layer, and to cause its opacity to change
     * circularly with a cycle time of cycleLength (the number of seconds it takes a "day").
     * @param gameObjects Game objects to add.
     * @param layer Layer to locate the object in the game.
     * @param windowDimensions Window's game dimensions.
     * @param cycleLength The number of seconds it takes a "day".
     * @return The method returns the night object created, in case the client want to do something with it.
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer,
                                    Vector2 windowDimensions,
                                    float cycleLength) {
        GameObject night = createNightObject(gameObjects, layer, windowDimensions);
        new Transition<Float>(night,
                night.renderer()::setOpaqueness,
                INITIAL_OPAQUENESS,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        return night;
    }

    /**
     * This function creates the night object by default with opacity 1 and add it to the gameObjects.
     * @param gameObjects Game objects to add.
     * @param layer Layer to locate the object in the game.
     * @param windowDimensions Game window's dimensions.
     * @return gameObject night gameObject.
     */
    private static GameObject createNightObject(GameObjectCollection gameObjects, int layer,
                                                Vector2 windowDimensions) {
        GameObject night = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(NIGHT_COLOR));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);
        night.setTag(TAG_NIGHT);
        return night;
    }
}
