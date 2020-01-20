package org.world.agents.pickups;

import org.engine.GameLoop;
import org.world.GameObject;
import org.world.agents.Shadow;

/**
 *  This class implements a simple pickup object. Pickups are objects that, as the name implies, can be picked up by
 *  the player when close enough to them. Upon pickup they call their pickup method which should then implement the actual
 *  desired effect the pickup should have omn the player/world.
 */
public abstract class Pickup extends GameObject
{

    protected Shadow shadow;

    @Override
    public void update()
    {
        if(render)
        if(this.distanceTo(GameLoop.getWorld().getPlayer()) < 20)
            pickedUp();
    }

    public void pickedUp()
    {

    }
}
