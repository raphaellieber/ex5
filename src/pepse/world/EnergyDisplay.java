package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.interfaces.Energized;

import java.awt.*;

public class EnergyDisplay extends GameObject {

    private static final String STRING_TO_DISPLAY = "Energy: ";

    private final TextRenderable textRenderable;
    private final Energized obj;

    /**
     * Constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *      *               Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param obj        represents the energized object
     */
    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions, Energized obj) {
        super(topLeftCorner, dimensions, null);

        this.obj = obj;
        String stringValue = Float.toString(obj.getEnergy());
        this.textRenderable = new TextRenderable(STRING_TO_DISPLAY + stringValue);
        this.textRenderable.setColor(Color.black);
        this.renderer().setRenderable(this.textRenderable);
    }

    /**
     * An override for the original function.
     * Updates the content of the string on the display
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        String stringValue = Float.toString(this.obj.getEnergy());
        this.textRenderable.setString(STRING_TO_DISPLAY + stringValue);
    }
}
