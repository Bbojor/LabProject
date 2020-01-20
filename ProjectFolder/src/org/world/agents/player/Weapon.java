package org.world.agents.player;

import org.engine.ui.StatBars;
import org.world.agents.player.melee.*;
import org.engine.GameLoop;
import org.engine.graphics.AnimationInformation;
import org.engine.graphics.Renderer;
import org.world.agents.enemies.Enemy;
import org.world.GameObject;
import org.world.tiles.Tile;

/**
 *  Weapon object used by the player during attacks.
 *  The actual computations regarding the attacks is handled by this class and it's associated ComboGraph.
 *  The Weapon in conjunction with the Player function as a finite state machine with the help of
 *  the ComboGraph class.
 *  The Weapon object contains a hitbox class that defines the points in the game world where the weapon actually hits.
 *  This class has the ability of "stabbing" an enemy object, binding it to the weapon and allowing the player to carry it around and throw it.
 *  with the next attack.
 */

public class Weapon extends GameObject
{
    /**
     *  Flags whether the weapon has been drawn and is ready to use
     */
    protected boolean drawn;

    /**
     * Flag set when an action (attack,dodge) is performed
     */
    protected boolean action;

    /**
     * Flag set when attacking
     */
    protected boolean attack;

    /**
     * Flag set when weapon is performing an attack and the specific animation frame it is in deals damage to enemies around it
     */
    protected boolean doingDamage;

    private Enemy stabVictim = null;

    //enable lifesteal by default
    private boolean lifesteal = true;

    private HitBox hitBox;

    private ComboGraph comboGraph = new ComboGraph(this);

    /**
     * Controls the speed of attack animations and the duration of attack states
     */
    protected float attackSpeed = 1.5f;

    private final int[][][] stabbedEnemyParameters = new int[ANIMATIONS.values().length][9][3];

    private Player owner;

    /**
     *  Animation enumeration for easier handling of the large amount of animations.
     */
    public enum ANIMATIONS
    {
        //PASSIVE ANIMATIONS
        SHEATHED(1,1,true),
        DRAWN_FRONT(3,3,true),
        DRAWN_FULL_FRONT(3,3,true),
        WALK_FRONT(4,4,true),
        WALK_FULL_FRONT(4,4,true),
        DRAWN_BACK(3,3,true),
        DRAWN_FULL_BACK(3,3,true),
        WALK_BACK(4,4,true),
        WALK_FULL_BACK(4,4,true),

        //ATTACK ANIMATIONS
        LIGHT_ATTACK_1(9,14,false),
        LIGHT_ATTACK_2(9,14,false),
        HEAVY_ATTACK_1(9,10,false),
        HEAVY_ATTACK_2(9,10,false),
        HEAVY_COMBO_1(9,10,false),
        HEAVY_COMBO_2(9,10,false),
        WHIRL(9,14,true);

        int frames;
        int fps;
        boolean loop;

        ANIMATIONS(int frames, int fps, boolean loop)
        {
            this.frames =frames;
            this.fps =fps;
            this.loop = loop;
        }
    }

    //helps select proper hitbox
    private final int PASSIVE_ANIMATION_COUNT = 9;

    //flag to update attack animation speed
    private boolean attackSpeedChanged = true;

