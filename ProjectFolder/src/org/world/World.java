package org.world;

import org.engine.graphics.Renderer;
import org.world.agents.enemies.Enemy;
import org.world.agents.enemies.SecurityBot;
import org.world.agents.player.Player;
import org.world.decoratioms.NonSolidDecoration;
import org.world.decoratioms.SolidDecoration;
import org.engine.ui.HUD;
import java.io.*;
import java.util.*;

/**
 *  The World class groups information about the various game objects currently present and active.
 *  It can be thought of as an individual level in the game
 *  This class handles the updating and rendering of all the game objects present in the game
 *  Currently implements a wave based/survival-type 16x16 tiles level in which the player's goal is to survive for as long as possible
 */

public class World implements Serializable
{

    private  Terrain terrain ;
    private  Random random = new Random();

    /**
     * The delay between two consecutive waves of enemies
     * Set to 10 seconds
     */
    public final int WAVE_DELAY = 600;

    /**
     * Wave counter
     */
    private int wave = 1;

    /**
     * Time counter
     */
    private int time;

    /**
     * Number of enemies currently in game
     */
    public int enemyCount = 0;

    /**
     * The score obtained by the player so far
     */
    public int score = 0;

    /**
     * Set when the game is paused. Objects are no longer updated
     */
    private boolean paused;

    private HUD hud;

 //   private  boolean terrain_modified = true;

    /**
     * Array holding all of the game objects
     */
    LinkedList<GameObject> gameObjects = new  LinkedList<>();

    /**
     * Buffer holding game objects that will be added in the next update cycle
     */
    private  LinkedList<GameObject> gameObjectsToBeAdded = new  LinkedList<>();

    /**
     * 2D Array holding decoration objects for easy access based on their positions
     */
    private  GameObject[][] decorations;

    private  Player player;

    // for camera shake
    private  int shakeTime = 0;
    private  int intensity = 0;

    /**
     * Holds enemies to be added after they are created but before they are introduced into the world
     */
    private Enemy[] newEnemies = new Enemy[2] ;

    /**
     * Keeps track of added enemies
     */
    private int addedEnemies = 0;

    /**
     * Flag set when the current wave is finished(all enemies are dead)
     */
    private boolean waveDone = true;

    /**
     * Applies camera shake to screen
     * @param duration in 1/60ths of a second
     * @param intensit values of 1 or 2 should be enough
     */
    public void CameraShake(int duration, int intensit)
    {
        shakeTime = duration;
        intensity = intensit;
    }

    /**
     * Initialize all the game world objects and other needed variables for a functioning world.
     * Currently creates a 16 x 16 map
     */
    public void initialiseWorld()
    {
        terrain = new Terrain(16, 16);
        terrain.init();

        //add random decorations
        decorations = new GameObject[terrain.getWidth()][terrain.getHeight()];
        for(int i = 1; i < terrain.getWidth(); i++)
            for(int j = 0; j < terrain.getHeight() - 1; j++)
            if(j != 0 && i != terrain.getWidth() -1){
                int num = random.nextInt(terrain.getWidth());

                if(num > 6 && num < 12)
                {
                    decorations[i][j] = new NonSolidDecoration(i ,j ,random.nextBoolean(),3,3,20,20,"/resources/Decorations/grass.png");
                    gameObjects.add(decorations[i][j]);
                }
                else
                if(num == 5)
                {
                    decorations[i][j] = new SolidDecoration(i,j,random.nextBoolean(),10,2,1,1,1,20,20,"/resources/Decorations/rock.png");
                    gameObjects.add(decorations[i][j]);
                }
                else
                    decorations[i][j] = null;
            }
    }

   /* public World readWorld(String path)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            World w = (World) in.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }*/

