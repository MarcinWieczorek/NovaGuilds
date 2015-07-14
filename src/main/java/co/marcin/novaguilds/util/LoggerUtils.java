package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.Bukkit;

public class LoggerUtils {
	public static void error(String error) {
		Bukkit.getLogger().severe(error);
	}

	public static void info(String msg) {
		Bukkit.getLogger().info(NovaGuilds.getInst().getConfigManager().getLogPrefix() + msg);
	}

	public static void debug(String msg) {
		if(NovaGuilds.getInst().getConfigManager().isDebugEnabled()) {
			ConfigManager.getLogger().info(NovaGuilds.getInst().getConfigManager().getLogPrefix() + "[DEBUG] " + msg);
		}
	}

	public static void exception(Exception e) {
		Throwable cause = e.getCause();
		StackTraceElement[] ste = e.getStackTrace();
		error("");
		error("[NovaGuilds] Severe error:");
		error("");
		error("Server Information:");
		error("  NovaGuilds: #" + NovaGuilds.getInst().getBuild());
		error("  Bukkit: " + Bukkit.getBukkitVersion());
		error("  Java: " + System.getProperty("java.version"));
		error("  Thread: " + Thread.currentThread());
		error("  Running CraftBukkit: " + Bukkit.getServer().getClass().getName().equals("org.bukkit.craftbukkit.CraftServer"));
		error("");
		if(cause != null && ste != null && ste.length > 0) {
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
