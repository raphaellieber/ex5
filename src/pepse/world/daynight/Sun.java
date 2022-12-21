package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Vector2;

public class Sun {
    GameObjectCollection gameObjects;
    Layer layer;
    Vector2 windowDimensions;
    float cycleLength;

    public Sun(GameObjectCollection gameObjects, Layer layer, Vector2 windowDimensions, float cycleLength) {
        this.gameObjects = gameObjects;
        this.layer = layer;
        this.windowDimensions = windowDimensions;
        this.cycleLength = cycleLength;
    }

    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
            float cycleLength) {

    }
}
