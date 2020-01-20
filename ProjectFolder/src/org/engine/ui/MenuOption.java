package org.engine.ui;

import org.engine.graphics.Renderer;

/**
 *  This class holds the some information about a menu option such as the name, size and position of the option
 *  The functionality of the doAction() method is left to be defined by the user
 */
public class MenuOption
{
    String text;
    public int x,y;

    void renderBox()
    {
        Renderer.setColor(255,255,255,255);
        Renderer.fillRect(x + Renderer.cameraX ,y + Renderer.cameraY,text.length()*Renderer.SMALL_FONT_WIDTH + 4,17);
        Renderer.setColor(0,0,0,255);
        Renderer.fillRect(x + Renderer.cameraX ,y + Renderer.cameraY,text.length()*Renderer.SMALL_FONT_WIDTH + 2,15);
    }

    void render()
    {
        Renderer.drawText(text, Renderer.SMALL_FONT,x ,- y - 3,255,255,255,255);
    }

    public MenuOption(String text,int x,int y)
    {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    /**
     *  Method that should implement the functionality of the option
     */
    void doAction() { }
}
