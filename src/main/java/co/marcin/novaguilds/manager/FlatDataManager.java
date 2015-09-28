package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.util.IOUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlatDataManager {
	private File playersDir;
	private File guildsDir;
	private File regionsDir;
	private File dataDirectory;

	private boolean connected = false;

	public FlatDataManager(NovaGuilds novaGuilds) {
		dataDirectory = novaGuilds.getDataFolder();
		connected = setupDirectories();
	}

	private boolean setupDirectories() {
		File dataDir = new File(dataDirectory, "data/");
		playersDir = new File(dataDirectory,"data/players/");
		guildsDir = new File(dataDirectory,"data/guilds/");
		regionsDir = new File(dataDirectory,"data/regions/");

		if(!dataDir.exists()) {
			if(dataDir.mkdir()) {
				LoggerUtils.info("Data directory created");
			}
		}

		if(dataDir.exists()) {
			if(!playersDir.exists()) {
				if(playersDir.mkdir()) {
					LoggerUtils.info("Players directory created");
				}
			}

			if(!guildsDir.exists()) {
				if(guildsDir.mkdir()) {
					LoggerUtils.info("Guilds directory created");
				}
			}

			if(!regionsDir.exists()) {
				if(regionsDir.mkdir()) {
					LoggerUtils.info("Regions directory created");
				}
			}

		}
		else {
			LoggerUtils.error("Could not setup directories!");
			LoggerUtils.error("Switching to secondary data storage type!");
			return false;
		}

		return true;
	}

	//save
	public void save(NovaPlayer nPlayer) {
		FileConfiguration playerData = getPlayerData(nPlayer.getName());

		if(playerData != null) {
			try {
				//set values
				playerData.set("uuid", nPlayer.getUUID().toString());
				playerData.set("name", nPlayer.getName());
				playerData.set("guild", nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
				playerData.set("invitedto",nPlayer.getInvitedTo());
				playerData.set("points",nPlayer.getPoints());
				playerData.set("kills",nPlayer.getKills());
				playerData.set("deaths",nPlayer.getDeaths());

				//save
				playerData.save(getPlayerFile(nPlayer.getName()));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing player. "+nPlayer.getName());
		}
	}

	//save
	public void save(NovaRegion region) {
		FileConfiguration regionData = getRegionData(region);

		if(regionData != null) {
			try {
				//set values
				regionData.set("world",region.getWorld().getName());
				regionData.set("guild",region.getGuild().getName());

				//corners
				regionData.set("corner1.x", region.getCorner(0).getBlockX());
				regionData.set("corner1.z",region.getCorner(0).getBlockZ());

				regionData.set("corner2.x",region.getCorner(1).getBlockX());
				regionData.set("corner2.z",region.getCorner(1).getBlockZ());

				//save
				regionData.save(getRegionFile(region.getGuild().getName()));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing region. " + region.getGuild().getName());
		}
	}

	public void save(NovaGuild guild) {
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

				//set values
				guildData.set("id",guild.getId());
				guildData.set("name",guild.getName());
				guildData.set("tag",guild.getTag());
				guildData.set("leader",guild.getLeader()==null ? "" : guild.getLeader().getName());
				guildData.set("allies", alliesNames);
				guildData.set("alliesinv", guild.getAllyInvitations());
				guildData.set("wars", warsNames);
				guildData.set("nowar", guild.getNoWarInvitations());
				guildData.set("money",guild.getMoney());
				guildData.set("points",guild.getPoints());
				guildData.set("lives",guild.getLives());
				guildData.set("slots", guild.getSlots());

				guildData.set("timerest",guild.getTimeRest());
				guildData.set("lostlive",guild.getLostLiveTime());
				guildData.set("activity",guild.getInactiveTime());
				guildData.set("created",guild.getTimeCreated());

				//spawnpoint
				Location home = guild.getSpawnPoint();
				guildData.set("home.world",home.getWorld().getName());
				guildData.set("home.x",home.getBlockX());
				guildData.set("home.y",home.getBlockY());
				guildData.set("home.z",home.getBlockZ());
				guildData.set("home.yaw",home.getYaw());

				//bankloc
				Location bankloc = guild.getVaultLocation();
				if(bankloc != null) {
					guildData.set("bankloc.world", bankloc.getWorld().getName());
					guildData.set("bankloc.x", bankloc.getBlockX());
					guildData.set("bankloc.y", bankloc.getBlockY());
					guildData.set("bankloc.z", bankloc.getBlockZ());
				}
				else {
					guildData.set("bankloc", null);
				}

				//save
				guildData.save(getGuildFile(guild.getName()));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing guild. " + guild.getName());
		}
	}

	//delete
	public void delete(NovaGuild guild) {
		boolean deleted = getGuildFile(guild.getName()).delete();

		if(deleted) {
			LoggerUtils.info("Deleted guild "+guild.getName()+"'s file.");
		}
		else {
			LoggerUtils.info("Failed to delete guild " + guild.getName() + "'s file.");
		}
	}

	public void delete(NovaRegion region) {
		boolean deleted = getRegionFile(region.getGuild().getName()).delete();

		if(deleted) {
			LoggerUtils.info("Deleted guild "+region.getGuild().getName()+" region's file.");
		}
		else {
			LoggerUtils.error("Failed to delete guild " + region.getGuild().getName() + " region's file.");
		}
	}

	//add
	public void add(NovaPlayer nPlayer) {
		savePlayerTemplate(nPlayer.getName());
		save(nPlayer);
	}

	public void add(NovaGuild guild) {
		saveGuildTemplate(guild.getName());
		save(guild);
	}

	public void add(NovaRegion region) {
		saveRegionTemplate(region.getGuild().getName());
		save(region);
	}

	private void savePlayerTemplate(String name) {
		try {
			getPlayerFile(name).createNewFile();
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
	}

	private void saveGuildTemplate(String name) {
		try {
			getGuildFile(name).createNewFile();
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
	}

	private void saveRegionTemplate(String name) {
		try {
			getRegionFile(name).createNewFile();
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
	}

	//Get files
	private File getPlayerFile(String name) {
		return new File(playersDir +"/"+ name + ".yml");
	}

	private File getGuildFile(String name) {
		return new File(guildsDir +"/"+ name + ".yml");
	}

	private File getRegionFile(String name) {
		return new File(regionsDir +"/"+ name + ".yml");
	}

	//get data
	public FileConfiguration getPlayerData(String name) {
		File file = getPlayerFile(name);

		return file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
	}

	public FileConfiguration getGuildData(NovaGuild guild) {
		return getGuildData(guild.getName());
	}

	public FileConfiguration getGuildData(String name) {
		File file = getGuildFile(name);

		return file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
	}

	public FileConfiguration getRegionData(NovaRegion region) {
		return getRegionData(region.getGuild().getName());
	}

	public FileConfiguration getRegionData(String name) {
		File file = getRegionFile(name);

		return file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
	}

	public List<String> getPlayerList() {
		return IOUtils.getFilesWithoutExtension(playersDir);
	}

	public List<String> getGuildList() {
		return IOUtils.getFilesWithoutExtension(guildsDir);
	}

	public List<String> getRegionList() {
		return IOUtils.getFilesWithoutExtension(regionsDir);
	}

	public boolean isConnected() {
		return connected;
	}
}
