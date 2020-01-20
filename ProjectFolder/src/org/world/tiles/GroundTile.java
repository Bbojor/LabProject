package org.world.tiles;

import org.engine.GameLoop;

/**
 *  Simple traversable ground tile with no animations (only a static image)
 */

public class GroundTile extends Tile
{

    /**
     * Constructor for a GroundTile object. If the supplied coordinates are invalid they will be set to 0,0;
     * @param x isometric coordinate on the x axis, doubles as first index in the terrain matrix
     * @param y isometric coordinate on the y axis, doubles as second index in the terrain matrix
     * @param height height at which the tile is placed
     * @param texture texture to be used, given as the texture file's name, without the format;
     *                file should be of .png type and placed inside the /resources/Tiles/ folder
     */
    public GroundTile(int x, int y, float height, String texture)
    {
        //compute coordinates
        if(!GameLoop.getWorld().inBounds(x,y))
        {
            x = y = 0;
        }
        this.x = (x + y) * GROUND_TILE_WIDTH/2f;
        this.y = (y - x + 1) * GROUND_TILE_HEIGHT/2f - GROUND_TILE_Z_HEIGHT * z;
        this.z = height;
        this.traversable = true;

        spriteSheetPath = "/resources/Tiles/" + texture + ".png";

        loadAnimations();
        if(animations == null)
            createTexture();
    }

}
