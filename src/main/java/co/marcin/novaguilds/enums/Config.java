/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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

	BARAPI_ENABLED,
	BARAPI_RAIDBAR,

	TABAPI_COLORTAGS,
	TABAPI_RANKPREFIX,

	TAGAPI_ENABLED,
	TAGAPI_RANKPREFIX,
	TAGAPI_ALLYCOLOR_ENABLED,
	TAGAPI_ALLYCOLOR_COLOR,
	TAGAPI_GUILDCOLOR_ENABLED,
	TAGAPI_GUILDCOLOR_COLOR,
	TAGAPI_WARCOLOR_ENABLED,
	TAGAPI_WARCOLOR_COLOR,
	TAGAPI_COLORTAGS,

	HOLOGRAPHICDISPLAYS_ENABLED,
	HOLOGRAPHICDISPLAYS_REFRESH,

	PACKETS_ENABLED,

	WWW_ENABLED,
	WWW_PORT,
	WWW_VERBOSE,

	LIVEREGENERATION_REGENTIME,

	CHAT_DISPLAYNAMETAGS,
	CHAT_CONFIRMTIMEOUT,

	CHAT_ALLY_PREFIX,
	CHAT_ALLY_ENABLED,
	CHAT_ALLY_COLORTAGS,
	CHAT_ALLY_LEADERPREFIX,
	CHAT_ALLY_FORMAT,
	CHAT_ALLY_MSGPREFIX,
	CHAT_GUILD_PREFIX,
	CHAT_GUILD_ENABLED,
	CHAT_GUILD_LEADERPREFIX,
	CHAT_GUILD_FORMAT,
	CHAT_GUILD_MSGPREFIX,

	GUILD_FROMSPAWN,
	GUILD_STRINGCHECK_ENABLED,
	GUILD_STRINGCHECK_REGEX,
	GUILD_STRINGCHECK_PATTERN,
	GUILD_STRINGCHECK_REGEXPATTERN,
	GUILD_SETTINGS_TAG_COLOR,
	GUILD_SETTINGS_TAG_MIN,
	GUILD_SETTINGS_TAG_MAX,
	GUILD_SETTINGS_NAME_MIN,
	GUILD_SETTINGS_NAME_MAX,
	GUILD_KILLPOINTS,
	GUILD_DEATHPOINTS,
	GUILD_EFFECT_DURATION,
	GUILD_EFFECT_LIST,
	GUILD_DEFAULTRANKS,

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
	REGION_TOOL,
	REGION_BLOCKEDCMDS,
	REGION_WATERFLOW,
	REGION_ALLYINTERACT,
	REGION_ADMINAUTOSIZE,
	REGION_BORDERPARTICLES,
	REGION_DENYINTERACT,
	REGION_DENYUSE,
	REGION_DENYMOBDAMAGE,
	REGION_DENYRIDING,

	REGION_MATERIALS_CHECK_HIGHLIGHT,
	REGION_MATERIALS_SELECTION_CORNER,
	REGION_MATERIALS_SELECTION_RECTANGLE,
	REGION_MATERIALS_SELECTION_INVALID,
	REGION_MATERIALS_RESIZE_CORNER,
	REGION_MATERIALS_RESIZE_RECTANGLE,

	GUILD_CREATEPROTECTION,
	GUILD_TAG,
	GUILD_HOMEFLOOR_ENABLED,
	GUILD_HOMEFLOOR_MATERIAL,

	GUILD_STARTPOINTS,
	GUILD_STARTLIVES,
	GUILD_STARTMONEY,
	GUILD_STARTSLOTS,

	GUILD_DISABLEDWORLDS,

	KILLING_STARTPOINTS,
	KILLING_RANKPERCENT,
	KILLING_COOLDOWN,
	KILLING_MONEYFORKILL,
	KILLING_MONEYFORREVENGE,

	TABLIST_ENABLED,
	TABLIST_SCHEME;

	private static final ConfigManager cM = NovaGuilds.getInstance() == null ? null : NovaGuilds.getInstance().getConfigManager();
	private final String path;

	Config() {
		path = StringUtils.replace(name(), "_", ".").toLowerCase();
	}

	public String getString() {
		String r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof String ? (String) cM.getEnumConfig(this) : cM.getString(path);
		cM.putInCache(this, r);
		return r;
	}

	public List<String> getStringList() {
		List<String> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<String>) cM.getEnumConfig(this) : cM.getStringList(path);
		cM.putInCache(this, r);
		return r;
	}

	public List<ItemStack> getItemStackList() {
		List<ItemStack> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<ItemStack>) cM.getEnumConfig(this) : cM.getItemStackList(path);
		cM.putInCache(this, r);
		return r;
	}

	public List<Material> getMaterialList() {
		List<Material> r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof List ? (List<Material>) cM.getEnumConfig(this) : cM.getMaterialList(path);
		cM.putInCache(this, r);
		return r;
	}

	public long getLong() {
		long r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Long ? (long) cM.getEnumConfig(this) : cM.getLong(path);
		cM.putInCache(this, r);
		return r;
	}

	public double getDouble() {
		double r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Double ? (double) cM.getEnumConfig(this) : cM.getDouble(path);
		cM.putInCache(this, r);
		return r;
	}

	public int getInt() {
		int r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Integer ? (int) cM.getEnumConfig(this) : cM.getInt(path);
		cM.putInCache(this, r);
		return r;
	}

	public boolean getBoolean() {
		boolean r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Boolean ? (boolean) cM.getEnumConfig(this) : cM.getBoolean(path);
		cM.putInCache(this, r);
		return r;
	}

	public int getSeconds() {
		int r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Integer ? (int) cM.getEnumConfig(this) : cM.getSeconds(path);
		cM.putInCache(this, r);
		return r;
	}

	public ItemStack getItemStack() {
		ItemStack r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof ItemStack ? (ItemStack) cM.getEnumConfig(this) : cM.getItemStack(path);
		cM.putInCache(this, r);
		return r;
	}

	public Material getMaterial() {
		Material r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Material ? (Material) cM.getEnumConfig(this) : cM.getMaterial(path);
		cM.putInCache(this, r);
		return r;
	}

	public byte getMaterialData() {
		byte r = cM.isInCache(this) && cM.getEnumConfig(this) instanceof Byte ? (byte) cM.getEnumConfig(this) : cM.getMaterialData(path);
		cM.putInCache(this, r);
		return r;
	}

	public ConfigurationSection getConfigurationSection() {
		return cM.getConfig().getConfigurationSection(path);
	}

	public static ConfigManager getManager() {
		return cM;
	}

	public void set(Object obj) {
		cM.set(path, obj);
	}

	public static Config fromPath(String path) {
		try {
			return Config.valueOf(StringUtils.replace(path, ".", "_").toUpperCase());
		}
		catch(Exception e) {
			return null;
		}
	}

	public double getPercent() {
		return getDouble() / 100;
	}
}
