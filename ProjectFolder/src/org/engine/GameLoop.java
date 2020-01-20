package org.engine;

import com.jogamp.newt.event.KeyEvent;
import org.engine.graphics.Renderer;
import org.engine.input.KeyInput;
import org.engine.input.MouseInput;
import org.engine.ui.MainMenu;
import org.world.World;
import org.world.agents.pickups.AttackSpeed;
import org.world.agents.player.Player;


/**
 *  The GameLoop class handles all actions performed by the game engine while the game is running.
 *  It contains a thread which runs continuously and
 *  performs the required updates and rendering on all the other components of the game. The thread seeks to achieve a
 *  number of 60 updates/frames drawn every second.
 */
public class GameLoop
{
    /**
     * Flags the thread as running. Setting it to false will stop the thread and subsequently close the game.
     */
    private static boolean running = false;

    /**
     * Counts the number of updates performed in case some delay has occurred and
     * catching up is required.
     */
    private static int updates = 0;

    /**
     *  Maximum number of updates that can be performed when "catching up"
     */
    private static final int  MAX_UPDATES = 5;

    /**
     * Keeps track of the last time an update was performed
     */
    private static long lastUpdateTime = 0;

    /**
     *  Currently active game world, acts as the current level
     */
    private static World world;

    /**
     * How many frames should be drawn each second
     */
    private static int targetFPS = 60;

    /**
     * Nanoseconds it should take to update and render a frame in case
     * everything goes well
     */
    //1000000000ns = 1s
    private static int  targetTime = 1000000000 / targetFPS;

    /**
     * Different states the program can be in.
     */
    public enum STATES
    {
        /**
         * Main menu
         */
        MENU,
        /**
         * During gameplay
         */
        GAME,
        /**
         * Temporary state while the game closes
         */
        EXIT
    }

    /** Current state
     *
     */
    private static STATES state = STATES.MENU;

    /**
     * Initializes and starts the thread
     */
    static void start()
    {
        Thread thread = new Thread(() -> {
            running = true;
            lastUpdateTime = System.nanoTime();

        //    int frames = 0; //counts frames per second, not needed for the actual running of the game, but might be useful
                            // to measure performance

            long lastFpsCheck = System.nanoTime();

            while (running)
            {
                    if(state == STATES.EXIT)
                    {
                        running = false;
                        return;
                    }
                    long currentTime = System.nanoTime();
                    updates = 0;

                    //catch up in case some delay occurred; perform at most 5 more or less simultaneous updates
                    while (currentTime - lastUpdateTime >= targetTime && updates < MAX_UPDATES)
                    {
                        //update all active components of the game
                        KeyInput.update();
                        MouseInput.update();

                        if(KeyInput.keyDown(KeyEvent.VK_ESCAPE) && state == STATES.GAME)
                            world.togglePause();

                        if(state == STATES.GAME)
                             world.update();
                        else
                            MainMenu.update();
                        lastUpdateTime += targetTime;
                        updates++;
                    }

                    //render game
                    Renderer.render();


                    //count and display fps (not needed for the running of the game, maybe useful for debugging)
               //    frames++;
                    if (System.nanoTime() >= lastFpsCheck + 1000000000)
                    {
                        //  System.out.println(frames);
                  //      frames = 0;
                        lastFpsCheck = System.nanoTime();
                    }

                    //see how long the update/render cycle took this iteration
                    long timeTaken = System.nanoTime() - currentTime;

                    //if it took less than a second, sleep in order to save CPU
                    if (timeTaken < targetTime)
                    {
                        try
                        {
                            Thread.sleep((targetTime - timeTaken) / 1000000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

            }
        });

        thread.setName("GameLoop");
        thread.start();
    }

    /**
        Returns the update delta of the game loop  (i.e. the time an update/render cycle should normally take, in seconds)
        Can be useful for some calculations regarding movement/animation speed
     */
    public static float updateDelta()
    {
        return (1.0f/1000000000) * targetTime;
    }

   // public static  void setWorld(World w) { world = w; }

    /**
     * Getter for the currently active world, which needs to be accessed in several places across the game engine
     * @return currently active world object
     */
    public static World getWorld() { return world; }

    /**
     * Returns the current state of the game (whether it's in the menu or in game, etc.)
     * @return variable of type STATES denoting the current state
     */
    public static STATES getState() { return state; }

    /**
     * Instantiates a new {@link org.world.World} object and sets it as the currently active world
     */
    public static void newGame()
    {
        world = new World();
        world.initialiseWorld();
        world.addPlayer(new Player());
        world.addGameObject(new AttackSpeed(0.5f,10,2,2));
    }

    /**
     * Resets the camera and clears the game world. Automatically called when exiting to menu
     */
    private static void reset()
    {
        Renderer.cameraX = 0;
        Renderer.cameraY = 0;
        world = null;
    }

    /**
     *  Changes the current state to a given one.
     *  Also calls the {@link #reset()} method.
     * @param state variable of the STATES enum defined in this class
     */
    public static void setState(STATES state)
    {
        if(GameLoop.state == STATES.GAME && state == STATES.MENU)
            reset();
        GameLoop.state = state;
    }
}
