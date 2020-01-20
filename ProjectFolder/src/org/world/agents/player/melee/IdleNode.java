package org.world.agents.player.melee;

import org.engine.GameLoop;

/**
 * This class implements an "idle" state in the ComboGraph, i.e. a state which is neither an attack or a dodge move
 * They are used for example while the player is walking/standing around (but do NOT themselves implement such actions),
 * these states leave most of the updates to the player/weapon objects and are more of a placeholder state while the player is not
 * engaged in combat
 */
public class IdleNode extends ComboNode
{

    /**
     * Creates a new combo node with the given parameters
     * @param graph reference to the ComboGraph object containing then node
     * @param weaponAnimation index of the weapon animation associated with this node
     * @param playerAnimation index of the player animation associated with this node
     * @param canDodge whether the player can dodge at the end fo this state
     * @param canMove whether the player can move during this state
     * @param canThrow whether the player can throw an enemy in this state
     * @param xOffset horizontal offset of the weapon relative to the player
     * @param yOffset vertical offset of the camera relative to the player
     * @param duration duration of the state
     * @param comboWindow time at the end of the state during which input is polled for deciding the next state
     * @param finishSoundPath string containing the path to the sound file, can be null
     * @param startSoundPath string containing the path to the sound file, can be null
     */
    public IdleNode(ComboGraph graph, int weaponAnimation, int playerAnimation, boolean canDodge, boolean canMove, boolean canThrow , int xOffset, int yOffset, int duration, int comboWindow, String finishSoundPath, String startSoundPath)
    {
        super(graph,weaponAnimation,playerAnimation,xOffset,yOffset,duration,comboWindow,0,canThrow,canDodge,canMove,startSoundPath,finishSoundPath);
    }

    @Override
    protected void atStart()
    {
        next = TimeUp;

        if(startEffect != null )
            startEffect.play();

        GameLoop.getWorld().getPlayer().solid = true;
    }

}
