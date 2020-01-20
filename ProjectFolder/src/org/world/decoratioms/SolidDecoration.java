package org.world.decoratioms;

import org.engine.GameLoop;
import org.engine.graphics.AnimationInformation;
import org.world.GameObject;
import org.world.tiles.Tile;

/**
 *  Solid decorative game object.
 *  Only serves as an obstacle to movement.
 *  Currently supports only one texture/animation.
 */
public class SolidDecoration extends GameObject
{

    /**
     * Creates a solid decoration object with given dimensions/texture.
     *  If the x/y coordinates are out of bounds they are set to 0,0.
     * @param x isometric coordinate
     * @param y isometric coordinate
     * @param flip whether the object should be flipped or not
     * @param xWidth width along the left/right axis
     * @param yWidth width along the front/back axis
     * @param zWidth width along the up/down axis
     * @param animationFrames number of frames in the animation (if left at one will attempt to load the file as a single texture)
     * @param animationFps frames per second of the animation
     * @param spriteWidth dimension of a single image in the animation, only relevant when the animation has more than one frame
     * @param spriteHeight dimension of a single image in the animation, only relevant when the animation has more than one frame
     * @param spritePath relative path to the image file
     */
    public SolidDecoration(int x, int y, boolean flip, int xWidth, int yWidth, int zWidth,int animationFrames,int animationFps,int spriteWidth,int spriteHeight,String spritePath)
    {
        if(!GameLoop.getWorld().inBounds(x,y))
            x = y =0;

        this.x = (x + y) * Tile.GROUND_TILE_WIDTH/2f;
        this.y = (y - x) * Tile.GROUND_TILE_HEIGHT/2f;
        this.flip = flip;

        if(xWidth < 0)
            xWidth = 0;

        if(yWidth < 0)
            yWidth = 0;

        if(zWidth < 0)
            zWidth = 0;

        this.xWidth = xWidth;
        this.yWidth = yWidth;
        this.zWidth = zWidth;
        this.solid = true;

        isoX = x;
        isoY = y;

        spriteSheetPath = spritePath;

        loadAnimations();
        if (animations == null)
        {
            if(animationFrames > 1)
            {
                AnimationInformation[] animationInfo = new AnimationInformation[1];
                animationInfo[0] = new AnimationInformation(animationFrames, animationFps, spriteWidth, spriteHeight, true);
                createAnimations(animationInfo);
            }
            else createTexture();
        }
    }

    /**
     * Does nothing
     */
    public void update()
    {}
}
