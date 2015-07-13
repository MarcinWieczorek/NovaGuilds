package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigManager {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private static final String logPrefix = "[NovaGuilds]";

	private final NovaGuilds plugin;
	private FileConfiguration config;

	private boolean debug;

	private DataStorageType primaryDataStorageType;
	private DataStorageType secondaryDataStorageType;
	private DataStorageType dataStorageType;

	private boolean useBarAPI;
	private boolean useHolographicDisplays;
	private boolean useMySQL;

	private String databasePrefix;

	private int saveInterval;

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
	private List<PotionEffectType> guildEffects = new ArrayList<>();

	public ConfigManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		reload();
	}

	public void reload() {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		config = plugin.getConfig();

		debug = config.getBoolean("debug");

		saveInterval = StringUtils.StringToSeconds(config.getString("saveinterval"));

		raidEnabled = config.getBoolean("raid.enabled");
		raidTimeRest = config.getLong("raid.timerest");
		raidTimeInactive = config.getLong("raid.timeinactive");

		guildDistanceFromSpawn = config.getLong("guild.fromspawn");
		guildLiveRegenerationTime = StringUtils.StringToSeconds(config.getString("liveregeneration.regentime"));
		guildLiveRegenerationTaskInterval = StringUtils.StringToSeconds(config.getString("liveregeneration.taskinterval"));

		//bank
		guildBankEnabled = config.getBoolean("bank.enabled");
		guildBankHologramEnabled = config.getBoolean("bank.hologram.enabled");
		guildBankHologramLines = config.getStringList("bank.hologram.lines");
		guildBankItem = getItemStack("bank.item");
		guildBankOnlyLeaderTake = config.getBoolean("bank.onlyleadertake");

		if(guildLiveRegenerationTaskInterval < 60) {
			logger.severe("Live regeneration task interval can't be shorter than 60 seconds.");
			guildLiveRegenerationTaskInterval = 60;
		}

		useHolographicDisplays = config.getBoolean("holographicdisplays.enabled");
		useBarAPI = config.getBoolean("barapi.enabled");

		cleanupEnabled = config.getBoolean("cleanup.enabled");
		cleanupInactiveTime = StringUtils.StringToSeconds(config.getString("cleanup.inactivetime"));
		cleanupInterval = StringUtils.StringToSeconds(config.getString("cleanup.interval"));

		if(cleanupInterval < 60) {
			logger.severe("Cleanup interval can't be shorter than 60 seconds.");
			cleanupEnabled = false;
		}

		useMySQL = config.getBoolean("usemysql");
		databasePrefix = config.getString("mysql.prefix");

		chatDisplayNameTags = config.getBoolean("chat.displaynametags");
		chatTagColors = config.getBoolean("tagapi.colortags");

		primaryDataStorageType = DataStorageType.valueOf(config.getString("datastorage.primary").toUpperCase());
		secondaryDataStorageType = DataStorageType.valueOf(config.getString("datastorage.secondary").toUpperCase());
		setToPrimaryDataStorageType();

		guildEffectDuration = config.getInt("guild.effect.duration");
		List<String> guildEffectsString = config.getStringList("guild.effect.list");
		for(String effect : guildEffectsString) {
			PotionEffectType effectType = PotionEffectType.getByName(effect);
			if(effectType != null) {
				guildEffects.add(effectType);
			}
		}
	}

	//getters
	public ItemStack getItemStack(String path) {
		return ItemStackUtils.stringToItemStack(config.getString(path));
	}

	public String getDatabasePrefix() {
		return databasePrefix;
	}

	public DataStorageType getDataStorageType() {
		return dataStorageType;
	}

	public int getSaveInterval() {
		return saveInterval;
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

	public String getLogPrefix() {
		return logPrefix;
	}

	public static Logger getLogger() {
		return logger;
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

	public boolean useMySQL() {
		return useMySQL;
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
}
