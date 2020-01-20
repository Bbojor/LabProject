package org.engine;

import org.engine.graphics.Renderer;

import java.io.IOException;

public class Main
{
    public static void main(String []arg)
    {
        //load OpenGL libraries
        try
        {
            DLLLoader.run();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //initialize OpenGL
        try
        {
            Renderer.init();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }

        GameLoop.start();
    }
}
