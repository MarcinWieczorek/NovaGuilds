package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import com.google.common.io.CharStreams;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
			LoggerUtils.exception(e);
		}
			    
		return body;
	}
		  
	private static String toString(InputStream in, String encoding) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len;
		
		while ((len = in.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		
		return new String(baos.toByteArray(), encoding);
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
				row = replace(pattern,"{GUILDNAME}",row);
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

//	public String Capslock(String message) {
//		String ch;
//		int countCharsCaps = 0;
//		int countWords = 1;
//
//		int wordCount = 0;
//		int characterCount = 6;
//		int percentage = 40;
//
//		int countChars = message.length();
//		if(countChars > 0) {
//			if(countChars > characterCount) {
//				for(int i = 0; i < countChars; i++) {
//					char c = message.charAt(i);
//					ch = Character.toString(c);
//					if(ch.matches("[A-Z]")) {
//						countCharsCaps++;
//					}
//					if(c == ' ') {
//						countWords++;
//					}
//				}
//				if(countWords >= wordCount) {
//					if(100/countChars*countCharsCaps >= percentage) {
//						message = message.toLowerCase();
//					}
//				}
//			}
//		}
//		return message;
//	}

	public static String replaceMap(String msg, HashMap<String,String> vars) {
		if(vars != null) {
			for(Map.Entry<String, String> entry : vars.entrySet()) {
				msg = replace(msg, "{" + entry.getKey() + "}", entry.getValue());
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

		if(unit == TimeUnit.DAYS) {
			str_hours="";
			str_minutes="";
			str_seconds="";
		}
		else if(unit == TimeUnit.HOURS) {
			str_minutes="";
			str_seconds="";
		}
		else if(unit == TimeUnit.MINUTES) {
			str_seconds="";
		}

		return str_years + str_days + str_hours + str_minutes + str_seconds;
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
		String allowed = NovaGuilds.getInst().getConfig().getString("guild.allowedchars");
		for(int i=0;i<string.length();i++) {
			if(allowed.indexOf(string.charAt(i)) == -1) {
				return false;
			}
		}

		return true;
	}

	public static String inputStreamToString(InputStream inputStream) {
		try {
			return CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
		return null;
	}
}
