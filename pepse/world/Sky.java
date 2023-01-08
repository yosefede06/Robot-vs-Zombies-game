package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class that creates the sky of the game. It's a blue background on the game.
 */
public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY_LAYER = "sky";

    /**
     * Static method that creates the blue sky.
     * @param gameObjects Game objects to add.
     * @param windowDimensions Game's window dimensions.
     * @param skyLayer Layer to locate the object in the game.
     * @return The method returns the sky object created, in case the client want to do something with it.
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int skyLayer) {
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag(SKY_LAYER);
        return sky;
    }


}
