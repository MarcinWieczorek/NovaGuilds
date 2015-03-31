package co.marcin.NovaGuilds;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public final class Utils {
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
		if (msg == null) {
			return msg;
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
		catch (TimeoutException e) {}
		catch (Exception e) {}
			    
		return body;
	}
		  
	public static String toString(InputStream in, String encoding) throws Exception {
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
		List<String> list = new ArrayList<String>();
		String[] split = str.split(";");
		
		for(String s : split) {
			list.add(s);
		}
		
		return list;
	}
}
