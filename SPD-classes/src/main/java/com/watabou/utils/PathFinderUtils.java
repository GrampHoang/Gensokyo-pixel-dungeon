package com.watabou.utils;

public class PathFinderUtils {

    
    // Find the order of the collison relative the the center
    public static int collision_i(int[] pathfinder, int collisionPos, int center){
        int count = -1;
        for (int i : pathfinder){
            count++;
            if(center + i == collisionPos){
                return count;
            }
        }
        return 1;
    }

    // REMINDER THAT THIS USE CIRCLE8 PATHFINDER


    //return the center + two random cell around the center that are opposite to each other
    //Unlike other method in here, this use normal Pather finder
    //  0 1 2
    //  3   4
    //  5 6 7
    public static int[] random_two_opposite_cell(int center){
        int i = Random.IntRange(0, 7);
        int[] cell = {1, 1, 1};
        cell[0] = center + PathFinder.NEIGHBOURS8[i];
        cell[1] = center;
        cell[2] = center + PathFinder.NEIGHBOURS8[7-i];
        return cell;
    }


    //Return the 3 opposite tile of a CIRCLE8 8 Pathfinder, mimick piercing AoE shot
    //  0 1 2
    //  7   3
    //  6 5 4

    //Output example may not have the same order, only the same number
    //     col: 3 -> output: [0,7,6]
    public static int[] opposite3(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] opposite3Tiles = {pathfinder[redBy(collision_i, 3)] + center, 
                                pathfinder[redBy(collision_i, 4)] + center,
                                pathfinder[incBy(collision_i, 3)] + center};
        return opposite3Tiles;
    }


    //Neightbor 3, colPos + 2 tile next to it
    // col: 3 -> output [2,3,4]
    public static int[] neighbour3(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = { pathfinder[left(collision_i)] + center, 
                                  collisionPos,
                                  pathfinder[right(collision_i)] + center};
        return neightbour3Tiles;
    }


    //  0 1 2
    //  7   3
    //  6 5 4
    //Small Triangle, colPos + 2 tile next to next to it
    // col: 3 -> output [1,3,5]
    public static int[] triangle(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = {pathfinder[redBy(collision_i, 2)] + center, 
                                  collisionPos,
                                  pathfinder[incBy(collision_i, 2)] + center};
        return neightbour3Tiles;
    }

    //  0 1 2
    //  7   3
    //  6 5 4
    //Big Triangle, colPos + 2 tile next to next to it
    // col: 3 -> output [0,3,6]
    public static int[] backtriangle(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = {pathfinder[redBy(collision_i, 3)] + center, 
                                  collisionPos,
                                  pathfinder[incBy(collision_i, 3)] + center};
        return neightbour3Tiles;
    }

    public static int[] backtrianglePosNumber(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);

        int left = (collision_i + 5)%8;
        int right= (collision_i + 3)%8;

        int[] posBehind = {left, right};
        return posBehind;
    }


    // col: 3 -> output [1,center,5]
    public static int[] perpendicular(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = {pathfinder[redBy(collision_i, 2)] + center, 
                                  center,
                                  pathfinder[incBy(collision_i, 2)] + center};
        return neightbour3Tiles;
    }

    public static int[] perpendicularPosNumber(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int left = (collision_i + 6)%8;
        int right= (collision_i + 2)%8;

        int[] posPerpen = {left, right};
        System.out.println(collision_i);
        System.out.println(left);
        System.out.println(right);
        return posPerpen;
    }


    //  0 1 2
    //  7   3
    //  6 5 4
    // col: 3 -> output [7,1,3,5]
    // col: 4 -> output [0,2,6,4]
    // Need test
    //Cross that contain collision Pos
    public static int[] cross_con(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = {pathfinder[redBy(collision_i, 2)] + center, 
                                  collisionPos,
                                  pathfinder[incBy(collision_i, 2)] + center,
                                  pathfinder[incBy(collision_i, 4)] + center};
        return neightbour3Tiles;
    }
    

    // col: 3 -> output [0,2,6,4]
    // col: 4 -> output [7,1,3,5]
    //Cross that doesn not contain collision Pos
    public static int[] cross(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = {pathfinder[redBy(collision_i, 3)] + center, 
                                   pathfinder[left(collision_i)] + center,
                                  pathfinder[incBy(collision_i, 3)] + center,
                                  pathfinder[right(collision_i)] + center};
        return neightbour3Tiles;
    }


    //Left and Right for circle 8
    private static int left(int direction){
		return direction == 0 ? 7 : direction-1;
	}
	
	private static int right(int direction){
		return direction == 7 ? 0 : direction+1;
	}

    // Increase postion by an amount in CIRCLE 8
    private static int incBy(int direction, int inc){
		return (direction + inc) % 8;
	}
    // Reduce
    private static int redBy(int direction, int red){
		return (direction + 8 - red) % 8;
	}
}
