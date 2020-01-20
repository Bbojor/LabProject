package org.world.agents.player.melee;

import com.jogamp.newt.event.MouseEvent;
import org.engine.input.KeyInput;
import org.engine.input.MouseInput;
import org.engine.resources.SoundClip;

/**
 *  This class implements a basic node class for the ComboGraph object. These notes implement states in the finite state machine
 *  represented by the ComboGraph. Each node holds various information about the player, weapon and the action being performed
 */

public abstract class ComboNode
{
    /**
     * Animation index for the weapon object
     */
    public int weaponAnimation;
    /**
     * Animation index for the player character
     */
    public int playerAnimation;

    /**
     *  Weapon's offset relative to the player
     */
    public int xOffset;

    /**
     *  Weapon's offset relative to the player
     */
    public int yOffset;

    /**
     *  Holds the regular duration of this state.
     *  Used to compute state duration based on the attack speed
     */
    public int baseDuration;

    /**
     *  Actual duration
     *  Computed state duration based on the attack speed
     */
    public int duration;

    /**
     * Time (in 1/60ths of a second) at the end of the state in which input is polled and the next state is decided
     * Set to 0 if a forced transition (independent of player input) is desired
     */
    public int comboWindow;

    /**
     * Stamina used to perform current state's action
     */
    int staminaCost;

    /**
     * Some moves can stab an enemy, allowing the player to pick them up, move them around and throw them
     */
    public boolean canThrowStabbedEnemy;

    /**
     * Marks whether the player can dodge from this move
     */
    boolean canDodge;

    /**
     * Marks whether the player is able to move in this state
     */
    boolean canMove;

    /**
     * Sound effect playing when the comboWindow begins
     */
    SoundClip finishEffect = null;

    /**
     * Sound effect played ath the start of the state
     */
    SoundClip startEffect = null;

    /**
     * Links to possible moves
     */
    ComboNode Light, Heavy, Special, TimeUp, DrawSheath,Dodge, next;

    /**
     * Count the elapsed time
     */
    protected int time;

    /**
     * Marks node as finished for the ComboGraph
     */
    boolean finished = false;

    /**
     * Reference to the combo graph associated with this
     */
    protected ComboGraph graph;


    /**
     * Creates a new combo node with the given parameters
     * @param combo reference to the ComboGraph object containing then node
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
     * @param staminaCost amount of stamina that the player should use to perform this attack
     */
    public ComboNode(ComboGraph combo,int weaponAnimation,int playerAnimation, int xOffset,int yOffset,int duration,int comboWindow,int staminaCost,boolean canThrow, boolean canDodge, boolean canMove,String startSoundPath,String finishSoundPath)
    {
        graph = combo;
        this.weaponAnimation = weaponAnimation;
        this.playerAnimation = playerAnimation;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.baseDuration = duration;
        this.duration = duration;
        this.comboWindow = comboWindow;
        this.staminaCost = staminaCost;
        this.canThrowStabbedEnemy = canThrow;
        this.canDodge = canDodge;
        this.canMove = canMove;

        if(startSoundPath != null)
            startEffect = new SoundClip(startSoundPath);

        if(finishSoundPath != null)
            finishEffect = new SoundClip(finishSoundPath);
    }

    protected void atStart()
    {

    }

    protected void atWindow()
    {

    }

    public void update()
    {
        graph.weapon.getOwner().setCanMove(canMove);

        //starting stuff
        if(time == 0)
        {
           atStart();
        }

        //SELECT NEXT STATE
        if(time >= duration - comboWindow)
        {
            if(canDodge && KeyInput.keyDown(KeyInput.JUMP_DODGE))
            {
                next = Dodge;
            }

            if(KeyInput.keyDown(KeyInput.SHEATHE))
                next = DrawSheath;
            else
            if(MouseInput.getMousePressed(MouseEvent.BUTTON1))
                next = Light;
            else
            if(MouseInput.getMousePressed(MouseEvent.BUTTON3))
                if(graph.weapon.getStabbedEnemy() == null) //IF HAVE A STABBED ENEMY THROW IT BEFORE DOING ANYTHING ELSE1
                    next = Heavy;
                else next = Light;
            else
            if(MouseInput.getMousePressed(MouseEvent.BUTTON2))
                if(graph.weapon.getStabbedEnemy() == null)
                    next = Special;
                else next = Light;
        }

        if(time == duration - comboWindow)
        {
            if(finishEffect != null )
                finishEffect.play();

            atWindow();
        }

        time++;

        if(time >= duration)
        {
            finished = true;
        }
    }

}

