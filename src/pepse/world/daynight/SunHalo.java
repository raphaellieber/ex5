package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {
    private static final Vector2 SUN_HALO_DIMENSIONS = new Vector2(150, 150);

    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun, Color color) {
        OvalRenderable haloImage = new OvalRenderable(color);
        GameObject sunHalo = new GameObject(Vector2.ZERO, SUN_HALO_DIMENSIONS, haloImage);
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        return sunHalo;
    }
}
