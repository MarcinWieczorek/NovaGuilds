/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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

import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class StringUtils {
	public static String fixColors(String msg) {
		if(msg == null) {
			return null;
		}
		
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String unTranslateAlternateColorCodes(String msg) {
		char altColorChar = ChatColor.COLOR_CHAR;

		char[] b = msg.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
				b[i] = '&';
				b[i+1] = Character.toLowerCase(b[i+1]);
			}
		}

		return new String(b);
	}
	
	public static String removeColors(String msg) {
		return ChatColor.stripColor(fixColors(msg));
	}
	
	public static String getContent(String s) throws IOException {
		String body = null;
		URL url = new URL(s);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		body = IOUtils.toString(in, encoding);
			    
		return body;
	}
	
	public static String parseDBLocation(Location l) {
		return l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ() + ";" + Math.round(l.getYaw());
	}
	
	public static String parseDBLocationCoords2D(Location l) {
		return l.getBlockX()+";"+l.getBlockZ();
	}
	
	public static String[] parseArgs(String[] args, int cut) {
		if(args.length==0 || args.length < cut) {
			return args;
		}
		
		String[] newargs = new String[args.length-cut];
		
		int index = 0;
		for(int i=0; i<args.length; i++) {
			if(i>=cut) {
				newargs[index] = args[i];
				index++;
			}
		}
		
		return newargs;
	}
	
	public static List<String> semicolonToList(String str) {
		List<String> list = new ArrayList<>();

		if(str.contains(";")) {
			String[] split = str.split(";");
			Collections.addAll(list, split);
		}
		else if(!str.isEmpty()) {
			list.add(str);
		}
		
		return list;
	}

	//TODO dafuq
	public static String join(List<String> items, String pattern, String separator) {
		String joined = "";

		if(!items.isEmpty()) {
			for(String row : items) {
				row = org.apache.commons.lang.StringUtils.replace(pattern,"{GUILDNAME}",row);
				joined = joined + row + separator;
			}

			joined = joined.substring(0,joined.length()-separator.length());
		}

		return joined;
	}

	public static String join(List<String> items, String separator) {
		String joined = "";
		for(String item : items) {
			joined = joined + item + separator;
		}

		return joined;
	}

	public static String join(String[] items, String separator) {
		String joined = "";

		if(items.length > 0) {
			for(String row : items) {
				joined = joined + row + separator;
			}

			joined = joined.substring(0,joined.length()-separator.length());
		}

		return joined;
	}

	public static String replaceMap(String msg, Map<String, String> vars) {
		if(vars != null) {
			for(Map.Entry<String, String> entry : vars.entrySet()) {
				msg = org.apache.commons.lang.StringUtils.replace(msg, "{" + entry.getKey() + "}", entry.getValue());
			}
		}

		return msg;
	}

	public static String secondsToString(long lseconds) {
		return secondsToString(lseconds, TimeUnit.SECONDS);
	}

	public static String secondsToString(long lseconds, TimeUnit unit) {
		int year = 31536000;
		int day = 86400;
		int hour = 3600;
		int minute = 60;

		int seconds = Integer.parseInt(lseconds+"");

		int years = seconds / year;
		seconds = seconds % year;

		int days = seconds / day;
		seconds = seconds % day;

		int hours = seconds / hour;
		seconds = seconds % hour;

		int minutes = seconds / minute;
		seconds = seconds % minute;

		String stringYears="", stringDays="", stringHours="", stringSeconds="", stringMinutes="";

		if(years > 0) {
			Message formYear = years > 1 ? Message.TIMEUNIT_YEAR_PLURAL : Message.TIMEUNIT_YEAR_SINGULAR;

			stringYears = years + " "+formYear.get()+" ";
		}

		if(days > 0) {
			Message formDay = days > 1 ? Message.TIMEUNIT_DAY_PLURAL : Message.TIMEUNIT_DAY_SINGULAR;

			stringDays = days + " "+formDay.get()+" ";
		}

		if(hours > 0) {
			Message formHour = hours > 1 ? Message.TIMEUNIT_HOUR_PLURAL : Message.TIMEUNIT_HOUR_SINGULAR;

			stringHours = hours + " "+formHour.get()+" ";
		}

		if(minutes > 0) {
			Message formMinute = minutes > 1 ? Message.TIMEUNIT_MINUTE_PLURAL : Message.TIMEUNIT_MINUTE_SINGULAR;

			stringMinutes = minutes + " "+formMinute.get()+" ";
		}

		if(seconds > 0) {
			Message formSecond = seconds > 1 ? Message.TIMEUNIT_SECOND_PLURAL : Message.TIMEUNIT_SECOND_SINGULAR;

			stringSeconds = seconds + " "+formSecond.get()+" ";
		}

		if(unit == TimeUnit.DAYS && days > 0) {
			stringHours="";
			stringMinutes="";
			stringSeconds="";
		}
		else if(unit == TimeUnit.HOURS && hours > 0) {
			stringMinutes="";
			stringSeconds="";
		}
		else if(unit == TimeUnit.MINUTES && minutes > 0) {
			stringSeconds="";
		}

		return stringYears + stringDays + stringHours + stringMinutes + stringSeconds;
	}

	public static int StringToSeconds(String str) {
		String[] spacexp = str.split(" ");
		int seconds = 0;

		for(String word : spacexp) {
			if(word.endsWith("s")) {
				word = word.substring(0, word.length() - 1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word);
				}
			}

			if(word.endsWith("m")) {
				word = word.substring(0,word.length()-1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60;
				}
			}

			if(word.endsWith("h")) {
				word = word.substring(0,word.length()-1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60;
				}
			}

			if(word.endsWith("d")) {
				word = word.substring(0,word.length()-1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60 * 24;
				}
			}

			if(word.endsWith("y")) {
				word = word.substring(0,word.length()-1);
				if(NumberUtils.isNumeric(word)) {
					seconds += Integer.parseInt(word) * 60 * 60 * 24 * 365;
				}
			}
		}

		return seconds;
	}

	public static boolean isStringAllowed(String string) {
		String allowed = Config.GUILD_ALLOWEDCHARS.getString();
		if(string.matches(allowed)){
			return true;
		}
		return false;
	}

	public static String getItemList(List<ItemStack> items) {
		String itemlist = "";
		int i = 0;
		for(ItemStack missingItemStack : items) {
			String itemrow = Message.CHAT_CREATEGUILD_ITEMLIST.get();
			itemrow = org.apache.commons.lang.StringUtils.replace(itemrow, "{ITEMNAME}", missingItemStack.getType().name());
			itemrow = org.apache.commons.lang.StringUtils.replace(itemrow, "{AMOUNT}", missingItemStack.getAmount() + "");

			itemlist += itemrow;

			if(i<items.size()-1) {
				itemlist += Message.CHAT_CREATEGUILD_ITEMLISTSEP.get();
			}
			i++;
		}

		return fixColors(itemlist);
	}

	public static List<String> jsonToList(String json) {
		return new Gson().fromJson(json, List.class);
	}
}
