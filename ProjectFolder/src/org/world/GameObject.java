package org.world;

import org.engine.GameLoop;
import org.engine.graphics.Animation;
import org.engine.graphics.AnimationInformation;
import org.engine.graphics.Animator;

import org.engine.graphics.Renderer;
import org.world.tiles.Tile;

/**
 *  Abstract class that sets the groundwork for the vast majority of objects making up a game world (level).
 *  Everything that can be interacted with during gameplay should be a descendant of this class.
 *  Each object is treated as a 3D box when it comes to collision calculations.
 */

public abstract class GameObject implements Comparable<GameObject>
{
    /**
     *  left/right coordinate inside the pseudo-3D game world.
     */
    public float x;

    /**
     *  Front/back coordinate inside the pseudo-3D game world.
     */
    public float y;

    /**
     *  Up/down coordinate inside the pseudo-3D game world.
     */
    public float z;

    /**
     * Reference to the object that this one last collided with, useful in various cases
     */
    protected GameObject previousCollision = null;

    /**
     *  Solid objects can take part in collisions, non-solid objects can be passed through by othero bjects
     */
    public boolean solid;

    /**
     *  Coordinate in the isometric system, used to identify which tile the game object is in
     */
    public int isoX;

    /**
     *  Coordinate in the isometric system, used to identify which tile the game object is in
     */
    public int isoY;

    /**
     *  Dimension of the game object on the x (left/right) axis, used for collision detection
     */
    public int xWidth;

    /**
     *  Dimension of the game object on the y(front\back) axis, used for collision detection
     */
    public int yWidth;

    /**
     *  Dimension of the game object on the z(up/down) axis, used for collision detection
     */
    public int zWidth;

    /**
     *  Used to mark objects for removal from the game world. Marked objects wil be removed during the next update cycle
     */
    public boolean remove = false;


   // public float depth;

    /**
     * Flag marks whether object should be drawn to screen or not
     */
    public boolean render = true;

    /**
     * Size of the sprite/texture, different from the size taken into account during collisions.
     * Used for rendering.
     */
    public int spriteWidth;

    /**
     * Size of the sprite/texture, different from the size taken into account during collisions.
     * Used for rendering.
     */
    public int spriteHeight;

    /**
     * Rotation of the object relative to the normal vertical(y) axis/
     * Values given in degrees/
     */
    public int rotation;

    /**
     * Object's animation array
     */
    public Animation[] animations;

    /**
     * Index for the current animation. Used for animation switching.
     */
    public int currentAnimation;

    /**
     * Index of the current frame in the current animation. Can be accessed for actions that require exact frame timing (such as attacks).
     */
    public int currentFrame;

    /**
     * Saves the previous animation, the game object needs to know when it's animation has changed in order to start from the first frame
     */
    protected int previousAnimation;

    /**
     * Saves the last time the current frame was changed. Used by {@link #playAnimation()};
     */
    private long lastFrameTime;

    /**
     * Relative path to the object's spritesheet/texture file. Example "/resources/player/player_anim.png"
     */
    protected String  spriteSheetPath = null;

    /**
     * Flag marks whether the object's sprite should be mirrored when rendered to the screen. Helps with orienting objects left/right
     */
    protected boolean flip = false;

    /**
     * Computes the euclidean distance between this object and another given one
     * @param go game object to calculate distance to
     * @return distance as a double
     */
    protected double distanceTo(GameObject go)
    {
        return Math.sqrt(Math.pow((go.x - this.x), 2) + Math.pow((go.y - this.y), 2) + Math.pow((go.z - this.z), 2));
    }

    /**
     * Computes the distance from this object to a given point in the game world
     * @param x coordinate  of the point on the x axis
     * @param y coordinate  of the point on the y axis
     * @param z coordinate  of the point on the z axis
     * @return distance as a double
     */
    protected double distanceTo(int x, int y, int z)
    {
        double actualX = (x + y) * Tile.GROUND_TILE_WIDTH / 2f;
        double actualY = (y - x) * Tile.GROUND_TILE_HEIGHT / 2f;
        double actualZ = this.z;
        return Math.sqrt(Math.pow((actualX - this.x), 2) + Math.pow((actualY - this.y), 2) + Math.pow((actualZ - this.z), 2));
    }

    /**
     * Attempts to load the animations from the Animator class
     * If no animation is present the reference is null after method call
     */
    protected void loadAnimations()
    {
        this.animations = Animator.getAnimation(this.getClass().getSimpleName());
        if(animations!=null)
        {
            spriteWidth = (int) animations[0].frames[0].getWidth();
            spriteHeight = (int) animations[0].frames[0].getHeight();
        }
    }

