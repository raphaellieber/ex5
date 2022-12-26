package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

public class Leaf extends GameObject {

    private static final int MAX_LIFE_TIME = 120;
    private static final int MIN_LIFE_TIME = 20;

    private static final int MAX_MOVE_WAIT_TIME = 10;
    private static final int MIN_MOVE_WAIT_TIME = 2;

    private static final float MAX_ANGLE = 10;
    private static final float ANGLE_TIME = 2F;

    private static final float DIM_VALUE_1 = 1.1F;
    private static final float DIM_VALUE_2 = 0.9F;
    private static final int DIMENTION_CHANGE_TIME = 5;

    private static final int FADEOUT_TIME = 10;
    private static final int FALLING_SPEED = 30;
    private static final int CHANGE_TIME = 3;

    private static final int MAX_DEATH_TIME = 10;
    private static final int MIN_DEATH_TIME = 3;

    private static final float MAX_HORIZONTAL_SPEED = 45;
    private static final float MIN_HORIZONTAL_SPEED = -45;
    private static final float MAX_VERTICAL_SPEED = 30;
    private static final float MIN_VERTICAL_SPEED = -5;


    private final Vector2 dimensions;
    private final Vector2 topLeftCorner;
    private final Random rand;
    private Transition<Float> horizontalTransition;
    private Transition<Float> verticalTransition;
    private Transition<Float> angelTransition;
    private Transition<Vector2> dimensionTransition;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        // default constructor:
        super(topLeftCorner, dimensions, renderable);

        this.rand = new Random();

        // Saving current state:
        this.dimensions = dimensions;
        this.topLeftCorner = topLeftCorner;

        // physics change so leaf will collide with terrain
        physics().preventIntersectionsFromDirection(Vector2.ZERO);

        // starting the leaf life cycle:
        this.leafLifeCycle();

    }

    private void leafLifeCycle(){

        // Creating for each leaf random lifeTime and random waitTime until the transitions will start
        float movementWaitTime = this.rand.nextInt(MIN_MOVE_WAIT_TIME, MAX_MOVE_WAIT_TIME);
        float lifeTime = this.rand.nextInt(MIN_LIFE_TIME, MAX_LIFE_TIME);

        // Initializing the Scheduled Transition Task for the movement Strategy
        new ScheduledTask(this, movementWaitTime, false, this::movementStrategy);

        // Initializing the Scheduled Transition Task for the falling Strategy which will invoke the leafDeath
        new ScheduledTask(this, lifeTime, false, this::fallingStrategy);
    }

    private void movementStrategy() {
        // Angel Transition:
        this.angelTransition = new Transition<>(this, this.renderer()::setRenderableAngle,
                -MAX_ANGLE, MAX_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT, ANGLE_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

        // Dimension Transition:
        Vector2 startState = dimensions;
        Vector2 finalState = new Vector2(startState.x() * DIM_VALUE_1, startState.y() * DIM_VALUE_2);

        this.dimensionTransition = new Transition<>(this, this::setDimensions, startState, finalState,
                Transition.CUBIC_INTERPOLATOR_VECTOR, DIMENTION_CHANGE_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private void fallingStrategy() {
        // setting falling speed and fadeout time which will invoke the leafDeath function after times-up
        this.renderer().fadeOut(FADEOUT_TIME, this::leafDeath);
        this.transform().setVelocityY(FALLING_SPEED);

        //  Horizontal Transition:
        this.horizontalTransition = new Transition<>(this, this.transform()::setVelocityX,
                MAX_HORIZONTAL_SPEED, MIN_HORIZONTAL_SPEED, Transition.CUBIC_INTERPOLATOR_FLOAT, CHANGE_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

        // Vertical Transition
//        this.verticalTransition = new Transition<>(this, this.transform()::setVelocityY,
//                MAX_VERTICAL_SPEED, MIN_VERTICAL_SPEED, Transition.LINEAR_INTERPOLATOR_FLOAT, CHANGE_TIME,
//                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private void leafDeath() {
        // getting a random leaf death time, on time up the leaf will rebirth
        int deathTime = this.rand.nextInt(MIN_DEATH_TIME, MAX_DEATH_TIME);
        new ScheduledTask(this, deathTime, false, this::leafRebirth);
    }

    private void leafRebirth() {
        // initializing characteristics:
        this.setTopLeftCorner(this.topLeftCorner);
        this.setDimensions(this.dimensions);
        this.renderer().fadeIn(0);
        this.renderer().setRenderableAngle(0);

        // starting a new leaf life cycle:
        this.leafLifeCycle();
    }

    @Override
    public void onCollisionStay(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        this.transform().setVelocityX(0);  // Clearing horizontal movement
//        this.transform().setVelocityY(0);   // Clearing vertical movement
        this.removeComponent(this.horizontalTransition);
//        this.removeComponent(this.verticalTransition);
        this.removeComponent(this.dimensionTransition);
        this.removeComponent(this.angelTransition);
    }

}
