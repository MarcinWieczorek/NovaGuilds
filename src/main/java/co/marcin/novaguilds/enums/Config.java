/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.manager.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("unchecked")
public enum Config {
	MYSQL_HOST,
	MYSQL_PORT,
	MYSQL_USERNAME,
	MYSQL_PASSWORD,
	MYSQL_DATABASE,
	MYSQL_PREFIX,

	DATASTORAGE_PRIMARY,
	DATASTORAGE_SECONDARY,

	LANG_NAME,
	LANG_OVERRIDEESSENTIALS,
	DEBUG,
	DELETEINVALID,

	BOSSBAR_ENABLED,
	BOSSBAR_RAIDBAR_ENABLED,
	BOSSBAR_RAIDBAR_STYLE,
	BOSSBAR_RAIDBAR_COLOR,

	TAGAPI_ENABLED,

	SIGNGUI_ENABLED,

	POINTSBELOWNAME,

	HOLOGRAPHICDISPLAYS_ENABLED,
	HOLOGRAPHICDISPLAYS_REFRESH,

	ADVANCEDENTITYUSE,

	LIVEREGENERATION_REGENTIME,

	CHAT_TAG_CHAT,
	CHAT_TAG_SCOREBOARD,
	CHAT_LEADERPREFIX,
	CHAT_DISPLAYNAMETAGS,
	CHAT_ADVANCED,
	CHAT_CONFIRMTIMEOUT,
	CHAT_TAGCOLORS_NEUTRAL,
	CHAT_TAGCOLORS_ALLY,
	CHAT_TAGCOLORS_WAR,
	CHAT_TAGCOLORS_GUILD,
	CHAT_ALLY_PREFIX,
	CHAT_ALLY_ENABLED,
	CHAT_ALLY_LEADERPREFIX,
	CHAT_ALLY_FORMAT,
	CHAT_GUILD_PREFIX,
	CHAT_GUILD_ENABLED,
	CHAT_GUILD_LEADERPREFIX,
	CHAT_GUILD_FORMAT,

	GUILD_DISABLEDWORLDS,
	GUILD_CREATEPROTECTION,
	GUILD_START_POINTS,
	GUILD_START_MONEY,
	GUILD_SLOTS_START,
	GUILD_SLOTS_MAX,
	GUILD_LIVES_START,
	GUILD_LIVES_MAX,
	GUILD_FROMSPAWN,
	GUILD_DEFAULTPVP,
	GUILD_STRINGCHECK_ENABLED,
	GUILD_STRINGCHECK_REGEX,
	GUILD_STRINGCHECK_PATTERN,
	GUILD_STRINGCHECK_REGEXPATTERN,
	GUILD_SETTINGS_TAG_MIN,
	GUILD_SETTINGS_TAG_MAX,
	GUILD_SETTINGS_NAME_MIN,
	GUILD_SETTINGS_NAME_MAX,
	GUILD_KILLPOINTS,
	GUILD_DEATHPOINTS,
	GUILD_EFFECT_DURATION,
	GUILD_EFFECT_LIST,

	RANK_MAXAMOUNT,
	RANK_GUI,
	RANK_DEFAULTRANKS,

	RAID_ENABLED,
	RAID_TIMEREST,
	RAID_TIMEINACTIVE,
	RAID_MINONLINE,
	RAID_POINTSTAKE,
	RAID_MULTIPLER,
	RAID_PVP_BONUSPERCENT_MONEY,
	RAID_PVP_BONUSPERCENT_POINTS,

	LIVEREGENERATION_TASKINTERVAL,

	SAVEINTERVAL,

	CLEANUP_ENABLED,
	CLEANUP_INACTIVETIME,
	CLEANUP_INTERVAL,
	CLEANUP_STARTUPDELAY,

	LEADERBOARD_GUILD_ROWS,

	VAULT_ENABLED,
	VAULT_ITEM,
	VAULT_HOLOGRAM_ENABLED,
	VAULT_HOLOGRAM_LINES,
	VAULT_DENYRELATIVE,

	USETITLES,

	REGION_AUTOREGION,
	REGION_MINSIZE,
	REGION_MAXSIZE,
	REGION_MINDISTANCE,
	REGION_MAXAMOUNT,
	REGION_TOOL,
	REGION_FLUIDPROTECT,
	REGION_BLOCKEDCMDS,
	REGION_WATERFLOW,
	REGION_ALLYINTERACT,
	REGION_BORDERPARTICLES,
	REGION_DENYINTERACT,
	REGION_DENYUSE,
	REGION_DENYMOBDAMAGE,
	REGION_DENYRIDING,

