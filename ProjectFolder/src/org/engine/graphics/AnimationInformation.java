package org.engine.graphics;

/** This class is used to package all relevant information about an object's animation(s)
 *  in order to send it to the static Animator class
 *
 *  It is only used once for each object subclass, since the retrieved animation is stored in the Animator class'
 *  animation pool after creation
 */
public class AnimationInformation
{
    /**
     * Number of frames in the animation
     */
    public int frames;
    /**
     * See {@link Animation#fps}
     */
    public int fps;

    /**
     * Dimensions of a single frame of the animation
     */
    public int tileWidth, tileHeight;

    /**
     * See {@link Animation#loop}
     */
    public boolean loop;

    /**
     *  Constructor
     * @param frames frames of the animation
     * @param fps frames per second
     * @param tileWidth width of the tile in the tileset
     * @param tileHeight height of the tile in the tileset
     * @param loop whether the animation is a loop or not
     */
    public AnimationInformation(int frames, int fps, int tileWidth, int tileHeight, boolean loop)
    {
        this.frames = frames;
        this.fps = fps;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.loop = loop;
    }
}
