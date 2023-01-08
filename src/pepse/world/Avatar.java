package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.interfaces.Energized;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject implements Energized {
    public static final Vector2 AVATAR_DIMENSIONS = new Vector2(50, 70);
    private static final float JUMP_VELOCITY_Y = -300;
    private static final float GRAVITY = 500;
    private static final float HORIZONTAL_VELOCITY_X = 300;
    private static final float MAX_ENERGY = 100;
    private static final float ENERGY_CHANGE = 0.5f;

    private static final String[] MOVEMENT_PATH = new String[] {"assets/leftFoot.png","assets/rightFoot.png"};
    private static final String STAND_STILL_PATH = "assets/standstill.png";
    private static final float TIME_BETWEEN_CLIPS = 0.1f;

    private float energy = MAX_ENERGY;
    private final UserInputListener inputListener;
    private final Renderable standStillImg;

    private final AnimationRenderable movementAnimation;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, ImageReader imageReader,
                  UserInputListener inputListener) {
        super(topLeftCorner, dimensions, null);

        this.inputListener = inputListener;
        this.standStillImg = imageReader.readImage(STAND_STILL_PATH, true);
        this.movementAnimation = new AnimationRenderable(MOVEMENT_PATH,imageReader,true,
                TIME_BETWEEN_CLIPS);
        this.renderer().setRenderable(this.standStillImg);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * Creates an Avatar object, and directly causes it to accelerate downwards according to the gravity.
     * @return the created Avatar object.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_DIMENSIONS, imageReader, inputListener);
        avatar.fall();
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateEnergy();
        cancelHorizontalVelocity();
        this.renderer().setRenderable(this.standStillImg);

        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) { moveRight(); }

        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) { moveLeft(); }

        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0) { fly(); }
            else { jump(); }
        }
    }

    /**
     * A getter for the Avatar's energy
     * @return the energy of the Avatar
     */
    public float getEnergy(){ return this.energy;}

    /**
     * A method which changes the velocity only horizontally, and causes the Avatar object to move to the right.
     */
    private void moveRight() {
        setVelocity(new Vector2(HORIZONTAL_VELOCITY_X, getVelocity().y()));
        this.renderer().setRenderable(this.movementAnimation);
        this.renderer().setIsFlippedHorizontally(false);
    }

    /**
     * A method which changes the velocity only horizontally, and causes the Avatar object to move to the left.
     */
    private void moveLeft() {
        setVelocity(new Vector2(- HORIZONTAL_VELOCITY_X, getVelocity().y()));
        this.renderer().setRenderable(this.movementAnimation);
        this.renderer().setIsFlippedHorizontally(true);
    }

    /**
     * Changes the vertical velocity to a defined value.
     * While flying, the energy is diminished by a constant value on each frame.
     */
    private void fly() {
        setVelocity(new Vector2(getVelocity().x(), JUMP_VELOCITY_Y));
        this.energy -= ENERGY_CHANGE;
    }

    /**
     * Jumping is possible only when the Avatar stands on a solid mass, hence having no vertical velocity.
     * When the latter condition is met, the method changes the Avatar's vertical velocity to a defined value.
     */
    private void jump() {
        if(getVelocity().y() == 0)
            setVelocity(new Vector2(getVelocity().x(), JUMP_VELOCITY_Y));
    }

    private void fall() {
        transform().setAccelerationY(GRAVITY);
    }

    /**
     * Adds the Avatar's energy by a constant factor if two conditions are met:
     *      1. The Avatar is standing on a solid mass (hence having no vertical velocity).
     *      2. The Avatar's energy hasn't reached its maximal value.
     */
    private void updateEnergy() {
        if(getVelocity().y() == 0 && energy < MAX_ENERGY)
            this.energy += ENERGY_CHANGE;
    }

    private void cancelHorizontalVelocity() {
        setVelocity(new Vector2(0, getVelocity().y()));
    }

    /**
     *
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
}
