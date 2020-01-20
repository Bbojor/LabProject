package org.world.agents.player;

import org.engine.graphics.Renderer;
import org.world.agents.player.melee.ComboNode;
import org.engine.GameLoop;
import org.engine.graphics.AnimationInformation;
import org.engine.input.KeyInput;
import org.engine.input.MouseInput;
import org.engine.resources.SoundClip;
import org.world.agents.Shadow;
import org.world.GameObject;
import org.world.agents.player.melee.DodgeNode;
import org.world.agents.player.melee.IdleNode;
import org.world.tiles.Tile;

/**
 *  This class implements a playable character capable of interaction with the game world
 *  The player object is capable of moving around the game world and attacking enemies
 *  The control scheme is handled by the KeyListener class
 *  Attacks are implemented through the Weapon class
 */
public final class Player extends GameObject {

    private static float MAX_SPEED = 30f;
    private static float RESTING_START_SPEED = 20f;
    private float speed = RESTING_START_SPEED;
    private float inertia = 6f;
    private final int INERTIA_MAX = 10;

    //step sound effects
    private int step = 0;
    private SoundClip[] steps = new SoundClip[2];

    //shadow
    private Shadow shadow = new Shadow(this);

    //weapon
    private Weapon weapon = new Weapon(this);

    //health
    private int health = 100;
    private final int HEALTH_MAX = 100;

    //stamina used for attacks/dodging
    private final int STAMINA_MAX = 100;
    private int stamina = 100;
    private int staminaRecoverySpeed = 3;
    private int staminaRecoveryTime;

    private boolean invincible;
    private boolean controllable = true;

    private boolean damaged;
    //invulnerability timer after taking damage
    private int damageCooldown;
    private int DAMAGE_TIMER = 30;

    public final float PLAYER_HEIGHT_OFFSET = 0;

    private boolean jump = false;
    private  boolean canJump = true;
    private boolean falling;
    private  int jumpTime = 0; //how long character has been jumping
    private final int MAX_JUMP_TIME = 20; // jump duration (actual jump + falling back down)
    private final double JUMP_HEIGHT = 2.0;
    private final double FALL_SPEED = 2.0;
    private boolean moved;
    private  boolean canMove;

    //handle inertia
    private int xInertia = 0;
    private int yInertia = 0;

    private int xInertiaTime = 0;
    private int yInertiaTime = 0;

    /**
     *  Flags whether the player is facing away from the screen or towards it
     */
    protected boolean facingAway = false;

    /**
     * Animation enum to hold indices, no. of frames, fps and whether or not the animation is a loop.
     * Created for easier animation management given the large number of animations the player possesses
     */
    public enum ANIMATIONS {
        IDLE_FRONT(3, 4,true),
        RUNNING_FRONT(12, 12,true),
        JUMPING_FRONT(8, 8,false),
        IDLE_WEAPON_FRONT(3, 4,true),
        RUNNING_WEAPON_FRONT(12, 12,true),
        JUMPING_WEAPON_FRONT(8, 6,true),
        DODGE_FRONT(3, 12,true),
        ATTACK_FRONT_1(9, 14,false),
        ATTACK_FRONT_2(9, 14,false),
        HEAVY_ATTACK_FRONT_1(9, 10,false),
        HEAVY_ATTACK_FRONT_2(9, 10,false),
        HEAVY_COMBO_FRONT_1(9, 10,false),
        HEAVY_COMBO_FRONT_2(9, 10,false),
        WHIRL_FRONT(9, 14,true),
        DEAD_FRONT(12,5,false),
        IDLE_BACK(3, 4,true),
        RUNNING_BACK(12, 12,true),
        JUMPING_BACK(8, 8,false),
        IDLE_WEAPON_BACK(3, 4,true),
        RUNNING_WEAPON_BACK(12, 12,true),
        JUMPING_WEAPON_BACK(8, 6,false),
        DODGE_BACK(3, 12,true),
        ATTACK_BACK_1(9, 14,false),
        ATTACK_BACK_2(9, 14,false),
        HEAVY_ATTACK_BACK_1(9, 10,false),
        HEAVY_ATTACK_BACK_2(9, 10,false),
        HEAVY_COMBO_BACK_1(9, 10,false),
        HEAVY_COMBO_BACK_2(9, 10,false),
        WHIRL_BACK(9, 14,true),
        DEAD_BACK(12,5,false);

