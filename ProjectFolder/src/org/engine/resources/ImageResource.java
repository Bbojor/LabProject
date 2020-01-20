package org.engine.resources;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import org.engine.graphics.Renderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * This class holds information about an image.
 * It is used only for game objects which have no proper animations (only still images).
 * .png files are preferred since they allow for transparent backgrounds
 */

public class ImageResource
{
    /**
     * OpenGL texture object, used by the {@link org.engine.graphics.Renderer} class to draw images on screen
     */
    private Texture texture = null;

    /**
     * Image object
     */
    private BufferedImage image;

    /**
     * Dimensions of the image
     */
    private float width, height;

    /**
     *  Attempts to retrieve a given image from the given path which should be relative tp the location of the source code
     *  If reading of the animation fails, an exception is thrown and the program terminates
     * @param path path relative to the game folder
     */
    public ImageResource(String path)
    {
        URL url = ImageResource.class.getResource(path); //this way path is relative to java project

        try
        {
            image = ImageIO.read(url);
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (image !=null)
        {
            image.flush();
        }
    }

    /**
     *  Creates an ImageResource object from an already existing image in memory, used for creating animations
     * @param image buffered image object
     */
    public ImageResource(BufferedImage image)
    {
            this.image = image;
            this.width = image.getWidth();
            this.height = image.getHeight();
    }

    /**
     * Gets the texture of the image, if the texture is not set it attempts to set it
     * @return JOGL {@link com.jogamp.opengl.util.texture.Texture} object
     */
    public Texture getTexture()
    {
        //if no image
        if(image == null)
        {
            return null;
        }

        //set texture
        if(texture == null)
        {
            texture = AWTTextureIO.newTexture(Renderer.getProfile(),image,true);
        }
        return texture;
    }

    /**
     * Returns the width of the image held in the object
     * @return width in pixels
     */
    public float getWidth() { return width; }

    /**
     * Returns the height of the image held in the object
     * @return height in pixels
     */
    public float getHeight() { return height; }

}
