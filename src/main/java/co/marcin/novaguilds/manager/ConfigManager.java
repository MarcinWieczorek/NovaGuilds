package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
	private final NovaGuilds plugin;
	private FileConfiguration config;

	private boolean debug;

	private DataStorageType primaryDataStorageType;
	private DataStorageType secondaryDataStorageType;
	private DataStorageType dataStorageType;

	private boolean useBarAPI;
	private boolean useHolographicDisplays;
	private boolean useTitles;

	private String databasePrefix;

	private long cleanupInactiveTime;
	private long cleanupInterval;
	private boolean cleanupEnabled;

	private boolean raidEnabled;
	private long raidTimeRest;
	private long raidTimeInactive;

	private long guildLiveRegenerationTime;
	private long guildDistanceFromSpawn;
	private long guildLiveRegenerationTaskInterval;

	private boolean chatTagColors;
	private boolean chatDisplayNameTags;

	private ItemStack guildBankItem;
	private boolean guildBankEnabled;
	private boolean guildBankOnlyLeaderTake;
	private boolean guildBankHologramEnabled;
	private List<String> guildBankHologramLines; //supports items, [ITEM]

	private int guildEffectDuration;
	private final List<PotionEffectType> guildEffects = new ArrayList<>();
	private ItemStack toolItem;

	private HashMap<Config, Object> cache = new HashMap<>();

	public ConfigManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		NovaGuilds.getInst().setConfigManager(this);
		reload();
		LoggerUtils.info("Enabled");
	}

	public void reload() {
		cache.clear();

		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		config = plugin.getConfig();

		debug = Config.DEBUG.getBoolean();

		raidEnabled = config.getBoolean("raid.enabled");
		raidTimeRest = StringUtils.StringToSeconds(config.getString("raid.timerest"));
		raidTimeInactive = StringUtils.StringToSeconds(config.getString("raid.timeinactive"));

		guildDistanceFromSpawn = config.getLong("guild.fromspawn");
		guildLiveRegenerationTime = StringUtils.StringToSeconds(config.getString("liveregeneration.regentime"));
		guildLiveRegenerationTaskInterval = StringUtils.StringToSeconds(config.getString("liveregeneration.taskinterval"));

		//bank
		guildBankEnabled = config.getBoolean("bank.enabled");
		guildBankHologramEnabled = config.getBoolean("bank.hologram.enabled");
		guildBankHologramLines = config.getStringList("bank.hologram.lines");
		guildBankItem = getItemStack("bank.item");
		guildBankOnlyLeaderTake = config.getBoolean("bank.onlyleadertake");

		useHolographicDisplays = config.getBoolean("holographicdisplays.enabled");
		useBarAPI = config.getBoolean("barapi.enabled");
		useTitles = config.getBoolean("usetitles");

		if(useTitles) {
			if(!plugin.getServer().getBukkitVersion().startsWith("1.8")) {
				useTitles = false;
				LoggerUtils.error("You can't use Titles with Bukkit older than 1.8");
			}
		}

		cleanupEnabled = config.getBoolean("cleanup.enabled");
		cleanupInactiveTime = StringUtils.StringToSeconds(config.getString("cleanup.inactivetime"));
		cleanupInterval = StringUtils.StringToSeconds(config.getString("cleanup.interval"));

		databasePrefix = config.getString("mysql.prefix");

		chatDisplayNameTags = config.getBoolean("chat.displaynametags");
		chatTagColors = config.getBoolean("tagapi.colortags");

		String primaryDataStorageTypeString = config.getString("datastorage.primary").toUpperCase();
		String secondaryDataStorageTypeString = config.getString("datastorage.secondary").toUpperCase();

		boolean primaryValid = false;
		boolean secondaryValid = false;

		if(primaryDataStorageTypeString.equals(secondaryDataStorageTypeString)) {
			LoggerUtils.error("Primary and secondary data storage types cannot be the same!");
			LoggerUtils.error("Resetting to defaults. (MySQL/Flat)");
			primaryDataStorageTypeString = DataStorageType.MYSQL.name();
			secondaryDataStorageTypeString = DataStorageType.FLAT.name();
		}

		for(DataStorageType dst : DataStorageType.values()) {
			if(dst.name().equals(primaryDataStorageTypeString)) {
				primaryValid = true;
			}

			if(dst.name().equals(secondaryDataStorageTypeString)) {
				secondaryValid = true;
			}
		}

		if(!primaryValid || !secondaryValid) {
			LoggerUtils.error("Not valid Data Storage Types.");
			LoggerUtils.error("Resetting to defaults. (MySQL/Flat)");
			primaryDataStorageTypeString = DataStorageType.MYSQL.name();
			secondaryDataStorageTypeString = DataStorageType.FLAT.name();
		}

		primaryDataStorageType = DataStorageType.valueOf(primaryDataStorageTypeString);
		secondaryDataStorageType = DataStorageType.valueOf(secondaryDataStorageTypeString);
		setToPrimaryDataStorageType();
		LoggerUtils.info("Data storage: Primary: "+primaryDataStorageType.name()+", Secondary: "+secondaryDataStorageType.name());

		guildEffectDuration = config.getInt("guild.effect.duration");
		List<String> guildEffectsString = config.getStringList("guild.effect.list");
		for(String effect : guildEffectsString) {
			PotionEffectType effectType = PotionEffectType.getByName(effect);
			if(effectType != null) {
				guildEffects.add(effectType);
			}
		}

		//tool
		toolItem = Config.REGION_TOOL.getItemStack();

		//Check time values
		if(guildLiveRegenerationTaskInterval < 60) {
			LoggerUtils.error("Live regeneration task interval can't be shorter than 60 seconds.");
			guildLiveRegenerationTaskInterval = 60;
		}

		if(cleanupInterval < 60) {
			LoggerUtils.error("Cleanup interval can't be shorter than 60 seconds.");
			cleanupEnabled = false;
		}
		if(Config.SAVEINTERVAL.getSeconds() < 60) {
			LoggerUtils.error("Save interval can't be shorter than 60 seconds.");
			config.set("saveinterval",60);
		}
	}

	//getters

	public String getDatabasePrefix() {
		return databasePrefix;
	}

	public DataStorageType getDataStorageType() {
		return dataStorageType;
	}

	public long getCleanupInactiveTime() {
		return cleanupInactiveTime;
	}

	public long getCleanupInterval() {
		return cleanupInterval;
	}

	public long getGuildLiveRegenerationTime() {
		return guildLiveRegenerationTime;
	}

	public long getGuildLiveRegenerationTaskInterval() {
		return guildLiveRegenerationTaskInterval;
	}

	public long getGuildDistanceFromSpawn() {
		return guildDistanceFromSpawn;
	}

	public long getRaidTimeRest() {
		return raidTimeRest;
	}

	public long getRaidTimeInactive() {
		return raidTimeInactive;
	}

	public int getGuildEffectDuration() {
		return guildEffectDuration;
	}

	public List<PotionEffectType> getGuildEffects() {
		return guildEffects;
	}

	public boolean isGuildBankEnabled() {
		return guildBankEnabled;
	}

	public ItemStack getGuildBankItem() {
		return guildBankItem;
	}

	public List<String> getGuildBankHologramLines() {
		return guildBankHologramLines;
	}

	public boolean isGuildBankHologramEnabled() {
		return guildBankHologramEnabled;
	}

	//checkers
	public boolean isCleanupEnabled() {
		return cleanupEnabled;
	}

	public boolean isRaidEnabled() {
		return raidEnabled;
	}

	public boolean useBarAPI() {
		return useBarAPI;
	}

	public boolean useHolographicDisplays() {
		return useHolographicDisplays;
	}

	public boolean useTitles() {
		return useTitles;
	}

	public boolean isDebugEnabled() {
		return debug;
	}

	public boolean isChatTagColorsEnabled() {
		return chatTagColors;
	}

	public boolean getGuildBankOnlyLeaderTake() {
		return guildBankOnlyLeaderTake;
	}

	public boolean useChatDisplayNameTags() {
		return chatDisplayNameTags;
	}

	//setters
	public void disableHolographicDisplays() {
		useHolographicDisplays = false;
	}

	public void disableBarAPI() {
		useBarAPI = false;
	}

	public void setDataStorageType(DataStorageType dataStorageType) {
		this.dataStorageType = dataStorageType;
	}

	public void setToSecondaryDataStorageType() {
		dataStorageType = secondaryDataStorageType;
	}

	public void setToPrimaryDataStorageType() {
		dataStorageType = primaryDataStorageType;
	}

	public ItemStack getToolItem() {
		return toolItem;
	}

	public Object getEnumConfig(Config c) {
		return cache.get(c);
	}

	public boolean isInCache(Config c) {
		return cache.containsKey(c);
	}

	public void putInCache(Config c, Object o) {
		if(!cache.containsKey(c)) {
			cache.put(c, o);
		}
	}

	//methods from enum

	public String getString(String path) {
		return config.getString(path) == null ? "" : config.getString(path);
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path) == null ? new ArrayList<String>() : config.getStringList(path);
	}

	public long getLong(String path) {
		return config.getLong(path);
	}

	public int getInt(String path) {
		return config.getInt(path);
	}

	public boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	public int getSeconds(String path) {
		return StringUtils.StringToSeconds(getString(path));
	}

	public ItemStack getItemStack(String path) {
		return ItemStackUtils.stringToItemStack(getString(path));
	}

	public Material getMaterial(String path) {
		return Material.getMaterial(this.getString(path).toUpperCase());
	}

	public List<ItemStack> getItemStackList(String path) {
		List<String> stringList = getStringList(path);
		List<ItemStack> itemStackList = new ArrayList<>();

		for(String string : stringList) {
			ItemStack is = ItemStackUtils.stringToItemStack(string);

			if(is != null) {
				itemStackList.add(is);
			}
		}

		return itemStackList;
	}
}
