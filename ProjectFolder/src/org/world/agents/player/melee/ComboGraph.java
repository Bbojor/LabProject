package org.world.agents.player.melee;

import org.world.agents.player.Player;
import org.world.agents.player.Weapon;
import java.util.LinkedList;

/**
 *  The combo graph class implements a state machine to handle the different states the player/weapon can be at a certain point
 *  It consists of several interconnected nodes each representing a state with its respective attributes (duration, possible transitions,etc)
 *  The relevant information can then be accessed by the player and weapon objects in their respective update methods
 */
public class ComboGraph
{

    /**
     *  The nodes that make up the graph
     */
    private ComboNode Sheathed, Idle, TransitionOpen, TransitionClosed,Dodge, L1, L2, H1, H2, HC1, HC2, Whirl, current;

    /**
     * List that contains the attack nodes
     * Used to compute new durations and speed up/slow down the respective player and weapon animations when the player's attack speed changes
     */
    private LinkedList<ComboNode>  nodes = new LinkedList<>();

    /**
     * Reference to the Weapon object that uses this specific combo graph
     */
    protected Weapon weapon;

    /**
     * Initializes a ComboGraph object and all the nodes contained in it
     * @param weapon reference to the weapon that will use this graph
     */
    public ComboGraph(Weapon weapon)
    {
        this.weapon = weapon;

        Sheathed = new IdleNode(this,Weapon.ANIMATIONS.SHEATHED.ordinal(), Player.ANIMATIONS.IDLE_FRONT.ordinal(),false,true,false, 6, -5,1,1,null,null);
        TransitionOpen = new IdleNode(this,Weapon.ANIMATIONS.DRAWN_FRONT.ordinal(), Player.ANIMATIONS.IDLE_FRONT.ordinal(),false,true, false,-10, -10,10,0,null,"/resources/sfx/drawn.wav");
        TransitionClosed = new IdleNode(this,Weapon.ANIMATIONS.DRAWN_FRONT.ordinal(), Player.ANIMATIONS.IDLE_FRONT.ordinal(),false,true,false, -10, -10,10,0,null,"/resources/sfx/drawn.wav");
        Idle = new IdleNode(this,Weapon.ANIMATIONS.DRAWN_FULL_FRONT.ordinal(), Player.ANIMATIONS.IDLE_FRONT.ordinal(),true,true,false, -10, -10,1,1,null,null);
        Dodge = new DodgeNode(this,Weapon.ANIMATIONS.DRAWN_FULL_FRONT.ordinal(), Player.ANIMATIONS.DODGE_FRONT.ordinal(),true,false,false, -10, -10,22,22,null,"/resources/sfx/sword_woosh.wav",15);
        L1 = new AttackNode(this,Weapon.ANIMATIONS.LIGHT_ATTACK_1.ordinal(), Player.ANIMATIONS.ATTACK_FRONT_1.ordinal(),true,false,true,1,15,30,60,20,"/resources/sfx/sword_hit.wav","/resources/sfx/sword_slash.wav",true,1,10,AttackNode.HORIZONTAL,15);
        L2 = new AttackNode(this,Weapon.ANIMATIONS.LIGHT_ATTACK_2.ordinal(), Player.ANIMATIONS.ATTACK_FRONT_2.ordinal(),true,false,true,1,15,30,60,20,"/resources/sfx/sword_hit.wav","/resources/sfx/sword_slash.wav",true,1,10,AttackNode.HORIZONTAL,15);
        H1 = new AttackNode(this,Weapon.ANIMATIONS.HEAVY_ATTACK_1.ordinal(), Player.ANIMATIONS.HEAVY_ATTACK_FRONT_1.ordinal(),true,false,false,-8,8,50,80,25,null,"/resources/sfx/sword_woosh.wav",false,0,0,AttackNode.VERTICAL,25);
        H2 = new AttackNode(this,Weapon.ANIMATIONS.HEAVY_ATTACK_2.ordinal(), Player.ANIMATIONS.HEAVY_ATTACK_FRONT_2.ordinal(),true,false,false,1,-7,50,80,25,"/resources/sfx/sword_hit.wav","/resources/sfx/sword_slash.wav",true,2,10,AttackNode.VERTICAL,25);
        HC1 = new AttackNode(this,Weapon.ANIMATIONS.HEAVY_COMBO_1.ordinal(), Player.ANIMATIONS.HEAVY_COMBO_FRONT_1.ordinal(),true,false,true,0,7,50,70,20,"/resources/sfx/sword_hit.wav","/resources/sfx/sword_slash.wav",true,2,10,AttackNode.HORIZONTAL,30);
        HC2 = new AttackNode(this,Weapon.ANIMATIONS.HEAVY_COMBO_2.ordinal(), Player.ANIMATIONS.HEAVY_COMBO_FRONT_2.ordinal(),true,false,false,0,-7,50,70,20,"/resources/sfx/sword_hit.wav","/resources/sfx/sword_slash.wav",true,2,10,AttackNode.HORIZONTAL,30);
        Whirl = new AttackNode(this,Weapon.ANIMATIONS.WHIRL.ordinal(), Player.ANIMATIONS.WHIRL_FRONT.ordinal(),true,true,true,1,15,30,240,20,null,"/resources/sfx/sword_slash.wav",false,0,0,false,50);

        Sheathed.Light = TransitionOpen;
        Sheathed.Heavy = TransitionOpen;
        Sheathed.Special = TransitionOpen;
        Sheathed.TimeUp = Sheathed;
        Sheathed.next = Sheathed;
        Sheathed.DrawSheath = TransitionOpen;
        Sheathed.Dodge = Dodge;

        TransitionOpen.Light = Idle;
        TransitionOpen.Heavy = Idle;
        TransitionOpen.Special = Idle;
        TransitionOpen.TimeUp = Idle;
        TransitionOpen.next = Idle;
        TransitionOpen.DrawSheath = Idle;
        TransitionOpen.Dodge = Dodge;

        TransitionClosed.Light = Sheathed;
        TransitionClosed.Heavy = Sheathed;
        TransitionClosed.Special = Sheathed;
        TransitionClosed.TimeUp = Sheathed;
        TransitionClosed.next = Sheathed;
        TransitionClosed.DrawSheath = Sheathed;
        TransitionClosed.Dodge = Sheathed;

        Idle.Light = L1;
        Idle.Heavy = H1;
        Idle.Special = Idle;
        Idle.TimeUp = Idle;
        Idle.next = Idle;
        Idle.DrawSheath = TransitionClosed;
        Idle.Dodge = Dodge;

        L1.Light = L2;
        L1.Heavy = HC1;
        L1.Special = Idle;
        L1.TimeUp = Idle;
        L1.next = Idle;
        L1.Dodge = Dodge;
        L1.DrawSheath = Idle;
        nodes.add(L1);

        L2.Light = L1;
        L2.Heavy = HC2;
        L2.Special = Idle;
        L2.TimeUp = Idle;
        L2.next = Idle;
        L2.Dodge = Dodge;
        L2.DrawSheath = Idle;
        nodes.add(L2);

        H1.Light = L1;
        H1.Heavy = H2;
        H1.Special = Idle;
        H1.TimeUp = Idle;
        H1.next = Idle;
        H1.Dodge = Dodge;
        H1.DrawSheath = Idle;
        ((AttackNode)H1).canStabEnemy = true;
        nodes.add(H1);

        H2.Light = L1;
        H2.Heavy = HC1;
        H2.Special = Idle;
        H2.TimeUp = Idle;
        H2.next = Idle;
        H2.Dodge = Dodge;
        H2.DrawSheath = Idle;
        nodes.add(H2);

        HC1.Light = L1;
        HC1.Heavy = Whirl;
        HC1.Special = Idle;
        HC1.TimeUp = Idle;
        HC1.next = Idle;
        HC1.Dodge = Dodge;
        HC1.DrawSheath = Idle;
        nodes.add(HC1);

        HC2.Light = L1;
        HC2.Heavy = Whirl;
        HC2.Special = Idle;
        HC2.TimeUp = Idle;
        HC2.next = Idle;
        HC2.Dodge = Dodge;
        HC2.DrawSheath = Idle;
        nodes.add(HC2);

        Whirl.Light = HC1;
        Whirl.Heavy = H1;
        Whirl.Special = Idle;
        Whirl.TimeUp = Idle;
        Whirl.next = Idle;
        Whirl.Dodge = Dodge;
        Whirl.DrawSheath = Idle;
        nodes.add(Whirl);

        Dodge.Light = L1;
        Dodge.Heavy = H1;
        Dodge.Special = Idle;
        Dodge.TimeUp = Idle;
        Dodge.next = Idle;
        Dodge.Dodge = Dodge;
        Dodge.DrawSheath = Idle;

        current = Sheathed;
    }

