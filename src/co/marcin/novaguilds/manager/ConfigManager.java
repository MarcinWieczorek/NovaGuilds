package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	private final NovaGuilds plugin;
	private final FileConfiguration config;

	private int saveperiod;
	private long inactiveTime;
	private long inactiveClearPeriod;

	public ConfigManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		config = plugin.getConfig();
	}

	//getters
	public int getSaveperiod() {
		return saveperiod;
	}

	public long getInactiveTime() {
		return inactiveTime;
	}

	public long getInactiveClearPeriod() {
		return inactiveClearPeriod;
	}

	public void loadVars() {
		saveperiod = config.getInt("saveperiod");
	}
}
