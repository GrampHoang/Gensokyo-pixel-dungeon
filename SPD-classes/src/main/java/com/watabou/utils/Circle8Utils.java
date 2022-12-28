package com.watabou.utils;

public class Circle8Utils {

    //Return the 3 opposite tile of a circle 8 Pathfinder, mimick piercing AoE shot
    //  0 1 2
    //  7   3
    //  6 5 4

    //Output example may not have the same order, only the same number
    //     col: 3 -> output: [0,7,6]
    public static int[] opposite3(int[] pathfinder, int collisionpos, int center){
        int oppositePos = Math.abs(5 - collisionpos);
        int[] opposite3Tiles = {left(oppositePos) + center, oppositePos + center, right(oppositePos) + center};
        return opposite3Tiles;
    }

    // col: 3 -> output [2,3,4]
    public static int[] neighbour3(int[] pathfinder, int collisionpos, int center){
        int[] neightbour3Tiles = {left(collisionpos) + center, collisionpos + center, right(collisionpos) + center};
        return neightbour3Tiles;
    }

    // col: 3 -> output [1,3,5]
    public static int[] triangle(int[] pathfinder, int collisionpos, int center){
        int[] neightbour3Tiles = {left(left(collisionpos)) + center, collisionpos + center, right(right(collisionpos)) + center};
        return neightbour3Tiles;
    }

    // col: 3 -> output [7,1,3,5]
    // col: 4 -> output [0,2,6,4]
    public static int[] cross(int[] pathfinder, int collisionpos, int center){
        int oppositePos = Math.abs(5 - collisionpos);
        int[] crossTiles = {oppositePos + center, left(left(collisionpos)) + center, collisionpos + center, right(right(collisionpos)) + center};
        return crossTiles;
    }

    //Left and Right for circle 8
    private static int left(int direction){
		return direction == 0 ? 7 : direction-1;
	}
	
	private static int right(int direction){
		return direction == 7 ? 0 : direction+1;
	}
}
