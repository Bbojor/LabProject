package org.world.agents.player.melee;

import org.engine.GameLoop;
import org.engine.input.KeyInput;

/**
 * The DodgeNode class extends the ComboNode class to allow the player to dodge
 * While dodging the player is invulnerable to damage and can move through enemies and obstacles
 */

public class DodgeNode extends ComboNode
{

    /**
     * Initializes a new combo node class
     * @param combo refernce to the graph containing this node
     * @param WA weapon animation
     * @param PA player animation
     * @param dodge whether the player can dodge at the end of this state
     * @param move whether the player can move during this state
     * @param canThrow whether the player can throw an enemy
     * @param x horizontal offset of the weapon
     * @param y vertical offset of the weapon
     * @param duration duration of this state
     * @param window time at the end when the input is polled to decide the next state
     * @param finishSoundPath sound played at the end of the state
     * @param startSoundPath sound played when the state begins
     * @param staminaCost cost to perform this action
     */
    public DodgeNode(ComboGraph combo,int WA, int PA, boolean dodge, boolean move, boolean canThrow , int x, int y, int duration, int window, String finishSoundPath, String startSoundPath,int staminaCost)
    {
        super(combo,WA,PA,x,y,duration,window,staminaCost,canThrow,dodge,move,startSoundPath,finishSoundPath);
    }

    @Override
    protected void atStart()
    {
        next = TimeUp;

        if(startEffect != null )
            startEffect.play();

        GameLoop.getWorld().getPlayer().solid = false;

        int dodgeDistance = 20; //how far to leap

        if(KeyInput.keyDown(KeyInput.LEFT) || KeyInput.keyHeld(KeyInput.LEFT))
            graph.weapon.getOwner().setXInertia(-dodgeDistance);

        if(KeyInput.keyDown(KeyInput.RIGHT) || KeyInput.keyHeld(KeyInput.RIGHT))
            graph.weapon.getOwner().setXInertia(dodgeDistance);

        if(KeyInput.keyDown(KeyInput.DOWN) || KeyInput.keyHeld(KeyInput.DOWN))
            graph.weapon.getOwner().setYInertia(dodgeDistance);

        if(KeyInput.keyDown(KeyInput.UP) || KeyInput.keyHeld(KeyInput.UP))
            graph.weapon.getOwner().setYInertia(-dodgeDistance);

        //if no movement key was pressed doge forward
        if(GameLoop.getWorld().getPlayer().getXInertia() == 0 && GameLoop.getWorld().getPlayer().getYInertia()  == 0)
            if(GameLoop.getWorld().getPlayer().isFlipped())
                GameLoop.getWorld().getPlayer().setXInertia(-dodgeDistance);
            else
                GameLoop.getWorld().getPlayer().setXInertia(dodgeDistance);

        //reduce dodge distance when going diagonally, otherwise dodging diagonally would be crazy long
        if(GameLoop.getWorld().getPlayer().getXInertia() != 0 && GameLoop.getWorld().getPlayer().getYInertia() != 0)
        {
            GameLoop.getWorld().getPlayer().setXInertia((int) (GameLoop.getWorld().getPlayer().getXInertia()/1.5));
            GameLoop.getWorld().getPlayer().setYInertia((int) (GameLoop.getWorld().getPlayer().getYInertia()/1.5));
        }

    }


}