       public int frames;
       public int fps;
       boolean loop;

        ANIMATIONS(int f, int fps, boolean loop)
        {
            frames = f;
            this.fps = fps;
            this.loop = loop;
        }
    }

    public Player()
    {
        isoX = 7;
        isoY = 7;
        x = (isoX + isoY) * Tile.GROUND_TILE_WIDTH/2f;
        y = (isoY - isoX) * Tile.GROUND_TILE_WIDTH/2f;
        z = GameLoop.getWorld().getTerrain().getTiles()[isoX][isoY].z;
        xWidth = 10;
        yWidth = 5;
        zWidth = 2;
        solid = true;

        spriteWidth = 40;
        spriteHeight = 60;

        spriteSheetPath = "/resources/player/player_anim.png";

        // load sounds
        steps[0] = new SoundClip("/resources/sfx/metallic_step_1.wav");
        steps[0].setVolume(-30f);
        steps[1] = new SoundClip("/resources/sfx/metallic_step_2.wav");
        steps[1].setVolume(-30f);

        //attempt to load animations
        loadAnimations();

        //if no animations are already present create them
        if(animations == null)
        {
           AnimationInformation[] animationInfo = new AnimationInformation[ANIMATIONS.values().length];


            for (ANIMATIONS a : ANIMATIONS.values())
            {
                animationInfo[a.ordinal()] = new AnimationInformation(a.frames,a.fps,spriteWidth,spriteHeight,a.loop);
            }

            createAnimations(animationInfo);
        }
    }

    /**
     *  Handles damaged state/invulnerability after being hit
     */
    private void damageBehaviour()
    {
        if (damaged) {
            damageCooldown = DAMAGE_TIMER;
            damaged = false;
            invincible = true;
        }

        if (damageCooldown <= 0) {
            invincible = false;
        } else damageCooldown--;
    }

    /**
     *  In case the player falls off the map place them back
     */
    private void respawnAfterFall()
    {
        x = 0;
        y = 0;
        z = PLAYER_HEIGHT_OFFSET;
        isoX = 0;
        isoY = 0;
        shadow.update();
        getDamaged(getHealth());
    }

