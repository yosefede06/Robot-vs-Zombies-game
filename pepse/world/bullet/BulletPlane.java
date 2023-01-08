package pepse.world.bullet;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Skill;
import java.util.Random;


/**
 * A class representing a bullet in a game.
 * It is a type of game object that moves in a straight line and disappears when it collides with
 * certain objects.
 */
public class BulletPlane extends GameObject implements BulletInterface {
    private static final String BULLET_TAG = "bullet";
    private static final Vector2 BULLET_DIMENSIONS = new Vector2(110 * 0.3f, 115 * 0.3f);
    private static final int BULLET_SPEED = 850;
    private static final int FACTOR_RANDOM_VELOCITY = 100;
    private final Skill bulletSkill;
    private static Random random;
    private static final String[] bulletImages = {
            "assets/avatar/Objects/BulletPlane (1).png",
            "assets/avatar/Objects/BulletPlane (2).png",
            "assets/avatar/Objects/BulletPlane (3).png",
            "assets/avatar/Objects/BulletPlane (4).png",
            "assets/avatar/Objects/BulletPlane (5).png"
    };
    private final GameObjectCollection gameObjects;
    private final int layer;

    /**
     * Construct a new BulletPlane instance.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object.
     * @param imageReader   An object that can read images from files.
     * @param right         Whether the bullet is moving to the right.
     * @param gameObjects   The collection of game objects that this bullet belongs to.
     * @param layer         The layer that this bullet is on.
     */
    public BulletPlane(Vector2 topLeftCorner,
                  Vector2 dimensions,
                  Renderable renderable,
                  ImageReader imageReader,
                  boolean right, GameObjectCollection gameObjects, int layer) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.layer = layer;
        renderer().setIsFlippedHorizontally(!right);
        int direction = right ? 1 : -1;
        random = new Random();
        transform().setVelocityX(direction * BULLET_SPEED + FACTOR_RANDOM_VELOCITY * random.nextFloat());
        bulletSkill = new Skill(bulletImages, Transition.TransitionType.TRANSITION_LOOP,
                            imageReader, renderer());
    }

    /**
     * Create is a static method that creates a new instance of BulletPlane and adds it to the given
     * GameObjectCollection.
     * @param gameObjects   The collection of game objects that this bullet belongs to.
     * @param layer         The layer that this bullet is on.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * @param imageReader   An object that can read images from files.
     * @param right         Whether the bullet is moving to the right.
     * @return BulletPlane object
     */
    public static BulletPlane create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                ImageReader imageReader,
                                boolean right) {
        BulletPlane bulletPlane = new BulletPlane(topLeftCorner,
                BULLET_DIMENSIONS,
                imageReader.readImage(bulletImages[0], true),
                imageReader, right, gameObjects, layer);
        gameObjects.addGameObject(bulletPlane, layer);
        bulletPlane.setTag(BULLET_TAG);
        return bulletPlane;
    }

    /**
     * Update is a method that updates the state of the bullet.
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
        bulletSkill.getNextRender(false);
    }

    /**
     * onCollisionEnter is a method that is called when the bullet collides with another game object.
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other instanceof Block) {
            gameObjects.removeGameObject(this, layer);
        }
    }
}