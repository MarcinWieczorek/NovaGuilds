package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class LoggerUtils {
	public static final Logger logger = Bukkit.getLogger();

	public static void error(String error) {
		logger.severe(NovaGuilds.getLogPrefix() + classPrefix() + space(error) + error);
	}

	public static void info(String msg) {
		logger.info(NovaGuilds.getLogPrefix() + classPrefix() + space(msg) + msg);
	}

	public static void debug(String msg) {
		if(NovaGuilds.getInst().getConfigManager() != null) {
			if(NovaGuilds.getInst().getConfigManager().isDebugEnabled()) {
				logger.info(NovaGuilds.getLogPrefix() + "[DEBUG] " + classPrefix() + msg);
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
		exception(e.getCause());
	}

	public static void exception(Throwable cause) {
		//Throwable cause = e.getCause();
		if(cause != null) {
			StackTraceElement[] ste = cause.getStackTrace();
			error("");
			error("[NovaGuilds] Severe error:");
			error("");
			error("Server Information:");
			error("  NovaGuilds: #" + NovaGuilds.getInst().getBuild());
			error("  Storage Type: " + NovaGuilds.getInst().getConfigManager().getDataStorageType().name());
			error("  Bukkit: " + Bukkit.getBukkitVersion());
			error("  Java: " + System.getProperty("java.version"));
			error("  Thread: " + Thread.currentThread());
			error("  Running CraftBukkit: " + Bukkit.getServer().getClass().getName().equals("org.bukkit.craftbukkit.CraftServer"));
			error("");

			if(ste != null && ste.length > 0) {
				error("Stack trace: ");
				error("Caused by: " + cause);
				for(StackTraceElement st : ste) {
					error("	at " + st.toString());
				}

				error("");
				error("End of Error.");
				error("");
			}
		}
	}

	public static Logger getLogger() {
		return logger;
	}
}
