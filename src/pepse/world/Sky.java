package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sky {
    private static final String SKY = "sky";
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int layer) {
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));

        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY);
        gameObjects.addGameObject(sky, layer);

        return sky;
    }
}
