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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public final class LoggerUtils {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private static final NovaGuilds plugin = NovaGuilds.getInstance();

	public static void error(String error) {
		logger.severe(StringUtils.fixColors(NovaGuilds.getLogPrefix() + classPrefix() + space(error) + error));
	}

	public static void info(String msg) {
		logger.info(StringUtils.fixColors(NovaGuilds.getLogPrefix() + classPrefix() + space(msg) + msg));
	}

	public static void debug(String msg) {
		if(plugin != null && plugin.getConfigManager() != null) {
			if(Config.DEBUG.getBoolean()) {
				info("[DEBUG] " + classPrefix() + msg);
			}
		}
	}

	public static String classPrefix() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String line = ste[3].toString();
		String[] split1 = org.apache.commons.lang.StringUtils.split(line, '(');
		String[] split2 = split1[1].split(":");
		String cname = split2[0].replace(".java", "");
		return cname.equals("NovaGuilds") ? "" : "[" + cname + "]";
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
		error("  NovaGuilds: #" + plugin.getBuild());
		error("  Storage Type: " + plugin.getConfigManager().getDataStorageType().name());
		error("  Bukkit: " + Bukkit.getBukkitVersion());
		error("  Java: " + System.getProperty("java.version"));
		error("  Thread: " + Thread.currentThread());
		error("  Running CraftBukkit: " + Bukkit.getServer().getClass().getName().equals("org.bukkit.craftbukkit.CraftServer"));
		error("  Exception Message: ");
		error("   " + e.getMessage());
		error("");

		StackTraceElement[] ste = cause == null ? e.getStackTrace() : cause.getStackTrace();

		if(ste != null && ste.length > 0) {
			error("Stack trace: ");
			error(cause == null ? "Invalid Cause!" : "Caused by: " + cause);
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
		Message.CHAT_ERROROCCURED.broadcast(Permission.NOVAGUILDS_ERROR);
	}
}
