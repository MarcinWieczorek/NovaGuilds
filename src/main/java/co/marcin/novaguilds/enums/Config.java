package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public enum Config {
	MYSQL_HOST,
	MYSQL_PORT,
	MYSQL_USERNAME,
	MYSQL_PASSWORD,
	MYSQL_DATABASE,
	MYSQL_PREFIX,

	DEBUG,
	RAID_ENABLED,
	RAID_TIMEREST,
	RAID_TIMEINACTIVE,
	RAID_MINONLINE,

	LIVEREGENERATION_TASKINTERVAL,

	CLEANUP_ENABLED,
	CLEANUP_INACTIVETIME,
	CLEANUP_INTERVAL,

	SAVEINTERVAL,

	BANK_ENABLED,
	BANK_ITEM,
	BANK_ONLYLEADERTAKE,
	BANK_HOLOGRAM_ENABLED,
	BANK_HOLOGRAM_LINES,

	BARAPI_ENABLED,
	BARAPI_RAIDBAR,

	USETITLES,

	REGION_MINSIZE,
	REGION_MAXSIZE,

	GUILD_CREATEPROTECTION,

	GUILD_HOMEFLOOR_ENABLED,
	GUILD_HOMEFLOOR_MATERIAL,
	;


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

	public int getSeconds() {
		return StringUtils.StringToSeconds(getString());
	}

	public ItemStack getItemStack() {
		return ItemStackUtils.stringToItemStack(getString());
	}

	public Material getMaterial() {
		return Material.getMaterial(this.getString().toUpperCase());
	}
}
