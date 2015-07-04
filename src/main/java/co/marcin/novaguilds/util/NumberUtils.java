package co.marcin.novaguilds.util;

import java.util.Random;

public class NumberUtils {
	public static int fixX(int x) {
		return x;
//		if(x<0) {
//			return x+1;
//		}
//
//		return x;
	}

	public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();

		return rand.nextInt((max - min) + 1) + min;
	}
}
