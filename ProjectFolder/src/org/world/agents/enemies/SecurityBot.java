package org.world.agents.enemies;

import org.world.agents.enemies.AI.AI;
import org.world.agents.player.melee.AttackNode;
import org.engine.GameLoop;
import org.engine.graphics.AnimationInformation;
import org.engine.resources.SoundClip;
import org.world.agents.Shadow;
import org.world.tiles.Tile;

/**
 *  Implements a basic test enemy that tries to reach the player and attack them
 */
public class SecurityBot extends Enemy
{

    private SoundClip hurtSound = new SoundClip("/resources/Enemies/clang.wav");
    private SoundClip deathSound = new SoundClip("/resources/Enemies/bot_dead.wav");
    private SoundClip stepSound = new SoundClip("/resources/Enemies/step_clang.wav");
    private SoundClip attackSound = new SoundClip("/resources/Enemies/swing.wav");

    /**
     * Decides the time the enemy has to wait after an attack.
     */
    private final float ATTACK_SPEED = 3f;

    /**
     * The duration of a basic attack
     */
    private final int MELEE_ATTACK_TIME = 45;

    /**
     * The damage an attack deals to the player
     */
    private final int MELEE_ATTACK_DAMAGE = 10;

    private final int DAMAGE_STATE_INVULNERABILITY = 50;

    private enum ANIMATIONS
    {
        IDLE(5,5,true),
        DAMAGED(1,1,false),
        DED(12,8,false),
        STAB(2,2,true),
        WALK(19,18,true),
        MELEE(8,12,true);

        int frames;
        int fps;
        boolean loop;

        ANIMATIONS(int frames, int fps, boolean loop)
        {
            this.frames = frames;
            this.fps = fps;
            this.loop = loop;
        }
    }

    /**
        Creates a new Security Bot enemy at given x,y (isometric) coordinates.
        If the coordinates are invalid they are set to 1,1.
     */
    public SecurityBot(int x, int y)
    {
        xWidth = 10;
        yWidth = 5;
        solid = true;
        stabbable = false;
        speed = 7f;
        shadow = new Shadow(this);
        scoreValue = 100;

        health = 150;
        hurtSound.setVolume(-10f);
        attackSound.setVolume(10f);
        damageStateDuration = DAMAGE_STATE_INVULNERABILITY;
        wait = 60;

        stabAnimation = 3;

        //attempt to load animations
        loadAnimations();

        //if no animations are already present create them
        if (animations == null)
        {
            AnimationInformation[] animationInfo = new AnimationInformation[ANIMATIONS.values().length];
            spriteSheetPath = "/resources/Enemies/security_bot.png";

            for (ANIMATIONS a : ANIMATIONS.values())
            {
                animationInfo[a.ordinal()] = new AnimationInformation(a.frames, a.fps, 80, 60, a.loop);
            }
            createAnimations(animationInfo);
        }

        if(GameLoop.getWorld().inBounds(x,y))
        {
            this.isoX = x;
            this.isoY = y;
        }
        else
        {
            isoX = 1;
            isoY = 1;
        }

        this.z = GameLoop.getWorld().getTerrain().getTiles()[x][y].z;

        this.x = (isoX + isoY) * Tile.GROUND_TILE_WIDTH / 2f;
        this.y = (isoY - isoX) * Tile.GROUND_TILE_HEIGHT / 2f;

        shadow.update();
    }

