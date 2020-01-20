package org.world;

import org.world.tiles.GroundTile;
import org.world.tiles.Tile;

import java.util.Random;

/**
 *  This class holds an array of Tile objects as well as information about the size of the playable area int the current world (level)
 */
public class Terrain
{
    //dimensions of map
    private static int height;
    private  static int width;

    //holds tiles
    private  Tile[][] tiles;

    /**
     *  Creates a terrain with give width and height and a tile array of default ground tiles at in-game height 0
     * @param h height of the terrain matrix
     * @param w width of the terrain matrix
     */
    public Terrain(int h, int w)
    {
        height = h;
        width = w;
        tiles = new Tile[h][w];

    }

    public void init()
    {
        for(int i = 0; i<height;i++)
            for(int j = 0;j<width;j++)
            {
                if(j == 0)
                    tiles[i][j] = new GroundTile(i,j,3,"cliff");
                else if(i == width - 1)
                    tiles[i][j] = new GroundTile(i,j,3,"cliff");
                else
                {
                    Random rand = new Random();
                    if(rand.nextInt(10) > 8)
                        tiles[i][j] = new GroundTile(i,j,0,"cliff");
                    else
                        tiles[i][j] = new GroundTile(i,j,0,"dirt");
                }
            }
    }

    /**
     * Calls the rendering function for each individual tile, in the order of their depth (so as to render the furthest tiles first)
     */
    public void render()
    {
        for (int i = height- 1; i >= 0; i--)
            for(int j = 0; j < width; j++)
        {
            if(tiles[i][j] != null)
            tiles[i][j].render();
        }
    }

    /**
     * Returns the tile matrix
     * @return a 2d Tile object array
     */
    public Tile[][] getTiles()
    {
        return tiles;
    }

    /**
     * @return the height of the tiles matrix
     */
    public  int getHeight() { return height; }

    /**
     * @return the width of the tile matrix
     */
    public  int getWidth() { return width; }
}
