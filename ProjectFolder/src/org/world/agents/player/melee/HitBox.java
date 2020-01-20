package org.world.agents.player.melee;

import org.engine.graphics.Renderer;
import org.world.agents.player.Weapon;

/**
 *  The HitBox class hold information about the position/orientation of the weapon during attacks as well as whether it is dealing
 *  damage in a specific animation frame. While enemies check for the distance/orientation relative to the player when checking for damage
 *  that is not entirely accurate, especially during longer attacks/attacks that cover a  large area.
 *  This class allows for fine-tuning the exact location/time that the weapon is in during each frame of the attack by holding an array of 9 damage points
 *  per frame of attack and an array of booleans for the whole attack which mark the frames of the animation that should damage enemies.
 *  In case an enemy is close enough to the player to be reasonably hit, it then checks the hitbox parameters in order to compute whether it was hit or not.
 *  Only if it is touched by any of the damage points corresponding to the current attack animation/frame does it register the hit.
 */
public class HitBox
{
    //constants
    /**
     * 3D array which holds the coordinates for each of the 9 damage points, for each frame damaging of each attack
     * The order index is attack, x/y coordinates for each point (9 points x 2 = 18), frame
     * Each attack animation is assumed to have a maximum of 9 frames
     */
    private int[][][] hitBoxParameters = new int[7][18][9];

    /**
     *  Array that marks in which frames the weapon is actually doing damage
     */
    private boolean[][] damageFrame = new boolean[7][9];

    //actual coordinates of the points checked for hit detection
    /**
     * Actual horizontal coordinates used in hit detection. Computed based on weapon's position and the point coordinates for the current animation/frame
     */
    private int[] currentXes = new int[9];
    /**
     * Actual vertical coordinates used in hit detection. Computed based on weapon's position and the point coordinates for the current animation/frame
     */
    private int[] currentYs = new int[9];

    private Weapon weapon;

