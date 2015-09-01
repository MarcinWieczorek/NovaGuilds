package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public final class LoggerUtils {
	public static final Logger logger = Logger.getLogger("Minecraft");

	public static void error(String error) {
		logger.severe(StringUtils.fixColors(NovaGuilds.getLogPrefix() + classPrefix() + space(error) + error));
	}

	public static void info(String msg) {
		logger.info(StringUtils.fixColors(NovaGuilds.getLogPrefix() + classPrefix() + space(msg) + msg));
	}

	public static void debug(String msg) {
		if(NovaGuilds.getInstance()!=null && NovaGuilds.getInstance().getConfigManager() != null) {
			if(Config.DEBUG.getBoolean()) {
				info("[DEBUG] " + classPrefix() + msg);
			}
		}
	}

	public static String classPrefix() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String line = ste[3].toString();
		String[] split1 = org.apache.commons.lang.StringUtils.split(line,'(');
		String[] split2 = split1[1].split(":");
		String cname = split2[0].replace(".java","");
		return cname.equals("NovaGuilds") ? "" : "["+cname+"]";
	}

	public static String space(String s) {
		return s.contains("Manager]") ? "" : " ";
	}

	public static void exception(Exception e) {
		Throwable cause = e.getCause();
		error("");
		error("[NovaGuilds] Severe error:");
		error("");
		error("Server Information:");
		error("  NovaGuilds: #" + NovaGuilds.getInstance().getBuild());
		error("  Storage Type: " + NovaGuilds.getInstance().getConfigManager().getDataStorageType().name());
		error("  Bukkit: " + Bukkit.getBukkitVersion());
		error("  Java: " + System.getProperty("java.version"));
		error("  Thread: " + Thread.currentThread());
		error("  Running CraftBukkit: " + Bukkit.getServer().getClass().getName().equals("org.bukkit.craftbukkit.CraftServer"));
		error("  Exception Message: ");
		error("   "+e.getMessage());
		error("");

		StackTraceElement[] ste = cause==null ? e.getStackTrace() : cause.getStackTrace();

		if(ste != null && ste.length > 0) {
			error("Stack trace: ");
			error(cause==null ? "Invalid Cause!" : "Caused by: "+cause);
			for(StackTraceElement st : ste) {
				error("	at " + st.toString());
			}

		}
		else {
			error("Null or empty stacktrace. Printing current:");
			error("Stack trace: ");
			for(StackTraceElement st : Thread.currentThread().getStackTrace()) {
				error("	at " + st.toString());
			}
		}

		error("");
		error("End of Error.");
		error("");

		//notify all permitted players
		Message.CHAT_ERROROCCURED.broadcast("novaguilds.error");
	}
}
