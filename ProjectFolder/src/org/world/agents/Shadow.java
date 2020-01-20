package org.world.agents;

import org.engine.GameLoop;
import org.engine.graphics.Animation;
import org.engine.resources.ImageResource;
import org.world.GameObject;

/**
 *  A class to implement simple shadows for game objects.
 *  A Shadow follows its parent object around when moving and adjusts it's size to be inversely proportional to the vertical distance between them.
 */
public class Shadow extends GameObject
{

    /**
     * Reference to the object whose shadow this is. All updates are dependant on this parent object.
     */
    GameObject parent;

    /**
     * Creates a shadow for the supplied parent object.
     * @param parent parent game object the shadow belongs to
     */
    public Shadow (GameObject parent)
    {
        animations = new Animation[1];

        animations[0] = new Animation();
        animations[0].frames = new ImageResource[1];
        animations[0].frames[0] = new ImageResource("/resources/player/shadow.png");

        spriteWidth = (int) animations[currentAnimation].getImage(currentFrame).getWidth();
        spriteHeight = (int) animations[currentAnimation].getImage(currentFrame).getHeight();

        this.parent = parent;
    }

    @Override
    public void update()
    {
        this.x = parent.x;
        this.y = parent.y + this.spriteHeight /2f;
        this.z = GameLoop.getWorld().getTerrain().getTiles()[parent.isoX][parent.isoY].z ;

        //make shadow size inversely proportional to distance to object
        spriteHeight = (int) (animations[currentAnimation].getImage(currentFrame).getHeight() / (1 + Math.abs(this.z - parent.z)));
        spriteWidth = (int) (animations[currentAnimation].getImage(currentFrame).getWidth()  / (1 + Math.abs(this.z - parent.z)));
    }

}
