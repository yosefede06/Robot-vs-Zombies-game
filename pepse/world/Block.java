package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * A class that creates the block for the ground.
 */
public class Block extends GameObject {
    public static final int SIZE = 30;

    /**
     * Constructor of the class that creates a new block.
     * @param topLeftCorner Top left corner block's position
     * @param renderable Render type.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

    }
}