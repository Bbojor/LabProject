package org.world.agents.enemies;

import org.engine.graphics.Renderer;
import org.world.agents.enemies.AI.AI;
import org.world.agents.enemies.AI.Point;
import org.world.agents.Shadow;
import org.world.agents.player.melee.AttackNode;
import org.world.agents.player.melee.HitBox;
import org.engine.GameLoop;
import org.world.GameObject;
import org.world.tiles.Tile;

import java.util.ArrayList;

/**
 *  Basic enemy template, adds some useful variables and methods to the GameObject superclass.
 *  Actual behaviour( general ai, death and damage behaviour) and characteristics(sprite, sounds) are defined in subclasses.
 *  The {@link #render()} and {@link #update()} functions should not be overridden since they provide all the needed functionality.
 */

public abstract class Enemy extends GameObject
{
    /**
     * Is set when enemy is in a damaged state
     */
    boolean damaged = false;

    /**
     * The duration of the damaged state. While being in this state enemies can not be damaged again
     */
    int damageStateDuration;

    /**
     * Is set when the enemy is attacking
     */
    boolean attacking;

    /**
     * Counts the time spend imn the attacking state
     */
    int currentAttackTime;

    /**
     * Flags whether enemie can be staggered, i.e. pushed back or interrupted  by the player's attacks
     */
    boolean canBeStaggered = true;

    /**
     * How many points the player is awarded for killing this particular enemy
     */
    int scoreValue;

    /**
     * If this particular enemy can be stabbed/picked up by the player
     */
    boolean stabbable;

    /**
     * Flags this enemy as dead.
     * Dead enemies do nothing in their update cycle
     */
    boolean dead;

    /**
     * If this enemy has been thrown by the player
     */
    public boolean thrown;

    /**
     * Flags this enemy as invulnerable (cannot be damaged by the player)
     */
    private boolean invulnerable = false;

    /**
     * The index of the animation played while being stabbed/carried by the player.
     * It is necessary to have this as a field since animations vary in number and name by game object
     */
    protected int stabAnimation;

    /**
     * Waiting time until next action.
     * When set the enemy does nothing in it's update cycle
     */
    int wait = 0;

    /**
     * Horizontal inertia value.
     * If different from 0 the enemy will be pushed either left or right depending on sign
     */
    public int xInertia;

    /**
     * Vertical inertia value.
     * If different from 0 the enemy will be pushed either left or right depending on sign
     */
    public int yInertia;

    /**
     * Amount of health the enemy possesses.
     * When health reaches 0 the enemy is marked as dead
     */
    int health;

    /**
     * Inertia value, decides the impact of the x/yInertia values
     */
    float inertia = 5f;

    /**
     * Movement speed
     */
    float speed = 5f;

    /**
     * Is set when the enemy is moving
     */
    boolean moving;

    /**
     * Currently used path
     */
    ArrayList<Point> path = null;

    /**
     * Current destination point, not the final one
     */
    Point currentDestination = null; //current destination (a point on the path)

    /**
     * Coordinates of the currently set final destination
     */
    int[] finalDestination = new int[2];


    Shadow shadow;

    public Enemy()
    {
        //implemented in subclasses
    }

    /**
     *  Handles being thrown. Code shared between all enemies that can be thrown.
     *  Enemy moves in the direction given by the inertia variables until reaching an obstacle,a tile that is higher than it,
     *  or the inertia values reach 0.
     *  Upon stopping the enemy dies immediately. In case the obstacle hit is another enemy, both enemies will be killed.
     */
    private void thrownBehaviour()
    {
        if(thrown && xInertia != 0)
        {
            float newX = x + Integer.signum(xInertia) * inertia;

            int[] isoCoords;
            isoCoords = Renderer.IsoCoordinates(newX,y);

            //check for out of bounds
            if(isoCoords[0] < 0 || isoCoords[0] >= GameLoop.getWorld().getTerrain().getWidth() || isoCoords[1] < 0 || isoCoords[1] >= GameLoop.getWorld().getTerrain().getHeight() || GameLoop.getWorld().getTerrain().getTiles()[isoCoords[0]][isoCoords[1]].z > this.z)
                xInertia = 1;
            else
                x = newX;

            xInertia -= Integer.signum(xInertia);

            if(checkCollision(GameLoop.getWorld().getPlayer()))
            {
                xInertia = 0;
                if(previousCollision instanceof Enemy)
                {
                    ((Enemy) previousCollision).damage(((Enemy) previousCollision).health);
                }
            }
        }

        if(thrown && xInertia == 0)
        {
            thrown = false;
            health = 0;
        }
    }

