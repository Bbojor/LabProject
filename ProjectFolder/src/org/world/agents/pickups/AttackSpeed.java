package org.world.agents.pickups;

import org.world.agents.Shadow;
import org.engine.GameLoop;
import org.engine.resources.SoundClip;
import org.world.tiles.Tile;

/**
 *  This class implements a pickup which increases the player's attack speed for a given amount of time
 *  On being picked up it will no longer be rendered but will stay in the game world for the rest of it's duration
 *  after which it will be marked for removal
 */
public class AttackSpeed extends Pickup
{

    /**
     *  Amount by which attack speed should be increased upon pickup
     */
    private float attackSpeedBonus;

    /**
     * Duration in seconds of the bonus
     */
    private int duration;

    /**
     * Sound played on picking
     */
    private SoundClip pickup = new SoundClip("/resources/sfx/pickups/AS_pick.wav");

    /**
     * Sound played after duration ends
     */
    private SoundClip expired = new SoundClip("/resources/sfx/pickups/AS_end.wav");

    /**
     * Used to mke the pickup bounce up and down
     */
    private int sign = 1;

    /**
     * Used to mke the pickup bounce up and down
     */
    private float bounce = 0f;


    /**
     * Creates a new pickup with the given properties
     * If the supplied coordinates are out of bounds it will be placed at 1,1
     * @param bonus a good value would be around 0.5f to 1f
     * @param duration duration in seconds
     * @param x isometric coordinate
     * @param y isometric coordinate
     */
    public AttackSpeed(float bonus, int duration, int x, int y)
    {
        attackSpeedBonus = bonus;
        this.duration = duration * 60; // convert duration to seconds

        //set coordinates and chck for validity
        if(x > 0 && x < GameLoop.getWorld().getTerrain().getWidth())
            isoX = x;
        else
            isoX = 1;
        if(y > 0 && y < GameLoop.getWorld().getTerrain().getWidth())
            isoY = y;
        else
            isoY = 1;

        shadow =  new Shadow(this);
        
        this.x = Tile.GROUND_TILE_WIDTH * (x + y);
        this.y = Tile.GROUND_TILE_WIDTH * (y - x);
        this.z = GameLoop.getWorld().getTerrain().getTiles()[x][y].z + 0.6f;

        spriteSheetPath = "/resources/attackspeed.png";

        loadAnimations();

        if(animations == null)
            createTexture();


    }

    @Override
    public void pickedUp()
    {
        if(!pickup.isRunning())
            pickup.play();

        if(render)
        {
            render = false;
            shadow.render = false;
            GameLoop.getWorld().getPlayer().getWeapon().modifyAttackSpeed(attackSpeedBonus);
        }
    }

    @Override
    public void update()
    {
        super.update();
        shadow.update();

        z -= bounce;

        if(bounce >= 0.1f || bounce <= -0.1f)
            sign = -sign;

        bounce += sign * 0.005f;

        z += bounce;

        if(!render)
        {
            if (duration > 0)
            {
                duration--;
            }
            else
            {
                GameLoop.getWorld().getPlayer().getWeapon().modifyAttackSpeed(-attackSpeedBonus);

                if(!expired.isRunning())
                    expired.play();
                this.remove = true;
            }
        }

    }

    @Override
    public void render()
    {
        shadow.render();
        super.render();
    }
}
