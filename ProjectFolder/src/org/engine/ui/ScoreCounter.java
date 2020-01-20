package org.engine.ui;

import org.engine.GameLoop;
import org.engine.graphics.Renderer;
import org.world.GameObject;

/**
 *  The score counter element displays the current score the player has accumulated
 */

public class ScoreCounter extends GameObject
{

    public  ScoreCounter()
    {
        //attempt to load animations
        loadAnimations();
        spriteSheetPath = "/resources/player/score.png";

        System.out.println();

        //if no animations are already present create them
        if(animations == null)
            createTexture();
    }

    public void update()
    {
        x = Renderer.cameraX + 128;
        y = Renderer.cameraY - 78;
    }

    public void render()
    {
        super.render();
        Renderer.drawText(Integer.toString(GameLoop.getWorld().score), Renderer.SMALL_FONT,137 , 80,255,255,255,255);
    }
}
