package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
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

	private boolean useBarAPI;
	private boolean useHolographicDisplays;
	private boolean useMySQL;

	private String databasePrefix;

	private int saveInterval;

	private long cleanupInactiveTime;
	private long cleanupInterval;
	private boolean cleanupEnabled;

	private long raidTimeRest;
	private long raidTimeInactive;

	private long guildLiveRegenerationTime;
	private long guildDistanceFromSpawn;
	private long guildLiveRegenerationTaskInterval;

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

		saveInterval = config.getInt("saveperiod");

		raidTimeRest = config.getLong("raid.timerest");
		raidTimeInactive = config.getLong("raid.timeinactive");

		guildDistanceFromSpawn = config.getLong("guild.fromspawn");
		guildLiveRegenerationTime = StringUtils.StringToSeconds(config.getString("liveregeneration.regentime"));
		guildLiveRegenerationTaskInterval = StringUtils.StringToSeconds(config.getString("liveregeneration.taskinterval"));

		useHolographicDisplays = config.getBoolean("holographicdisplays.enabled");
		useBarAPI = config.getBoolean("barapi.enabled");

		cleanupEnabled = config.getBoolean("cleanup.enabled");
		cleanupInactiveTime = StringUtils.StringToSeconds(config.getString("cleanup.inactivetime"));
		cleanupInterval = StringUtils.StringToSeconds(config.getString("cleanup.interval"));

		useMySQL = config.getBoolean("usemysql");
		databasePrefix = config.getString("mysql.prefix");

		guildEffectDuration = config.getInt("effectduration");
		List<String> guildEffectsString = config.getStringList("effects");
		for(String effect : guildEffectsString) {
			PotionEffectType effectType = PotionEffectType.getByName(effect);
			if(effectType != null) {
				guildEffects.add(effectType);
			}
		}
	}

	//getters
	public String getDatabasePrefix() {
		return databasePrefix;
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

	//checkers
	public boolean isCleanupEnabled() {
		return cleanupEnabled;
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

	//setters
	public void disableHolographicDisplays() {
		useHolographicDisplays = false;
	}

	public void disableBarAPI() {
		useBarAPI = false;
	}
}
