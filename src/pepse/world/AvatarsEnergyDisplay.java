package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class AvatarsEnergyDisplay extends GameObject {

    private static final String STRING_TO_DISPLAY = "Energy: ";

//    private final float valueToDisplay;
    private final TextRenderable textRenderable;
    private final Avatar avatar;

    /**
     * Constructor
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *      *               Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param avatar        represents the avatar object
     */
    public AvatarsEnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions, Avatar avatar) {
        super(topLeftCorner, dimensions, null);

        this.avatar = avatar;
        String stringValue = Float.toString(avatar.getEnergy());
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

        String stringValue = Float.toString(this.avatar.getEnergy());
        this.textRenderable.setString(STRING_TO_DISPLAY + stringValue);
    }
}
