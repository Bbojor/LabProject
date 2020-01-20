package org.world.agents.enemies.AI;

/**
 * Implements a 2D point that is used in pathfinding
 * The coordinates point to the center of a tile
 */
public class Point implements Comparable
{
    //tile coordinates
    /**
     * Isometric x coordinate
     */
    public int x;

    /**
     * Isometric y coordinate
     */
    public int y;
    //cost of choosing this point
    /**
     * Cost of moving to this point (see A* for details)
     */
    int cost;

    /**
     * Parent point (see A* for details)
     */
    public Point parent;

    /**
     * Creates a point with given coordinates and parent
     * Assumes the coordinates it receives are valid in the current game world,
     * does not check for out of bounds conditions
     * @param x isometric coordinate
     * @param y isometric coordinate
     * @param parent parent point
     */
    public Point(int x, int y,Point parent)
    {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    //cost from moving from a square to another, we consider diagonal moves to take longer than a straight one
    /**
     * Constant used to compute the move cost
     */
    static final int STRAIGHT_COST = 10;
    /**
     * Constant used to compute the move cost
     */
    static final int DIAGONAL_COST = 14;

    /**
     * Compares points based on the moving cost
     */
    @Override
    public int compareTo(Object p)
    {
        if(this.cost>((Point)p).cost)
            return 1;

        if(this.cost == ((Point)p).cost)
            return 0;

        return -1;
    }

    /**
     * Approximates cost of the path from point p to the destination cooordinates
     * @param p point object
     * @param xDest  isometric coordinate
     * @param yDest  isometric coordinate
     */
     static int hScore(Point p, int xDest, int yDest)
    {
        return STRAIGHT_COST * (Math.abs(p.x - xDest) + Math.abs(p.y - yDest));
    }

}
