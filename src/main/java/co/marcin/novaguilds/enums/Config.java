package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public enum Config {
	MYSQL_HOST,
	MYSQL_PORT,
	MYSQL_USERNAME,
	MYSQL_PASSWORD,
	MYSQL_DATABASE,
	MYSQL_PREFIX;


	private final FileConfiguration config = NovaGuilds.getInst().getConfig();
	private final String path;

	Config() {
		path = StringUtils.replace(name(), "_", ".").toLowerCase();
	}

	public String getString() {
		return config.getString(path)==null ? "" : config.getString(path);
	}

	public long getLong() {
		return config.getLong(path);
	}

	public int getInt() {
		return config.getInt(path);
	}

	public boolean getBoolean() {
		return config.getBoolean(path);
	}

	public int stringToSeconds() {
		return StringUtils.StringToSeconds(getString());
	}

	public ItemStack getItemStack() {
		return ItemStackUtils.stringToItemStack(getString());
	}
}