    /**
     *  Implemented in subclasses.
     *  Defines what happens when this enemy's health falls below 0.
     */
    abstract void deathBehaviour();

    /**
     *  Defines what happens when this enemy has nonzero inertia values but is not thrown.
     *  Pushes enemy in the direction given by the inertia values.
     */
    private void inertiaBehaviour()
    {
        //handle inertia on the x axis
        float newX = x + Integer.signum(xInertia) * inertia /4f;

        int[] isoCoords;
        isoCoords = Renderer.IsoCoordinates(newX,y);

        //check for out of bounds
        if(!GameLoop.getWorld().inBounds(isoCoords[0],isoCoords[1]) || !canMove(isoCoords[0],isoCoords[1]))
            xInertia = 1; //set to one so the decrement makes it 0 = no inertia (negative inertia is valid, but in the opposite direction)
        else
            x = newX;

        xInertia -= Integer.signum(xInertia);

        //handle inertia on the y axis
        float newY = y + Integer.signum(yInertia) *  inertia /4f;

        isoCoords = Renderer.IsoCoordinates(x,newY);

        //check for out of bounds
        if(!GameLoop.getWorld().inBounds(isoCoords[0],isoCoords[1]) || !canMove(isoCoords[0],isoCoords[1]))
            yInertia = 1; //set to one so the decrement makes it 0 = no inertia (negative inertia is valid, but in the opposite direction)
        else
            y = newY;

        yInertia -= Integer.signum(yInertia);
    }

    /**
     *  Implemented in subclasses.
     *  Defines what enemy should do in case it is damaged.
     */
    abstract void damageBehaviour();

    /**
     *  Implemented in subclass.
     *  Defines general behaviour when left on it's own/not interrupted by attacks or otherwise.
     *  Should set the attacking flag to true in case an attack is desired. Actions pertaining to attacks should
     *  be handled in the {@link #attack()} method.
     */
    abstract void ai();

    /**
     * Checks whether moving to a given tile is possible. Accounts for the current height difference.
     * Validity of the coordinates is checked by a call to the {@link org.world.agents.enemies.AI.AI#isTraversable(int x, int y)} method
     * @param x isometric coordinate of the tile
     * @param y isometric coordinate of the tile
     */
    private boolean canMove(int x,int y)
    {
        return AI.isTraversable(x,y) &&  (z - GameLoop.getWorld().getTerrain().getTiles()[x][y].z  >= 0);
    }

    /**
     *  Does all the relevant updates.
     *  It is modular, subclasses should override the various behaviour methods instead of this to implement the actual functionality.
     *  In case the enemy is dead, the method returns instantly.
     *  The override-able sub-method call order is: {@link #deathBehaviour()} and {@link #freeResources()}, if health less than 0; {@link #damageBehaviour()} if damaged;
     *  {@link #ai()} if not moving and not attacking or {@link #attack()} otherwise.
     *  Only one of the groups of methods above can be executed in on update cycle, the method returns after entering one execution branch.
     */
    public void update()
    {
        if(dead)
            return; // don't do anything after death

        updateShadow();

        thrownBehaviour();

        if(thrown)
        {
            setFrame();
            moving = false;
            return; //don't do anything else while being thrown
        }
        else

            if((xInertia != 0 || yInertia != 0) && !moving)
            inertiaBehaviour();


        if(health <= 0) //die
        {
            GameLoop.getWorld().score += scoreValue;
            GameLoop.getWorld().enemyCount--;
            deathBehaviour();
            setFrame();
            moving = false;
            freeResources();
            return;
        }

        //check for damage if not in damage state
        if(!damaged)
            checkForDamage();

        if(damaged)
        {
            damageBehaviour();
            if (canBeStaggered) //end update
            {
                moving = false;
                setFrame();
                return;
            }
        }


        moving = false;
        if (wait > 0)
        {
            wait--;
        }

        else
            {
                if (!attacking)
                    ai();
                else
                    attack();
            }


        setFrame();
    }

