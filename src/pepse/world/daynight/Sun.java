package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.*;

public class Sun {
    private static final float LONG_RADIUS_WINDOW_RATIO = 0.6f;
    private static final float SHORT_RADIUS_WINDOW_RATIO = 0.7f;
    private static final Vector2 SUN_DIMENSIONS = new Vector2(100, 100);
    private static final String SUN_TAG = "sun";
    private static final float SUN_INIT_ANGLE = (float) (Math.PI / 2);
    private static final float SUN_FINAL_ANGLE = (float) (- Math.PI * 3 / 2);
    private static final Color SUN_COLOR = Color.YELLOW;


    /**
     * Creates the sun of type GameObject.
     * @param cycleLength equals the cycleLength of a whole day.
     * @return the sun GameObject.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
            float cycleLength) {

        Renderable sunImage = new OvalRenderable(SUN_COLOR);
        GameObject sun = new GameObject(Vector2.ZERO, SUN_DIMENSIONS, sunImage);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        // The Transition Object takes care of the sun position based on its angle.
        // When referring to angles, the window is considered a polar coordinate system.
        // The window center is considered as the coordinate origin.
        // The angle range runs from PI/2 (midday with normal coordinates [0,1]) and performs a whole circle.
        // Since the sun turns clockwise, the angle diminishes at every step and the circle terminates at -(3/2) * PI.
        // This is different from as mentioned in the exercise instructions, where the angle in question is in relation
        // to VECTOR_UP (which is [0,1]).
        // The Consumer (which is the 2nd parameter of the constructor) receives a lambda callback.
        // The latter takes an angle of type float, and sets the sun center by means of a private
        // method "calcSunPosition".
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
     * The sun travels in an ellipse, which has a long and short radius.
     * Since this ellipse is longer on the horizontal axis, the x coordinate uses the long radius and
     * the y coordinate the short radius.
     * @param angleInSky the polar angle of the sun in relation to the window center.
     * @return
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        Vector2 origin = new Vector2(windowDimensions.x() * 0.5f, windowDimensions.y() * 0.8f);
        float sunPathLongRadius = windowDimensions.x() * LONG_RADIUS_WINDOW_RATIO;
        float sunPathShortRadius = windowDimensions.y() * SHORT_RADIUS_WINDOW_RATIO;
        float positionX = (float) (sunPathLongRadius * Math.cos(angleInSky));
        float positionY = (float) (sunPathShortRadius * Math.sin(angleInSky));
        Vector2 sunPosition = new Vector2(origin.x() + positionX, origin.y() - positionY);
        return sunPosition;
    }
}