    /**
     * Attempt to create the object's animations via the {@link org.engine.graphics.Animator}'s
     * {@link org.engine.graphics.Animator#createAnimation(String, AnimationInformation[])} method.
     * Also sets the current object's animations after their creation.
     *
     * @param animationInfo animationInformation array created from the object's animation enum
     */
    protected void createAnimations(AnimationInformation[] animationInfo)
    {
        assert (spriteSheetPath != null);
        this.animations = Animator.createAnimation(spriteSheetPath,animationInfo);
        spriteWidth = (int) animations[0].frames[0].getWidth();
        spriteHeight = (int) animations[0].frames[0].getHeight();
    }

    /**
     * Attempt to create the object's texture via the {@link org.engine.graphics.Animator}'s
     * {@link org.engine.graphics.Animator#createSingleTexture(String)}  method.
     * Also sets the current object's animations after their creation.
     * Used for objects that only have one static texture.
     */
    protected void createTexture()
    {
        assert (spriteSheetPath != null);
        this.animations = Animator.createSingleTexture(spriteSheetPath);
        spriteWidth = (int) animations[0].frames[0].getWidth();
        spriteHeight = (int) animations[0].frames[0].getHeight();
    }

    /**
     * Increases the current frame when necessary depending on the animation's fps while accounting for it's loop property.
     * See {@link  org.engine.graphics.Animation}.
     */
    protected void playAnimation()
    {
        long currentTime = System.nanoTime();

        if(currentTime > lastFrameTime + 1000000000 / animations[currentAnimation].fps)
        {
            currentFrame++;
            if(currentFrame >= animations[currentAnimation].frames.length)
            {
                if(animations[currentAnimation].loop)
                    currentFrame = 0;
                else
                    currentFrame--;
            }
            lastFrameTime = currentTime;
        }
    }

    /**
     * Update method called each update cycle as long the object is still present in the game world
     * Implemented in subclasses according to their respective needs
     */
    public abstract void update();

    /**
     * This should be called for objects with more than 1 animation at the end of the update cycle.
     * It sets the currentFrame to 0 when the previous animation differs from the current one, then saves the current animation in
     * the previousAnimation variable for use next cycle.
     * Therefore it ensures that animations always start with the first frame,
     * and prevents array out of bounds errors in case of animations with different lengths
     */
    public void setFrame()
    {
        if (previousAnimation != currentAnimation)
        {
            currentFrame = 0;
            lastFrameTime = System.nanoTime();
        }

        previousAnimation = currentAnimation;
    }

    /**
     *  Checks for collisions with nearby game objects. Each solid object has a "box" around it
     *  defined by its x,y,and z dimensions. This function checks if they overlap
     *
     * @param exception game object to ignore when considering collisions, can be occasionally useful
     */
    public boolean checkCollision(GameObject exception)
    {
        for(GameObject go : GameLoop.getWorld().gameObjects)
        {
            if( !this.equals(go) && !go.equals(exception) && go.solid && distanceTo(go) <= 30 )
                {
                boolean xCollision = (this.x + this.xWidth  >= go.x - go.xWidth) && (go.x - go.xWidth >= this.x - this.xWidth) || (this.x - this.xWidth  >= go.x - go.xWidth) && (go.x + go.xWidth >= this.x - this.xWidth) ;
                boolean yCollision = (this.y >= go.y - go.yWidth) && (go.y - go.yWidth >= this.y - this.yWidth) || (this.y - this.yWidth  >= go.y - go.yWidth) && (go.y >= this.y - this.yWidth) ;
                boolean zCollision = (this.z <= go.z + go.zWidth) && (go.z + go.zWidth <= this.z + this.zWidth) || (this.z + this.zWidth  >= go.z) && (go.z + go.zWidth >= this.z + this.zWidth) ;

                if( xCollision && yCollision && zCollision)
                {
                    //report collision and save what the last collision was
                    previousCollision = go;
                    return true;
                }
            }
        }
        previousCollision = null;
        return false;
    }

    /**
     *  Renders the object when necessary, is overridden in some instances(ex. Player, Tiles) when more/different
     *  functionality is needed
     */
    public void render() throws ArrayIndexOutOfBoundsException
    {
        if(!render)
            return;

        Renderer.setRotation(rotation);
        Renderer.drawImage(animations[currentAnimation].getImage(currentFrame), spriteWidth, spriteHeight,x,y - Tile.GROUND_TILE_Z_HEIGHT * z - spriteHeight / 2f,flip);
        Renderer.setRotation(0);
        playAnimation();
    }

    /**
     * //comparison for depth sorting when rendering
     * @param go object to be compared to
     * @return greater than 0 if current object is behind go, 0 if they are at the same depth, less than 0 if the current object is in front
     */
    @Override
    public int compareTo(GameObject go)
    {
        if(this.y > go.y)
            return 1;

        if(this.y < go.y)
            return -1;

        return 0;
    }

}