    /**
     *  Checks the input for move commands and other factors that may cause movement(ex. inertia)
     */
    private void handleMovement()
    {

        float xInput = 0;
        float yInput = 0;

        // normal movement
        if (canMove)
        {

            if (KeyInput.keyHeld(KeyInput.UP) || KeyInput.keyDown(KeyInput.UP)) {
                yInput--;
                yInertia = -INERTIA_MAX;
                moved = true;
                yInertiaTime = 5;
            } else yInertiaTime--;

            if (KeyInput.keyHeld(KeyInput.DOWN) || KeyInput.keyDown(KeyInput.DOWN)) {
                yInput++;
                yInertia = INERTIA_MAX;
                moved = true;
                yInertiaTime = INERTIA_MAX;
            } else yInertiaTime--;

            if (KeyInput.keyHeld(KeyInput.LEFT) || KeyInput.keyDown(KeyInput.LEFT)) {
                xInput--;
                xInertia = -INERTIA_MAX;
                moved = true;
                xInertiaTime = INERTIA_MAX;
            } else
                xInertiaTime--;

            if (KeyInput.keyHeld(KeyInput.RIGHT) || KeyInput.keyDown(KeyInput.RIGHT)) {
                xInput++;
                xInertia = INERTIA_MAX;
                moved = true;
                xInertiaTime = INERTIA_MAX;
            } else xInertiaTime--;

            if (xInertiaTime <= 0)
                xInertia = 0;

            if (yInertiaTime <= 0)
                yInertia = 0;

        }

       /* //movement in-between attacks
        if (!weapon.action && !ComboGraph.dodged) {


            if (KeyInput.keyHeld(KeyEvent.VK_W) || KeyInput.keyDown(KeyEvent.VK_W)) {
                yInertia = -INERTIA_MAX;
                yInertiaTime = INERTIA_MAX;
            }


            if (KeyInput.keyHeld(KeyEvent.VK_S) || KeyInput.keyDown(KeyEvent.VK_S)) {
                yInertia = INERTIA_MAX;
                yInertiaTime = INERTIA_MAX;
            }

            if (KeyInput.keyHeld(KeyEvent.VK_A) || KeyInput.keyDown(KeyEvent.VK_A)) {
                xInertia = -INERTIA_MAX;
                xInertiaTime = INERTIA_MAX;
            }


            if (KeyInput.keyHeld(KeyEvent.VK_D) || KeyInput.keyDown(KeyEvent.VK_D)) {
                xInertia = INERTIA_MAX;
                xInertiaTime = INERTIA_MAX;
            }
        }
*/
        //====================================================================//

        //move
        if (moved || xInertia != 0 || yInertia != 0)
        {

            currentAnimation = ANIMATIONS.RUNNING_FRONT.ordinal();

            //save old coordinates
            float oldX = x;
            float oldY = y;

            //play sounds
            if (!steps[step].isRunning()) {
                step = (step++) % 2;
                steps[step % 2].play();
            }

            //do isometric updates on y first then on x to make edge collisions less shitty
            if (moved)

                y += yInput * GameLoop.updateDelta() * Renderer.scale * speed;
            else if (yInertia != 0) {
                y += yInertia * GameLoop.updateDelta() * Renderer.scale * inertia;
                yInertia -= Integer.signum(yInertia);
            }

            //compute iso coordinates
            int[] tempIso = Renderer.IsoCoordinates(x, y);

            //if move is valid
            if (!(solid && checkCollision(null)) && GameLoop.getWorld().inBounds(tempIso[0],tempIso[1]) &&
                    (GameLoop.getWorld().getTerrain().getTiles()[Math.abs(tempIso[0])][Math.abs(tempIso[1])].traversable || jump) &&
                    GameLoop.getWorld().getTerrain().getTiles()[Math.abs(tempIso[0])][Math.abs(tempIso[1])].z <= z)
            {

                //update position
                isoX = tempIso[0];
                isoY = tempIso[1];
            }
            else
                {
                //keep old position
                x = oldX;
                y = oldY;
            }

            if (moved)
                x += xInput * GameLoop.updateDelta() * Renderer.scale * speed;
            else if (xInertia != 0) {

                x += xInertia * GameLoop.updateDelta() * Renderer.scale * inertia;
                xInertia -= Integer.signum(xInertia);
            }

            tempIso = Renderer.IsoCoordinates(x, y);

            //if move is valid
            if ( !(solid && checkCollision(null)) &&  GameLoop.getWorld().inBounds(tempIso[0],tempIso[1]) &&
                    (GameLoop.getWorld().getTerrain().getTiles()[Math.abs(tempIso[0])][Math.abs(tempIso[1])].traversable || jump) &&
                    GameLoop.getWorld().getTerrain().getTiles()[Math.abs(tempIso[0])][Math.abs(tempIso[1])].z <= z )
            {
                //update position
                isoX = tempIso[0];
                isoY = tempIso[1];
            }
            else
                {
                //keep old position
                x = oldX;
                y = oldY;
            }
        }

        if(moved && speed < MAX_SPEED)
            speed += 0.5f;
        else
        if(!moved)
            speed = RESTING_START_SPEED;

    }

    /**
     *  Jumps or handle jump state when necessary
     */
    private void handleJumping()
    {
        canJump = !weapon.drawn && !falling;

        //==================// JUMPING //========================//

        if (!jump && canJump) {
            if (KeyInput.keyDown(KeyInput.JUMP_DODGE)) {
                jump = true;
                jumpTime = 0;
                canJump = false;
            }
        } else if (jump) {
            currentAnimation = ANIMATIONS.JUMPING_FRONT.ordinal();

            //jump
            if (jumpTime < MAX_JUMP_TIME) {
                z += JUMP_HEIGHT / (MAX_JUMP_TIME);
                jumpTime++;
            } else
                jump = false;
        }

        //fall
        if (!jump) {
            if (z > GameLoop.getWorld().getTerrain().getTiles()[isoX][isoY].z + PLAYER_HEIGHT_OFFSET) {
                z -= FALL_SPEED / (MAX_JUMP_TIME);
                falling = true;
            }

            //finish fall
            else {
                jumpTime = 0;
                canJump = true;
                z = GameLoop.getWorld().getTerrain().getTiles()[isoX][isoY].z + PLAYER_HEIGHT_OFFSET;
                falling = false;
            }
        }
    }

