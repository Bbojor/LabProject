package org.world.agents.player.melee;

import org.engine.GameLoop;

/**
 *  The AttackNode class extends the functionality of the ComboNode class to allow for the implementation of different attacks for the player.
 *  Because of this it contains some additional information compared to the other node types
 */

public class AttackNode extends ComboNode
{
    /**
     *  Constant to flag the attack performed by this node as horizontal
     */
    public static final boolean HORIZONTAL = false;

    /**
     *  Constant to flag the attack performed by this node ans vertical
     */
    public static final boolean VERTICAL = true;

    /**
     *  Useful for computing which enemies should be hit by the attack
     */
    public boolean sweepType;

    /**
     * Amount of damage done to enemies by this attack
     */
    public int damage;

    /**
     *  Flags whether this attack can stab an enemy, allowing the player to move them around and throw them
     */
    public boolean canStabEnemy;

    /**
     * Flags whether this attack should produce screen shake at the end of the attack
     */
    private boolean shake;

    /**
     * The intensity of the screen shake, should use relatively low values, 3 or less
     */
    private int shakeIntensity;

    /**
     * The duration of the screen shake in 1/60ths of a second, recommended values are around 10
     */
    private int shakeDuration;

    /**
     * Creates a new attack node with the given parameters
     * @param graph reference to the ComboGraph object containing then node
     * @param WA index of the weapon animation associated with this node
     * @param PA index of the player animation associated with this node
     * @param dodge whether the player can dodge at the end fo this state
     * @param move whether the player can move during this state
     * @param canThrow whether the player can throw an enemy in this state
     * @param x horizontal offset of the weapon relative to the player
     * @param y vertical offset of the camera relative to the player
     * @param damage damage dealt by this attack
     * @param duration duration of the state
     * @param window time at the end of the state during which input is polled for deciding the next state
     * @param finishSoundPath string containing the path to the sound file, can be null
     * @param startSoundPath string containing the path to the sound file, can be null
     * @param shake whether this attack should cause screen shake at the end
     * @param intensity screen shake intensity (recommended to be lower than 3)
     * @param shakeDuration duration of the screen shake in 1/60ths of a second (recommended value 10)
     * @param sweepType whether the attack is vertical or horizontal (use constants provided by this class)
     * @param staminaCost amount of stamina that the player should use to perform this attack
     */
    public AttackNode(ComboGraph graph ,int WA, int PA, boolean dodge, boolean move, boolean canThrow , int x, int y, int damage, int duration, int window, String finishSoundPath, String startSoundPath, boolean shake, int intensity, int shakeDuration, boolean sweepType,int staminaCost)
    {
        super(graph,WA,PA,x,y,duration,window,staminaCost,canThrow,dodge,move,startSoundPath,finishSoundPath);
        this.damage = damage;
        this.shake = shake;
        shakeIntensity = intensity;
        this.shakeDuration = shakeDuration;
        this.sweepType = sweepType;
    }

    @Override
    protected void atStart()
    {
        if(startEffect != null )
            startEffect.play();

        GameLoop.getWorld().getPlayer().solid = true;
    }

    @Override
    protected void atWindow()
    {
        if(shake)
        {
            GameLoop.getWorld().CameraShake(shakeDuration,shakeIntensity);
        }
    }
}