    public HitBox(Weapon weapon)
    {
        this.weapon = weapon;
        //============================// LIGHT ATTACK 1 //=========================//
        // x0 / y0
        hitBoxParameters[0][0] =  new int[] {-42, -8, 31, 40, 40,  6,-28,-36,-26};
        hitBoxParameters[0][1] =  new int[] {  6, 16, -5,-33,-57,-55,-48,  3, -5};
        // x1 / y1
        hitBoxParameters[0][2] =  new int[] {-28, -3, 22, 60, 39,  6,-20,-26,-26};
        hitBoxParameters[0][3] =  new int[] { -6, 12, -9,-34,-50,-45,-34, -6, -5};
        // x2 / y2
        hitBoxParameters[0][4] =  new int[] {-35,-13, 27, 54, 32, 11,-17,-33,-26};
        hitBoxParameters[0][5] =  new int[] { -6, 12,-12,-28,-55,-48,-44, -6, -5};
        // x3 / y3
        hitBoxParameters[0][6] =  new int[] {-29, -3, 22, 54, 28,  2,-26,-27,-26};
        hitBoxParameters[0][7] =  new int[] {  1,  8,-18,-39,-50,-48,-40,  1, -5};
        // x4 / y4
        hitBoxParameters[0][8] =  new int[] {-20,-16, 12, 30, 34,  3,-13,-18,-26};
        hitBoxParameters[0][9] =  new int[] { -7,  8,-13,-40,-44,-35,-40, -7, -5};
        // x5 / y5
        hitBoxParameters[0][10] = new int[] {-28, -8, 17, 30, 32, 12,-15,-26,-26};
        hitBoxParameters[0][11] = new int[] {-14,  6,-16,-26,-40,-35,-25,-14, -5};
        // x6 / y6
        hitBoxParameters[0][12] = new int[] {-14, -8, 17, 42, 23,  0, -5,-12,-26};
        hitBoxParameters[0][13] = new int[] { -9, -1,-25,-26,-48,-29,-38, -9, -5};
        // x7 / y7
        hitBoxParameters[0][14] = new int[] {-25,-17,  7, 42, 29, 12,-14,-23,-26};
        hitBoxParameters[0][15] = new int[] {-16,  1,-18,-40,-34,-29,-34,-21, -5};
        // x8 / y8
        hitBoxParameters[0][16] = new int[] {-21, -1, 13, 30, 16,  6, -7,-19,-26};
        hitBoxParameters[0][17] = new int[] {-13,  1,-20,-33,-44,-29,-27,-13, -5};
        //hitframes
        damageFrame[0] = new boolean[] {false,true,true,true,true,true,true,true,false};

        //================// LIGHT ATTACK 2 //======================================//
        // x0 / y0
        hitBoxParameters[1][0] = new int[] {-36,-28,  6, 40, 40, 31, -8,-42,-36};
        hitBoxParameters[1][1] = new int[] {  3,-48,-55,-57,-33, -5, 16,  6,100};
        // x1 / y1
        hitBoxParameters[1][2] = new int[] {-26,-20,  6, 39, 60, 22, -3,-28,-36};
        hitBoxParameters[1][3] = new int[] { -6,-34,-45,-50,-34, -9, 12, -6,100};
        // x2 / y2
        hitBoxParameters[1][4] = new int[] {-33,-17, 11, 32, 49, 27,-13,-35,-36};
        hitBoxParameters[1][5] = new int[] { -6,-44,-48,-55,-28,-12, 12, -6,100};
        // x3 / y3
        hitBoxParameters[1][6] = new int[] {-27,-26,  2, 28, 49, 22, -3,-29,-36};
        hitBoxParameters[1][7] = new int[] {  1,-40,-48,-50,-39,-18,  8,  1,100};
        // x4 / y4
        hitBoxParameters[1][8] = new int[] {-18,-13,  3, 34, 30, 12,-16,-20,-36};
        hitBoxParameters[1][9] = new int[] { -7,-40,-35,-44,-40,-13,  8, -7,100};
        // x5 / y5
        hitBoxParameters[1][10] = new int[] {-26,-15, 12, 32, 30, 17, -8,-28,-36};
        hitBoxParameters[1][11] = new int[] {-14,-25,-35,-40,-26,-16,  6,-14,100};
        // x6 / y6
        hitBoxParameters[1][12] = new int[] {-12, -5,  0, 23, 37, 17, -8,-14,-26};
        hitBoxParameters[1][13] = new int[] { -9,-38,-29,-48,-26,-25, -1, -9,500};
        // x7 / y7
        hitBoxParameters[1][14] = new int[] {-23,-14, 12, 29, 37,  7,-17,-25,-26};
        hitBoxParameters[1][15] = new int[] {-21,-34,-29,-34,-40,-18,  1,-17,500};
        // x8 / y8
        hitBoxParameters[1][16] = new int[] {-19, -7,  6, 16, 30, 13, -1,-21,-26};
        hitBoxParameters[1][17] = new int[] {-13,-27,-29,-44,-33,-20,  1,-13,500};
        //hitframes
        damageFrame[1] = new boolean[] {false,true,true,true,true,true,true,true,false};

        //============================// HEAVY ATTACK 1 //===================================//
        // x0/ y0
        hitBoxParameters[2][0] =  new int[] {-30,  0, 30, 60, 35, 55, 60, 60, 60};
        hitBoxParameters[2][1] =  new int[] {  5, 20,  5,  0,-12,-12,-14,-16,-20};
        // x1 / y1
        hitBoxParameters[2][2] =  new int[] {-30,  0, 30, 60, 15, 35, 45, 40, 45};
        hitBoxParameters[2][3] =  new int[] {  5, 20,  5,  0,-12,-12,-20,-24,-14};
        // x2 / y2
        hitBoxParameters[2][4] =  new int[] {-30,  0, 30, 60, 25, 45, 35, 50, 55};
        hitBoxParameters[2][5] =  new int[] {  5, 20,  5,  0,-18,-18,-20,-18,-24};
        // x3 / y3
        hitBoxParameters[2][6] =  new int[] {-30,  0, 30, 60, 25, 45, 52, 50, 55};
        hitBoxParameters[2][7] =  new int[] {  5, 20,  5,  0, -8, -8,-14,-12,-16};
        // x4 / y4
        hitBoxParameters[2][8] =  new int[] {-30,  0, 30, 60, 15, 30, 35, 40, 45};
        hitBoxParameters[2][9] =  new int[] {  5, 20,  5,  0,-18,-18,-10,-24,-26};
        // x5 / y5
        hitBoxParameters[2][10] = new int[] {-30,  0, 30, 60,  5, 18, 30, 30, 30};
        hitBoxParameters[2][11] = new int[] {  5, 20,  5,  0,-12, -8,-150,-18,-14};
        // x6 / y6
        hitBoxParameters[2][12] = new int[] {-12, -5,  0, 23,  5, 18, 15, 30, 30};
        hitBoxParameters[2][13] = new int[] { -9,-38,-29,-48,-20,-20,-21,-24,-26};
        // x7 / y7
        hitBoxParameters[2][14] = new int[] {-23,-14, 12, 29,  5, 25, 15, 30, 45};
        hitBoxParameters[2][15] = new int[] {-21,-34,-29,-34, -7,-12, -9, -12,-20};
        // x8 / y8
        hitBoxParameters[2][16] = new int[] {-19, -7,  6, 16, 15, 35, 45, 40, 35};
        hitBoxParameters[2][17] = new int[] {-13,-27,-29,-44, -8, -8,-10, -12,-20};
        //hitframes
        damageFrame[2] = new boolean[] {false,false,false,false,true,true,true,true,true};

        //===========================// HEAVY ATTACK 2 //===================================//
        // x0 / y0
        hitBoxParameters[3][0] =  new int[] {-30,  0, 30, 60, 30,  0, 46, 55, 55};
        hitBoxParameters[3][1] =  new int[] {  5, 20,  5,  0, -5,-20,-42, 20, 25};
        // x1 / y1
        hitBoxParameters[3][2] =  new int[] {-30,  0, 30, 60, 30,  0, 40, 45, 45};
        hitBoxParameters[3][3] =  new int[] {  5, 20,  5,  0, -5,-20,-30, 17, 22};
        // x2 / y2
        hitBoxParameters[3][4] =  new int[] {-30,  0, 30, 60, 30,  0, 44, 35, 35};
        hitBoxParameters[3][5] =  new int[] {  5, 20,  5,  0, -5,-20,-38, 14, 19};
        // x3 / y3
        hitBoxParameters[3][6] =  new int[] {-30,  0, 30, 60, 30,  0, 35, 25, 25};
        hitBoxParameters[3][7] =  new int[] {  5, 20,  5,  0, -5,-20,-20, 11, 16};
        // x4 / y4
        hitBoxParameters[3][8] =  new int[] {-30,  0, 30, 60, 30,  0, 27, 45, 45};
        hitBoxParameters[3][9] =  new int[] {  5, 20,  5,  0, -5,-20,-10, 12, 17};
        // x5 / y5
        hitBoxParameters[3][10] = new int[] {-30,  0, 30, 60, 30,  0, 37, 35, 45};
        hitBoxParameters[3][11] = new int[] {  5, 20,  5,  0, -5,-20,-35, 24, 29};
        // x6 / y6
        hitBoxParameters[3][12] = new int[] {-12, -5,  0, 23,  5, 18, 20, 25, 30};
        hitBoxParameters[3][13] = new int[] { -9,-38,-29,-48,-20,-20,-15, 24, 29};
        // x7 / y7
        hitBoxParameters[3][14] = new int[] {-23,-14, 12, 29,  5, 25, 27, 40, 45};
        hitBoxParameters[3][15] = new int[] {-21,-34,-29,-34, -7,-12,-35, 25, 30};
        // x8 / y8
        hitBoxParameters[3][16] = new int[] {-19, -7,  6, 16, 15, 35, 35, 30, 35};
        hitBoxParameters[3][17] = new int[] {-13,-27,-29,-44, -8, -8,-42, 25, 30};
        //hitframes
        damageFrame[3] = new boolean[] {false,false,false,false,false,false,true,true,true};

        //===========================// HEAVY COMBO 1 //===================================//
        // x0 / y0
        hitBoxParameters[4][0] =  new int[] {-50,-57,-37,-37, 30,  0, 34, 44, 44};
        hitBoxParameters[4][1] =  new int[] {  5,-15,-55,-55, -5,-20, 12, 18, 18};
        // x1 / y1
        hitBoxParameters[4][2] =  new int[] {-50,-37,-27,-27, 30,  0, 27, 37, 37};
        hitBoxParameters[4][3] =  new int[] {  5,-15,-45,-45, -5,-20,  1,  7,  7};
        // x2 / y2
        hitBoxParameters[4][4] =  new int[] {-50,-17,-17,-17, 30,  0, 27, 37, 37};
        hitBoxParameters[4][5] =  new int[] {  5,-15,-35,-35, -5,-20, 15, 21, 21};
        // x3 / y3
        hitBoxParameters[4][6] =  new int[] {-50,-47,-37,-37, 30,  0, 20, 30, 30};
        hitBoxParameters[4][7] =  new int[] {  5,-20,-45,-45, -5,-20, -2,  4,  4};
        // x4 / y4
        hitBoxParameters[4][8] =  new int[] {-50,-47,-23,-23, 30,  0, 20, 30, 30};
        hitBoxParameters[4][9] =  new int[] {  5,-10,-50,-50, -5,-20,  7, 13, 13};
        // x5 / y5
        hitBoxParameters[4][10] = new int[] {-50,-30,-15,-15, 30,  0, 10, 20, 20};
        hitBoxParameters[4][11] = new int[] {  5,-10,-40,-40, -5,-20, -5,  1,  1};
        // x6 / y6
        hitBoxParameters[4][12] = new int[] {-12,-30,-25,-25,  5, 18, 10, 20, 20};
        hitBoxParameters[4][13] = new int[] {  9,-20,-35,-35,-20,-20,  5, 11, 11};
        // x7 / y7
        hitBoxParameters[4][14] = new int[] {-23,-17,-20,-20,  5, 25, 10, 20, 20};
        hitBoxParameters[4][15] = new int[] { 21,-24,-25,-25, -7,-12,-12, -6, -6};
        // x8 / y8
        hitBoxParameters[4][16] = new int[] {-19,-17,-10,-10, 15, 35,  5, 15, 15};
        hitBoxParameters[4][17] = new int[] {-13, -6,-35,-35, -8, -8, -6,  0,  0};
        //hitframes
        damageFrame[4] = new boolean[] {false,true,true,true,false,false,true,true,true};

        //===========================// HEAVY COMBO 2 //===================================//
        // x0 / y0
        hitBoxParameters[5][0] =  new int[] {-50,-37,-27,-27, 30,  0, 40, 44, 44};
        hitBoxParameters[5][1] =  new int[] {  5,  5,-45,-45, -5,-20, 22, 33, 33};
        // x1 / y1
        hitBoxParameters[5][2] =  new int[] {-50,-32,-17,-17, 30,  0, 37, 37, 37};
        hitBoxParameters[5][3] =  new int[] {  5,  2,-35,-35, -5,-20, 11, 22, 22};
        // x2 / y2
        hitBoxParameters[5][4] =  new int[] {-50, -7, -7, -7, 30,  0, 37, 37, 37};
        hitBoxParameters[5][5] =  new int[] {  5,  5,-25,-25, -5,-20, 25, 36, 36};
        // x3 / y3
        hitBoxParameters[5][6] =  new int[] {-50,-32,-27,-27, 30,  0, 30, 30, 30};
        hitBoxParameters[5][7] =  new int[] {  5,  0,-35,-35, -5,-20,  8, 19, 19};
        // x4 / y4
        hitBoxParameters[5][8] =  new int[] {-50,-37,-13,-13, 30,  0, 30, 30, 30};
        hitBoxParameters[5][9] =  new int[] {  5, 10,-40,-40, -5,-20, 17, 28, 28};
        // x5 / y5
        hitBoxParameters[5][10] = new int[] {-50,-20, -5, -5, 30,  0, 20, 20, 20};
        hitBoxParameters[5][11] = new int[] {  5, 10,-30,-30, -5,-20,  5, 16, 16};
        // x6 / y6
        hitBoxParameters[5][12] = new int[] {-12,-20,-15,-15,  5, 18, 20, 20, 20};
        hitBoxParameters[5][13] = new int[] {  9,  0,-25,-25,-20,-20, 15, 26, 26};
        // x7 / y7
        hitBoxParameters[5][14] = new int[] {-23, -7,-10,-10,  5, 25, 20, 20, 20};
        hitBoxParameters[5][15] = new int[] { 21, -4,-15,-15, -7,-12,  8,  9,  9};
        // x8 / y8
        hitBoxParameters[5][16] = new int[] {-19, -7,  0,  0, 15, 35, 15, 15, 15};
        hitBoxParameters[5][17] = new int[] {-13, 14,-25,-25, -8, -8,  4, 15, 15};
        //hitframes
        damageFrame[5] = new boolean[] {false,true,true,true,false,false,true,true,true};

        //===============================// WHIRL //===================================//
        // x0 / y0
        hitBoxParameters[6][0] =  new int[] {-42, -8, 31, 40, 40,  6,-28,-36,-26};
        hitBoxParameters[6][1] =  new int[] {  6, 16, -5,-33,-57,-55,-48,  3, -5};
        // x1 / y1
        hitBoxParameters[6][2] =  new int[] {-28, -3, 22, 60, 39,  6,-20,-26,-26};
        hitBoxParameters[6][3] =  new int[] { -6, 12, -9,-34,-50,-45,-34, -6, -5};
        // x2 / y2
        hitBoxParameters[6][4] =  new int[] {-35,-13, 27, 54, 32, 11,-17,-33,-26};
        hitBoxParameters[6][5] =  new int[] { -6, 12,-12,-28,-55,-48,-44, -6, -5};
        // x3 / y3
        hitBoxParameters[6][6] =  new int[] {-29, -3, 22, 54, 28,  2,-26,-27,-26};
        hitBoxParameters[6][7] =  new int[] {  1,  8,-18,-39,-50,-48,-40,  1, -5};
        // x4 / y4
        hitBoxParameters[6][8] =  new int[] {-20,-16, 12, 30, 34,  3,-13,-18,-26};
        hitBoxParameters[6][9] =  new int[] { -7,  8,-13,-40,-44,-35,-40, -7, -5};
        // x5 / y5
        hitBoxParameters[6][10] = new int[] {-28, -8, 17, 30, 32, 12,-15,-26,-26};
        hitBoxParameters[6][11] = new int[] {-14,  6,-16,-26,-40,-35,-25,-14, -5};
        // x6 / y6
        hitBoxParameters[6][12] = new int[] {-14, -8, 17, 42, 23,  0, -5,-12,-26};
        hitBoxParameters[6][13] = new int[] { -9, -1,-25,-26,-48,-29,-38, -9, -5};
        // x7 / y7
        hitBoxParameters[6][14] = new int[] {-25,-17,  7, 42, 29, 12,-14,-23,-26};
        hitBoxParameters[6][15] = new int[] {-16,  1,-18,-40,-34,-29,-34,-21, -5};
        // x8 / y8
        hitBoxParameters[6][16] = new int[] {-21, -1, 13, 30, 16,  6, -7,-19,-26};
        hitBoxParameters[6][17] = new int[] {-13,  1,-20,-33,-44,-29,-27,-13, -5};
        //hitframes
        damageFrame[6] = new boolean[] {true,true,true,true,true,true,true,true,false};

    }

