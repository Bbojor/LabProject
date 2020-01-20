package org.engine.ui;

import org.engine.GameLoop;
import org.engine.graphics.Renderer;
import org.engine.input.KeyInput;
import org.engine.resources.SoundClip;

/**
 *  The class implements the main menu of the game and represents the entry point of the application from the users's perspective.
 *  It groups together 3 menus:
 *  - The main menu proper which allows for starting a new game, accessing the settings and help sub-menus and quitting the application
 *  - The settings menu which currently only allows for changing the keyboard bindings
 *  - The help menu which should provide some explanation of the gameplay and various systems
 */

public class MainMenu
{
    /**
     * Flags that the system is waiting for a key press in order to set a key binding in the settings menu
     */
    private static boolean waitingForKey;
    /**
     *  Set when the settings were changed, tells the program to re-write the preferences.txt file
     */
    private static boolean settingsChanged;

    private static SoundClip menuMusic;

    private static Menu mainMenu,settingsMenu,helpMenu;

    /**
     * The states the menu van be in.
     */
    private enum STATES
    {
        MAIN,
        SETTINGS,
        HELP
    }

    private static STATES state = STATES.MAIN;

    static {init();}

    /**
     * Initializes the 3 menus with their respective options
     */
    public static void init()
    {
        menuMusic = new SoundClip("/resources/Music/ambience.wav");
        menuMusic.setVolume(-5f);

        //configure main menu buttons and actions
        mainMenu = new Menu(4);
        mainMenu.options[0] = new MenuOption("New Game", 0,-30)
                                    {
                                        void doAction()
                                        {
                                            //start game
                                            GameLoop.newGame();
                                            menuMusic.stop();
                                            GameLoop.setState(GameLoop.STATES.GAME);
                                        }
                                    };

        mainMenu.options[1] = new MenuOption("Settings", 0,0)
                                    {
                                        void doAction()
                                        {
                                            //open settings menu
                                            state = STATES.SETTINGS;
                                            KeyInput.clear(); // clear keys to ensure no selection carries on from the main menu
                                        }
                                    };

        mainMenu.options[2] = new MenuOption("Help", 0,30)
                                    {
                                        //opee help menu
                                        void doAction()
                                        {
                                            state = STATES.HELP;
                                        }
                                    };
        mainMenu.options[3] = new MenuOption("Exit Game", 0,60) {void doAction() { GameLoop.setState(GameLoop.STATES.EXIT); } };


        //configure settings menu buttons and actions
        settingsMenu = new Menu(7);

        //buttons used for selecting a key binding, set the waiting for key flag
        settingsMenu.options[0] = new MenuOption("UP:",-117,-40) {void doAction() {   waitingForKey = true; KeyInput.clear(); settingsChanged = true; } };
        settingsMenu.options[1] = new MenuOption("DOWN:",-117,-10) {void doAction() {   waitingForKey = true; KeyInput.clear(); settingsChanged = true; } };
        settingsMenu.options[2] = new MenuOption("LEFT:",-117, 20) {void doAction() {   waitingForKey = true; KeyInput.clear(); settingsChanged = true; } };
        settingsMenu.options[3] = new MenuOption("RIGHT:",-117,50) {void doAction() {   waitingForKey = true; KeyInput.clear(); settingsChanged = true; } };
        settingsMenu.options[4] = new MenuOption("JUMP/DODGE:",50,-40) {void doAction() {   waitingForKey = true; KeyInput.clear(); settingsChanged = true; } };
        settingsMenu.options[5] = new MenuOption("DRAW/SHEATHE:",50,-10) {void doAction() {   waitingForKey = true; KeyInput.clear(); settingsChanged = true; } };
        settingsMenu.options[6] = new MenuOption("BACK",0,70)
                                        {
                                            void doAction()
                                            {
                                                if(settingsChanged)
                                                    KeyInput.saveBindings();
                                                state = STATES.MAIN;
                                            }
                                        };

        helpMenu = new Menu(1);
        helpMenu.options[0] = new MenuOption("BACK",0,80)
                                    {
                                         void doAction()
                                         {
                                            state = STATES.MAIN;
                                         }
                                    };
    }

