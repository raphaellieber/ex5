package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject{
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(50, 50);
    private static final float JUMP_VELOCITY_Y = -300;
    private static final float FALL_ACCELERATION_Y = 500;
    private static final float HORIZONTAL_VELOCITY_X = 300;
    private static final float MIN_ENERGY = 0;
    private static final float MAX_ENERGY = 100;
    private static final float ENERGY_CHANGE = 0.5f;
    private float energy = MAX_ENERGY;
    private boolean flying = false;
    private UserInputListener inputListener;
    private GameObjectCollection gameObjects;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        Renderable avatarRenderable = new RectangleRenderable(Color.PINK);
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_DIMENSIONS, avatarRenderable);
        avatar.gameObjects = gameObjects;
        avatar.inputListener = inputListener;
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.fall();
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateEnergy();
        cancelHorizontalVelocity();
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            moveRight();
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT))
            moveLeft();
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE))
            jump();
    }

    private void moveRight() {
        setVelocity(new Vector2(HORIZONTAL_VELOCITY_X, getVelocity().y()));

    }

    public void moveLeft() {
        setVelocity(new Vector2(- HORIZONTAL_VELOCITY_X, getVelocity().y()));
    }

    private void jump() {
        if(getVelocity().y() == 0)
            setVelocity(new Vector2(getVelocity().x(), JUMP_VELOCITY_Y));
            fall();
    }

    private void fall() {
        transform().setAccelerationY(FALL_ACCELERATION_Y);
    }

    private void updateEnergy() {
        if(flying && energy > MIN_ENERGY)
            energy -= ENERGY_CHANGE;
        else if(energy < MAX_ENERGY)
            energy += ENERGY_CHANGE;
    }

    private void cancelHorizontalVelocity() {
        setVelocity(new Vector2(0, getVelocity().y()));
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        System.out.println("hi");
    }
}
