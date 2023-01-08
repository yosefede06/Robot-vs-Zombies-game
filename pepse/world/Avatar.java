package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.world.bullet.Bullet;
import pepse.world.bullet.BulletPlane;
import pepse.world.ui.energy.Energy;

import java.awt.event.KeyEvent;

/**
 * The Avatar class represents a game character that can perform various actions,
 * such as standing, running, shooting, jumping, and flying.
 */
public class Avatar extends GameObject {

    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(100,102);

    private static final String[] AVATAR_IMAGES_STANDING = {
            "assets/avatar/Idle (1).png",
            "assets/avatar/Idle (2).png",
            "assets/avatar/Idle (3).png",
            "assets/avatar/Idle (4).png",
            "assets/avatar/Idle (5).png",
            "assets/avatar/Idle (6).png",
            "assets/avatar/Idle (7).png",
            "assets/avatar/Idle (8).png",
            "assets/avatar/Idle (9).png",
            "assets/avatar/Idle (10).png"
    };

    private static final String[] AVATAR_IMAGES_RUN_SHOOT = {
            "assets/avatar/RunShoot (1).png",
            "assets/avatar/RunShoot (2).png",
            "assets/avatar/RunShoot (3).png",
            "assets/avatar/RunShoot (4).png",
            "assets/avatar/RunShoot (5).png",
            "assets/avatar/RunShoot (6).png",
            "assets/avatar/RunShoot (7).png",
            "assets/avatar/RunShoot (8).png",
            "assets/avatar/RunShoot (9).png"
    };

    private static final String[] AVATAR_IMAGES_SHOOT = {
            "assets/avatar/RunShoot (9).png"
    };
    private static final String[] AVATAR_IMAGES_DEAD_FLY = {
            "assets/avatar/DeadFly (1).png"
    };
    private static final String[] AVATAR_IMAGES_RUNNING= {
            "assets/avatar/Run (1).png",
            "assets/avatar/Run (2).png",
            "assets/avatar/Run (3).png",
            "assets/avatar/Run (4).png",
            "assets/avatar/Run (5).png",
            "assets/avatar/Run (6).png",
            "assets/avatar/Run (7).png",
            "assets/avatar/Run (8).png"
    };
    private static final String[] AVATAR_IMAGES_FLYING = {
            "assets/avatar/Fly (1).png",
            "assets/avatar/Fly (2).png",
    };
    private static final String[] AVATAR_IMAGES_JUMPING = {
            "assets/avatar/Jump (1).png",
            "assets/avatar/Jump (2).png",
            "assets/avatar/Jump (3).png",
            "assets/avatar/Jump (4).png",
            "assets/avatar/Jump (5).png",
            "assets/avatar/Jump (6).png",
            "assets/avatar/Jump (7).png",
            "assets/avatar/Jump (8).png",
            "assets/avatar/Jump (9).png"
    };
    private static final String[] AVATAR_IMAGES_SHOOT_FLY = {
            "assets/avatar/ShootFly (1).png",
            "assets/avatar/ShootFly (2).png",
            "assets/avatar/ShootFly (3).png",
            "assets/avatar/ShootFly (4).png",
            "assets/avatar/ShootFly (5).png"
    };
    private static final String AVATAR_TAG = "avatar";

    // Constants that define the avatar's behavior
    private static final float AVATAR_RUNNING_SPEED_X = 200;
    private static final float DOWN_GRAVITY = 500;
    private static final float JUMP_SPEED = -400;
    private static final float MAX_SPEED = 500;
    private static final int MAX_ENERGY = 200;
    private static final int ENERGY_LAYER = Layer.UI;

    // Whether the last key pressed by the user was the right arrow key
    private boolean lastKeyIsRight = true;

    // Whether the avatar is currently in fly mode
    private boolean flyMode = false;

    // Collection of all game objects in the game
    private GameObjectCollection gameObjects;

    // The avatar's layer in the game (determines order of rendering)
    private int layer;

    // Listener for user input events (e.g. key presses)
    private final UserInputListener inputListener;

    // Utility for loading images
    private final ImageReader imageReader;

    // Counter for the avatar's energy level
    private Counter energyCounter;

    // Maximum energy level for the avatar
    private int maxEnergy = 200;

    // Skills that the avatar can perform
    private Skill runSkill;
    private Skill jumpSkill;
    private Skill standSkill;
    private Skill flySkill;
    private Skill runShoot;
    private Skill shoot;
    private Skill deadFly;
    private Skill shootFly;

    // Temporary status (used when the avatar is transitioning between states)
    private Status tempStatus;