    /**
     *  Orients/flips the player to always face towards the mouse
     */
    private void mouseOrientation()
    {
            //flip, but not during attacks
            flip = MouseInput.getPixelX() < x;

            // back/front facing
            if (MouseInput.getPixelY() > y && currentAnimation >= ANIMATIONS.values().length / 2) {
                currentAnimation -= ANIMATIONS.values().length / 2;
                facingAway = false;
            } else if (MouseInput.getPixelY() < y && currentAnimation < ANIMATIONS.values().length / 2) {
                currentAnimation += ANIMATIONS.values().length / 2;
                facingAway = true;
            }
    }

    /**
     *  Updates the state of the player
     */
    @Override
    public void update()
    {
        if(health <= 0)
        {
            controllable = false;
            currentAnimation = ANIMATIONS.DEAD_FRONT.ordinal();
            weapon.y = this.y - 10;
            weapon.rotation = 90;
            weapon.getComboGraph().setIdle();
            weapon.currentAnimation = Weapon.ANIMATIONS.SHEATHED.ordinal();
            return;
        }

        damageBehaviour();

        if(stamina < STAMINA_MAX)
        {
            if(weapon.getComboGraph().getCurrentNode() instanceof IdleNode)
                staminaRecoveryTime++;
            else
                staminaRecoveryTime = 0;

            if(staminaRecoveryTime == staminaRecoverySpeed)
            {
                stamina += 1;
                staminaRecoveryTime = 0;
            }
        }
        if(stamina  > STAMINA_MAX)
            stamina = STAMINA_MAX;


        if (controllable)
        {

            //some initialisations
            moved = false;
            currentAnimation = ANIMATIONS.IDLE_FRONT.ordinal();
            facingAway = false;
            invincible = invincible || weapon.getComboGraph().getCurrentNode() instanceof DodgeNode;

            //if fell off
            if (z < -4)
            {
                respawnAfterFall();
            }

            handleMovement();

            handleJumping();

            if ((!moved && !jump) || weapon.action)
            {
                currentAnimation = weapon.getComboGraph().getCurrentNode().playerAnimation;
            }

            if (!weapon.isAttacking())
                mouseOrientation();

            //update camera
            Renderer.cameraX = x;
            Renderer.cameraY = y;

            // weapon /no weapon
            if (weapon.drawn && (currentAnimation < (ANIMATIONS.values().length / 4) - 4 ||
                    (currentAnimation >= ANIMATIONS.values().length / 2 && currentAnimation < 3 * (ANIMATIONS.values().length / 4) - 4)))
            {
                currentAnimation += ANIMATIONS.values().length / 4 - 4;
            }

            //ATTACK SPEED CHANGES
            if (weapon.isAttackSpeedChanged()) {
                for (ComboNode a : weapon.getComboGraph().getAttackNodes())
                {
                    animations[a.playerAnimation].fps = (int) ((float) ANIMATIONS.values()[a.playerAnimation].fps * weapon.attackSpeed) + 1;
                    animations[a.playerAnimation + animations.length / 2].fps = (int) ((float) ANIMATIONS.values()[a.playerAnimation].fps * weapon.attackSpeed) + 1;
                }
            }

            //if animation just switched start at frame 0
            setFrame();

            //update shadow, weapon and depth
            shadow.update();
            weapon.update();
        }
    }

    @Override
    public void render()
    {
        //render shadow first so it comes underneath player
        shadow.render();

        //render weapon behind player when necessary
       if (!weapon.action && ((!facingAway && !weapon.drawn) || (facingAway && weapon.drawn)))
           weapon.render();

        Renderer.setRotation(rotation);
        Renderer.drawImage(animations[currentAnimation].getImage(currentFrame), spriteWidth, spriteHeight, x, y - Tile.GROUND_TILE_Z_HEIGHT * z - spriteHeight / 2f, flip);
        Renderer.setRotation(0);
        playAnimation();

        //render weapon in front of player
        if (weapon.action || (facingAway && !weapon.drawn) || (!facingAway && weapon.drawn))
            weapon.render();

    }

   /* public float getDepth()
    {
        return playerDepth;
    }*/

    public float getSpriteHeight() {
        return spriteHeight;
    }

    /**
     *  Deals damage to the player
     * @param damage amount of damage as an integer
     */
    public void getDamaged(int damage)
    {
        if (!invincible)
        {
            health -= damage;
            damaged = true;
        }
    }

