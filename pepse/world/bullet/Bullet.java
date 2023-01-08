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
 * Bullet class represents a bullet in a game. It has instance variables for a skill, game object collection, and layer,
 * and has a constructor that initializes these variables and sets the velocity of the bullet in the x direction based
 * on the "right" parameter. It also has a static "create" method that creates a new instance of Bullet and adds it to
 * the given gameObjects collection, and also creates an explosion at the position of the bullet and adds it to the
 * gameObjects collection. The Bullet class has a "tick" method that updates the position and state of the bullet, and
 * a "onCollision" method that is called when the bullet collides with another game object. It also has a "destroy"
 * method that removes the bullet from the gameObjects collection.
 */
public class Bullet extends GameObject implements BulletInterface {
    private static final String BULLET_TAG = "bullet";
    private static final Vector2 BULLET_DIMENSIONS = new Vector2(12.5f, 15);
    private static final int BULLET_SPEED = 750;
    private static final int DIRECTION_FACTOR_X = 15;
    private static final int DIRECTION_FACTOR_Y = 30;
    private static final int FACTOR_RANDOM_VELOCITY = 100;
    private final Skill bulletSkill;
    private static Random random;
    private static final String[] bulletImages = {
            "assets/avatar/Objects/Bullet_000.png",
            "assets/avatar/Objects/Bullet_001.png",
            "assets/avatar/Objects/Bullet_002.png",
            "assets/avatar/Objects/Bullet_003.png"
    };
    private final GameObjectCollection gameObjects;
    private final int layer;

    /**
     * The constructor initializes a new instance of Bullet with the given parameters.
     *  It sets the position and dimensions of the bullet using the superclass's constructor, and stores the
     *  gameObjects and layer parameters in instance variables.  It also sets the horizontal flipping of the
     *  bullet's renderer based on the right parameter and sets the velocity of the bullet in the x direction
     *  using the BULLET_SPEED constant and a random factor.
     *  Finally, it creates a new Skill object using the bulletImages, an image reader,
     *  and the renderer of the bullet.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object.
     * @param imageReader   An object that can read images from files.
     * @param right         Whether the bullet is moving to the right.
     * @param gameObjects   The collection of game objects that this bullet belongs to.
     * @param layer         The layer that this bullet is on.
     */
    public Bullet(Vector2 topLeftCorner,
                  Vector2 dimensions,
                  Renderable renderable,
                  ImageReader imageReader,
                  boolean right,
                  GameObjectCollection gameObjects,
                  int layer) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.layer = layer;
        renderer().setIsFlippedHorizontally(!right);
        int direction = right ? 1 : -1;
        random = new Random();
        transform().setVelocityX(direction * BULLET_SPEED + FACTOR_RANDOM_VELOCITY * random.nextFloat());
        bulletSkill = new Skill(bulletImages, Transition.TransitionType.TRANSITION_LOOP, imageReader,
                        renderer());
    }

    /**
     *  Create is a static method that creates a new instance of Bullet and adds it to the given gameObjects
     *  collection. It also creates an explosion at the position of the bullet and adds it to the gameObjects
     *  collection.
     * @param gameObjects a GameObjectCollection that the bullet and explosion will be added to.
     * @param layer an int representing the layer on which the bullet and explosion will be rendered.
     * @param topLeftCorner a Vector2 representing the position of the bullet in window coordinates (pixels).
     * @param imageReader an ImageReader for reading image files.
     * @param right a boolean indicating whether the bullet is moving to the right or left.
     * @return newly created Bullet object
     */
    public static Bullet create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                ImageReader imageReader,
                                boolean right) {
        Bullet bullet = new Bullet(topLeftCorner,
                BULLET_DIMENSIONS,
                imageReader.readImage(bulletImages[0], true),
                imageReader, right, gameObjects, layer);
        int direction = right ? 1 : -1;
        Explode.create(gameObjects,
                layer,
                new Vector2(topLeftCorner.x() - direction * DIRECTION_FACTOR_X,
                topLeftCorner.y() - DIRECTION_FACTOR_Y),
                imageReader);
        gameObjects.addGameObject(bullet, layer);
        bullet.setTag(BULLET_TAG);
        return bullet;
    }

    /**
     *  Update is a method that updates the state of the bullet.
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
     *  onCollisionEnter is a method that is called when the bullet collides with another game object.
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

/**
 * Explode is a class that represents an explosion object in a game.
 */
class Explode extends GameObject {
    private static final String[] explodeImages = {
            "assets/avatar/Objects/Muzzle_000.png",
            "assets/avatar/Objects/Muzzle_001.png",
            "assets/avatar/Objects/Muzzle_002.png",
            "assets/avatar/Objects/Muzzle_003.png",
            "assets/avatar/Objects/Muzzle_004.png"
    };
    private static final Vector2 BULLET_EXPLODE_DIMENSIONS = new Vector2(6, 80);
    private static final String EXPLODE_TAG = "explode";
    private final Skill explodeSkill;
    private final GameObjectCollection gameObjects;
    private final int layer;

    /**
     * Constructor of Object Bullet
     * @param topLeftCorner The position of the object, in window coordinates (pixels).
     * @param dimensions    The width and height of the object, in window coordinates.
     * @param renderable    The Renderable representing the object.
     * @param imageReader   An object that can read images from files.
     * @param gameObjects   The collection of game objects that this Explode object belongs to.
     * @param layer         The layer that this Explode object is on.
     */
    public Explode(Vector2 topLeftCorner,
                  Vector2 dimensions,
                  Renderable renderable,
                  ImageReader imageReader,
                   GameObjectCollection gameObjects,
                   int layer) {
        super(topLeftCorner, dimensions, renderable);
        explodeSkill = new Skill(explodeImages, Transition.TransitionType.TRANSITION_ONCE, imageReader,
                renderer());
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    /**+
     * This static method creates a new Explode object and adds it to the given gameObjects collection on
     * the given layer. It returns the created Explode object.
     * @param gameObjects The collection of game objects to add the new explode object to.
     * @param layer        The layer to add the new Explode object on.
     * @param topLeftCorner The position of the object, in window coordinates (pixels).
     * @param imageReader  An object that can read images from files.
     * @return the created Explode object.
     */
    public static Explode create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                ImageReader imageReader) {
        Explode explode = new Explode(topLeftCorner,
                BULLET_EXPLODE_DIMENSIONS,
                imageReader.readImage(explodeImages[0], true),
                imageReader,
                gameObjects,
                layer);
        gameObjects.addGameObject(explode, layer);
        explode.setTag(EXPLODE_TAG);
        return explode;
    }

    /**
     * Update is a method that updates the state of the explode.
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
        explodeSkill.getNextRender(false);
        if(!explodeSkill.isSkillActive()) {
            gameObjects.removeGameObject(this, layer);
        }
    }
}
