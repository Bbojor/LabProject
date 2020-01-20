package org.engine.graphics;

import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import org.engine.GameLoop;
import org.engine.input.KeyInput;
import org.engine.input.MouseInput;
import org.engine.resources.ImageResource;
import org.world.tiles.Tile;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Handles the creation of the game frame, the initialization of some related OpenGL objects and provides useful functions for
 * rendering graphics onto the screen
 */
public class Renderer
{
    private static GLWindow window = null;
    private static  GLProfile profile = null;

    private static final int SCREEN_WIDTH = 320;
    private static final int SCREEN_HEIGHT = 180;

    /**
     * Viewport dimensions and scale, useful for rendering objects to the screen. They are not final since they are
     * calculated at runtime based on the screen resolution, or in case the game window changed it's dimensions.
    */
    public static int unitsWide = 320, unitsHigh = 180, scale = 6;

    /**
     * Camera coordinates, provide a moving camera functionality
     */
    public static float cameraX ,cameraY = 0;

    /**
     *  Color parameters used when rendering
     */
    private static float red = 1f, green = 1f,blue = 1f,alpha = 1f;

    /**
     * Constants used to choose font size when rendering text
     */
    public static final short SMALL_FONT = 0, MEDIUM_FONT = 1, LARGE_FONT = 2;

    /**
     * Dimensions of the available fonts
     */
    public static int SMALL_FONT_WIDTH, MEDIUM_FONT_WIDTH, LARGE_FONT_WIDTH;

    /**
     * Rotation of the gl context
     */
    private static float rotation = 0f;


    private static TextRenderer smallTextRenderer = null, mediumTextRenderer = null, largeTextRenderer = null;

    /**
     *  Initializes the window and some OpenGL objects
     * @throws IllegalStateException  fatal error, prints stack trace and exits,
     */
    public static void init() throws IllegalStateException
    {
        GLProfile.initSingleton();
        profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);

        window = GLWindow.create(caps);
        window.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
        window.setResizable(false);
        window.setTitle("Geim");
        window.requestFocus();
        window.addGLEventListener(new EventListener());
        window.setFullscreen(true);
        window.addMouseListener(new MouseInput());
        window.addKeyListener(new KeyInput() );
        window.setVisible(true);
        window.addWindowListener(new WindowListener()
        {
            @Override
            public void windowResized(WindowEvent windowEvent)
            {

            }

            @Override
            public void windowMoved(WindowEvent windowEvent)
            {

            }

            @Override
            public void windowDestroyNotify(WindowEvent windowEvent)
            {

            }

            @Override
            public void windowDestroyed(WindowEvent windowEvent)
            {
                GameLoop.setState(GameLoop.STATES.EXIT);
            }

            @Override
            public void windowGainedFocus(WindowEvent windowEvent)
            {

            }

            @Override
            public void windowLostFocus(WindowEvent windowEvent)
            {

            }

            @Override
            public void windowRepaint(WindowUpdateEvent windowUpdateEvent)
            {

            }
        });