    /**
     *  Heals the player up to the specified maximum health value
     * @param amount amount of HP to heal
     * @param maxValue maximum value that can be healed
     */
    public void heal(int amount, int maxValue)
    {
        health += amount;
        if (health > maxValue)
            health = maxValue;
    }

    /**
     *  Heals the player up to full health
     * @param amount amount of HP to heal
     */
    public void heal(int amount)
    {
        health += amount;
        if (health > HEALTH_MAX)
            health = HEALTH_MAX;
    }

    /**
     * Returns the current hea;th value of the player
     * @return health value as an int
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets player's inertia
     * After the player stops moving, it slides a certain distance proportional to the current inertia value
     * If the given value is outside of the possible inertia range ( 6f to 30f) the value is set to the minimum or respectively maximum values
     * Useful for making slippery surfaces in the game
     * @param inertia inertia as a float
     */
    public void setInertia(float inertia)
    {
        if(inertia < 6f)
            this.inertia = 6f;
        else
        if(inertia > INERTIA_MAX)
            this.inertia = INERTIA_MAX;
        else
            this.inertia = inertia;
    }

    /**
     * Checks whether the player ois invincible or not
     * An invincible player cannot be damaged
     */
    public boolean isInvincible()
    {
        return invincible;
    }

    /**
     * Sets the invincibility of the player
     * @param invincible boolean with desired value
     */
    public void setInvincible(boolean invincible)
    {
        this.invincible = invincible;
    }

    /**
     * Checks whether the player object can be controlled by the player
     */
    public boolean isControllable()
    {
        return controllable;
    }

    /**
     * Sets the controllable flag to the given boolean value
     */
    public void setControllable(boolean controllable)
    {
        this.controllable = controllable;
    }

   /* public boolean canJump()
    {
        return canJump;
    }
    */

   /*
    public void setCanJump(boolean canJump) { this.canJump = canJump; }
    */

    /**
     * Checks whether the player object can move
     */
    public boolean canMove() { return canMove; }

    /**
     * Sets the canMove flag indicating whether the player is capable of moving
     */
    public void setCanMove(boolean canMove) { this.canMove = canMove; }

    /**
     * Checks whether the player is jumping
     */
    public boolean isJumping()
    {
        return jump;
    }

    /**
     * Ckecks whether the player is falling (either from a jump or from walking to a tile with a lower height than the previous one)

     */
    public boolean isFalling()
    {
        return falling;
    }

    /**
     * Sets the player's inertia value (slide distance) on the x axis
     * Can be used to push the player object around
     */
    public void setXInertia(int xInertia)
    {
        this.xInertia = xInertia;
    }

    /**
     *   Sets the player's inertia value (slide distance) on the y axis
     *   Can be used to push the player object around
     */
    public void setYInertia(int yInertia)
    {
        this.yInertia = yInertia;
    }

    /**
     *   Returns the player's inertia value (slide distance) on the x axis
     */
    public int getXInertia()
    {
        return xInertia;
    }

    /**
     *   Returns the player's inertia value (slide distance) on the y axis
     */
    public int getYInertia()
    {
        return yInertia;
    }

  /*  public void setXInertiaTime(int xInertiaTime)
    {
        if(xInertiaTime > 0)
        this.xInertiaTime = xInertiaTime;
    }

    public void setYInertiaTime(int yInertiaTime)
    {
        if(yInertiaTime > 0)
        this.yInertiaTime = yInertiaTime;
    }

    public int getXInertiaTime()
    {
        return xInertiaTime;
    }

    public int getYInertiaTime()
    {
        return yInertiaTime;
    }*/

    /**
     * Returns the currently equipped weapon object
     * @return the weapon object bound to the player
     */
    public Weapon getWeapon()
    {
        return weapon;
    }

    /**
     * Checks whether the player is flipped (facing right) or not (facing left)
     */
    public boolean isFlipped()
    {
        return flip;
    }

    /**
     * Checks whether player has moved during the current update cycle
     */
    public boolean isMoving()
    {
        return moved;
    }

    /**
     * Returns the current stamina level
     * @return current stamina value as an integer
     */
    public int getStamina()
    {
        return stamina;
    }

    /**
     * Decreases the player's stamina value by the given (positive) amount
     * If the passed parameter is negative no change is made
     * @param amount integer value corresponding to the stamina to be subtracted
     */
    public void decreaseStamina(int amount)
    {
        if(stamina > 0)
            stamina -= amount;
    }
}
