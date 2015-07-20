package co.marcin.novaguilds.util;

import java.util.Random;

public class NumberUtils {
	public static int negativeIsPlusOne(int i) {
		return i<0 ? i+1 : i;
	}

	public static double negativeIsPlusOne(double i) {
		return i<0 ? i+1 : i;
	}

	public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();

		return rand.nextInt((max - min) + 1) + min;
	}

	public static double roundOffTo2DecPlaces(double val) {
		return Double.parseDouble(String.format("%.2f", val));
		//TODO fix
		//return val;
	}

	public static long systemSeconds() {
		return System.currentTimeMillis() / 1000;
	}
}
