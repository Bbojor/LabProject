package org.engine.graphics;

import org.engine.resources.ImageResource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *  This class contains the set of all animations needed for the current instance of the game.
 *  At the creation of a new visible game object, the game engine checks if the required animation already exists.
 *  If not it is created and stored in the Animator's "animationPool", a set implemented as a hash map using the animation;s relative file path as key.
 *  Once an animation is created it is simply referenced when another object using it is created.
 *
 */

public class Animator
{
    /**
     * Map which holds animations for all game objects so that they don't have to be loaded when creating multiple instances of the same object
     */
    private static Map <String, Animation[]> animationPool = new HashMap<>();

    /**
     * Returns the animation associated with an object class
     * @param animation Simple Name of the object class for which to retrieve the animation
     * @return The corresponding animation or null if no such animation exists
     */
    public static Animation[] getAnimation(String animation)
        {
            return animationPool.get(animation);
        }


    /**
     * Creates a single static texture from the given path.
     * @param path relative path to the image file
     * @return the created animation, after storing it in the pool
     */
    public static Animation[] createSingleTexture(String path)
    {
        Animation[] animation = new Animation[1];
        animation[0] = new Animation();
        animation[0].frames = new ImageResource[1];
        animation[0].frames[0] = new ImageResource(path);

        animationPool.put(path,animation);

        return animation;
    }

    /**
     *  Creates an tiled animation from the given file
     * @param path path to the image file (all animations pertaining to an object should be in the same file and have frames of the same width/height)
     * @param info an Animation Information array containing all data needed to properly load the animation
     * @return the created Animation array, after it is stored in the animation pool
     */
    public static Animation[] createAnimation(String path, AnimationInformation[] info)
    {
        //allocate animations array
        Animation[] animations = new Animation[info.length];

        //load tiles
        ImageResource[][] tiles =  getTiles(path,info);

        //put tiles and animation data in place
        for (int i = 0; i < animations.length; i++)
        {
            animations[i] = new Animation();
            animations[i].frames = new ImageResource[info[i].frames];
            animations[i].fps = info[i].fps;
            animations[i].loop = info[i].loop;
            System.arraycopy(tiles[i], 0, animations[i].frames, 0, animations[i].frames.length);
        }

        //add created animation to the pool
        animationPool.put(path,animations);

        return animations;
    }

    /**
     * Splits a spritesheet image into a matrix of image resources where each row corresponds to a specific animation
     */
    private static  ImageResource[][] getTiles(String path,AnimationInformation[] info)
    {

        ImageResource[][] images;
        URL url = ImageResource.class.getResource(path); //this way path is relative to java project
        //open spritesheet
        BufferedImage tempImage = null;

        BufferedImage subImage = null;

        images = new ImageResource[info.length][];

        try
        {
            tempImage = ImageIO.read(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < info.length; i++)
        {
            //allocate animation
            images[i] = new ImageResource[info[i].frames];

            for (int j = 0; j < info[i].frames; j++)
            {
                if (tempImage != null)
                {
                    subImage = tempImage.getSubimage(j * info[0].tileWidth, i * info[0].tileHeight,  info[0].tileWidth,  info[0].tileHeight);
                }

                if (subImage != null)
                    subImage.flush();

                assert subImage != null;
                images[i][j] = new ImageResource(subImage);

            }
        }
        return images;
    }
}