    /**
     * Defined in subclass.
     * This method should handle all possible attack actions the enemy is capable of.
     * After an attack is finished the attacking flag should be set to false to allow the enemy to execute other actions
     */
    abstract void attack();


    /**
     *  Deal damage to this enemy. Does nothing if enemy is marked as being invulnerable at time of calling
     * @param damage amount of damage
     */
    protected void damage(int damage)
    {
        if(!invulnerable)
            health -= damage;
    }

    /**
     *  This class is actually used to check for damage, it in turn calls the checkForHit method and handles some other actions
     *  like the player's lifesteal and if enemy has been stabbed/skewered
     */
    protected void checkForDamage()
    {
        if(distanceTo(GameLoop.getWorld().getPlayer()) > 100 || GameLoop.getWorld().getPlayer().z > z + 1)
            return;

        if(GameLoop.getWorld().getPlayer().getWeapon().isDoingDamage())
            damaged = checkForHit(GameLoop.getWorld().getPlayer().getWeapon().getHitBox());

        if(damaged)
        {
            GameLoop.getWorld().getPlayer().getWeapon().lifesteal();

            //get stabbed
            if(stabbable && GameLoop.getWorld().getPlayer().getWeapon().getStabbedEnemy() == null &&
                    GameLoop.getWorld().getPlayer().getWeapon().getComboGraph().getCurrentNode() instanceof  AttackNode &&
                    ((AttackNode)GameLoop.getWorld().getPlayer().getWeapon().getComboGraph().getCurrentNode()).canStabEnemy)
            {
                GameLoop.getWorld().getPlayer().getWeapon().setStabbedEnemy(this);
                remove = true;
                GameLoop.getWorld().enemyCount--;
            }
        }

    }

    /**
     * Checks if this enemy has been hit by the player by checking all the damage points in the current hit box
     * given by the attack type and animation frame
     * @param damageBox Player's weapon's hitbox
     * @return true if hit, false if not
     */
    private boolean checkForHit(HitBox damageBox)
    {
        AttackNode node = (AttackNode)(GameLoop.getWorld().getPlayer().getWeapon().getComboGraph().getCurrentNode());
        if(node.sweepType)
        //if vertical attack
        {
            if(Math.abs(this.isoX - GameLoop.getWorld().getPlayer().isoX) > 2 || Math.abs(this.isoY - GameLoop.getWorld().getPlayer().isoY) > 2)
                return false;
        }

        //check all the point in the hitbox
        for(int i = 0; i < 9; i++)
        {
            if(Renderer.isInRectangle(damageBox.getCurrentXes()[i],damageBox.getCurrentYs()[i],this.x,this.y - this.spriteHeight /2f,this.spriteWidth,this.spriteHeight))
                return true;
        }

        return false;
    }

