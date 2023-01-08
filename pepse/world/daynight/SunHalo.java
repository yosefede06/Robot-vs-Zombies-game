package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class that creates the sun aura of the game. It represents an oval moving around the center of the
 * window screen.
 */
public class SunHalo {
    private static final String TAG_AURA_SUN = "sun-aura";
    private static final int SCALE_AURA_RADIUS_TO_SUN = 2;

    /**
     * The role of the method is to produce an oval around the sun according to sun dimensions,
     * to combine it with the game itself in the layer, and to cause the sun aura to move
     * circularly. To do this we will update for each frame the sun aura's center to be the same as
     * the sun's center.
     * @param gameObjects Game objects to add.
     * @param layer Layer to locate the object in the game.
     * @param sun sun object of the game.
     * @param color Aura's sun color.
     * @return sun aura object created.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color) {
        GameObject sunAura = createSunObject(sun, gameObjects, layer, color);
        sunAura.addComponent(deltaTime -> sunAura.setCenter(sun.getCenter()));
        return sunAura;
    }

    /**
     * This function creates the sun aura object and add it to the gameObjects.
     * @param sun sun.
     * @param gameObjects Game objects to add.
     * @param layer Layer to locate the object in the game.
     * @return The sun object.
     */
    private static GameObject createSunObject(GameObject sun,
                                              GameObjectCollection gameObjects,
                                              int layer,
                                              Color color) {
        float auraRadius = getAuraRadius(sun);
        GameObject sunAura = new GameObject(
                Vector2.ZERO,
                new Vector2(auraRadius, auraRadius),
                new OvalRenderable(color));
        sunAura.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunAura, layer);
        sunAura.setTag(TAG_AURA_SUN);
        return sunAura;
    }

    /**
     * Calculates the sun aura radius according to sun dimensions.
     * @param sun sun object.
     * @return sun aura radius.
     */
    private static float getAuraRadius(GameObject sun) {
        return sun.getDimensions().x() * SCALE_AURA_RADIUS_TO_SUN;
    }
}
