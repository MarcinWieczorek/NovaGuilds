package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
	private boolean useTitles;

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
	private ItemStack toolItem;

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
		if(saveInterval < 60) {
			logger.severe("Save interval can't be shorter than 60 seconds.");
			saveInterval = 60;
		}

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

		if(guildLiveRegenerationTaskInterval < 60) {
			logger.severe("Live regeneration task interval can't be shorter than 60 seconds.");
			guildLiveRegenerationTaskInterval = 60;
		}

		useHolographicDisplays = config.getBoolean("holographicdisplays.enabled");
		useBarAPI = config.getBoolean("barapi.enabled");
		useTitles = config.getBoolean("usetitles");

		if(useTitles) {
			if(!plugin.getServer().getBukkitVersion().startsWith("1.8")) {
				useTitles = false;
				logger.severe("You can't use Titles with Bukkit older than 1.8");
			}
		}

		cleanupEnabled = config.getBoolean("cleanup.enabled");
		cleanupInactiveTime = StringUtils.StringToSeconds(config.getString("cleanup.inactivetime"));
		cleanupInterval = StringUtils.StringToSeconds(config.getString("cleanup.interval"));

		if(cleanupInterval < 60) {
			logger.severe("Cleanup interval can't be shorter than 60 seconds.");
			cleanupEnabled = false;
		}

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

		//tool
		Material toolMaterial = Material.getMaterial(plugin.getConfig().getString("region.tool.item").toUpperCase());
		toolItem = new ItemStack(toolMaterial, 1);
		ItemMeta meta = toolItem.getItemMeta();
		meta.setDisplayName(StringUtils.fixColors(plugin.getMessageManager().getMessagesString("items.tool.name")));

		List<String> lorecodes = plugin.getMessageManager().getMessages().getStringList("items.tool.lore");
		List<String> lore = new ArrayList<>();

		for(String l : lorecodes) {
			lore.add(StringUtils.fixColors(l));
		}

		meta.setLore(lore);

		toolItem.setItemMeta(meta);
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
}
