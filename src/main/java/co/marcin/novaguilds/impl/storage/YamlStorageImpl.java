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

package co.marcin.novaguilds.impl.storage;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YamlStorageImpl extends AbstractFileStorage {
	public YamlStorageImpl(File dataDirectory) {
		super(dataDirectory);
		setExtension("yml");
	}

	@Override
	public List<NovaPlayer> loadPlayers() {
		List<NovaPlayer> list = new ArrayList<>();

		for(File playerFile : getPlayerFiles()) {
			FileConfiguration configuration = fileToFileConfiguration(playerFile);

			if(configuration != null) {
				UUID uuid = UUID.fromString(configuration.getString("uuid"));
				NovaPlayer nPlayer = new NovaPlayer(uuid);

				Player player = plugin.getServer().getPlayer(uuid);

				if(player != null) {
					if(player.isOnline()) {
						nPlayer.setPlayer(player);
					}
				}

				nPlayer.setName(configuration.getString("name"));
				List<NovaGuild> invitedToList = plugin.getGuildManager().nameListToGuildsList(configuration.getStringList("invitedto"));
				nPlayer.setInvitedTo(invitedToList);

				nPlayer.setPoints(configuration.getInt("points"));
				nPlayer.setKills(configuration.getInt("kills"));
				nPlayer.setDeaths(configuration.getInt("deaths"));

				String guildName = configuration.getString("guild").toLowerCase();
				if(!guildName.isEmpty()) {
					NovaGuild guild = GuildManager.getGuildByName(guildName);

					if(guild != null) {
						guild.addPlayer(nPlayer);
					}
				}

				nPlayer.setUnchanged();

				list.add(nPlayer);
			}
		}

		return list;
	}

	@Override
	public List<NovaGuild> loadGuilds() {
		List<NovaGuild> list = new ArrayList<>();

		for(File guildFile : getGuildFiles()) {
			FileConfiguration configuration = fileToFileConfiguration(guildFile);

			if(configuration != null) {
				NovaGuild guild = new NovaGuildImpl(UUID.fromString(trimExtension(guildFile)));
				guild.setId(configuration.getInt("id"));
				guild.setName(configuration.getString("name"));
				guild.setTag(configuration.getString("tag"));
				guild.setLeaderName(configuration.getString("leader"));

				guild.setAlliesNames(configuration.getStringList("allies"));
				guild.setWarsNames(configuration.getStringList("wars"));
				guild.setNoWarInvitations(configuration.getStringList("nowar"));
				guild.setAllyInvitationNames(configuration.getStringList("alliesinv"));

				guild.setMoney(configuration.getDouble("money"));
				guild.setPoints(configuration.getInt("points"));
				guild.setLives(configuration.getInt("lives"));
				guild.setSlots(configuration.getInt("slots"));

				guild.setTimeRest(configuration.getLong("timerest"));
				guild.setLostLiveTime(configuration.getLong("lostlive"));
				guild.setInactiveTime(configuration.getLong("activity"));
				guild.setTimeCreated(configuration.getLong("created"));
				guild.setOpenInvitation(configuration.getBoolean("openinv"));

				//home
				World homeWorld = plugin.getServer().getWorld(configuration.getString("home.world"));
				if(homeWorld != null) {
					int x = configuration.getInt("home.x");
					int y = configuration.getInt("home.y");
					int z = configuration.getInt("home.z");
					float yaw = (float) configuration.getDouble("home.yaw");
					Location homeLocation = new Location(homeWorld, x, y, z);
					homeLocation.setYaw(yaw);
					guild.setSpawnPoint(homeLocation);
				}

				//bankloc
				if(configuration.isConfigurationSection("bankloc")) {
					World vaultWorld = plugin.getServer().getWorld(configuration.getString("bankloc.world"));
					if(vaultWorld != null) {
						int x = configuration.getInt("bankloc.x");
						int y = configuration.getInt("bankloc.y");
						int z = configuration.getInt("bankloc.z");
						Location vaultLocation = new Location(vaultWorld, x, y, z);
						guild.setVaultLocation(vaultLocation);
					}
				}

				guild.setUnchanged();

				//Fix slots amount
				if(guild.getSlots() <= 0) {
					guild.setSlots(Config.GUILD_SLOTS_START.getInt());
				}

				list.add(guild);
			}
		}

		return list;
	}

	@Override
	public List<NovaRegion> loadRegions() {
		List<NovaRegion> list = new ArrayList<>();

		for(File regionFile : getRegionFiles()) {
			FileConfiguration configuration = fileToFileConfiguration(regionFile);

			if(configuration != null) {
				World world = plugin.getServer().getWorld(configuration.getString("world"));

				if(world != null) {
					String guildName = trimExtension(regionFile);
					NovaGuild guild = GuildManager.getGuildFind(guildName);

					if(guild == null) {
						LoggerUtils.error("There's no guild matching region " + guildName);
						continue;
					}

					NovaRegion region = new NovaRegion();

					Location c1 = new Location(world, configuration.getInt("corner1.x"), 0, configuration.getInt("corner1.z"));
					Location c2 = new Location(world, configuration.getInt("corner2.x"), 0, configuration.getInt("corner2.z"));

					region.setCorner(0, c1);
					region.setCorner(1, c2);
					region.setWorld(world);
					guild.setRegion(region);
					region.setUnChanged();

					list.add(region);
				}
			}
		}

		return list;
	}

	@Override
	public List<NovaRank> loadRanks() {
		List<NovaRank> list = new ArrayList<>();

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			boolean fixPlayerList = false;

			FileConfiguration guildData = getGuildData(guild);
			ConfigurationSection ranksConfigurationSection = guildData.getConfigurationSection("ranks");
			List<String> rankNamesList = new ArrayList<>();

			if(!guildData.isConfigurationSection("ranks")) {
				continue;
			}

			rankNamesList.addAll(ranksConfigurationSection.getKeys(false));

			for(String rankName : rankNamesList) {
				ConfigurationSection rankConfiguration = ranksConfigurationSection.getConfigurationSection(rankName);

				NovaRank rank = new NovaRank(0);
				rank.setName(rankName);

				List<String> permissionsStringList = rankConfiguration.getStringList("permissions");
				List<GuildPermission> permissionsList = new ArrayList<>();
				for(String permissionString : permissionsStringList) {
					permissionsList.add(GuildPermission.valueOf(permissionString));
				}
				rank.setPermissions(permissionsList);

				guild.addRank(rank);
				rank.setGuild(guild);

				for(String playerName : rankConfiguration.getStringList("members")) {
					NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playerName);

					if(nPlayer == null) {
						LoggerUtils.error("Player " + playerName + " doesn't exist, cannot be added to rank '" + rank.getName() + "' of guild " + rank.getGuild().getName());
						fixPlayerList = true;
						continue;
					}

					rank.addMember(nPlayer);
				}

				rank.setDefault(rankConfiguration.getBoolean("def"));
				rank.setClone(rankConfiguration.getBoolean("clone"));

				if(!fixPlayerList) {
					rank.setUnchanged();
				}

				list.add(rank);
			}
		}

		return list;
	}

	@Override
	public void add(NovaPlayer nPlayer) {
		if(createFileIfNotExists(getPlayerFile(nPlayer))) {
			save(nPlayer);
		}
	}

	@Override
	public void add(NovaGuild guild) {
		if(createFileIfNotExists(getGuildFile(guild))) {
			save(guild);
		}
	}

	@Override
	public void add(NovaRegion region) {
		if(createFileIfNotExists(getRegionFile(region))) {
			save(region);
		}
	}

	@Override
	public void add(NovaRank rank) {
		throw new UnsupportedOperationException("Not supported yet");
	}

	@Override
	public void save(NovaPlayer nPlayer) {
		if(!nPlayer.isChanged()) {
			return;
		}

		FileConfiguration playerData = getPlayerData(nPlayer);

		if(playerData != null) {
			try {
				//set values
				playerData.set("uuid", nPlayer.getUUID().toString());
				playerData.set("name", nPlayer.getName());
				playerData.set("guild", nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
				playerData.set("invitedto", nPlayer.getInvitedTo());
				playerData.set("points", nPlayer.getPoints());
				playerData.set("kills", nPlayer.getKills());
				playerData.set("deaths", nPlayer.getDeaths());

				//save
				playerData.save(getPlayerFile(nPlayer));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing player. " + nPlayer.getName());
		}
	}

	@Override
	public void save(NovaGuild guild) {
		if(!guild.isChanged()) {
			return;
		}

		FileConfiguration guildData = getGuildData(guild);

		if(guildData != null) {
			try {
				List<String> alliesNames = new ArrayList<>();
				for(NovaGuild ally : guild.getAllies()) {
					alliesNames.add(ally.getName());
				}

				List<String> warsNames = new ArrayList<>();
				for(NovaGuild war : guild.getWars()) {
					warsNames.add(war.getName());
				}

				List<String> allyInvitationNames = new ArrayList<>();
				for(NovaGuild guildLoop : guild.getAllyInvitations()) {
					allyInvitationNames.add(guildLoop.getName());
				}

				//set values
				guildData.set("id", guild.getId());
				guildData.set("name", guild.getName());
				guildData.set("tag", guild.getTag());
				guildData.set("leader", guild.getLeader().getName());
				guildData.set("allies", alliesNames);
				guildData.set("alliesinv", allyInvitationNames);
				guildData.set("wars", warsNames);
				guildData.set("nowar", guild.getNoWarInvitations());
				guildData.set("money", guild.getMoney());
				guildData.set("points", guild.getPoints());
				guildData.set("lives", guild.getLives());
				guildData.set("slots", guild.getSlots());

				guildData.set("timerest", guild.getTimeRest());
				guildData.set("lostlive", guild.getLostLiveTime());
				guildData.set("activity", guild.getInactiveTime());
				guildData.set("created", guild.getTimeCreated());
				guildData.set("openinv", guild.isOpenInvitation());

				//spawnpoint
				Location home = guild.getHome();
				guildData.set("home.world", home.getWorld().getName());
				guildData.set("home.x", home.getBlockX());
				guildData.set("home.y", home.getBlockY());
				guildData.set("home.z", home.getBlockZ());
				guildData.set("home.yaw", home.getYaw());

				//bankloc
				Location vaultLocation = guild.getVaultLocation();
				if(vaultLocation != null) {
					guildData.set("bankloc.world", vaultLocation.getWorld().getName());
					guildData.set("bankloc.x", vaultLocation.getBlockX());
					guildData.set("bankloc.y", vaultLocation.getBlockY());
					guildData.set("bankloc.z", vaultLocation.getBlockZ());
				}
				else {
					guildData.set("bankloc", null);
				}

				//Ranks
				if(!guildData.isConfigurationSection("ranks")) {
					guildData.createSection("ranks");
				}

				ConfigurationSection ranksConfigurationSection = guildData.getConfigurationSection("ranks");
				List<String> rankList = new ArrayList<>(ranksConfigurationSection.getKeys(false));

				for(NovaRank rank : guild.getRanks()) {
					rankList.remove(rank.getName());

					if(!rank.isChanged()) {
						continue;
					}

					//Permission list
					List<String> permissionNamesList = new ArrayList<>();
					for(GuildPermission permission : rank.getPermissions()) {
						permissionNamesList.add(permission.name());
					}

					//Member list
					List<String> memberNamesList = new ArrayList<>();
					if(!rank.isDefault()) {
						for(NovaPlayer nPlayer : rank.getMembers()) {
							memberNamesList.add(nPlayer.getName());
						}
					}

					if(!ranksConfigurationSection.isConfigurationSection(rank.getName())) {
						ranksConfigurationSection.createSection(rank.getName());
					}

					ranksConfigurationSection.set(rank.getName() + ".members", memberNamesList);
					ranksConfigurationSection.set(rank.getName() + ".permissions", permissionNamesList);
					ranksConfigurationSection.set(rank.getName() + ".def", rank.isDefault());
					ranksConfigurationSection.set(rank.getName() + ".clone", rank.isClone());
					LoggerUtils.debug("Saved rank: " + rank.getName());

					rank.setUnchanged();
				}

				for(String rankName : rankList) {
					LoggerUtils.debug("clearing rank " + rankName);
					ranksConfigurationSection.set(rankName, null);
				}

				//save
				guildData.save(getGuildFile(guild));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing guild. " + guild.getName());
		}
	}

	@Override
	public void save(NovaRegion region) {
		if(!region.isChanged()) {
			return;
		}

		FileConfiguration regionData = getRegionData(region);

		if(regionData != null) {
			try {
				//set values
				regionData.set("world", region.getWorld().getName());

				//corners
				regionData.set("corner1.x", region.getCorner(0).getBlockX());
				regionData.set("corner1.z", region.getCorner(0).getBlockZ());

				regionData.set("corner2.x", region.getCorner(1).getBlockX());
				regionData.set("corner2.z", region.getCorner(1).getBlockZ());

				//save
				regionData.save(getRegionFile(region));
				region.setUnChanged();
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing region. " + region.getGuild().getName());
		}
	}

	@Override
	public void save(NovaRank rank) {
		rank.getGuild().changed();
		save(rank.getGuild());
	}

	@Override
	public void remove(NovaPlayer nPlayer) {
		if(getPlayerFile(nPlayer).delete()) {
			LoggerUtils.info("Deleted player " + nPlayer.getName() + "'s file.");
		}
		else {
			LoggerUtils.error("Failed to delete player " + nPlayer.getName() + "'s file.");
		}
	}

	@Override
	public void remove(NovaGuild guild) {
		if(getGuildFile(guild).delete()) {
			LoggerUtils.info("Deleted guild " + guild.getName() + "'s file.");
		}
		else {
			LoggerUtils.error("Failed to delete guild " + guild.getName() + "'s file.");
		}
	}

	@Override
	public void remove(NovaRegion region) {
		if(getRegionFile(region).delete()) {
			LoggerUtils.info("Deleted guild " + region.getGuild().getName() + " region's file.");
		}
		else {
			LoggerUtils.error("Failed to delete guild " + region.getGuild().getName() + " region's file.");
		}
	}

	@Override
	public void remove(NovaRank rank) {
		save(rank.getGuild());
	}

	//Getting data
	private FileConfiguration getPlayerData(NovaPlayer nPlayer) {
		File file = getPlayerFile(nPlayer);
		return fileToFileConfiguration(file);
	}

	private FileConfiguration getGuildData(NovaGuild guild) {
		File file = getGuildFile(guild);
		return fileToFileConfiguration(file);
	}

	private FileConfiguration getRegionData(NovaRegion region) {
		File file = getRegionFile(region);
		return fileToFileConfiguration(file);
	}

	private FileConfiguration getRankData(NovaRank rank) {
		File file = getGuildFile(rank.getGuild());
		FileConfiguration configuration = fileToFileConfiguration(file);

		if(configuration == null) {
			return null;
		}

		return (FileConfiguration) configuration.getConfigurationSection("ranks");
	}

	private FileConfiguration fileToFileConfiguration(File file) {
		return file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
	}

	private boolean createFileIfNotExists(File file) {
		if(!file.exists()) {
			try {
				if(!file.createNewFile()) {
					throw new IOException("File creating failed (" + file.getPath() + ")");
				}
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
				return false;
			}
		}

		return true;
	}
}