    // Possible states that the avatar can be in
    private enum Status {STANDS, RUNS, FLIES, JUMPS, RUNS_SHOOT, SHOOT, SHOOT_FLY, DEAD_FLY}

    // The avatar's current state
    private Status status;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Avatar(GameObjectCollection gameObjects,
                  int layer,
                  Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader,
                  Renderable renderable,
                  Vector2 dimensions) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        initializeEnergy();
        status = Status.STANDS;
        tempStatus = status;
        initSkills();
    }

    /**
     * Initializes the avatar's skills, which are used to render the avatar in different states.
     */
    private void initSkills() {
        /**
         * A skill for rendering the avatar when it is jumping.
         */
        jumpSkill = new Skill(AVATAR_IMAGES_JUMPING, Transition.TransitionType.TRANSITION_ONCE, imageReader,
                renderer());

        /**
         * A skill for rendering the avatar when it is standing still.
         */
        standSkill = new Skill(AVATAR_IMAGES_STANDING, Transition.TransitionType.TRANSITION_LOOP, imageReader,
                renderer());

        /**
         * A skill for rendering the avatar when it is flying.
         */
        flySkill = new Skill(AVATAR_IMAGES_FLYING, Transition.TransitionType.TRANSITION_LOOP, imageReader,
                renderer());

        /**
         * A skill for rendering the avatar when it is running.
         */
        runSkill = new Skill(AVATAR_IMAGES_RUNNING, Transition.TransitionType.TRANSITION_LOOP, imageReader,
                renderer());

        /**
         * A skill for rendering the avatar when it is running and shooting.
         */
        runShoot = new Skill(AVATAR_IMAGES_RUN_SHOOT, Transition.TransitionType.TRANSITION_ONCE, imageReader,
                renderer());

        /**
         * A skill for rendering the avatar when it is shooting.
         */
        shoot = new Skill(AVATAR_IMAGES_SHOOT, Transition.TransitionType.TRANSITION_ONCE, imageReader,
                renderer());

        /**
         A skill for rendering the avatar when it is dead and flying.
         */
        deadFly = new Skill(AVATAR_IMAGES_DEAD_FLY, Transition.TransitionType.TRANSITION_ONCE, imageReader,
                renderer());

        /**
         * A skill for rendering the avatar when it is shooting and flying.
         */
        shootFly = new Skill(AVATAR_IMAGES_SHOOT_FLY, Transition.TransitionType.TRANSITION_LOOP, imageReader,
                renderer());
    }


    /**
     * Creates a new Avatar object and adds it to the specified GameObjectCollection.
     *
     * @param gameObjects the GameObjectCollection to which the avatar should be added
     * @param layer the layer in which the avatar should be added
     * @param topLeftCorner a Vector2 object representing the top left corner of the avatar's position
     * @param inputListener a UserInputListener object for handling user input
     * @param imageReader an ImageReader object for reading images
     * @return a reference to the newly created Avatar object
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {
        Avatar avatar = new Avatar(gameObjects,
                layer,
                topLeftCorner,
                inputListener,
                imageReader,
                imageReader.readImage(AVATAR_IMAGES_STANDING[0], true),
                AVATAR_DIMENSIONS);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(DOWN_GRAVITY);
        gameObjects.addGameObject(avatar, layer);
        avatar.setTag(AVATAR_TAG);
        return avatar;
    }

    /**
     * Initializes the energy counter and creates a new Energy object, adding it to the gameObjects list.
     */
    private void initializeEnergy() {
        energyCounter = new Counter(MAX_ENERGY);
        Energy.create(gameObjects, imageReader, ENERGY_LAYER, energyCounter, MAX_ENERGY);
    }

    /**
     * /**
     * Check and enforce the maximum speed of the avatar.
     * The vertical velocity of the avatar is set to the minimum of its current velocity and the maximum speed.
     * This ensures that the avatar's speed does not exceed the maximum allowed speed.
     * */
    private void checkMaximumSpeed() {
        transform().setVelocityY(Math.min(transform().getVelocity().y(), MAX_SPEED));
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        listenInput();
        checkMaximumSpeed();
        updateRenderImages();
        checkIfStandsToUpdateEnergy();
    }

    /**
     Check if the avatar is standing on the ground and update its energy if it is.
     If the avatar's vertical velocity is 0 (i.e. it is standing on the ground) and its
     energy is less than the maximum amount of energy it can have, the energy counter
     is incremented.
     */
    private void checkIfStandsToUpdateEnergy() {
        if(transform().getVelocity().y() == 0 && energyCounter.value() < maxEnergy) {
            energyCounter.increment();
        }
    }


    /**
     * This method listens to input from the user and responds accordingly.
     * If the right arrow key is pressed, the avatar is set to run to the right
     * and potentially shoot a bullet (if the enter key is also pressed).
     * If the left arrow key is pressed, the avatar is set to run to the left and potentially
     * shoot a bullet. If the space key is pressed while the avatar is on the ground, the avatar will jump.
     * If the space key is pressed while the shift key is also pressed and the avatar has energy, the avatar will fly.
     * If the avatar is in the air and has no energy, it will fall to the ground. If the enter key is pressed,
     * the avatar will shoot a bullet (or a bullet plane if the avatar is in fly mode and has energy).
     * If no keys are pressed, the avatar will stand still.
     */
    private void listenInput() {
        boolean isKeyPressed = false;
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            lastKeyIsRight = true;
            renderer().setIsFlippedHorizontally(false);
            transform().setVelocityX(AVATAR_RUNNING_SPEED_X);
            if(transform().getVelocity().y() == 0) {
                status = Status.RUNS;
                if(inputListener.isKeyPressed(KeyEvent.VK_ENTER)) {
                    Bullet.create(gameObjects, layer + 2,
                            new Vector2(getCenter().x() + getDimensions().x() / 2, getCenter().y()),
                            imageReader, true);
                    status = Status.RUNS_SHOOT;
                }
            }
            isKeyPressed = true;
        }

        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            lastKeyIsRight = false;
            renderer().setIsFlippedHorizontally(true);
            transform().setVelocityX(-AVATAR_RUNNING_SPEED_X);
            if(transform().getVelocity().y() == 0) {
                status = Status.RUNS;
                if(inputListener.isKeyPressed(KeyEvent.VK_ENTER)) {
                    Bullet.create(gameObjects,
                            layer + 2,
                            new Vector2(getCenter().x() - getDimensions().x() / 2, getCenter().y()),
                            imageReader,
                            false);
                    status = Status.RUNS_SHOOT;
                }
            }
            isKeyPressed = true;
        }

        if(getVelocity().y() == 0) {
            flyMode = false;
            if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
                transform().setVelocityY(JUMP_SPEED);
                status = Status.JUMPS;
                isKeyPressed = true;

            }
        }

        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)
                && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)
                && energyCounter.value() > 0) {
            energyCounter.decrement();
            transform().setVelocityY(JUMP_SPEED);
            status = Status.FLIES;
            isKeyPressed = true;
            flyMode = true;
        }

        if(getVelocity().y() > 0 && energyCounter.value() == 0) {
            status = Status.DEAD_FLY;
            isKeyPressed = true;
        }

        if(inputListener.isKeyPressed(KeyEvent.VK_ENTER)) {
            if(getVelocity().y() == 0 || !flyMode) {
                int direction = lastKeyIsRight ? 1 : -1;
                Bullet.create(gameObjects,
                        layer + 2,
                        new Vector2(getCenter().x() + direction * getDimensions().x() / 2, getCenter().y()),
                        imageReader,
                        lastKeyIsRight);
                status = Status.SHOOT;
                isKeyPressed = true;
            }
            else if(energyCounter.value() > 0) {
                int direction = lastKeyIsRight ? 1 : -1;
                BulletPlane.create(gameObjects,
                        layer + 2,
                        new Vector2(getCenter().x() + direction * getDimensions().x() / 2, getCenter().y()),
                        imageReader,
                        lastKeyIsRight);
                status = Status.SHOOT_FLY;
                isKeyPressed = true;
            }
        }

        if(!isKeyPressed) {
            transform().setVelocityX(0);
            if(transform().getVelocity().y() == 0) {
                status = Status.STANDS;
            }
        }
    }

    /**
     Update the images used for rendering the avatar based on its current status.
     If the avatar's status has changed, the relevant image skill is reset.
     The image skill to use is determined by the avatar's current status.
     The image skill is then used to retrieve the next image to render.
     */
    private void updateRenderImages() {
        boolean reInitRender = tempStatus != status;
        tempStatus = status;
        switch (status) {
            case RUNS:
                runSkill.getNextRender(reInitRender);
                break;
            case STANDS:
                standSkill.getNextRender(reInitRender);
                break;
            case JUMPS:
                jumpSkill.getNextRender(reInitRender);
                break;
            case FLIES:
                flySkill.getNextRender(reInitRender);
                break;
            case RUNS_SHOOT:
                runShoot.getNextRender(reInitRender);
                break;
            case SHOOT:
                shoot.getNextRender(reInitRender);
                break;
            case DEAD_FLY:
                deadFly.getNextRender(reInitRender);
                break;
            case SHOOT_FLY:
                shootFly.getNextRender(reInitRender);
                break;
        }
    }
}