    /**
     *  Checks the distance to the player. If too far away to attack,  attempt to find a path via the {@link org.world.agents.enemies.AI.AI#aStar(int, int, int, int)}
     *  algorithm. If close enough, initiate attack.
     */
    @Override
    void ai()
    {
       if (distanceTo(GameLoop.getWorld().getPlayer()) > 25 && currentDestination == null) //find path towards player
        {
            if (path == null ||  ( finalDestination[0] != GameLoop.getWorld().getPlayer().isoX ||  finalDestination[1] != GameLoop.getWorld().getPlayer().isoY ))
            {
                //if no path or player changed location
                finalDestination[0] = GameLoop.getWorld().getPlayer().isoX ;
                finalDestination[1] = GameLoop.getWorld().getPlayer().isoY ;
                if(!AI.isTraversable(finalDestination[0],finalDestination[1]))
                    return;

                path = AI.aStar(this.isoX, this.isoY, finalDestination[0],  finalDestination[1]);
            }
            if (path != null && !path.isEmpty())
            {
                currentDestination = path.remove(path.size() - 1);
            }
        }

        else //has a path or is close enough to the player
        {
            if (currentDestination != null) //move towards player
            {
                double xDist = Math.abs(x - (currentDestination.x + currentDestination.y) * Tile.GROUND_TILE_WIDTH/2f);
                double yDist = Math.abs(y - (currentDestination.y - currentDestination.x) * Tile.GROUND_TILE_HEIGHT/2f);
                if (xDist <= 2 && yDist <= 2)
                {
                    currentDestination = null;
                }
                else
                    {
                      moveToPoint(currentDestination.x , currentDestination.y);
                      currentAnimation = ANIMATIONS.WALK.ordinal();
                      if((currentFrame == 5 || currentFrame == 13) && !stepSound.isRunning())
                          stepSound.play();
                    }
            }
            else
            {
                currentAnimation = ANIMATIONS.MELEE.ordinal();
            }
            if (path != null && path.isEmpty())
                path = null;
        }

        //if close enough to the player set the path to null and attack
        if(distanceTo(GameLoop.getWorld().getPlayer()) < 25 )
            {
                flip = this.x > GameLoop.getWorld().getPlayer().x;
                currentDestination = null;
                path = null;
                attacking = true;
                currentAttackTime = 0;
            }
    }

    @Override
    void deathBehaviour()
    {
        currentAnimation = ANIMATIONS.DED.ordinal();
        hurtSound.stop();
        deathSound.play();
        dead = true;
        solid = false;
    }

    @Override
    void damageBehaviour()
    {
        if (damageStateDuration == DAMAGE_STATE_INVULNERABILITY) //on first update after being damaged
        {
            AttackNode node = (AttackNode)(GameLoop.getWorld().getPlayer().getWeapon().getComboGraph().getCurrentNode());
            hurtSound.play();
            damage(node.damage);
            if(health < 50)
                stabbable = true;
            if(canBeStaggered)
            {
                if(this.x - GameLoop.getWorld().getPlayer().x > 0 )
                    xInertia = 10;
                else xInertia = -10;
            }
        }

        if(canBeStaggered)
            currentAnimation = ANIMATIONS.DAMAGED.ordinal();

        if (damageStateDuration > 0)
            damageStateDuration--;
        else
        {
            currentAnimation = ANIMATIONS.IDLE.ordinal();
            damageStateDuration = DAMAGE_STATE_INVULNERABILITY;
            damaged = false;
        }
    }

    @Override
    public void render()
    {
        super.render();
     /*   if (path != null && !path.isEmpty())
        {
            for (Point p : path)
            {
                Graphics.setColor(255,0,0,255);
                Graphics.fillRect((p.x + p.y)* Tile.GROUND_TILE_WIDTH/2f,(p.y - p.x)* Tile.GROUND_TILE_HEIGHT/2f,1,1);
                Graphics.setColor(255,255,255,255);
            }
        }*/

    }

    @Override
    void attack()
    {

        //beginning of attack
        if(currentAttackTime == 0)
        {
            currentAnimation = ANIMATIONS.MELEE.ordinal();
            canBeStaggered = false;
        }
        else
        if(currentFrame == 6)
        {
            if (!attackSound.isRunning())
                attackSound.play();
            if(Math.abs(z- GameLoop.getWorld().getPlayer().z) <= 1)
            {
                if (!flip && GameLoop.getWorld().getPlayer().x > x && GameLoop.getWorld().getPlayer().x - x < 30 && Math.abs(GameLoop.getWorld().getPlayer().y - y) <= 30)
                    GameLoop.getWorld().getPlayer().getDamaged(MELEE_ATTACK_DAMAGE);
                else if (flip && GameLoop.getWorld().getPlayer().x < x && x - GameLoop.getWorld().getPlayer().x < 30 && Math.abs(GameLoop.getWorld().getPlayer().y - y) <= 30)
                    GameLoop.getWorld().getPlayer().getDamaged(MELEE_ATTACK_DAMAGE);
            }
        }
        else
        if(currentAttackTime == MELEE_ATTACK_TIME )
        {
            wait = (int) (60/ATTACK_SPEED);
            currentAnimation = ANIMATIONS.IDLE.ordinal();
            attacking = false;
            canBeStaggered = true;
        }
        currentAttackTime++;
    }

    @Override
    void freeResources()
    {

        hurtSound = null;
        stepSound = null;
        deathSound = null;
        attackSound = null;

        path = null;
        currentDestination = null;
    }

}