        window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
    }

    /**
     * Sets the rotation of the renderer relative to the screen. Should be reset (set back to 0) after every use since
     * the last value set carries over to all future renderings
     * @param r rotation in degrees
     */
    public static void setRotation(float r)
    {
        rotation = r;
    }

    /**
     * Draws a solid fill rectangle with it's "center" (intersection of diagonals) at the given coordinates x,y and specified width/height
     * @param x horizontal coordinate of the "center"
     * @param y vertical coordinate of the "center"
     * @param width full width of the rectangle being drawn
     * @param height full height of the rectangle being drawn
     */
    public static void fillRect(float x, float y, float width, float height)
    {
        //don't render if too far left or too far right
        if((x-width/2 > Renderer.unitsWide /2f + Renderer.cameraX)
                || (x+width/2 < -Renderer.unitsWide /2f + Renderer.cameraX ))
            return;

        //don't render if too high or low
        if(y-height/2 > Renderer.unitsHigh /2f + Renderer.cameraY
                || y+height/2 < -Renderer.unitsHigh /2f + Renderer.cameraY )
            return;

        GL2 gl = EventListener.gl;

        gl.glTranslatef(x,y,0);
        gl.glRotatef(rotation,0,0,1); //rotate around z

        gl.glColor4f(red,green,blue,alpha);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(-width/2,-height/2);
        gl.glVertex2f(-width/2,+height/2);
        gl.glVertex2f(+width/2,+height/2);
        gl.glVertex2f(+width/2,-height/2);
        gl.glEnd();
        gl.glFlush();

        gl.glRotatef(-rotation,0,0,1); //rotate back
        gl.glTranslatef(-x,-y,0);
    }

    /**
     *  Draws a given Image Resource object to the screen, used for pretty much everything
     * @param image Image resource (retrieved from an objects Animations)
     * @param width width of the image (should be used with getters from the image object rather than hardcoded)
     * @param height height of the image
     * @param x coordinate of the image's centre
     * @param y ditto
     * @param flip horizontal flip
     */
    public static void drawImage(ImageResource image , float width, float height, float x, float y, boolean flip)
    {
        //don't render if too far left or too far right
        if((x-width/2 > Renderer.unitsWide /2f + Renderer.cameraX)
                || (x+width/2 < -Renderer.unitsWide /2f + Renderer.cameraX ))
        {return;}

        //don't render if too high or low
        if(y-height/2 > Renderer.unitsHigh /2f + Renderer.cameraY
                || y+height/2 < -Renderer.unitsHigh /2f + Renderer.cameraY )
            return;

        GL2 gl = EventListener.gl;

        Texture texture = image.getTexture();

        //bind texture to openGL
        if(texture != null)
        {
            gl.glBindTexture(GL2.GL_TEXTURE_2D,texture.getTextureObject());
        }
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MAG_FILTER,GL2.GL_NEAREST);

        gl.glTranslatef(x,y,0);
        gl.glRotatef(rotation,0,0,1); //rotate around z

        gl.glColor4f(red, green, blue, alpha);
        gl.glBegin(GL2.GL_QUADS);

        if(flip)
        {
            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(-width / 2, -height / 2);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(-width / 2, height / 2);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(width / 2, height / 2);
            gl.glTexCoord2f(0, 0);
        }
        else
        {
            gl.glTexCoord2i(0, 0);
            gl.glVertex2f(-width / 2, -height / 2);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(-width / 2, height / 2);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(width / 2, height / 2);
            gl.glTexCoord2f(1, 0);
        }
        gl.glVertex2f(width / 2, -height / 2);

        gl.glEnd();
        gl.glFlush();

        //unbind texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D,0);

        gl.glRotatef(-rotation,0,0,1); //rotate back
        gl.glTranslatef(-x,-y,0);
    }


    //load the required font when necessary
    private static void loadFont()
    {
        URL fontUrl;
        try
        {
            Font smallFont,mediumFont,largeFont;
            fontUrl = Renderer.class.getResource("/resources/Fonts/8bit.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
            font = font.deriveFont(Font.PLAIN,10);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            smallFont = font;

            font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
            font = font.deriveFont(Font.PLAIN,15);
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            mediumFont = font;

            font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
            font = font.deriveFont(Font.PLAIN,20);
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            largeFont = font;
            smallTextRenderer = new TextRenderer(smallFont);
            mediumTextRenderer = new TextRenderer(mediumFont);
            largeTextRenderer = new TextRenderer(largeFont);

            SMALL_FONT_WIDTH = (int) smallTextRenderer.getCharWidth('A');
            MEDIUM_FONT_WIDTH = (int) mediumTextRenderer.getCharWidth('A');
            LARGE_FONT_WIDTH = (int) largeTextRenderer.getCharWidth('A');

        }
        catch (FontFormatException | IOException e)
        {
            e.printStackTrace();
        }
    }

    /**TODO
     *   Add support for text boxes/paragraphs
     *
     *  Draws a given string on the screen centered ath the given x,y coordinates
     * @param text text to be rendered
     * @param textSize size of then in-game font (chosen between 3 sizes given as public finals)
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param r red color value(0 to 1f)
     * @param g green color value(0 to 1f)
     * @param b blue color value(0 to 1f)
     * @param a alpha color value(0 to 1f)
     */
    public static void drawText(String text,short textSize,int x, int y,float r, float g, float b, float a)
    {
        TextRenderer textRenderer;

        if(textSize == MEDIUM_FONT)
            textRenderer = mediumTextRenderer;
        else if(textSize == LARGE_FONT)
            textRenderer = largeTextRenderer;
        else
            textRenderer = smallTextRenderer;

        if(textRenderer == null)
        {
            loadFont();
            if(textSize == MEDIUM_FONT)
                textRenderer = mediumTextRenderer;
            else if(textSize == LARGE_FONT)
                textRenderer = largeTextRenderer;
            else
                textRenderer = smallTextRenderer;
        }

        setColor(r,g,b,a);
        textRenderer.beginRendering(Renderer.unitsWide,Renderer.unitsHigh);
        textRenderer.setSmoothing(false);
        textRenderer.draw(text, Renderer.unitsWide/2 + x - text.length() * (int)textRenderer.getCharWidth('A') /2,Renderer.unitsHigh/2 + y);
        textRenderer.endRendering();
        setColor(255f,255f,255f,255f);
    }

    /**
     * Sets rendering color, all values should be maxed out for rendering properly coloured images
     * @param r red value (0f to 1f)
     * @param g green value (0f to 1f)
     * @param b blue value (0f to 1f)
     * @param a alpha aka transparency (0f to 1f) (0 = invisible, 1 = opaque)
     * If any of the parameters are outside of the allow range, the function sets the respective value to 0 or 1,
     * depending on whether the argument was below 0 or above 1
     */
    public static void setColor(float r, float g, float b, float a)
    {
        red = Math.max(0,Math.min(1,r));
        green = Math.max(0,Math.min(1,g));
        blue = Math.max(0,Math.min(1,b));
        alpha = Math.max(0,Math.min(1,a));
    }

    /**
     * Computes the isometric coordinates from the regular x/y coordinates
     * @param x coordinate on the x axis
     * @param y coordinate on the y axis
     * @return returns an integer array of length 2 containing the isometric x and isometric y respectively in that order
     */
    public static int[] IsoCoordinates(double x, double y)
    {
        int[] isometricCoords = new int[2];

        //=============// CALCULATE NEW ISOMETRIC X //============================//
        double tempX = x ;

        //get origin to align with vertex of the 0,0 iso square
        double tempY = y -  Tile.GROUND_TILE_HEIGHT/2f;

        //make aspect ratio  1
        tempY = -2 * tempY;

        //rotate 45 degs
        double xRot = tempY * Tile.SINE + tempX * Tile.COSINE;

        //make negative values work
        if (xRot < 0)
        {
            xRot -= Tile.OFFSET;
        }

        isometricCoords[0] = (int) (xRot / Tile.OFFSET);

        //========================================================================//

        //=============// CALCULATE NEW ISOMETRIC Y //============================//
        tempX = x ;
        tempY = y - Tile.GROUND_TILE_HEIGHT/2f;

        //make aspect ratio 1
        tempY = -2 * tempY;

        //rotate 45 degs
        double yRot = -tempX * Tile.SINE + Tile.COSINE * tempY;

        //make negative values work
        if (yRot < 0)
        {
            yRot -= Tile.OFFSET;
        }

        isometricCoords[1] = (int) (-yRot / Tile.OFFSET);

        return isometricCoords;
    }

    /**
     *  Checks whether the given point with coordinates x1,y1 is inside of the rectangle with "centre" (intersection of
     *  diagonals) at x2,y2
     *  and with given width and height
     * @param x1 horizontal coordinate of the point in question
     * @param y1 vertical coordinate of the point in question
     * @param x2 horizontal coordinate of the rectangle (its intersection of diagonals)
     * @param y2 vertical coordinate of the rectangle (its intersection of diagonals)
     * @param width full width of the rectangle
     * @param height full height of the rectangle
     */
    public static boolean isInRectangle(float x1,float y1, float x2,float y2, float width, float height )
    {
        if(x1 < x2 - width/2 || x1 > x2 + width/2 )
            return false;
        return !(y1 < y2 - height / 2) && !(y1 > y2 + height / 2);
    }


    /**
     *  Displays the window and all it's contents
     */
    public static void render()
    {
        if(window == null)
            return;
        window.display(); //in EventListener class
    }

    /**
     *  Fetches the current width of the window
     * @return width of the window as an integer
     */
    public static int getWindowWidth()
    {
        return window.getWidth();
    }

    /**
     *  Fetches the current height of the window
     * @return height of the window as an integer
     */
    public static int getWindowHeight()
    {
        return window.getHeight();
    }

    /**
     *  Returns the current GLProfile object which holds some useful graphics-related information
     * @return GLProfile object created during initialization
     */
    public static GLProfile getProfile() { return profile; }

}
