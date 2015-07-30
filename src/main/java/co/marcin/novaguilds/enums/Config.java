package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Material;
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
	REGION_MINDISTANCE,

	GUILD_CREATEPROTECTION,
	GUILD_MAXPLAYERS,

	GUILD_HOMEFLOOR_ENABLED,
	GUILD_HOMEFLOOR_MATERIAL,
	;

	private final ConfigManager cM = NovaGuilds.getInst().getConfigManager();
	private final String path;

	Config() {
		path = StringUtils.replace(name(), "_", ".").toLowerCase();
	}

	public String getString() {
		String r = cM.isInCache(this) ? (String) cM.getEnumConfig(this) : cM.getString(path);
		cM.putInCache(this, r);
		return r;
	}

	public long getLong() {
		long r = cM.isInCache(this) ? (long) cM.getEnumConfig(this) : cM.getLong(path);
		cM.putInCache(this, r);
		return r;
	}

	public int getInt() {
		int r = cM.isInCache(this) ? (int) cM.getEnumConfig(this) : cM.getInt(path);
		cM.putInCache(this, r);
		return r;
	}

	public boolean getBoolean() {
		boolean r = cM.isInCache(this) ? (boolean) cM.getEnumConfig(this) : cM.getBoolean(path);
		cM.putInCache(this, r);
		return r;
	}

	public int getSeconds() {
		int r = cM.isInCache(this) ? (int) cM.getEnumConfig(this) : cM.getSeconds(path);
		cM.putInCache(this, r);
		return r;
	}

	public ItemStack getItemStack() {
		ItemStack r = cM.isInCache(this) ? (ItemStack) cM.getEnumConfig(this) : cM.getItemStack(path);
		cM.putInCache(this, r);
		return r;
	}

	public Material getMaterial() {
		Material r = cM.isInCache(this) ? (Material) cM.getEnumConfig(this) : cM.getMaterial(path);
		cM.putInCache(this, r);
		return r;
	}

	public static String[] getTablistScheme() {
		String[] scheme = new String[]{
				"potato {PLAYER}",
				"lel {SECOND}"
		};
		return scheme;
	}
}
