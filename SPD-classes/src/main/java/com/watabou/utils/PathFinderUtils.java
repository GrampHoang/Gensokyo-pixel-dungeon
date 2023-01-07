package com.watabou.utils;

import java.io.Console;

public class PathFinderUtils {

    //Return the 3 opposite tile of a circle 8 Pathfinder, mimick piercing AoE shot
    //  0 1 2
    //  7   3
    //  6 5 4

    //Output example may not have the same order, only the same number
    //     col: 3 -> output: [0,7,6]
    public static int collision_i(int[] pathfinder, int collisionPos, int center){
        int count = -1;
        System.out.println("Check:");
        for (int i : pathfinder){
            count++;
            System.out.println(count);
            if(center + i == collisionPos){
                return count;
            }
        }
        return 1;
    }


    public static int[] opposite3(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        // System.out.println("opposite");
        // System.out.print(left(left(left(collision_i))));
        // System.out.print(left(left(left(left(collision_i)))));
        // System.out.println(right(right(right(collision_i))));
        // System.out.println("PAthfinder");
        // System.out.print(pathfinder[left(left(left(collision_i)))]);
        // System.out.print(pathfinder[left(left(left(left(collision_i))))]);
        // System.out.println(pathfinder[right(right(right(collision_i)))]);
        // System.out.println("Pathfinder + center");
        // System.out.print(pathfinder[left(left(left(collision_i)))] + center);
        // System.out.print(pathfinder[left(left(left(left(collision_i))))] + center);
        // System.out.println(pathfinder[right(right(right(collision_i)))] + center);

        int[] opposite3Tiles = {pathfinder[left(left(left(collision_i)))] + center, 
                                pathfinder[left(left(left(left(collision_i))))] + center,
                                pathfinder[right(right(right(collision_i)))] + center};
        return opposite3Tiles;
    }

    // col: 3 -> output [2,3,4]
    public static int[] neighbour3(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = {pathfinder[left(collision_i)] + center, 
                                collisionPos,
                                pathfinder[right(collision_i)] + center};
        return neightbour3Tiles;
    }

    // col: 3 -> output [1,3,5]
    public static int[] triangle(int[] pathfinder, int collisionPos, int center){
        int collision_i = collision_i(pathfinder, collisionPos, center);
        int[] neightbour3Tiles = {pathfinder[left(left(collision_i))] + center, 
                                collisionPos,
                                pathfinder[right(right(collision_i))] + center};
        return neightbour3Tiles;
    }

    // col: 3 -> output [7,1,3,5]
    // col: 4 -> output [0,2,6,4]
    //Need fix
    // public static int[] cross(int[] pathfinder, int collisionPos, int center){
    //     int oppositePos = Math.abs(5 - collisionPos);
    //     int[] crossTiles = {oppositePos + center, left(left(collisionPos)) + center, collisionPos + center, right(right(collisionPos)) + center};
    //     return crossTiles;
    // }

    //Left and Right for circle 8
    private static int left(int direction){
		return direction == 0 ? 7 : direction-1;
	}
	
	private static int right(int direction){
		return direction == 7 ? 0 : direction+1;
	}
}
