package org.engine.ui;

import org.engine.graphics.Renderer;
import org.world.GameObject;
import org.world.agents.player.Player;
import org.world.agents.player.Weapon;

/**
 *  The StatBars element displays the current health and stamina of the player as colored bars whose lengths are directly proportional
 *  to said values. Also provides some relevant information used for the {@link Weapon#lifesteal()} ;} ability.
 *
 */

public class StatBars extends GameObject
{
    private float currentHealthWidth;
    private float oldHealthWidth;
    private final float HEALTH_BAR_HEIGHT = 4;

    private float currentStaminaWidth;
    private float oldStaminaWidth;
    private final float STAMINA_BAR_HEIGHT = 3.3f;

    private final int TIME = 180;
    private int healthTimeToDisappear;
    private int staminaTimeToDisappear;

    static boolean canLifesteal;
    static int lifestealAmount;

    private Player owner;

    public  StatBars(Player player)
    {
        owner = player;

        //attempt to load animations
        loadAnimations();
        spriteSheetPath = "/resources/player/health_bar.png";

        System.out.println();

        //if no animations are already present create them
        if(animations == null)
            createTexture();
    }

    public void update()
    {
        if(healthTimeToDisappear == 0)
          oldHealthWidth = currentHealthWidth;

        if(staminaTimeToDisappear == 0)
            oldStaminaWidth = currentStaminaWidth;

        currentHealthWidth = owner.getHealth() * 82/100f;
        currentStaminaWidth = owner.getStamina() * 60/100f;


        //if player took damage show old health bar for a second
        if(healthTimeToDisappear == 0 && oldHealthWidth > currentHealthWidth)
        {
            healthTimeToDisappear = TIME;
            lifestealAmount = (int) (oldHealthWidth * 100/81 - currentHealthWidth * 100/81) ;
        }

        //if player used stamina show old stamina bar for a second
        if(staminaTimeToDisappear == 0 && oldStaminaWidth > currentStaminaWidth)
        {
            staminaTimeToDisappear = TIME;
        }

        canLifesteal = healthTimeToDisappear > TIME/4f;

        x = Renderer.cameraX - 110;
        y = Renderer.cameraY - 70;

        if(healthTimeToDisappear > 0)
            healthTimeToDisappear--;

        if(staminaTimeToDisappear > 0)
            staminaTimeToDisappear--;

    }

   public void render()
   {

       super.render();

       //render previous health bar in yellow behind the current one when taking damage, make it slowly deplete to current health level
       if(healthTimeToDisappear > 0 )
       {
           if( oldHealthWidth > 0)
           {
               float oldX = Renderer.cameraX - 145f + oldHealthWidth / 2;

               Renderer.setColor(255,255,0,255);
               Renderer.fillRect(oldX,y - 15, oldHealthWidth, HEALTH_BAR_HEIGHT);
               Renderer.setColor(255,255,255,255);

               if(healthTimeToDisappear <= TIME/4) //shrink gradually
                   oldHealthWidth -= (oldHealthWidth - currentHealthWidth)/4f;
           }

       }

       //render previous stamina bar in yellow behind the current one when performing an action which takes stamina, make it slowly deplete to current stamina level
       if(staminaTimeToDisappear > 0)
       {
           if(  oldStaminaWidth > 0)
           {
               float oldX = Renderer.cameraX - 147f + oldStaminaWidth / 2;

               Renderer.setColor(255, 255, 0, 255);
               Renderer.fillRect(oldX, y - 4.5f, oldStaminaWidth, STAMINA_BAR_HEIGHT);
               Renderer.setColor(255, 255, 255, 255);

               if (staminaTimeToDisappear <= TIME / 4) //shrink gradually
                   oldStaminaWidth -= (oldStaminaWidth - currentStaminaWidth) / 4f;
           }
       }

       if(currentHealthWidth > 0)
       {
           Renderer.setColor(102,0,254,255);
           Renderer.fillRect(Renderer.cameraX - 145f + currentHealthWidth / 2,y - 15, currentHealthWidth, HEALTH_BAR_HEIGHT);
       }

       if(currentStaminaWidth > 0)
       {
           Renderer.setColor(0,153,90,255);
           Renderer.fillRect(Renderer.cameraX - 147f + currentStaminaWidth / 2,y - 4.5f, currentStaminaWidth, STAMINA_BAR_HEIGHT);
       }
       Renderer.setColor(255,255,255,255);
   }

    /**
     *  Returns the health value the player had in the previous update cycle
     * @return old health value as a float
     */
    public float getOldHealth()
    {
        return oldHealthWidth * 100/81;
    }

    /**
     *  Checks whether the player is capable of lifestealing (i.e. gaining back health when striking an enemy)
     * @return boolean value
     */
    public static boolean canLifesteal()
    {
        return canLifesteal;
    }

    /**
     *  Returns the maximum amount of health the player can gain back (based on the previous and current health values)
     * @return health amount as an integer
     */
    public static int getLifestealAmount()
    {
        return lifestealAmount;
    }
}