    public void update(float x,float y,int currentAnimation,int currentFrame)
    {
        if(damageFrame[currentAnimation][currentFrame])
        for(int i = 0; i < 9; i++)
        {
            if(weapon.getOwner().isFlipped())
            {
                currentXes[i] = (int) (x - hitBoxParameters[currentAnimation][2 * i][currentFrame]);
            }
            else
            {
                currentXes[i] = (int) (x + hitBoxParameters[currentAnimation][2 * i][currentFrame]);
            }

            currentYs[i] = (int) (y + hitBoxParameters[currentAnimation][2 * i + 1][currentFrame] + 10);
        }
    }

    public void render()
    {
        Renderer.setColor(240,0,0,255);
        for(int i = 0; i < 9; i++)
            Renderer.fillRect(currentXes[i], currentYs[i],1,1);
        Renderer.setColor(255,255,255,255);
    }

    /**
     * Returns the damage frame array
     * @return 2D boolean array containing the information for each animation
     */
    public boolean[][] isDamageFrame()
    {
        return damageFrame;
    }

    /**
     * Returns the array containing the computed x coordinates of the damage points associated with the current animation/frame
     * @return coordinates in an array of integers
     */
    public int[] getCurrentXes()
    {
        return currentXes;
    }

    /**
     * Returns the array containing the computed y coordinates of the damage points associated with the current animation/frame
     * @return coordinates as integers
     */
    public int[] getCurrentYs()
    {
        return currentYs;
    }
}