    /**
     *  Attempts to move an enemy towards the position given by x,y.
     *  Ensures the enemy does not end up in an illegal position in the current game world even if the given coordinates are unreachable.
     *  The coordinates refer to points on th ground. The height of the destination is not needed since it can be deduced from the {@link  org.world.tiles.Tile} information
     *  TODO Better collision detection, especially with other enemies
     * @param x coordinate in the game world
     * @param y coordinate in the game world
     */
    public void moveToCoords(float x, float y)
    {
        moving = true;
        flip = this.x > x; //set flip according to movement direction
        int[] isoCoords;

        if (Math.abs(x - this.x) > 1) //if distance to
        {
            float oldX = this.x;
            this.x = this.x - ((this.x - x) / Math.abs(this.x - x) )  * speed / 4f; //move one unit
            isoCoords = Renderer.IsoCoordinates(this.x , this.y);

            if(canMove(isoCoords[0],isoCoords[1]))
            { //move
                isoX = isoCoords[0];
                isoY = isoCoords[1];
            }
            else
            {
                //tile above not traversable
                if(isoY < GameLoop.getWorld().getTerrain().getHeight() && GameLoop.getWorld().getTerrain().getTiles()[isoX][isoY + 1] == null || !GameLoop.getWorld().getTerrain().getTiles()[isoX][isoY + 1].traversable)
                {
                    this.y += 1;
                }
                //tile below not traversable
                else if( isoY > 0 && (GameLoop.getWorld().getTerrain().getTiles()[isoX][isoY - 1] == null || !GameLoop.getWorld().getTerrain().getTiles()[isoX][isoY - 1].traversable))

                {
                    this.y -= 1;
                }

                   else
                {

                    checkCollision(null);
                    if(previousCollision != null)
                    {
                        if(previousCollision.y < this.y)
                            this.y += previousCollision.yWidth;
                        else
                            this.y -= previousCollision.yWidth;
                    }
                }

                //try moving again with the new coordinates
                isoCoords = Renderer.IsoCoordinates(this.x, this.y);

                //check for out of bounds
                if(canMove(isoCoords[0],isoCoords[1]))
                {
                    isoX = isoCoords[0];
                    isoY = isoCoords[1];
                }
                else this.x = oldX; //keep old coordinate
            }
        }

        if (Math.abs(y - this.y) > 1)
        {
            float oldY = this.y;
            this.y = this.y - ((this.y - y) / Math.abs(this.y - y) )  * speed / 4f; //move one unit
            isoCoords = Renderer.IsoCoordinates(this.x, this.y);


            if(canMove(isoCoords[0], isoCoords[1]))
            {
                isoX = isoCoords[0];
                isoY = isoCoords[1];
            }
            else
            {
                //tile to the left non-traversable
                if((isoX > 0 && isoY > 0) && (GameLoop.getWorld().getTerrain().getTiles()[isoX - 1][isoY - 1] == null || !GameLoop.getWorld().getTerrain().getTiles()[isoX - 1][isoY - 1].traversable ))
                {
                    this.x+=1;
                }
                else //tile to the right non-traversable
                    if((isoX < GameLoop.getWorld().getTerrain().getWidth() - 1 && isoY < GameLoop.getWorld().getTerrain().getHeight() - 1) && (GameLoop.getWorld().getTerrain().getTiles()[isoX + 1][isoY + 1] == null || !GameLoop.getWorld().getTerrain().getTiles()[isoX + 1][isoY + 1].traversable ))

                {
                    this.x-=1;
                }
                    else
                    {
                        checkCollision(null);
                        if(previousCollision != null)
                        {
                            if(previousCollision.x < this.x)
                                this.x += previousCollision.xWidth;
                            else
                                this.x -= previousCollision.xWidth;
                        }
                    }

                //try moving again with the new coordinates
                isoCoords = Renderer.IsoCoordinates(this.x, this.y);

                //check for out of bounds
                if(canMove(isoCoords[0], isoCoords[1]))
                {
                    isoX = isoCoords[0];
                    isoY = isoCoords[1];
                }
                else this.y = oldY; //keep old coordinate
            }
        }
    }

    /**
     *  Moves to a set of (int) isometric coordinates by calculating the real world (float) coordinates and calling the {@link #moveToCoords(float, float)} method
     * @param x isometric coordinate
     * @param y isometric coordinate
     */
    public void  moveToPoint(int x, int y)
    {
        float actualX = (x + y) * Tile.GROUND_TILE_WIDTH/2f;
        float actualY = (y - x) * Tile.GROUND_TILE_HEIGHT/2f;
        moveToCoords(actualX,actualY);
    }

    public void render()
    {
        renderShadow();
        super.render();
    }

    /**
     *  updates this enemy's shadow if it exists
     */
    public void updateShadow()
    {
        if(shadow != null)
            shadow.update();
    }

    /**
     *  renders this enemy's shadow if it exists
     */
    private void renderShadow()
    {
        if(shadow != null)
            shadow.render();
    }

    //frees unnecessary resources after dying
    abstract void freeResources();

    /**
     * Returns the enemy's stab animation index
     * @return index in the animation array
     */
    public int getStabAnimation()
    {
        return stabAnimation;
    }

    /**
     * Marks this enemy as invulnerable. Invulnerable enemies cannot be damaged
     */
    public void setInvulnerable(boolean invulnerable)
    {
        this.invulnerable = invulnerable;
    }
}
