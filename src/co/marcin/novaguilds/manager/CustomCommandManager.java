package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class CustomCommandManager {
	private final HashMap<String,String> aliases = new HashMap<>();

	public CustomCommandManager(NovaGuilds plugin) {
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("aliases");

		for(String key : section.getKeys(false)) {
			for(String alias : section.getStringList(key)) {
				aliases.put(alias,key);
			}
		}
	}

	public String getMainCommand(String alias) {
		return aliases.get(alias);
	}

	public boolean existsAlias(String alias) {
		return aliases.containsKey(alias);
	}
}
