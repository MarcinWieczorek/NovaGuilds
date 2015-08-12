package co.marcin.novaguilds.util;

import java.util.Random;

public final class NumberUtils {
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
		return Math.round(val*100)/100;
	}

	public static long systemSeconds() {
		return System.currentTimeMillis() / 1000;
	}
}
