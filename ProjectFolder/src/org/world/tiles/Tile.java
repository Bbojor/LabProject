package org.world.tiles;

import org.engine.graphics.Renderer;
import org.world.GameObject;

/**
 * This abstract class implements a basic ground tile object, which make up the playable area. Each tile has a certain texture,height
 * and traversable property(i.e. whether or not the player or other entities such as enemies can move across it). All tiles share the
 * same dimensions which take part in isometric computations.
 */

public abstract class Tile extends GameObject
{
    public boolean traversable;

    /**
     * Do nothing
     */
    public void update()
    {}

    /**
     *  Override the usual GameObject render method, needs a slightly different offset to render at correct location.
     *  If a tile is located at a height greater than 0, all the space below it will be drawn with the same texture to create the illusion of
     *  multiple stacked tiles.
     */
    @Override
    public void render()
    {
        Renderer.setRotation(rotation);
        if(z > 0)
            for(int i =0;i<z;i++)
                Renderer.drawImage(animations[currentAnimation].getImage(currentFrame), spriteWidth, spriteHeight, x, y - GROUND_TILE_Z_HEIGHT * i, flip);

        Renderer.drawImage(animations[currentAnimation].getImage(currentFrame), spriteWidth, spriteHeight, x, y - GROUND_TILE_Z_HEIGHT * z, flip);
        Renderer.setRotation(0);
    }

    /**
     *  constants used for computing isometric coordinates
     */
    public static final double SINE = Math.sin(Math.PI/4);
    public static final double COSINE = Math.cos(Math.PI/4);
    public static final int GROUND_TILE_WIDTH = 40;
    public static final int GROUND_TILE_HEIGHT = 20;
    public static final int GROUND_TILE_Z_HEIGHT = 20;
    public static final double OFFSET = GROUND_TILE_WIDTH/Math.sqrt(2);

}
