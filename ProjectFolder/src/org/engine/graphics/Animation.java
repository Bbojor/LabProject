package org.engine.graphics;

import org.engine.resources.ImageResource;

/**
 * Groups together some useful information about an animation belonging to a game object.
 * It holds the actual frames(images) making up the animation, the fps of the animation, and whether the animation should loop indefinitely or not.
 * An animation is comprised of a sequence of individual images, each represented by an ImageResource object.
 * Their creation is handled by the {@link Animator} class, hence no constructor is provided.
 */
public class Animation
{
    /**
     * Holds the individual frames of  the animation
     */
    public ImageResource[] frames;

    /**
     * The frames per second of the animation. Controls animation speed.
     */
    public int fps = 1;

    /**
     * Whether the animation should loop indefinitely. When set to false the animation stops on the last frame.
     */
    public boolean loop;

    /**
     * Fetches the image at the given index from the animation sequence, used to get the current frame in order to render it
     * @param currentFrame frame to be retrieved
     * @return ImageResource class containing the desired image/information
     */
    public ImageResource getImage(int currentFrame)
    {
        return frames[currentFrame];
    }

}

