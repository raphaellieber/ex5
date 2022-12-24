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
    private static final float SUN_FINAL_ANGLE = (float) (- Math.PI);

    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
            float cycleLength) {
        Renderable sunImage = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, SUN_DIMENSIONS, sunImage);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        /**
         * The Transition Object takes care of the sun position based on its angle.
         * When referring to angles, the window is considered a polar coordinate system.
         * The window center is considered as the coordinate origin.
         * The angle range runs from 0 (normal coordinates [1,0]) to PI (normal coordinates [-1,0]).
         * There is no use for angles between PI and 2*PI since the sun should only be visible in the upper
         * part of the window.
         * This is different than as mentioned in the exercise instructions, where the angle in question is in relation
         * to VECTOR_UP (which is [0,1]).
         *
         * The Consumer (which is the 2nd parameter of the constructor) receives a lambda callback.
         * The latter takes an angle of type float, and sets the sun center by means of a private
         * method "calcSunPosition".
         */
        Transition<Float> sunPosition = new Transition(
                sun, (angle) -> sun.setCenter(calcSunPosition(windowDimensions, (Float) angle)),
                SUN_INIT_ANGLE, SUN_FINAL_ANGLE, Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength, Transition.TransitionType.TRANSITION_LOOP, null);
        gameObjects.addGameObject(sun, layer);
        return sun;
    }

    /**
     * The sun distance is defined as the distance between the window center and the sun center.
     * Using the distance and the polar angle, the x and y coordinates of the sun can be calculated as follows:
     *      x coordinate = distance * cos(angle)
     *      y coordinate = distance * sin(angle)
     * The above coordinates are relative to the window center.
     * Since the program refers to positions with the top left corner of the window as origin,
     * the coordinates need to be adapted as following:
     *      absolute (x,y) coordinates = window center coordinates (x0,y0) +/- relative sun coordinates (x,y).
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        Vector2 origin = new Vector2(windowDimensions.mult(0.5f));
        float positionX = (float) (SUN_MIN_DISTANCE * Math.cos(angleInSky));
        float positionY = (float) ((SUN_MIN_DISTANCE - 150) * Math.sin(angleInSky));
        Vector2 sunPosition = new Vector2(origin.x() + positionX, origin.y() - positionY);
        return sunPosition;
    }
}
