package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {
    GameObjectCollection gameObjects;
    Layer layer;
    Vector2 windowDimensions;
    static Vector2 SUN_DIMENSIONS = new Vector2(100, 100);
    float cycleLength;
    private static final String SUN_TAG = "sun";
    private static final float SUN_MIN_DISTANCE = 300;
    private static final float SUN_INIT_ANGLE = (float) (Math.PI);
    private static final float SUN_FINAL_ANGLE = (float) (0);

    public Sun(GameObjectCollection gameObjects, Layer layer, Vector2 windowDimensions, float cycleLength) {
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.windowDimensions = windowDimensions;
        this.cycleLength = cycleLength;
    }

    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
            float cycleLength) {
        Renderable sunImage = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, SUN_DIMENSIONS, sunImage);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        Transition<Float> sunPosition = new Transition(
                sun, (angle) -> sun.setCenter(calcSunPosition(windowDimensions, (Float) angle)),
                SUN_INIT_ANGLE, SUN_FINAL_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        gameObjects.addGameObject(sun, layer);
        return sun;
    }

    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        Vector2 origin = new Vector2(windowDimensions.mult(0.5f));
        float tempDist = (float) (SUN_MIN_DISTANCE);
        float positionX = (float) (tempDist * Math.cos(angleInSky));
        float positionY = (float) (tempDist * Math.sin(angleInSky));
        Vector2 sunPosition = new Vector2(origin.x() + positionX, origin.y() - positionY);
        return sunPosition;
    }
}
