package org.world.tiles;

/**
 *  A tile with a high depth/low height and no texture, used for making empty spaces/holes in the playable area without having to deal
 *  with null tiles in the terrain.
 */
public class VoidTile extends Tile
{
    public VoidTile(int x, int y)
    {
        //compute coordinates
        this.x = (x + y) * GROUND_TILE_WIDTH/2f;
        this.y = (y - x + 1) * GROUND_TILE_HEIGHT/2f - GROUND_TILE_Z_HEIGHT * z;
        this.z = -100;
        this.traversable = false;
    }
}
