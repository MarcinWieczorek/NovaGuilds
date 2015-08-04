package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
	REGION_TOOL,

	GUILD_CREATEPROTECTION,
	GUILD_MAXPLAYERS,

	GUILD_HOMEFLOOR_ENABLED,
	GUILD_HOMEFLOOR_MATERIAL,

	TABLIST_SCHEME
	;

	private static final ConfigManager cM = NovaGuilds.getInst().getConfigManager();
	private final String path;

	Config() {
		path = StringUtils.replace(name(), "_", ".").toLowerCase();
	}

	public String getString() {
		String r = cM.isInCache(this) ? (String) cM.getEnumConfig(this) : cM.getString(path);
		cM.putInCache(this, r);
		return r;
	}

	public List<String> getStringList() {
		List<String> r = cM.isInCache(this) ? (List<String>) cM.getEnumConfig(this) : cM.getStringList(path);
		cM.putInCache(this, r);
		return r;
	}

	public List<ItemStack> getItemStackList() {
		List<ItemStack> r = cM.isInCache(this) ? (List<ItemStack>) cM.getEnumConfig(this) : cM.getItemStackList(path);
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
		List<String> contentList = Config.TABLIST_SCHEME.getStringList();
		String[] content = contentList.toArray(new String[60]);

		String[] scheme = new String[60];
		System.arraycopy(content, 0, scheme, 0, content.length);
		int co = 1;
		int coi = 0;

		for(int i=0; i<60; i++) {
			if(coi!=0 && coi % 10 == 0) {
				co++;
				coi = 0;
			}

			if(scheme[i]==null || scheme[i].isEmpty()) {
				scheme[i] = "";
				for(int i2=0; i2<co; i2++) {
					scheme[i] += StringUtils.fixColors("&" + coi);
				}
			}
			else {
				scheme[i] = StringUtils.fixColors(scheme[i]);
			}

			coi++;
		}

		return scheme;
	}

	public static ConfigManager getManager() {
		return cM;
	}
}
