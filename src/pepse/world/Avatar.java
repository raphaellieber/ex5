package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject implements energized{
    public static final Vector2 AVATAR_DIMENSIONS = new Vector2(50, 70);
    private static final float JUMP_VELOCITY_Y = -300;
    private static final float FALL_ACCELERATION_Y = 500;
    private static final float HORIZONTAL_VELOCITY_X = 300;
//    private static final float MIN_ENERGY = 0;
    private static final float MAX_ENERGY = 100;
    private static final float ENERGY_CHANGE = 0.5f;

    private static final String[] MOVEMENT_PATH = new String[] {"assets/leftFoot.png", "assets/rightFoot.png"};
    private static final float TIME_BETWEEN_CLIPS = 0.1f;

    private float energy = MAX_ENERGY;
//    private boolean flying = false;
    private final UserInputListener inputListener;
//    private GameObjectCollection gameObjects;
    private final Renderable standStillImg;

    AnimationRenderable movementAnimation;

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
        this.standStillImg = imageReader.readImage("assets/standstill.jpg", true);
        this.movementAnimation = new AnimationRenderable(MOVEMENT_PATH,imageReader,true,TIME_BETWEEN_CLIPS);

        this.renderer().setRenderable(this.standStillImg);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

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

        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
//            System.out.println(this.getCenter().x());  // TODO

            moveRight();
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            moveLeft();
//            System.out.println(this.getCenter().x());   // TODO
        }
            if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0)
                fly();
            else jump();
        }
    }

    /**
     * A getter for the Avatar's energy
     * @return the energy of the Avatar
     */
    public float getEnergy(){ return this.energy;}

    private void moveRight() {
        setVelocity(new Vector2(HORIZONTAL_VELOCITY_X, getVelocity().y()));
        this.renderer().setRenderable(this.movementAnimation);
        this.renderer().setIsFlippedHorizontally(false);
    }

    private void moveLeft() {
        setVelocity(new Vector2(- HORIZONTAL_VELOCITY_X, getVelocity().y()));
        this.renderer().setRenderable(this.movementAnimation);
        this.renderer().setIsFlippedHorizontally(true);
    }

    private void fly() {
        setVelocity(new Vector2(getVelocity().x(), JUMP_VELOCITY_Y));
        this.energy -= ENERGY_CHANGE;
    }

    private void jump() {
        if(getVelocity().y() == 0)
            setVelocity(new Vector2(getVelocity().x(), JUMP_VELOCITY_Y));
    }

    private void fall() {
        transform().setAccelerationY(FALL_ACCELERATION_Y);
    }

    private void updateEnergy() {
        if(getVelocity().y() == 0 && energy < MAX_ENERGY)
            this.energy += ENERGY_CHANGE;
    }

    private void cancelHorizontalVelocity() {
        setVelocity(new Vector2(0, getVelocity().y()));
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getClass() == Block.class)
            this.setVelocity(new Vector2(0,this.getVelocity().y()));
    }
}
