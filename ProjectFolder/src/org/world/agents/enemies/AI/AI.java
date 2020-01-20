package org.world.agents.enemies.AI;

import org.engine.GameLoop;

import java.util.*;

/**
 *  Abstract class to implement some AI-related methods, currently only used for one type of pathfinding
 */
public  interface AI
{
    /**
     *  Checks whether moving through tile x,y is possible (tile exists, is traversable and no obstacles are inside of it)
     * @param x isometric x coordinate of  the tile
     * @param y isometric y coordinate of the tile
     * @return boolean value
     */
    static boolean isValidMove(int x, int y, float z)
    {
        //if out of map's bounds
        if(x < 0 || x >= GameLoop.getWorld().getTerrain().getWidth() || y < 0 || y >= GameLoop.getWorld().getTerrain().getHeight())
            return false;

        //if tile at p.x, p.y is traversable and no solid decoration is present and height is same or below
        if(GameLoop.getWorld().getTerrain().getTiles()[x][y] != null && GameLoop.getWorld().getTerrain().getTiles()[x][y].traversable  && (GameLoop.getWorld().getDecorations()[x][y]== null || !GameLoop.getWorld().getDecorations()[x][y].solid)
        && GameLoop.getWorld().getTerrain().getTiles()[x][y].z <= z)
        {
            return true;
        }

        return false;
    }

    /**
     *  Method to simplify checking whether the tile at given coordinates exists and is traversable. In some cases whether decorations are present or not is not relevant
     *  (for example in enemy movement when enemies can move around obstacles while still in the same tile as the obstacle) so this method is preferred to isValidMove
     *  In case the given coordinates are out of bounds, the function returns false
     * @param x isometric x coordinate of  the tile
     * @param y isometric y coordinate of the tile
     */
    static boolean isTraversable(int x, int y)
    {
        //if out of map's bounds
        if(x < 0 || x >= GameLoop.getWorld().getTerrain().getWidth() || y < 0 || y >= GameLoop.getWorld().getTerrain().getHeight())
            return false;

        //if tile at p.x, p.y is traversable and no solid decoration is present
        if(GameLoop.getWorld().getTerrain().getTiles()[x][y] != null && GameLoop.getWorld().getTerrain().getTiles()[x][y].traversable)
        {
            return true;
        }

        return false;
    }

    /**
     *   Parameter list for easy accessing of neighbouring points, first half represents straight moves, second half diagonal ones
     */
     int[][] PARAMS = { {0 , 0, 1,-1, 1,-1, 1,-1},
            {1, -1, 0, 0,-1, 1, 1,-1}};

    /**
     *  Given a destination point, builds a path to the original source by going along the parent chain
     * @param dest destination point
     * @return an array list of point from the destination to the source
     */
    private static ArrayList<Point> buildPath(Point dest)
    {
        ArrayList<Point> route = new ArrayList<>();

        // route.add(dest);

        while(dest != null)
        {
            //dest = dest.parent;
            route.add(dest);
            dest = dest.parent;
        }

        return route;
    }

    /**
     * Applies the A* search algorithm on the current game world, with the given start and destination coordinates.
     * It is essentially a "guided" extension of Dijkstra's algorithm, employing a heuristic to improve performance
     * It only considers tiles to be traversable when they are marked as such and there are no obstacles present
     *
     * @param xStart isometric coordinate of the starting point
     * @param yStart isometric coordinate of the starting point
     * @param xDest  isometric coordinate of the starting point
     * @param yDest  isometric coordinate of the starting point
     * @return an array list of the points along the path from destination to the source
     */
     static  ArrayList<Point> aStar(int xStart, int yStart, int xDest, int yDest)
    {

        Queue<Point> discovered = new PriorityQueue<>();
        Point current = new Point(xStart, yStart,null);
        discovered.add(current);
        boolean[][] closed = new boolean[GameLoop.getWorld().getTerrain().getWidth()][GameLoop.getWorld().getTerrain().getHeight()];

        while(discovered.peek() != null)
        {
            current = discovered.poll();
            //check all neighbouring points and add the ones that can be traversed
           for(int i = 0;i < 8; i++)
           {
               int newX = current.x + PARAMS[0][i];
               int newY = current.y + PARAMS[1][i];

               //add possible moves and calculate their cost
               if(isValidMove(newX, newY, GameLoop.getWorld().getTerrain().getTiles()[xStart][yStart].z) && !closed[newX][newY])
               {
                   //System.out.println("in");
                   Point p = new Point(newX, newY,current);
                   p.cost = current.cost;

                   //add proper cost depending on whether the move is straight or diagonal
                   if(i < 4)
                   {
                       p.cost += Point.STRAIGHT_COST;
                   }
                   else p.cost += Point.DIAGONAL_COST;

                   p.cost = p.cost + Point.hScore(p,xDest,yDest);
                   discovered.add(p);
               }

           }

            closed[current.x][current.y] = true;

            current = discovered.poll();

           if(current != null && Math.abs(current.x - xDest) <= 1 && Math.abs(current.y - yDest) <=1)
               return buildPath(current);
        }

    return null;
    }
}