    /**
     * Handles every update related action during a normal game loop.
     * It iterates through the list of game objects, updating them , removes ones that are no longer needed and
     * inserts new game objects as needed.
     */
    public void update()
    {


        if(!paused)
        {
         /*   //spawn test enemy
            if(KeyInput.keyDown(KeyEvent.VK_1))
            {
                gameObjects.add(new SecurityBot(1,1));
                enemyCount++;
            }*/

         //create new enemies each update cycle to improve performance
            if(waveDone && addedEnemies < wave * 2)
            {
                int[] enemyXCoords = {0 ,1,0,2,13,13,12,12};
                int[] enemyYCoords = {1,1,2,2,12,14,14,12};
                int ran = random.nextInt(5);
                if (addedEnemies < (wave * 2) / 2)
                {
                    newEnemies[addedEnemies] = new SecurityBot(enemyXCoords[ran],  enemyYCoords[ran]);
                }
                else
                    newEnemies[addedEnemies] = new SecurityBot(enemyXCoords[3 + ran],  enemyYCoords[3 + ran] );
                addedEnemies++;
            }

            //spawn enemies at 1/3rd of a second to avoid lag due to batch spawning
            if(!waveDone)
            {
                if(time % 20 == 0)
                {
                    addedEnemies--;
                    addGameObject(newEnemies[addedEnemies]);
                }

                time++;
            }

            // mark the start of the wave
            if(time == WAVE_DELAY && addedEnemies > 0)
            {

                   waveDone = false;
                   time = 0;
            }

            if(waveDone && time < WAVE_DELAY && enemyCount == 0)
            {
                time++;
            }

            //mark end of a wave
            if(!waveDone && addedEnemies <= 0)
            {
                wave++;
                newEnemies = new Enemy[wave * 2];
                addedEnemies = 0;
                time = 0;
                waveDone = true;
            }


            //add new game objects from previous update cycle here, otherwise JVM throws a fit
            if(!gameObjectsToBeAdded.isEmpty())
            {
                gameObjects.addAll(gameObjectsToBeAdded);
            }

            //clear game object buffer
            gameObjectsToBeAdded.clear();

            // update stuff
            ListIterator<GameObject> it = gameObjects.listIterator();

            while (it.hasNext())
            {
                GameObject go = it.next();

                go.update();

                //remove garbage
                if (go.remove)
                    it.remove();
            }
        }

        if(hud != null)
            hud.update();

    }

    /**
     * Renders all the game objects and the terrain contained in the world object.
     * Before the actual rendering objects are sorted based on their distance to the screen, so that the furthest objects are
     * render first (behind objects in the foreground).
     */
    public void render()
    {
        if( shakeTime > 0)
        {
            shakeTime--;
            Renderer.cameraX += intensity;
            Renderer.cameraY += intensity;
            intensity = -intensity;
        }

        terrain.render();

        //sort and render according to depth
        Collections.sort(gameObjects);

        for (GameObject go : gameObjects)
        {
            try
            {
                go.render();
            }
            catch ( ArrayIndexOutOfBoundsException e)
            {
                System.out.println("Array out of bounds at " + go.getClass().getSimpleName() + " for animation " + go.currentAnimation +  " frame " + go.currentFrame);
                go.setFrame();
            }
        }

        //render HUD on top of everything
        if(hud != null)
            hud.render();

    }

    /**
     * Adds a game object to a buffer to be added next update cycle.
     * Checks the object's coordinates to make sure it has a valid position (isometric coordinates).
     * If the object does not pass the check it is discarded. Also increases the world's enemy count when the added object was an enemy.
     * @param go game object to be added
     */
    public void addGameObject(GameObject go)
    {
        if(!inBounds(go.isoX,go.isoY))
        {
            System.out.println("Given " + go.getClass().getSimpleName() + " object does not have a valid position " + go.isoX + " " + go.isoY + ". Was not added.");
            return;
        }
        gameObjectsToBeAdded.add(go);
        if(go instanceof Enemy)
            enemyCount++;
    }

    /**
     * Adds a Player object to the game and creates a reference to it for easy access.
     * Also creates the hud object and binds it to the player.
     * Can only add one player to the game since the engine doesn't support multiplayer.
     * If a player is already present then the method simply returns without doing anything.
     * @param p newly created player object
     */
    public void addPlayer(Player p)
    {
        if(player != null)
            return;
        player = p;
        hud = new HUD(p);
        gameObjectsToBeAdded.add(p);
    }

   /* public void saveWorld()
    {
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test.lvl"));
            World w = new World();
            out.writeObject(w);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }*/

    /**
     * Returns the terrain, used for pathfinding and movement
     */
    public Terrain getTerrain() { return terrain; }

    /**
     * Returns the player object
     */
    public Player getPlayer() { return player; }

    /**
     *  Returns the decoration array.
     *  "Decorations" are passive game objects that can serve as obstacles in movement
     */
    public GameObject[][] getDecorations()
    {
        return decorations;
    }

    /**
     *  Returns the HUD(heads-up display = in-game menu/user interface)
     */
    public HUD getHUD()
    {
        return hud;
    }

    /**
     * Pauses or un-pauses the game
     */
    public void togglePause() { paused = !paused ;}

    /**
     * Checks whether the game is paused
     */
    public boolean isPaused()
    {
        return paused;
    }

    /**
     * Gets the value of the current wave of enemies
     * @return wave # as an int
     */
    public int getWave()
    {
        return wave;
    }

    /**
     * Returns the current time elepsed since the last wave
     * @return time as an integer
     */
    public int getTime()
    {
        return time;
    }

    /**
     * Checks whether the given isometric coordinate are valid given the current size of the game world
     * @param isoX isometric coordinate
     * @param isoY isometric coordinate
     */
    public boolean inBounds(int isoX,int isoY)
    {
        if(isoX < 0 || isoY < 0 || isoX >= terrain.getWidth() || isoY >= terrain.getHeight())
            return false;
        return  true;
    }

}
