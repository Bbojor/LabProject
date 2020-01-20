package org.engine.input;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import java.io.*;

/**
 *  The KeyInput class implements the KeyListener interface provided in the JOGL library.
 *  It keeps track of the state of the keyboard and passes that information on to the rest of the engine where needed.
 */

public class KeyInput implements KeyListener
{

    private static boolean[] keyDown = new boolean[256];
    private static boolean[] keyHeld = new boolean[256];

    private static short lastKeyPressed;

    private static boolean keyAvailable;

    private static boolean []currentState = new boolean[256];
    private static boolean []previousState = new boolean[256];

    /**
     * Saved control bindings as key event values.
     */
    public static short UP, DOWN, LEFT, RIGHT, JUMP_DODGE, SHEATHE;

    /**
     * Load/Set control bindings from the preferences.txt file. If no file is present, it creates it
     */
    private static void loadBindings()
    {
        File preferences = new File("preferences.txt");
        if(!preferences.exists())
        {
            try
            {
                preferences.createNewFile();
                FileWriter fwr = new FileWriter(preferences.getAbsoluteFile());
                BufferedWriter bfwr = new BufferedWriter(fwr);
                bfwr.write("======[CONTROLS]=====\n");
                bfwr.write("UP="+ KeyEvent.VK_W +"\n");
                bfwr.write("DOWN=" + KeyEvent.VK_S +"\n");
                bfwr.write("LEFT=" + KeyEvent.VK_A + "\n");
                bfwr.write("RIGHT=" + KeyEvent.VK_D + "\n");
                bfwr.write("JUMP_DODGE=" + KeyEvent.VK_SPACE + "\n");
                bfwr.write("SHEATHE="  + KeyEvent.VK_X +  "\n");
                bfwr.write("=====================\n");
                bfwr.close();
                fwr.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            FileReader fr = new FileReader(preferences.getAbsoluteFile());
            BufferedReader bfr = new BufferedReader(fr);

            String line;
            boolean began = false;

            while ((line = bfr.readLine())!=null)
            {
                if(line.equals("======[CONTROLS]====="))
                    began = true;

                else
                if(began)
                {
                    if(line.equals("====================="))
                    {
                        bfr.close();
                        fr.close();
                        return;
                    }
                    else
                    {
                        String[] binding = line.split("=");
                        switch (binding[0])
                        {
                            case "UP" : UP = Short.parseShort(binding[1]);
                                break;
                            case "DOWN" : DOWN = Short.parseShort(binding[1]);
                                break;
                            case "LEFT" : LEFT = Short.parseShort(binding[1]);
                                break;
                            case "RIGHT" : RIGHT = Short.parseShort(binding[1]);
                                break;
                            case "JUMP_DODGE" : JUMP_DODGE = Short.parseShort(binding[1]);
                                break;
                            case "SHEATHE" :  SHEATHE = Short.parseShort(binding[1]);
                                break;
                        }
                    }
                }

            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current control bindings in the preferences.txt file
     */
    public static void saveBindings()
    {
        File preferences = new File("preferences.txt");

            try
            {
               if( preferences.createNewFile())
               {   System.out.println(preferences.getAbsolutePath());
                   FileWriter fwr = new FileWriter(preferences.getAbsoluteFile());
                   BufferedWriter bfwr = new BufferedWriter(fwr);
                   bfwr.write("======[CONTROLS]=====\n");
                   bfwr.write("UP=" + KeyInput.UP + "\n");
                   bfwr.write("DOWN=" + KeyInput.DOWN + "\n");
                   bfwr.write("LEFT=" + KeyInput.LEFT + "\n");
                   bfwr.write("RIGHT=" + KeyInput.RIGHT + "\n");
                   bfwr.write("JUMP_DODGE=" + KeyInput.JUMP_DODGE + "\n");
                   bfwr.write("SHEATHE=" + KeyInput.SHEATHE + "\n");
                   bfwr.write("=====================\n");
                   bfwr.close();
                   fwr.close();
               }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

    }

    static
    {
        loadBindings();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        currentState[keyEvent.getKeyCode()] = true;
        lastKeyPressed = keyEvent.getKeyCode();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        if(keyEvent.isAutoRepeat())
            return;
        currentState[keyEvent.getKeyCode()] = false;
    }

    /**
     *  Updates the necessary flags. Should be called once per update cycle.
     */
    public static void update()
    {
        keyAvailable = false;
        for(int i =1 ;i < 256; i++)
        {
            keyAvailable = keyAvailable || currentState[i];
            keyDown[i] = !previousState[i]  && currentState[i];
            keyHeld[i] = previousState[i] && currentState[i];
            previousState[i] = currentState[i];
        }
    }

    /**
     *  Checks to see if the given key was just pressed this update cycle, useful for actions that require a one-time
     *  press only since it returns false if key is held for two or more update cycles
     * @param keyCode the code of the required key (available as constant int the JOGL KeyEvent class)
     */
    public static boolean keyDown(int keyCode) { return keyDown[keyCode]; }

    /**
     *  Checks if the given key was held for at least two update cycles
     * @param keyCode the code of the required key (available as constant int the JOGL KeyEvent class)
     */
    public static boolean keyHeld(int keyCode) { return keyHeld[keyCode]; }

    /**
     * Checks if any key was pressed in the last update cycle
     * @return true if any key was pressed during the last update cycle
     */
    public static boolean isKeyAvailable()
    {
        return keyAvailable;
    }

    /**
     * Fetches the code of the last key pressed in the current update cycle
     * @return the short UNICODE value of the last key pressed
     */
    public static short getLastKeyPressed()
    {
        return lastKeyPressed;
    }

    /**
     * Resets all the flags used by the listener to false, effectively clearing all the previous input information
     */
    public static void clear()
    {
        for(int i =1 ;i < 256; i++)
        {
            keyDown[i] = false;
            keyHeld[i] = false;
            previousState[i] = false;
            currentState[i] = false;
        }
        keyAvailable = false;
    }
}
