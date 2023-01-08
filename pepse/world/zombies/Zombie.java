package pepse.world.zombies;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Skill;
import pepse.world.bullet.BulletInterface;

import java.util.Random;

/**
 * The Zombie class represents a zombie enemy in the game world.
 * It has three different states (running, attacking, and dead) and corresponding Skill objects to handle
 * each state's rendering. It also has a direction field that determines which direction it should move,
 * and a lives counter field to keep track of the player's lives.
 */
public class Zombie extends GameObject {
    private static final Vector2 ZOMBIES_DIMENSIONS = new Vector2(82, 100); // Default dimensions for the zombie
    private static final float AVATAR_RUNNING_SPEED_X = 200; // Default speed for the zombie along the x-axis
    private static final float AVATAR_RUNNING_SPEED_Y = -200; // Default speed for the zombie along the y-axis
    private static final float PROBABILITY_OF_JUMP_ACTION = 0.01f; // Probability of the zombie jumping
    private static final float DOWN_GRAVITY = 500; // Gravity applied to the zombie when it is falling
    private static final String ZOMBIE_TAG = "zombie"; // Tag for identifying zombie objects
    private static final float ZOMBIE_RIGHT_DIRECTION_PROBABILITY = 0.5f; // Probability of the
    // zombie facing right at the start of the game
    private static Random random; // Random number generator for determining the zombie's initial
    // direction and jump actions
    private static Counter killsCounter; // Counter for keeping track of the number of zombies killed
    private float directionSpeed; // Speed at which the zombie should move along the x-axis
    private Skill runSkill; // Skill object for handling the zombie's running state
    private Skill attackSkill; // Skill object for handling the zombie's attacking state
    private Skill deadSkill; // Skill object for handling the zombie's dead state

    /**
     * An enumeration for the three different states the zombie can be in: running, attacking, and dead.
     */
    private enum Status {RUNS, DEAD, ATTACK}
    private Status status; // The current state of the zombie

    private static final String[] ZOMBIES_IMAGES_RUNNING= {
            "assets/zombie/Walk (1).png",
            "assets/zombie/Walk (2).png",
            "assets/zombie/Walk (3).png",
            "assets/zombie/Walk (4).png",
            "assets/zombie/Walk (5).png",
            "assets/zombie/Walk (6).png",
            "assets/zombie/Walk (7).png",
            "assets/zombie/Walk (8).png"
    };

    private static final String[] ZOMBIES_IMAGES_DEAD = {
            "assets/zombie/Dead (1).png",
            "assets/zombie/Dead (2).png",
            "assets/zombie/Dead (3).png",
            "assets/zombie/Dead (4).png",
            "assets/zombie/Dead (5).png",
            "assets/zombie/Dead (6).png",
            "assets/zombie/Dead (7).png",
            "assets/zombie/Dead (8).png",
            "assets/zombie/Dead (9).png",
            "assets/zombie/Dead (10).png",
    };

    private static final String[] ZOMBIES_IMAGES_ATTACK = {
            "assets/zombie/Attack (1).png",
            "assets/zombie/Attack (2).png",
            "assets/zombie/Attack (3).png",
            "assets/zombie/Attack (4).png",
            "assets/zombie/Attack (5).png",
            "assets/zombie/Attack (6).png",
            "assets/zombie/Attack (7).png",
            "assets/zombie/Attack (8).png"
    };
    private GameObjectCollection gameObjects; // Collection of game objects in the game world
    private int layer; // Layer on which the zombie is rendered
    private final ImageReader imageReader; // ImageReader object for reading in the zombie images
    private Counter livesCounter; // Counter for keeping track of the player's lives

