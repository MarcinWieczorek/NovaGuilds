package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerGuildImpl extends AbstractYAMLResourceManager<NovaGuild> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerGuildImpl(Storage storage) {
		super(storage, NovaGuild.class, "guild/");
	}

	@Override
	public List<NovaGuild> load() {
		List<NovaGuild> list = new ArrayList<>();

		for(File guildFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(guildFile);

			if(configuration != null) {
				NovaGuild guild = new NovaGuildImpl(UUID.fromString(trimExtension(guildFile)));
				guild.setAdded();
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
				String homeWorldName = configuration.getString("home.world");
				if(homeWorldName == null || homeWorldName.isEmpty()) {
					LoggerUtils.error("Found null or empty world (guild: " + guild.getName() + ")");
					continue;
				}

				World homeWorld = plugin.getServer().getWorld(homeWorldName);
				if(homeWorld == null) {
					LoggerUtils.error("Found invalid world: " + homeWorldName + " (guild: " + guild.getName() + ")");
					continue;
				}

				int x = configuration.getInt("home.x");
				int y = configuration.getInt("home.y");
				int z = configuration.getInt("home.z");
				float yaw = (float) configuration.getDouble("home.yaw");
				Location homeLocation = new Location(homeWorld, x, y, z);
				homeLocation.setYaw(yaw);
				guild.setHome(homeLocation);

				//bankloc
				if(configuration.isConfigurationSection("bankloc")) {
					World vaultWorld = plugin.getServer().getWorld(configuration.getString("bankloc.world"));
					if(vaultWorld != null) {
						x = configuration.getInt("bankloc.x");
						y = configuration.getInt("bankloc.y");
						z = configuration.getInt("bankloc.z");
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
	public boolean save(NovaGuild guild) {
		if(!guild.isChanged()) {
			return false;
		}

		if(!guild.isAdded()) {
			add(guild);
		}

		FileConfiguration guildData = getData(guild);

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

					rank.setUnchanged();
				}

				for(String rankName : rankList) {
					ranksConfigurationSection.set(rankName, null);
				}

				//save
				guildData.save(getFile(guild));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing guild. " + guild.getName());
		}

		return true;
	}

	@Override
	public void remove(NovaGuild guild) {
		if(!guild.isAdded()) {
			return;
		}

		if(getFile(guild).delete()) {
			LoggerUtils.info("Deleted guild " + guild.getName() + "'s file.");
		}
		else {
			LoggerUtils.error("Failed to delete guild " + guild.getName() + "'s file.");
		}
	}

	@Override
	public File getFile(NovaGuild guild) {
		return new File(getDirectory(), guild.getUUID().toString() + ".yml");
	}
}