    /**
     * Constructor must be given a player type object to assign the weapon to
     * Most of update method relies on this association
     * @param player that uses this weapon
     */
    Weapon(Player player)
    {

        //TODO
        //offset stabbed enemy to align with weapon sprite, needs some fine-tuning
        {
            //SHEATHED
            stabbedEnemyParameters[0][0][2] = -90;

            //DRAWN FRONT
            stabbedEnemyParameters[2][0][0] =   0;
            stabbedEnemyParameters[2][0][1] =  20;
            stabbedEnemyParameters[2][0][2] = -90;

            stabbedEnemyParameters[2][1][0] =   0;
            stabbedEnemyParameters[2][1][1] =  20;
            stabbedEnemyParameters[2][1][2] = -90;

            stabbedEnemyParameters[2][2][0] =   0;
            stabbedEnemyParameters[2][2][1] =  20;
            stabbedEnemyParameters[2][2][2] = -90;

            //WALK FRONT
            stabbedEnemyParameters[4][0][0] =   -1;
            stabbedEnemyParameters[4][0][1] =  20;
            stabbedEnemyParameters[4][0][2] = -90;

            stabbedEnemyParameters[4][1][0] =   0;
            stabbedEnemyParameters[4][1][1] =  20;
            stabbedEnemyParameters[4][1][2] = -90;

            stabbedEnemyParameters[4][2][0] =   1;
            stabbedEnemyParameters[4][2][1] =  20;
            stabbedEnemyParameters[4][2][2] = -90;

            stabbedEnemyParameters[4][3][0] =   1;
            stabbedEnemyParameters[4][3][1] =  20;
            stabbedEnemyParameters[4][3][2] = -90;

            //DRAWN BACK
            stabbedEnemyParameters[6][0][0] =   0;
            stabbedEnemyParameters[6][0][1] =  20;
            stabbedEnemyParameters[6][0][2] = -90;

            stabbedEnemyParameters[6][1][0] =   0;
            stabbedEnemyParameters[6][1][1] =  20;
            stabbedEnemyParameters[6][1][2] = -90;

            stabbedEnemyParameters[6][2][0] =   0;
            stabbedEnemyParameters[6][2][1] =  20;
            stabbedEnemyParameters[6][2][2] = -90;

            //WALK BACK
            stabbedEnemyParameters[8][0][0] =   1;
            stabbedEnemyParameters[8][0][1] =  20;
            stabbedEnemyParameters[8][0][2] = -90;

            stabbedEnemyParameters[8][1][0] =   1;
            stabbedEnemyParameters[8][1][1] =  20;
            stabbedEnemyParameters[8][1][2] = -90;

            stabbedEnemyParameters[8][2][0] =   1;
            stabbedEnemyParameters[8][2][1] =  20;
            stabbedEnemyParameters[8][2][2] = -90;

            stabbedEnemyParameters[8][3][0] =   1;
            stabbedEnemyParameters[8][3][1] =  20;
            stabbedEnemyParameters[8][3][2] = -90;

            //STAB
            stabbedEnemyParameters[11][0][0] = 10;
            stabbedEnemyParameters[11][0][1] = 10;
            stabbedEnemyParameters[11][0][2] = 0;

            stabbedEnemyParameters[11][1][0] = 10;
            stabbedEnemyParameters[11][1][1] = 10;
            stabbedEnemyParameters[11][1][2] = 0;

            stabbedEnemyParameters[11][2][0] = 10;
            stabbedEnemyParameters[11][2][1] = 10;
            stabbedEnemyParameters[11][2][2] = 0;

            stabbedEnemyParameters[11][3][0] = 15;
            stabbedEnemyParameters[11][3][1] = 10;
            stabbedEnemyParameters[11][3][2] = 0;

            stabbedEnemyParameters[11][4][0] =  20;
            stabbedEnemyParameters[11][4][1] =  10;
            stabbedEnemyParameters[11][4][2] =   0;

            stabbedEnemyParameters[11][5][0] =  25;
            stabbedEnemyParameters[11][5][1] =  10;
            stabbedEnemyParameters[11][5][2] =   0;

            stabbedEnemyParameters[11][6][0] =  25;
            stabbedEnemyParameters[11][6][1] =  10;
            stabbedEnemyParameters[11][6][2] =   0;

            stabbedEnemyParameters[11][7][0] =  25;
            stabbedEnemyParameters[11][7][1] =  10;
            stabbedEnemyParameters[11][7][2] =   0;

            stabbedEnemyParameters[11][8][0] =  30;
            stabbedEnemyParameters[11][8][1] =  10;
            stabbedEnemyParameters[11][8][2] =   0;
        }
        //attempt to load animations

        spriteWidth = 120;
        spriteHeight = 90;

        loadAnimations();

        //if no animations are present create them
        if(animations == null)
        {
            AnimationInformation[] animationInfo = new AnimationInformation[ANIMATIONS.values().length];
            spriteSheetPath = "/resources/player/Weapons/sword.png";
            for ( ANIMATIONS a: ANIMATIONS.values() )
            {
                animationInfo[a.ordinal()] = new AnimationInformation(a.frames,a.fps,spriteWidth,spriteHeight,a.loop);
            }

            createAnimations(animationInfo);
        }

        owner = player;
        hitBox = new HitBox(this);
    }

