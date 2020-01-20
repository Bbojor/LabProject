package org.engine.input;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import org.engine.graphics.Renderer;

/**
 *  The MouseInput class implements the MouseListener interface and handles all the mouse-related events that the rest
 *  of the engine needs. Only the getter methods should be used by users outside of this class since the others are
 *  automatic responses to mouse events
 */
public class MouseInput implements MouseListener
{
    //the position of the mouse on the screen
    private static double mouseX;
    private static double mouseY;
    private static double oldMouseX;
    private static double oldMouseY;
    private static boolean moved = false;
    private static int isoX;
    private static int isoY;

    private static boolean[] currentState = new boolean[5];
    private static boolean[] previousState = new boolean[5];

    private static boolean[] mousePressed = new boolean[5]; //is true in case of a single click
    private static boolean[] mouseHeld = new boolean[5]; //is true when mouse was held for 2 or more update cycles

    /**
     * Updates all the stored values. Should be called once per update cycle.
     */
    public static void update()
    {
        if(oldMouseX == mouseX && oldMouseY == mouseY)
            moved = false;

        for(int i = 0; i < 5; i++)
        {
            oldMouseX = mouseX;
            oldMouseY = mouseY;
            mousePressed[i] = currentState[i] && !previousState[i];
            mouseHeld[i] = currentState[i] && previousState[i];
            previousState[i] = currentState[i];
        }
    }

    /**
     *  Not used, the mousePressed method offers the desired functionality
     */
    @Override
    public void mouseClicked(MouseEvent mouseEvent) { }

    /**
     * Not implemented
     */
    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }

    /**
     * Not implemented
     */
    @Override
    public void mouseExited(MouseEvent mouseEvent) { }

    /**
     *  Automatically called in the case of a mouse event by the listener, sets the value corresponding to the
     *  button causing the event to true
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) { currentState[mouseEvent.getButton()] = true; }

    /**
     *  Automatically called in the case of a mouse event by the listener, sets the value corresponding to the
     *  button causing the event to false
     */
    @Override
    public void mouseReleased(MouseEvent mouseEvent) { currentState[mouseEvent.getButton()] = false; }

    /**
     * Automatically gets called by the listener when the mouse is moved, updates the saved mouse position accordingly
     */
    @Override
    public void mouseMoved(MouseEvent mouseEvent)
    {
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
        moved  = true;
    }

    /**
     * Automatically gets called by the listener when the mouse is held and moved, updates its position accordingly
     */
    @Override
    public void mouseDragged(MouseEvent mouseEvent)
    {
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
    }

    /**
     * Not implemented
     */
    @Override
    public void mouseWheelMoved(MouseEvent mouseEvent) { }

    /**
     *  Returns the isometric x coordinate of the mouse pointer. In case the isometric values were not updated since the
     *  last time the mouse was moved, it computes and sets the new ones before returning
     * @return the horizontal position on the isometric grid as an integer
     */
    public static int getIsoX()
    {
        if(moved) //compute new values
        {
            int[] isometricCoordinates = Renderer.IsoCoordinates(getPixelX(), getPixelY());
            isoX = isometricCoordinates[0];
            isoY = isometricCoordinates[1];
            moved = false; //mark the values as updated
        }
        return isoX;
    }

    /**
     *  Returns the isometric y coordinate of the mouse pointer. In case the isometric values were not updated since the
     *  last time the mouse was moved, it computes and sets the new ones before returning
     * @return the horizontal position on the isometric grid as an integer
     */
    public static int getIsoY()
    {
        if(moved) //compute new values
        {
            int[] isometricCoordinates = Renderer.IsoCoordinates(getPixelX(), getPixelY());
            isoX = isometricCoordinates[0];
            isoY = isometricCoordinates[1];
            moved = false; //mark the values as updated
        }
        return isoY;
    }

    /**
     *  Returns the x position of the mouse on the in-game screen, based on the renderer and the camera.
     * @return the horizontal mouse coordinate as a double
     */
    public static double getPixelX() { return mouseX/Renderer.scale -Renderer.unitsWide /2f + Renderer.cameraX; }

    /**
     *  Returns the y position of the mouse on the in-game screen, based on the renderer and the camera.
     * @return the vertical mouse coordinate as a double
     */
    public static double getPixelY() { return mouseY/Renderer.scale -Renderer.getWindowHeight()/(float)Renderer.scale /2f + Renderer.cameraY; }

    /**
     *  Returns the x position of the mouse on the actual screen of the device running the game
     *  @return mouseX as a double
     */
    public static double getRealPixelX() { return mouseX; }

    /**
     *  Returns the y position of the mouse on the actual screen of the device running the game
     *  @return mouseY as a double
     */
    public static double getRealPixelY() { return mouseY; }

    /**
     * Checks whether the given mouse button was pressed.
     * @param button short value of the button (constants provided in the jogl awt MouseEvent class)
     */
    public static boolean getMousePressed(short button) { return mousePressed[button]; }

    /**
     * Checks whether the given mouse button was held down for more than one update cycle.
     */
    public static boolean getMouseHeld(short button) { return mouseHeld[button]; }

    /**
     * Checks whether the mouse has moved from its previously stored position
     * during the last update cycle.
     */
    public static boolean hasMoved()
    {
        return moved;
    }
}
