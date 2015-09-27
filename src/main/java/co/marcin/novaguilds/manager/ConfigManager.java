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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {
	private final NovaGuilds plugin;
	private FileConfiguration config;

	private DataStorageType primaryDataStorageType;
	private DataStorageType secondaryDataStorageType;
	private DataStorageType dataStorageType;

	private boolean useVanishNoPacket = true;

//	private List<String> guildVaultHologramLines; //supports items, [ITEM]

	private final List<PotionEffectType> guildEffects = new ArrayList<>();

	private final HashMap<Config, Object> cache = new HashMap<>();

	public ConfigManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		NovaGuilds.getInstance().setConfigManager(this);
		reload();
		LoggerUtils.info("Enabled");
	}

	public void reload() {
		cache.clear();

		if(!new File(plugin.getDataFolder(),"config.yml").exists()) {
			LoggerUtils.info("Creating default config...");
			plugin.saveDefaultConfig();
		}

		plugin.reloadConfig();
		config = plugin.getConfig();

		long guildLiveRegenerationTaskInterval = StringUtils.StringToSeconds(config.getString("liveregeneration.taskinterval"));

		if(Config.USETITLES.getBoolean()) {
			if(!plugin.getServer().getBukkitVersion().startsWith("1.8")) {
				Config.USETITLES.set(false);
				LoggerUtils.error("You can't use Titles with Bukkit older than 1.8");
			}
		}

		long cleanupInterval = StringUtils.StringToSeconds(config.getString("cleanup.interval"));

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

		List<String> guildEffectsString = config.getStringList("guild.effect.list");
		for(String effect : guildEffectsString) {
			PotionEffectType effectType = PotionEffectType.getByName(effect);
			if(effectType != null) {
				guildEffects.add(effectType);
			}
		}

		//Check time values
		if(guildLiveRegenerationTaskInterval < 60) {
			LoggerUtils.error("Live regeneration task interval can't be shorter than 60 seconds.");
			config.set("liveregeneration.taskinterval", 60);
		}

		if(cleanupInterval < 60) {
			LoggerUtils.error("Cleanup interval can't be shorter than 60 seconds.");
			config.set("cleanup.interval", 60);
		}

		if(Config.SAVEINTERVAL.getSeconds() < 60) {
			LoggerUtils.error("Save interval can't be shorter than 60 seconds.");
			config.set("saveinterval",60);
		}
	}

	//getters
	public DataStorageType getDataStorageType() {
		return dataStorageType;
	}

	public List<PotionEffectType> getGuildEffects() {
		return guildEffects;
	}

	public boolean useVanishNoPacket() {
		return useVanishNoPacket;
	}

	//setters
	public void disableVanishNoPacket() {
		useVanishNoPacket = false;
	}

	public void setToSecondaryDataStorageType() {
		dataStorageType = secondaryDataStorageType;
	}

	public void setToPrimaryDataStorageType() {
		dataStorageType = primaryDataStorageType;
	}

	//Cache
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

	public void removeFromCache(Config c) {
		if(cache.containsKey(c)) {
			cache.remove(c);
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

	public double getDouble(String path) {
		return config.getDouble(path);
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
		return Material.getMaterial(getString(path).toUpperCase());
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

	public List<Material> getMaterialList(String path) {
		List<String> stringList = getStringList(path);
		List<Material> materialList = new ArrayList<>();

		for(String string : stringList) {
			Material material = Material.getMaterial(string);
			if(material != null) {
				materialList.add(material);
			}
		}

		return materialList;
	}

	public void set(String path, Object obj) {
		config.set(path, obj);
		removeFromCache(Config.fromPath(path));
	}
}
