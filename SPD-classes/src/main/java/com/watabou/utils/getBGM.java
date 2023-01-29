package com.watabou.utils;

public class getBGM {
    
	//Right now it's just stage name lol
    public static String getDepth(int branch, int depth){
        String BGM;
        if (branch == 0) {
			switch (depth) {
				case 1:
				case 2:
				case 3:
				case 4:
					BGM = "Sewer";
					break;
				case 5:
					BGM = "Goo";
					break;
				case 6:
				case 7:
				case 8:
				case 9:
					BGM = "Prison";
					break;
				case 10:
					BGM = "Tengu";
					break;
				case 11:
				case 12:
				case 13:
				case 14:
					BGM = "Cave";
					break;
				case 15:
					BGM = "DM300";
					break;
				case 16:
				case 17:
				case 18:
				case 19:
					BGM = "City";
					break;
				case 20:
                    BGM = "Dwarf King";
					break;
				case 21:
				case 22:
				case 23:
				case 24:
					BGM = "Demon Hall";
					break;
				case 25:
					BGM = "Yog";
					break;
				case 26:
					BGM = "Amulet";
					break;
				default:
					BGM = "Error";
			}
		} else if (branch == 1) {
			switch (depth) {
				case 1:
				case 2:
				case 3:
				case 4:
					BGM = "Forest";
					break;
				case 5:
					BGM = "Fairies";
					break;
				case 6:
				case 7:
				case 8:
				case 9:
					BGM = "Mansion";
					break;
				case 10:
					BGM = "Scarlet";
					break;
				case 11:
				case 12:
				case 13:
				case 14:
					BGM = "Shrine";
					break;
				case 15:
					BGM = "DM-300";
					break;
				case 16:
				case 17:
				case 18:
				case 19:
					BGM = "Bamboo";
					break;
				case 20:
					BGM = "Magician";
					break;
				case 21:
				case 22:
				case 23:
				case 24:
					BGM = "Demon";
					break;
				case 25:
					BGM = "Yog";
					break;
				case 26:
					BGM = "Amulet";
					break;
				default:
					BGM = "Error";
			}
		}
		else {
			BGM = "Error";
		}
        return BGM;
    }
}