	REGION_MATERIALS_HIGHLIGHT_REGION_CORNER,
	REGION_MATERIALS_HIGHLIGHT_REGION_BORDER,
	REGION_MATERIALS_HIGHLIGHT_RESIZE_CORNER,
	REGION_MATERIALS_HIGHLIGHT_RESIZE_BORDER,
	REGION_MATERIALS_SELECTION_VALID_CORNER,
	REGION_MATERIALS_SELECTION_VALID_BORDER,
	REGION_MATERIALS_SELECTION_INVALID_CORNER,
	REGION_MATERIALS_SELECTION_INVALID_BORDER,
	REGION_MATERIALS_RESIZE_CORNER,
	REGION_MATERIALS_RESIZE_BORDER,

	KILLING_STARTPOINTS,
	KILLING_RANKPERCENT,
	KILLING_COOLDOWN,
	KILLING_MONEYFORKILL,
	KILLING_MONEYFORREVENGE,

	TABLIST_ENABLED,
	TABLIST_REFRESH,
	TABLIST_HEADER,
	TABLIST_FOOTER,
	TABLIST_TEXTURE,
	TABLIST_TOPROW_PLAYERS_POINTS,
	TABLIST_TOPROW_PLAYERS_KDR,
	TABLIST_TOPROW_GUILDS,
	TABLIST_SCHEME;

	private static final ConfigManager cM = NovaGuilds.getInstance() == null ? null : NovaGuilds.getInstance().getConfigManager();
	private final String path;

	/**
	 * The constructor
	 */
	Config() {
		path = StringUtils.replace(name(), "_", ".").toLowerCase();
	}

	/**
	 * Gets the path
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets a string
	 *
	 * @return the string
	 */
	public String getString() {
		String r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof String ? (String) cM.getEnumConfig(this) : cM.getString(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets string list
	 *
	 * @return the list
	 */
	public List<String> getStringList() {
		List<String> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<String>) cM.getEnumConfig(this) : cM.getStringList(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets ItemStack list
	 *
	 * @return the list
	 */
	public List<ItemStack> getItemStackList() {
		List<ItemStack> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<ItemStack>) cM.getEnumConfig(this) : cM.getItemStackList(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets material list
	 *
	 * @return the list
	 */
	public List<Material> getMaterialList() {
		List<Material> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<Material>) cM.getEnumConfig(this) : cM.getMaterialList(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets a long
	 *
	 * @return long
	 */
	public long getLong() {
		long r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Long ? (long) cM.getEnumConfig(this) : cM.getLong(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets a double
	 *
	 * @return double
	 */
	public double getDouble() {
		double r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Double ? (double) cM.getEnumConfig(this) : cM.getDouble(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets an int
	 *
	 * @return int
	 */
	public int getInt() {
		int r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Integer ? (int) cM.getEnumConfig(this) : cM.getInt(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets a boolean
	 *
	 * @return boolean
	 */
	public boolean getBoolean() {
		boolean r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Boolean ? (boolean) cM.getEnumConfig(this) : cM.getBoolean(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets time in seconds
	 *
	 * @return seconds
	 */
	public int getSeconds() {
		int r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Integer ? (int) cM.getEnumConfig(this) : cM.getSeconds(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets an ItemStack
	 *
	 * @return itemstack
	 */
	public ItemStack getItemStack() {
		ItemStack r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof ItemStack ? (ItemStack) cM.getEnumConfig(this) : cM.getItemStack(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets a material
	 *
	 * @return material
	 */
	public Material getMaterial() {
		Material r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Material ? (Material) cM.getEnumConfig(this) : cM.getMaterial(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets material data (durability)
	 *
	 * @return byte
	 */
	public byte getMaterialData() {
		byte r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Byte ? (byte) cM.getEnumConfig(this) : cM.getMaterialData(path);
		cM.putInCache(this, r);
		return r;
	}

	/**
	 * Gets percents
	 *
	 * @return double value (%)
	 */
	public double getPercent() {
		return getDouble() / 100;
	}

	/**
	 * Gets configuration section
	 *
	 * @return the section
	 */
	public ConfigurationSection getConfigurationSection() {
		return cM.getConfig().getConfigurationSection(path);
	}

	/**
	 * Gets config from path
	 *
	 * @param path the path
	 * @return the enum
	 */
	public static Config fromPath(String path) {
		try {
			return Config.valueOf(StringUtils.replace(path, ".", "_").toUpperCase());
		}
		catch(Exception e) {
			return null;
		}
	}

	/**
	 * Sets a value
	 *
	 * @param obj the value
	 */
	public void set(Object obj) {
		cM.set(this, obj);
	}

	/**
	 * Converts the value to an enum
	 *
	 * @param clazz enum class
	 * @param <E>   enum class
	 * @return enum value
	 */
	public <E extends Enum> E toEnum(Class<E> clazz) {
		for(E enumConstant : clazz.getEnumConstants()) {
			if(enumConstant.name().equalsIgnoreCase(getString())) {
				return enumConstant;
			}
		}

		return null;
	}
}
