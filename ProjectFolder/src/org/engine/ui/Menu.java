package org.engine.ui;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import org.engine.input.KeyInput;
import org.engine.input.MouseInput;

/**
 *  The Menu class helps ease the creation of menus by grouping together all the given options in a menu and providing
 *  methods to update the current state of the menu and render it as a whole
 */
public class Menu
{
    /**
     * Index for the currently selected option
     */
    private int currentOption;

    /**
     * Total number of options held  by the menu
     */
    private int optionCount;
    MenuOption[] options;

    /**
     * Initializes a menu with an empty array of options
     * @param options
     */
    public Menu(int options)
    {
        this.optionCount = options;
        this.options = new MenuOption[options];
    }

    /**
     *  Update selection and do the relevant action when an option is chosen
     */
    public void update()
    {

        if(KeyInput.keyDown(KeyEvent.VK_W) || KeyInput.keyDown(KeyEvent.VK_UP))
        {
            currentOption--;
            if(currentOption < 0)
                currentOption = optionCount - 1;
        }

        if(KeyInput.keyDown(KeyEvent.VK_S) || KeyInput.keyDown(KeyEvent.VK_DOWN))
        {
            currentOption++;
            if(currentOption >= optionCount)
                currentOption = 0;
        }

        if(KeyInput.keyDown(KeyEvent.VK_SPACE) || KeyInput.keyDown(KeyEvent.VK_ENTER) || MouseInput.getMousePressed(MouseEvent.BUTTON1))
        {
            options[currentOption].doAction();
        }
    }

    /**
     *  Render all options and highlight currently selected one
     */
    public void render()
    {
        options[currentOption].renderBox();
        for(int i = 0;i < optionCount; i++)
        {
            options[i].render();
        }
    }

    /**
     * Gets the currently selected option
     * @return the index of the currently selected option as an integer
     */
    public int getCurrentOption()
    {
        return currentOption;
    }
}