    @Override
    public void update()
    {

        //compute new attack durations based on the attack speed in case it has changed
        if (attackSpeedChanged)
        {
            for(ComboNode a: comboGraph.getAttackNodes())
            {
                animations[a.weaponAnimation].fps = (int) ((float)ANIMATIONS.values()[a.weaponAnimation].fps * attackSpeed) + 1;
                a.duration = (int) ((float)a.baseDuration / attackSpeed) + 5;
            }

            attackSpeedChanged = false;
        }

        comboGraph.update();

        attack = comboGraph.getCurrentNode() instanceof  AttackNode;

        currentAnimation = comboGraph.getCurrentNode().weaponAnimation;

        if (owner.isFlipped())
        {
            flip = true;
            x = owner.x + comboGraph.getCurrentNode().xOffset;
        }
        else {
            flip = false;
            x = owner.x - comboGraph.getCurrentNode().xOffset;
            }

        y = owner.y + comboGraph.getCurrentNode().yOffset - owner.getSpriteHeight() / 2;
        z = owner.z;

        drawn = (currentAnimation > 0);

        action = currentAnimation >= 9 || comboGraph.getCurrentNode() instanceof DodgeNode;

         if(!action)
         {
             //set up WALKING animations =================================================================//
             if (drawn && comboGraph.getCurrentNode() instanceof IdleNode && owner.isMoving() && !owner.isJumping() && !owner.isFalling())
             {
                 flip = !flip;

                 if ( currentAnimation < 3 || (currentAnimation > 4 && currentAnimation < 7))
                     currentAnimation += 2;

                 y -= 14;

                 if (owner.isFlipped())
                 {
                     x += 20;
                 }
                 else
                     {
                     x -= 20;
                 }
             }

             if (owner.facingAway && currentAnimation < 5 && currentAnimation > 0)
                 currentAnimation += 4;
         }

        //if animation just switched start at frame 0
        setFrame();

        //place hitbox where it should be depending on weapon position and the current animation/frame
        if(attack && hitBox.isDamageFrame()[currentAnimation - PASSIVE_ANIMATION_COUNT][currentFrame])
        {
            hitBox.update(x,y,currentAnimation - PASSIVE_ANIMATION_COUNT,currentFrame);
            doingDamage = true;
        }
        else
            doingDamage = false;


        //handle stabbed enemy
        if(stabVictim != null)
        {
            if(flip)
            stabVictim.x = this.x - stabbedEnemyParameters[currentAnimation][currentFrame][0];
            else
                stabVictim.x = this.x +  stabbedEnemyParameters[currentAnimation][currentFrame][0];

            stabVictim.y = owner.y;
            stabVictim.z =  owner.z + (float) stabbedEnemyParameters[currentAnimation][currentFrame][1]/ Tile.GROUND_TILE_Z_HEIGHT;
            stabVictim.rotation = stabbedEnemyParameters[currentAnimation][currentFrame][2];

            stabVictim.updateShadow();

            stabVictim.currentAnimation = stabVictim.getStabAnimation();

            stabVictim.yWidth = 20;

            if(comboGraph.getCurrentNode().canThrowStabbedEnemy)
            {
                if(flip)
                    stabVictim.xInertia = -50;
                else
                    stabVictim.xInertia = 50;

                stabVictim.remove = false;

                stabVictim.thrown = true;

                GameLoop.getWorld().addGameObject(stabVictim);
                stabVictim = null;
            }
        }
    }

    @Override
    public void render()
    {
        Renderer.setRotation(rotation);
        Renderer.drawImage(animations[currentAnimation].getImage(currentFrame), spriteWidth, spriteHeight, x, y  - Tile.GROUND_TILE_Z_HEIGHT * z  ,flip);
        Renderer.setRotation(0);
        playAnimation();

      //if(doingDamage)
      //     hitBox.render();

        if(stabVictim != null)
            stabVictim.render();
    }

    /**
     *  Attempts to heal the player based on amount of damage dealt to enemies if the "lifesteal" property is set
     */
    public void lifesteal()
    {
        if(lifesteal)
        {
            AttackNode  node = (AttackNode)(comboGraph.getCurrentNode());
            if(StatBars.canLifesteal() && node != null)
                owner.heal(StatBars.getLifestealAmount() / 10 * node.damage / 10, (int) GameLoop.getWorld().getHUD().getStats().getOldHealth());
        }
    }

    /**
     * Binds an enemy to the player's weapon. That enemy can then be carried around and thrown
     * @param e enemy to be bound
     */
    public void setStabbedEnemy(Enemy e) { stabVictim = e; }

    /**
     * Returns the currently bound enemy
     * @return Enemy object
     */
    public Enemy getStabbedEnemy()
    {
        return stabVictim;
    }

    /**
     * Gets the owner(Player object) of the weapon
     * @return player object
     */
    public Player getOwner() { return owner; }

    /**
     * Checks whether the weapon is doing damage at a certain point in time
     * Not all animation frames during attacks actually deal damage to enemies
     */
    public boolean isDoingDamage() { return doingDamage; }

    /**
     * Returns the hitbox object associated with this weapon
     * @return hitbox object
     */
    public HitBox getHitBox() { return hitBox; }

    /**
     * Gets the ComboGraph object associated with this weapon
     * @return ComboGraph object
     */
    public ComboGraph getComboGraph() { return comboGraph; }

    /**
     * Checks whether the weapon is in an attack state
     * Note that this does not necessarily coincide with the doingDamage property
     */
    public boolean isAttacking() { return attack; }

    /**
     *  Modify attack speed with the given value
     *  If resulting attack speed is negative, make no changes
     * @param speed amount by which to change the attack speed (can be both negative and positive)
     */
    public void modifyAttackSpeed(float speed)
    {
        float newAttackSpeed = attackSpeed + speed;
        if(newAttackSpeed > 0)
        {
            attackSpeedChanged = true;
            attackSpeed = newAttackSpeed;
        }
    }

    /**
     * Checks whether attack speed has been modified
     * Allows for updating the speed of animations for weapon and player
     */
    public boolean isAttackSpeedChanged()
    {
        return attackSpeedChanged;
    }
}