    public static void render()
    {
        if(state == STATES.MAIN)
        {
            Renderer.drawText("PERIHELION",Renderer.LARGE_FONT,0,60,255,255,255,255);

            mainMenu.render();

            Renderer.drawText("C @ Bojor Barbu, 2019",Renderer.SMALL_FONT,105,-88,255,255,255,255);
        }

        if(state == STATES.SETTINGS)
        {
            Renderer.drawText("SETTINGS",Renderer.LARGE_FONT,0,65,255,255,255,255);

            settingsMenu.render();

            String aux;
            String[] parts;

            //render currently set key bindings
           if(settingsMenu.getCurrentOption() != 0 || !waitingForKey)
            {
                aux = Character.getName(KeyInput.UP);
                parts = aux.split(" ");
                Renderer.drawText(parts[parts.length-1],Renderer.SMALL_FONT,-87,37,255,255,255,255);
            }

            if(settingsMenu.getCurrentOption() != 1 || !waitingForKey)
            {
                aux = Character.getName(KeyInput.DOWN);
                parts = aux.split(" ");
                Renderer.drawText(parts[parts.length - 1], Renderer.SMALL_FONT, -87, 7, 255, 255, 255, 255);
            }

            if(settingsMenu.getCurrentOption() != 2 || !waitingForKey)
            {
                aux = Character.getName(KeyInput.LEFT);
                parts = aux.split(" ");
                Renderer.drawText(parts[parts.length - 1], Renderer.SMALL_FONT, -87, -23, 255, 255, 255, 255);
            }

            if(settingsMenu.getCurrentOption() != 3 || !waitingForKey)
            {
                aux = Character.getName(KeyInput.RIGHT);
                parts = aux.split(" ");
                Renderer.drawText(parts[parts.length - 1], Renderer.SMALL_FONT, -87, -53, 255, 255, 255, 255);
            }

            if(settingsMenu.getCurrentOption() != 4 || !waitingForKey)
            {
                aux = Character.getName(KeyInput.JUMP_DODGE);
                parts = aux.split(" ");
                Renderer.drawText(parts[parts.length - 1], Renderer.SMALL_FONT, 120, 37, 255, 255, 255, 255);
            }

            if(settingsMenu.getCurrentOption() != 5 || !waitingForKey)
            {
                aux = Character.getName(KeyInput.SHEATHE);
                parts = aux.split(" ");
                Renderer.drawText(parts[parts.length - 1], Renderer.SMALL_FONT, 120, 7, 255, 255, 255, 255);
            }
        }

        if(state == STATES.HELP)
        {
            //print a wall of text
            Renderer.drawText("The aim of the game is to survive for as long as",Renderer.SMALL_FONT,10,75,1,1,1,1);
            Renderer.drawText("possible against increasingly numerous waves",Renderer.SMALL_FONT,-5,55,1,1,1,1);
            Renderer.drawText("of enemies.",Renderer.SMALL_FONT,-120,35,1,1,1,1);
            Renderer.drawText("Use \"UP\",\"DOWN\",\"LEFT\",\"RIGHT\" and \"JUMP\\",Renderer.SMALL_FONT,-15,15,1,1,1,1);
            Renderer.drawText("DODGE\" to move around the game map (see SETTINGS",Renderer.SMALL_FONT,10,-5,1,1,1,1);
            Renderer.drawText("for actual bindings). Left-click=light attack",Renderer.SMALL_FONT,-2,-25,1,1,1,1);
            Renderer.drawText("and Right-click=heavy attack. Combining them",Renderer.SMALL_FONT,-5,-45,1,1,1,1);
            Renderer.drawText("results in more complex attacks.",Renderer.SMALL_FONT,-45,-65,1,1,1,1);

            helpMenu.render();
        }

    }

    public static void update()
    {
        menuMusic.loop();

        switch (state)
        {
            case MAIN:  mainMenu.update();
            break;

            case SETTINGS: if(!waitingForKey) settingsMenu.update();
            break;

            case HELP: helpMenu.update();
            break;
        }

        if(waitingForKey && KeyInput.isKeyAvailable())
        {
            switch (settingsMenu.getCurrentOption())
            {
                case 0: KeyInput.UP = KeyInput.getLastKeyPressed();
                    waitingForKey = false;
                    return;

                case 1: KeyInput.DOWN = KeyInput.getLastKeyPressed();
                    waitingForKey = false;
                    return;

                case 2: KeyInput.LEFT = KeyInput.getLastKeyPressed();
                    waitingForKey = false;
                    return;

                case 3: KeyInput.RIGHT = KeyInput.getLastKeyPressed();
                    waitingForKey = false;
                    return;

                case 4: KeyInput.JUMP_DODGE = KeyInput.getLastKeyPressed();
                    waitingForKey = false;
                    return;

                case 5: KeyInput.SHEATHE = KeyInput.getLastKeyPressed();
                    waitingForKey = false;

            }
        }
    }
}
