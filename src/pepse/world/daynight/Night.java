package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Night {
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final Float MIDDAY_OPACITY = 0f;
    private static final String NIGHT_TAG = "night";

    /**
     * Creates a GameObject in the form of a black rectangle which changes transparency through a cycle.
     * @return The created GameObject is returned (the class is considered a "creator" class).
     */

    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        Renderable nightRenderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightRenderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        Transition<Float> nightTransition = new Transition<>(
                night, night.renderer()::setOpaqueness, MIDDAY_OPACITY, MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength, Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT_TAG);
        return night;
    }
}