    /**
     * Updates the current node and changes it when necessary
     * Also checks for stamina constraints
     * Stops when the player is dead
     */
    public void update()
    {
        if(weapon.getOwner().getHealth() < 0)
        {
            current = Idle;
            return;
        }

        current.update();

        if(current.finished)
        {
            if( weapon.getOwner().getStamina() > 0 || current.next.staminaCost == 0)
            {
                current = current.next;
                weapon.getOwner().decreaseStamina(current.staminaCost);
                if(weapon.getOwner().getStamina() < 0)
                    weapon.getOwner().decreaseStamina(current.staminaCost * 2);
            }
            else
                current = Idle;
            current.time = 0;
            current.finished = false;
            current.next = current.TimeUp;
        }
    }

    /**
     * Returns a linked list attack nodes. Used for performing the relevant updates when the player's attack speed changes
     * @return node array containing only the attack nodes
     */
    public LinkedList<ComboNode> getAttackNodes()
    {
        return nodes;
    }

    /**
     * Returns the current node
     * @return reference to the current node
     */
    public ComboNode getCurrentNode()
    {
        return current;
    }

    /**
     * Sets the current node to the idle one. Used to reset the ComboGraph.
     */
    public void setIdle()
    {
        current = Idle;
        current.time = 0;
        current.finished = false;
        current.next = current.TimeUp;

    }
}
