package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {
    private static final Vector2 SUN_HALO_DIMENSIONS = new Vector2(150, 150);

    /**
     * Creates the sun halo GameObject.
     * @return the sun halo GameObject.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun, Color color) {
        OvalRenderable haloImage = new OvalRenderable(color);
        GameObject sunHalo = new GameObject(Vector2.ZERO, SUN_HALO_DIMENSIONS, haloImage);
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        // A lambda callback which sets the sunHalo center at the same place of the sun centering
        // each new frame (after deltaTime).
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));
        return sunHalo;
    }
}
