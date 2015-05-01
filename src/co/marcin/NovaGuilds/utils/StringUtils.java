package co.marcin.NovaGuilds.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public final class StringUtils {
	public static String replace(String text, String searchString, String replacement) {
		if((text == null) || (text.isEmpty()) || (searchString.isEmpty()) || (replacement == null)) {
			return text;
		}
		
		int start = 0;
		int max = -1;
		int end = text.indexOf(searchString, start);
		
		if(end == -1) {
			return text;
		}
		
		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = increase < 0 ? 0 : increase;
		increase *= (max > 64 ? 64 : max < 0 ? 16 : max);
		StringBuilder sb = new StringBuilder(text.length() + increase);
		
		while(end != -1) {
			sb.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			max--;
			
			if(max == 0) {
				break;
			}
			end = text.indexOf(searchString, start);
		}
		sb.append(text.substring(start));
		return sb.toString();
	}
	
	public static String fixColors(String msg) {
		if(msg == null) {
			return null;
		}
		
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static String removeColors(String msg) {
		return ChatColor.stripColor(fixColors(msg));
	}
	
	public static String getContent(String s) {
		String body = null;
		try {
			URL url = new URL(s);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			body = toString(in, encoding);
		}
		catch (Exception e) {
			Logger.getLogger("Minecraft").info(e.getMessage());
		}
			    
		return body;
	}
		  
	private static String toString(InputStream in, String encoding) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		
		while ((len = in.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		
		return new String(baos.toByteArray(), encoding);
	}
	
	public static String parseDBLocation(Location l) {
		return l.getWorld().getName()+";"+l.getBlockX()+";"+l.getBlockY()+";"+l.getBlockZ()+";"+Math.round(l.getYaw());
	}
	
	public static String parseDBLocationCoords2D(Location l) {
		return l.getBlockX()+";"+l.getBlockZ();
	}
	
	public static int fixX(int x) {
		if(x<0) {
			return x++;
		}
		else {
			return x;
		}
	}
	
	public static boolean isNumeric(String str) {
	    return str.matches("[+-]?\\d*(\\.\\d+)?");
	}


	
	public static String[] parseArgs(String[] args, int cut) {
		if(args.length==0) {
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
		String[] split = str.split(";");

		Collections.addAll(list, split);
		
		return list;
	}

	public static String join(List<String> items, String pattern, String separator) {
		String joined = "";

		if(items.size() > 0) {
			for(String row : items) {
				row = replace(pattern,"{GUILDNAME}",row);
				joined = joined + row + separator;
			}

			joined = joined.substring(0,joined.length()-separator.length());
		}

		return joined;
	}

	public String Capslock(String message) {
		String ch;
		int countChars = 0;
		int countCharsCaps = 0;
		int countWords = 1;

		int wordcount = 0;
		int charactercount = 6;
		int percentage = 40;

		countChars = message.length();
		if(countChars > 0) {
			if(countChars > charactercount) {
				for(int i = 0; i < countChars; i++) {
					char c = message.charAt(i);
					ch = Character.toString(c);
					if(ch.matches("[A-Z]")) {
						countCharsCaps++;
					}
					if(c == ' ') {
						countWords++;
					}
				}
				if(countWords >= wordcount) {
					if(100/countChars*countCharsCaps >= percentage) {
						message = message.toLowerCase();
					}
				}
			}
		}
		return message;
	}

	public static String replaceMap(String msg, HashMap<String,String> vars) {
		for(Map.Entry<String, String> entry : vars.entrySet()) {
			msg = replace(msg,"{"+entry.getKey()+"}",entry.getValue());
		}

		return msg;
	}

	public static String secondsToString(long lseconds) {
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

		String str_years="", str_days="", str_hours="", str_seconds="", str_minutes="";

		if(years > 0) {
			String formYear = "year";

			if(years > 1) {
				formYear = "years";
			}

			str_years = years + " "+formYear+" ";
		}

		if(days > 0) {
			String formDay = "day";

			if(days > 1) {
				formDay = "days";
			}

			str_days = days + " "+formDay+" ";
		}

		if(hours > 0) {
			String formHour = "hour";

			if(hours > 1) {
				formHour = "hours";
			}

			str_hours = hours + " "+formHour+" ";
		}

		if(minutes > 0) {
			String formMinute = "minute";

			if(minutes > 1) {
				formMinute = "minutes";
			}

			str_minutes = minutes + " "+formMinute+" ";
		}

		if(seconds > 0) {
			String formSecond = "second";

			if(seconds > 1) {
				formSecond = "seconds";
			}

			str_seconds = seconds + " "+formSecond+" ";
		}

		String str = str_years + str_days + str_hours + str_minutes + str_seconds;

		return str;
	}
}