    /**
     * Constructor for the Zombie class.
     *
     * @param gameObjects      A GameObjectCollection of all the objects in the game world.
     * @param layer            An integer representing the layer on which the zombie should be rendered.
     * @param topLeftCorner    A Vector2 representing the top left corner position of the zombie.
     * @param imageReader      An ImageReader object for reading in the zombie images.
     * @param dimensions       A Vector2 representing the dimensions of the zombie.
     * @param renderable       A Renderable object for rendering the zombie.
     * @param livesCounter     A Counter for keeping track of the player's lives.
     */
    public Zombie(GameObjectCollection gameObjects,
                  int layer,
                  Vector2 topLeftCorner,
                  ImageReader imageReader,
                  Vector2 dimensions,
                  Renderable renderable,
                  Counter livesCounter) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.imageReader = imageReader;
        this.livesCounter = livesCounter;
        status = Status.RUNS;
        directionSpeed = getZombieDirection();
        if(directionSpeed == 1) {
            renderer().setIsFlippedHorizontally(false);
        }
        else {
            renderer().setIsFlippedHorizontally(true);
        }
        initSkills();
    }

    /**
     * Initializes the zombie's run, attack, and dead skills using the corresponding image arrays and the ImageReader.
     */
    private void initSkills() {
        runSkill = new Skill(ZOMBIES_IMAGES_RUNNING,Transition.TransitionType.TRANSITION_LOOP,
                imageReader,
                renderer());
        attackSkill = new Skill(ZOMBIES_IMAGES_ATTACK,
                Transition.TransitionType.TRANSITION_ONCE,
                imageReader,
                renderer());
        deadSkill = new Skill(ZOMBIES_IMAGES_DEAD,
                Transition.TransitionType.TRANSITION_ONCE,
                imageReader,
                renderer());
    }

    /**
     * Creates a new Zombie instance.
     *
     * @param gameObjects    The collection of game objects to which the zombie will belong.
     * @param layer          The layer on which the zombie will be rendered.
     * @param topLeftCorner  The top left corner of the zombie's bounding box.
     * @param imageReader    The image reader for loading images.
     * @param random         The random number generator.
     * @param livesCounter   The counter for keeping track of lives.
     * @param killsCounter   The counter for keeping track of kills.
     * @return                A new Zombie instance.
     */
    public static Zombie create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                ImageReader imageReader,
                                Random random,
                                Counter livesCounter,
                                Counter killsCounter) {
        Zombie.random = random;
        Zombie.killsCounter = killsCounter;
        Zombie zombie = new Zombie(gameObjects,
                layer,
                topLeftCorner,
                imageReader,
                ZOMBIES_DIMENSIONS,
                imageReader.readImage(ZOMBIES_IMAGES_RUNNING[0], true), livesCounter);
        zombie.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        zombie.transform().setAccelerationY(DOWN_GRAVITY);
        gameObjects.addGameObject(zombie, layer);
        zombie.setTag(ZOMBIE_TAG);
        return zombie;
    }



    /**
     * Updates the zombie's state.
     *
     * @param deltaTime  The time elapsed since the last frame, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateRenderImages();
        setSpeedX();
    }

    /**
     * Sets the speed of the zombie along the x-axis.
     */
    private void setSpeedX() {
        if(status != Status.DEAD) {
            transform().setVelocityX(directionSpeed * AVATAR_RUNNING_SPEED_X);
            if (checkIfJump()) {
                transform().setVelocityY(AVATAR_RUNNING_SPEED_Y);
            }
        }
        else {
            transform().setVelocityX(0);
        }
    }

    /**
     * Gets the direction of the zombie.
     *
     * @return  A float representing the direction of the zombie.
     */
    private float getZombieDirection() {
        return random.nextFloat() > ZOMBIE_RIGHT_DIRECTION_PROBABILITY ? 1 : -1;
    }

    /**
     * Updates the images displayed by the zombie's renderer.
     */
    private void updateRenderImages() {
        switch (status) {
            case RUNS:
                runSkill.getNextRender(false);
                break;
            case ATTACK:
                attackSkill.getNextRender(false);
                break;
            case DEAD:
                deadSkill.getNextRender(false);

        }
    }


    /**
     * Handles the event of the zombie entering a collision with another game object.
     *
     * @param other      The other game object involved in the collision.
     * @param collision  The collision object containing information about the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other instanceof Avatar && status != Status.DEAD) {
            livesCounter.decrement();
        }
        if(other instanceof BulletInterface) {
            if(status != Status.DEAD) {
                killsCounter.increment();
            }
            status = Status.DEAD;
        }
    }


    /**
     * Handles the event of the zombie staying in a collision with another game object.
     *
     * @param other      The other game object involved in the collision.
     * @param collision  The collision object containing information about the collision.
     */
    @Override
    public void onCollisionStay(GameObject other, Collision collision) {
        super.onCollisionStay(other, collision);
        if(other instanceof Avatar) {
            if(status != Status.DEAD) {
                status = Status.ATTACK;
            }
        }
    }


    /**
     * Handles the event of the zombie exiting a collision with another game object.
     *
     * @param other      The other game object involved in the collision.
     */
    @Override
    public void onCollisionExit(GameObject other) {
        super.onCollisionExit(other);
        if(other instanceof Avatar) {
            if(status != Status.DEAD) {
                status = Status.RUNS;
            }
        }
    }


    /**
     * Determines if the zombie should jump.
     *
     * @return  A boolean indicating whether the zombie should jump.
     */
    private boolean checkIfJump() {
        return random.nextDouble() >= 1 - PROBABILITY_OF_JUMP_ACTION;
    }

}
