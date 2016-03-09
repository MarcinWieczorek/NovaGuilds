/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.util;

import java.util.Random;

public final class NumberUtils {
	private NumberUtils() {
	}

	public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();

		return rand.nextInt((max - min) + 1) + min;
	}

	public static double roundOffTo2DecPlaces(double val) {
		return Math.round(val * 100) / 100;
	}

	public static long systemSeconds() {
		return System.currentTimeMillis() / 1000;
	}
}
