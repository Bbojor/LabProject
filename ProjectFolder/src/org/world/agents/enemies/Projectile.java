package org.world.agents.enemies;

import org.engine.GameLoop;
import org.engine.graphics.Animation;
import org.engine.graphics.Renderer;
import org.world.agents.Shadow;
import org.world.GameObject;

public class Projectile extends GameObject
{
    org.world.agents.Shadow shadow = new Shadow(this);

    private int damage = 10;

    private float destinationX, destinationY, destinationZ;
    private float stepX, stepY, stepZ;
    private final float speed = 2f;
    private boolean collision ;

    public Projectile(float x, float y, float z,float destX,float destY, float flightHeight, float destZ,Animation[] anim)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        destinationX = destX;
        destinationY = destY;
        destinationZ = destZ;

        double distance = distanceTo(GameLoop.getWorld().getPlayer());
        stepX = (destinationX  -this.x)/(float)distance;
        stepY = (destinationY - this.y + flightHeight)/(float)distance;
        stepZ = (destinationZ - this.z)/(float)distance;

        animations = anim;

    }

    public void update()
    {

        if(collision)
        {
            currentAnimation = 1;
            if(currentFrame == 3)
            {
                render = false;
                remove = true;
            }
            return;
        }
        else
        if(distanceTo(GameLoop.getWorld().getPlayer()) <= 10)
        {
            collision = true;
            GameLoop.getWorld().getPlayer().getDamaged(damage);
         //   depth = Player.playerDepth + 0.1f;
            return;
        }
        else
            currentAnimation = 0;

            x += stepX  * speed;
            y += stepY  * speed;
            z += stepZ  * speed;

        int isoX = Renderer.IsoCoordinates(x,y)[0];
        int isoY = Renderer.IsoCoordinates(x,y)[1];

        if(isoX < 0 || isoY < 0 || isoY >= GameLoop.getWorld().getTerrain().getHeight() || isoX >= GameLoop.getWorld().getTerrain().getWidth())
        {
            collision = true;
            return;
        }

       shadow.update();
    }

    public void render()
    {
        this.spriteHeight = (int) animations[currentAnimation].frames[0].getHeight();
        this.spriteWidth = (int) animations[currentAnimation].frames[0].getWidth();
        super.render();
        shadow.render();
    }
}
