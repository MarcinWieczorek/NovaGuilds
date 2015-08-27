package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.NovaGuildsAPI;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class VersionUtils {
	public static int buildCurrent;
	public static int buildLatest;
	public static int buildDev;
	public static boolean updateAvailable = false;
	public static boolean init = false;

	static {
		if(!init) new VersionUtils();
	}

	public VersionUtils() {
		init = true;
		NovaGuildsAPI ng = NovaGuilds.getInstance();

		buildCurrent = ng == null ? YamlConfiguration.loadConfiguration(new File("./src/main/resources/plugin.yml")).getInt("version") : ng.getBuild();
		buildLatest = Integer.parseInt(StringUtils.getContent("http://novaguilds.pl/latest.info"));
		buildDev = Integer.parseInt(StringUtils.getContent("http://novaguilds.pl/dev.info"));
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
	}
}
