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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.NovaGuildsAPI;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class VersionUtils {
	public static int buildCurrent;
	public static int buildLatest = 0;
	public static int buildDev = 0;
	public static boolean updateAvailable = false;
	public static boolean init = false;

	static {
		if(!init) new VersionUtils();
	}

	public VersionUtils() {
		init = true;
		NovaGuildsAPI ng = NovaGuilds.getInstance();

		buildCurrent = ng == null ? YamlConfiguration.loadConfiguration(new File("./src/main/resources/plugin.yml")).getInt("version") : ng.getBuild();

		try {
			String latestString = StringUtils.getContent("http://novaguilds.pl/latest.info");
			String devString = StringUtils.getContent("http://novaguilds.pl/dev.info");

			buildLatest = Integer.parseInt(latestString);
			buildDev = Integer.parseInt(devString);
		}
		catch(IOException e) {
			LoggerUtils.error("Failed to fetch versions");
		}
	}

	public static void checkVersion() {
		LoggerUtils.info("You're using build: #" + buildCurrent);
		LoggerUtils.info("Latest stable build of the plugin is: #" + buildLatest);

		if(buildCurrent == buildLatest) {
			LoggerUtils.info("Your plugin build is the latest stable one");
		}
		else if(buildCurrent > buildLatest) {
			if(buildCurrent > buildDev) {
				LoggerUtils.info("You are using unreleased build #" + buildCurrent);
			}
			else if(buildCurrent == buildDev) {
				LoggerUtils.info("You're using latest development build");
			}
			else {
				LoggerUtils.info("Why the hell are you using outdated dev build?");
				updateAvailable = true;
			}
		}
		else {
			LoggerUtils.info("You should update your plugin to #" + buildLatest + "!");
			updateAvailable = true;
		}

		//Notify admins if there's an update (only for reload)
		if(updateAvailable) {
			Message.CHAT_UPDATE.broadcast(Permission.NOVAGUILDS_ADMIN_UPDATEAVAILABLE);
		}
	}
}
