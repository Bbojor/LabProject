package org.world.decoratioms;

import org.engine.GameLoop;
import org.engine.graphics.AnimationInformation;
import org.world.GameObject;
import org.world.tiles.Tile;

/**
 *  A non-solid decorative game object.
 *  Serves no gameplay purpose, only visual.
 *  Currently only supports one texture/animation.
 */
public class NonSolidDecoration extends GameObject
{

    /**
     *  Creates a non-solid decoration act the given isometric coordinates.
     *  In case the coordinates are invalid they are set to 0,0.
     * @param x isometric coordinate
     * @param y isometric coordinate
     * @param flip whether image should be flipped
     * @param animationFrames number of frames in the animation (if set to 1 it will attempt to load the whole image as a single texture)
     * @param animatioFps frames per second of the animation
     * @param spriteWidth dimension of a single image in the animation, only relevant when the animation has more than one frame
     * @param spriteHeight dimension of a single image in the animation, only relevant when the animation has more than one frame
     * @param spriteSheet relative path to the image file
     */
    public NonSolidDecoration(int x, int y, boolean flip,int animationFrames,int animatioFps,int spriteWidth,int spriteHeight, String spriteSheet)
    {

        if(!GameLoop.getWorld().inBounds(x,y))
            x = y =0;

        this.x = (x + y) * Tile.GROUND_TILE_WIDTH/2f;
        this.y = (y - x) * Tile.GROUND_TILE_HEIGHT/2f;
        this.flip = flip;

        this.solid = false;

        isoX = x;
        isoY = y;

        spriteSheetPath = spriteSheet;

        loadAnimations();
        if (animations == null)
        {
            if(animationFrames > 1)
            {
                AnimationInformation[] animationInfo = new AnimationInformation[1];
                animationInfo[0] = new AnimationInformation(animationFrames, animatioFps, spriteWidth, spriteHeight, true);
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
