package org.engine.ui;

import com.jogamp.newt.event.KeyEvent;
import org.engine.GameLoop;
import org.engine.graphics.Renderer;
import org.engine.input.KeyInput;
import org.world.agents.player.Player;

/**
 *  The HUD (Heads-Up Display) represents the in-game menu providing information to the player during gameplay.
 *  It consists of 3 parts:
 *      - The status bars which convey information about the players current health and stamina levels;
 *      - The score counter which keeps track of the score accumulated by the player;
 *      - The pause menu which, as the name implies, lets the player pause the game, exit to the main menu or quit the game;
 *  This class also displays the number of enemies currently in the game world and handles returning to the main menu on player death.
 */

public class HUD
{
    private StatBars stats;
    private ScoreCounter score;
    private Player owner;
    private Menu menu;

    public HUD(Player p)
    {
        owner = p;
        stats = new StatBars(p);
        score = new ScoreCounter();
        menu = new Menu(3);
        menu.options[0] = new MenuOption("Resume", 0,-30){ void doAction(){ GameLoop.getWorld().togglePause(); }};
        menu.options[1] = new MenuOption("Exit to menu", 0,0) { void doAction() {GameLoop.setState(GameLoop.STATES.MENU);}};
        menu.options[2] = new MenuOption("Quit game", 0,30){ void doAction() {GameLoop.setState(GameLoop.STATES.EXIT);}};
    }

    public void update()
    {
        if(!GameLoop.getWorld().isPaused())
        {
            stats.update();
            score.update();
        }
        else
            menu.update();
       
        if(owner.getHealth() <= 0 )
        {
            if(KeyInput.keyDown(KeyEvent.VK_SPACE))
            {
                GameLoop.setState(GameLoop.STATES.MENU);
            }
        }
    }

    public void render()
    {
        if(owner.getHealth() <= 0)
        {
            Renderer.drawText("YOU DIED!", Renderer.MEDIUM_FONT,  0 ,0,255,255,255,255);
            Renderer.drawText("Press SPACE to continue", Renderer.MEDIUM_FONT, 0, - 30,255,255,255,255);
        }

        Renderer.drawText(Integer.toString(GameLoop.getWorld().enemyCount),Renderer.SMALL_FONT,0,80,255,255,255,255);

        if(GameLoop.getWorld().getTime() > 3/4f * GameLoop.getWorld().WAVE_DELAY && GameLoop.getWorld().getTime() < GameLoop.getWorld().WAVE_DELAY )
            Renderer.drawText("WAVE " + GameLoop.getWorld().getWave(),Renderer.SMALL_FONT,0,30,255,255,255,255);


        if(GameLoop.getWorld().isPaused())
            menu.render();

        stats.render();
        score.render();
    }

    /**
     * @return The Status bar object
     */
    public  StatBars getStats()
    {
        return stats;
    }
}
