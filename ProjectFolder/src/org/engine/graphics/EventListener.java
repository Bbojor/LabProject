package org.engine.graphics;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import org.engine.GameLoop;
import org.engine.ui.MainMenu;

/**
 * Implements the GLEventListener interface provided by the JOGL library, providing various methods required for OpenGL functionality.
 * These methods are used by the JOGL library classes and should not be called by the user
 */
public class EventListener implements GLEventListener
{
    static GL2 gl = null;

    /**
     *  Initializes the OpenGL object with the parameters required to function properly. Shouldn't be explicitly called
     */
    @Override
    public void init(GLAutoDrawable drawable)
    {
        gl = drawable.getGL().getGL2();

        //set clear color to black
        gl.glClearColor(0,0,0,1);

        //enable 2D texture use
        gl.glEnable(GL2.GL_TEXTURE_2D);

        //enable blending
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);

      //  gl.setSwapInterval(0);  //this disables V-sync => no FPS cap, possible screen tearing should probably not be used since game speed is
                                  // directly proportional to the FPS
    }

    /**
     * Not used/declared
     */
    @Override
    public void dispose(GLAutoDrawable drawable)
    {

    }

    /**
     * Renders the game world on screen, provides camera functionality. Should not be explicitly called
     */
    @Override
    public void display(GLAutoDrawable drawable)
    {
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glTranslatef(-Renderer.cameraX,-Renderer.cameraY,0); //offset by camera
        if(GameLoop.getState() == GameLoop.STATES.GAME)
             GameLoop.getWorld().render();
        else
            MainMenu.render();
        gl.glTranslatef(Renderer.cameraX,Renderer.cameraY,0); //undo offset*/
    }

    /**
     *  Resize the viewport when the size of the game window changes, tries to keep the same resolution/aspect ration for the game.
     *  Called automatically in case of such an event, no need to explicitly call it in user-defined code
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        Renderer.unitsHigh = Renderer.getWindowHeight() / (Renderer.getWindowWidth() /Renderer.unitsWide); // keep aspect ratio
        Renderer.scale = Renderer.getWindowWidth()/Renderer.unitsWide;

        gl.glOrtho(-Renderer.unitsWide /2f,Renderer.unitsWide /2f,  Renderer.unitsHigh /2f,-  Renderer.unitsHigh /2f,-1.0f,1.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
}
