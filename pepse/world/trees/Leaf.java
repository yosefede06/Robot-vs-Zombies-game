package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import java.util.Random;

/**
 * A class that extends GameObject and creates a leaf. It represents a static leaf inside the top of the tree.
 */
public class Leaf extends GameObject {
    private static final String TAG_LEAF = "leaf";
    private static final Float ANGLE_MOVEMENT = 10f;
    private static final float TIME_TRANSITION_ANGLE_MOVEMENT = 2;
    private static final float FACTOR_RANDOM_FLOAT = 2;
    private static final float RESIZE_LEAF_SCALE = 1.1f;
    private static final float TIME_TRANSITION_SIZE_CHANGE = 2;
    private static final int FADEOUT_TIME = 5;
    private static final int MAX_DEAD_TIME = 5;
    private static final int MIN_DEAD_TIME = 2;
    private static final float MIN_LIFE_TIME = 0;
    private static final float FADE_IN_TIME_LEAF_DEAD = 0;
    private static final float MAX_LIFE_TIME = 30;
    private static final float DROP_SPEED = 120;
    private static final float TIME_TRANSITION_HORIZONTAL_MOVEMENT = 2;
    private static final float HORIZONTAL_TRANSITION_FALL = 30;
    private final Random random;
    private final Vector2 originalPosition;
    private float timeTransitionAngleMovement;
    private Float angleMovement;
    private Transition<Float> horizontalTransition;
    private Transition<Float> angleTransition;


    /**
     * Constructor of the class that initializes the leaf object.
     * @param position Vector2 leaf's position.
     * @param random random function.
     * @param renderable Render value.
     */
    Leaf(Vector2 position, Random random, Renderable renderable) {
        super(position, new Vector2(Block.SIZE, Block.SIZE), renderable);
        this.originalPosition = position;
        this.random = random;
        setTag(TAG_LEAF);
        setLeafMovementFromWind();
        newLeafLife();
        angleMovement = ANGLE_MOVEMENT;
        timeTransitionAngleMovement = TIME_TRANSITION_ANGLE_MOVEMENT;
        angleTransition = null;

    }

    /**
     * Gives a new life to leaf starting a new cycle.
     */
    private void newLeafLife() {
        new ScheduledTask(this, getLifeTime(),
                false, this::dropLeaf);
    }

    /**
     * Drops leaf, defined as protected so that class that extend leaf could be able
     * to drop the leaf from other reasons.
     */
     protected void dropLeaf() {
        this.transform().setVelocityY(DROP_SPEED);
        this.setHorizontalTransition();
        this.renderer().fadeOut(FADEOUT_TIME, this::deadCall);
    }

    /**
     * Method to call when leaf is dead to action leaf with a fadeout and falling movement.
     */
    private void deadCall() {
        this.transform().setVelocity(Vector2.ZERO);
        this.setCenter(originalPosition);
        removeComponent(horizontalTransition);
        new ScheduledTask(this, getDeadTime(), false,
                () -> {
                    this.renderer().fadeIn(FADE_IN_TIME_LEAF_DEAD);
                    this.newLeafLife();
                }
        );
    }

    /**
     *
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.removeComponent(horizontalTransition);
        this.transform().setVelocity(Vector2.ZERO);
    }

    /**
     * Produces a horizontal movement effect when falling.
     */
    private void setHorizontalTransition() {
        int randomStart = random.nextFloat() < 0.5 ? -1 : 1;
        horizontalTransition = new Transition<>(this, this.transform()::setVelocityX,
                randomStart * HORIZONTAL_TRANSITION_FALL,
                -randomStart * HORIZONTAL_TRANSITION_FALL,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                TIME_TRANSITION_HORIZONTAL_MOVEMENT,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    /**
     * Computes the move action of the leaf caused by the wind.
     */
    private void setLeafMovementFromWind() {
        moveAngle();
        changeSize();
    }

    /**
     * Computes leaf's lifetime.
     * @return Random int lifetime between (MIN_LIFE_TIME , MAX_LIFE_TIME)
     */
    private int getLifeTime() {
        return Math.round(MIN_LIFE_TIME + (MAX_LIFE_TIME - MIN_LIFE_TIME) * random.nextFloat());
    }

    /**
     * Computes leaf's dead time.
     * @return Random int dead time between (MIN_DEAD_TIME , MAX_DEAD_TIME)
     */
    private int getDeadTime() {
        return Math.round(MIN_DEAD_TIME + (MAX_DEAD_TIME - MIN_DEAD_TIME) * random.nextFloat());
    }

    /**
     * Changes the dimensions of the leave.
     */
    private void changeSize() {
        new ScheduledTask(this, random.nextFloat() * FACTOR_RANDOM_FLOAT,
                false,
                () -> new Transition<>(
                this,
                (r) -> this.setDimensions(new Vector2(r, r)),
                this.getDimensions().x() - this.getDimensions().x() * (1 - RESIZE_LEAF_SCALE),
                this.getDimensions().x() + this.getDimensions().x() * (1 - RESIZE_LEAF_SCALE),
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TIME_TRANSITION_SIZE_CHANGE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        ));
    }

    /**
     * Updates leaf angle
     * @param angle Float angle
     */
    public void updateLeafAngle(Float angle, Float timeTransition) {
        angleMovement = angle;
        timeTransitionAngleMovement = timeTransition;
        setAngleTransition();
    }

    /**
     * Sets the leaf angle.
     */
    protected void moveAngle() {
        new ScheduledTask(this,
                random.nextFloat() * FACTOR_RANDOM_FLOAT,
                false,
                this::setAngleTransition);
    }

    /**
     *This method is setting up a transition to oscillate the angle of a renderable object.
     * It first removes any previously set angle transition from the object,
     * then it determines the starting angle of the transition by randomly choosing either -1 or 1.
     * It then creates a new Transition object that updates the angle of the object's renderer between the
     * start angle and the opposite of the start angle, using a linear interpolator and repeating back and
     * forth. The duration of the transition and the object on which it is being set are also specified.
     * Finally, the Transition object is added as a component to the object.
     */
    private void setAngleTransition() {
        if(angleTransition != null) {
            removeComponent(angleTransition);
        }
        int randomStart = random.nextFloat() < 0.5 ? -1 : 1;
        angleTransition = new Transition<>(
                this,
                this.renderer()::setRenderableAngle,
                randomStart * angleMovement,
                -randomStart * angleMovement,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                timeTransitionAngleMovement,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }
}
