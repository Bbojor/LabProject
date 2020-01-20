package org.world;

// This class implements a trigger for certain events once a condition is met
// it can be added to any game object present in the game world and will be processed by the World class once it is
// activated

public class Trigger
{

    //holds the type of trigger (ex: spawner, dialogue, cutscene, etc.)
    public String type;

    public boolean active;

    //spawn related stuff
    public GameObject spawnObjects[];

   //TO DO other functionalities

    public Trigger(String typ, GameObject[] objects)
    {
        type = typ;
        switch(type)
        {
            case "spawn": spawnObjects = objects;
                            break;
            default: break;
        }

    }

}
