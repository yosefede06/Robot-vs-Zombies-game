package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class that creates the sun of the game. It represents an oval moving around the center of the window
 * screen.
 */
public class Sun {
    private final static float INITIAL_ANGLE = 0f;
    private final static float FINAL_ANGLE = (float)(2 * Math.PI);
    private final static String TAG_SUN = "sun";
    private static final Color SUN_COLOR =  Color.YELLOW;
    private static final int SUN_WIDTH_SCALE = 8;
    private final static float WINDOW_FACTOR = 0.5f;
    private final static int FLAT_ANGLE = 180;


    /**
     * The role of the method is to produce an oval (then sun) according to windowDimensions,
     * to combine it with the game itself in the layer, and to cause the sun to move
     * circularly with a cycle time of cycleLength.
     * @param gameObjects Game objects to add.
     * @param layer Layer to locate the object in the game.
     * @param windowDimensions Game's window dimensions.
     * @param cycleLength The number of seconds it takes a "day".
     * @return The method returns the sun object created, in case the client want to do something with it.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength) {
        GameObject sun = createSunObject(windowDimensions, gameObjects, layer);
        new Transition<Float>(
                sun,
                angleInSky -> calcSunPosition(sun, windowDimensions, angleInSky - FLAT_ANGLE),
                INITIAL_ANGLE,
                FINAL_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }

    /**
     * This function creates the sun object and add it to the gameObjects.
     * @param windowDimensions Game's window dimension.
     * @param gameObjects Game objects to add.
     * @param layer Layer to locate the object in the game.
     * @return The sun object.
     */
    private static GameObject createSunObject(Vector2 windowDimensions, GameObjectCollection gameObjects,
                                              int layer) {
        GameObject sun = new GameObject(
                getInitialSunPosition(windowDimensions),
                getSunDimension(windowDimensions),
                new OvalRenderable(SUN_COLOR));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        sun.setTag(TAG_SUN);
        return sun;
    }

    /**
     * Getter for the sun radius.
     * @param windowDimensions game's window dimensions.
     * @return sun radius.
     */
    private static float getSunRadius(Vector2 windowDimensions) {
        return windowDimensions.x() / SUN_WIDTH_SCALE;
    }

    /**
     * getter for the sun initial position.
     * @param windowDimensions Game's window dimension.
     * @return Returns sun initial position.
     */
    private static Vector2 getInitialSunPosition(Vector2 windowDimensions) {
        return windowDimensions.mult(WINDOW_FACTOR);
    }

    /**
     * Calculates sun's size.
     * @param windowDimensions Game's window dimension.
     * @return Sun's size.
     */
    private static Vector2 getSunDimension(Vector2 windowDimensions) {
        return new Vector2(getSunRadius(windowDimensions), getSunRadius(windowDimensions));
    }

    /**
     * Calculates the new position of the sun, by using the ellipse formula in order to cover the whole
     * window.
     * @param sun sun object
     * @param windowDimensions Game's window dimension.
     * @param angleInSky Sky angle.
     */
    private static void calcSunPosition(GameObject sun, Vector2 windowDimensions, float angleInSky) {
        double sunRadius = sun.getDimensions().x();
        double padding = sunRadius * WINDOW_FACTOR;
        double centerX = (windowDimensions.x() - sunRadius) * WINDOW_FACTOR;
        double centerY = (windowDimensions.y() - sunRadius) * WINDOW_FACTOR;
        double ellipseX = centerX + padding + centerX * Math.sin(angleInSky);
        double ellipseY = centerY + padding + centerY * Math.cos(angleInSky);
        sun.setCenter(new Vector2((float)ellipseX, (float)ellipseY));
    }
}