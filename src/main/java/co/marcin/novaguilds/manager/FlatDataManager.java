package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FlatDataManager {
	private final NovaGuilds plugin;
	private File dataDir;
	private File playersDir;
	private File guildsDir;
	private File regionsDir;

//	private FileConfiguration playerTemplate;

	public FlatDataManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		setupDirectories();

//		File playerTemplateFile = new File(plugin.getDataFolder(), "defaultuser.yml");
//		playerTemplate = YamlConfiguration.loadConfiguration(playerTemplateFile);
	}

	private void setupDirectories() {
		dataDir = new File(plugin.getDataFolder(),"data/");
		playersDir = new File(plugin.getDataFolder(),"data/players/");
		guildsDir = new File(plugin.getDataFolder(),"data/guilds/");
		regionsDir = new File(plugin.getDataFolder(),"data/regions/");

		if(!dataDir.exists()) {
			if(dataDir.mkdir()) {
				plugin.info("[FlatDataManager] Data directory created");
			}
		}

		if(dataDir.exists()) {
			if(!playersDir.exists()) {
				if(playersDir.mkdir()) {
					plugin.info("[FlatDataManager] Players directory created");
				}
			}

			if(!guildsDir.exists()) {
				if(guildsDir.mkdir()) {
					plugin.info("[FlatDataManager] Guilds directory created");
				}
			}

			if(!regionsDir.exists()) {
				if(regionsDir.mkdir()) {
					plugin.info("[FlatDataManager] Regions directory created");
				}
			}

		}
		else {
			ConfigManager.getLogger().severe("Could not setup directories!");
			ConfigManager.getLogger().severe("Switching to secondary data storage type!");
			plugin.getConfigManager().setToSecondaryDataStorageType();
		}

//		File messagesFile = new File(plugin.getDataFolder() + "/lang", lang + ".yml");
//		if(!messagesFile.exists()) {
//			if(plugin.getResource("lang/" + lang + ".yml") != null) {
//				plugin.saveResource("lang/" + lang + ".yml", false);
//				plugin.info("New messages file created: " + lang + ".yml");
//			}
//			else {
//				plugin.info("Couldn't find language file: " + lang + ".yml");
//				return false;
//			}
//		}
	}

	//save
	public void savePlayer(NovaPlayer nPlayer) {
		FileConfiguration playerData = getPlayerData(nPlayer.getName());

		if(playerData != null) {
			try {
				//set values
				playerData.set("uuid", nPlayer.getUUID().toString());
				playerData.set("name", nPlayer.getName());
				playerData.set("guild", nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
				playerData.set("invitedto",nPlayer.getInvitedTo());

				//save
				playerData.save(getPlayerFile(nPlayer.getName()));
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		else {
			plugin.debug("[FlatDataManager] Attempting to save non-existing player. "+nPlayer.getName());
		}
	}

	//save
	public void saveRegion(NovaRegion region) {
		FileConfiguration regionData = getRegionData(region);

		if(regionData != null) {
			try {
				//set values
				regionData.set("world",region.getWorld().getName());
				regionData.set("guild",region.getGuild().getName());

				//corners
				regionData.set("corner1.x",region.getCorner(0).getBlockX());
				regionData.set("corner1.z",region.getCorner(0).getBlockZ());

				regionData.set("corner2.x",region.getCorner(1).getBlockX());
				regionData.set("corner2.z",region.getCorner(1).getBlockZ());

				//save
				regionData.save(getRegionFile(region.getGuild().getName()));
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		else {
			plugin.debug("[FlatDataManager] Attempting to save non-existing region. " + region.getGuild().getName());
		}
	}

	public void saveGuild(NovaGuild guild) {
		FileConfiguration guildData = getGuildData(guild);

		if(guildData != null) {
			try {
				//set values
				guildData.set("id",guild.getId());
				guildData.set("money",guild.getMoney());
				guildData.set("points",guild.getPoints());
				guildData.set("name",guild.getName());
				guildData.set("tag",guild.getTag());
				guildData.set("leader",guild.getLeader()==null ? "" : guild.getLeader().getName());
				guildData.set("lives",guild.getLives());

				guildData.set("allies",guild.getAlliesNames());
				guildData.set("wars",guild.getWarsNames());
				guildData.set("nowar",guild.getNoWarInvitations());
				guildData.set("alliesinv",guild.getAllyInvitations());

				guildData.set("activity",guild.getInactiveTime());
				guildData.set("timerest",guild.getTimeRest());
				guildData.set("lostlive",guild.getLostLiveTime());

				//spawnpoint
				Location home = guild.getSpawnPoint();
				guildData.set("home.world",home.getWorld().getName());
				guildData.set("home.x",home.getBlockX());
				guildData.set("home.y",home.getBlockY());
				guildData.set("home.z",home.getBlockZ());
				guildData.set("home.yaw",home.getYaw());

				//save
				guildData.save(getGuildFile(guild.getName()));
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		else {
			plugin.debug("[FlatDataManager] Attempting to save non-existing guild. "+guild.getName());
		}
	}

	//delete
	public void deleteGuild(NovaGuild guild) {
		boolean deleted = getGuildFile(guild.getName()).delete();

		if(deleted) {
			plugin.info("[FlatDataManager] Deleted guild "+guild.getName()+"'s file.");
		}
		else {
			plugin.info("[FlatDataManager] Failed to delete guild " + guild.getName() + "'s file.");
		}
	}

	public void deleteRegion(NovaRegion region) {
		boolean deleted = getRegionFile(region.getGuild().getName()).delete();

		if(deleted) {
			plugin.info("[FlatDataManager] Deleted guild "+region.getGuild().getName()+" region's file.");
		}
		else {
			plugin.info("[FlatDataManager] Failed to delete guild " + region.getGuild().getName() + " region's file.");
		}
	}

	//add
	public void addPlayer(NovaPlayer nPlayer) {
		savePlayerTemplate(nPlayer.getName());
		savePlayer(nPlayer);
	}

	public void addGuild(NovaGuild guild) {
		saveGuildTemplate(guild.getName());
		saveGuild(guild);
	}

	public void addRegion(NovaRegion region) {
		saveRegionTemplate(region.getGuild().getName());
		saveRegion(region);
	}

	private void savePlayerTemplate(String name) {
		saveInputStreamToFile(plugin.getResource("templates/player.yml"), getPlayerFile(name));
	}

	private void saveGuildTemplate(String name) {
		//saveInputStreamToFile(plugin.getResource("templates/player.yml"), getGuildFile(name));
		try {
			getGuildFile(name).createNewFile();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void saveRegionTemplate(String name) {
		saveInputStreamToFile(plugin.getResource("templates/player.yml"), getRegionFile(name));
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

	//util
	private static void saveInputStreamToFile(InputStream inputStream, File file) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);

			int read;
			byte[] bytes = new byte[1024];

			while((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStream != null) {
				try {
					outputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public List<String> getPlayerList() {
		return getFilesWithoutExtension(playersDir);
	}

	public List<String> getGuildList() {
		return getFilesWithoutExtension(guildsDir);
	}

	public List<String> getRegionList() {
		return getFilesWithoutExtension(regionsDir);
	}

	public List<String> getFilesWithoutExtension(File directory) {
		List<String> list = new ArrayList<>();
		File[] filesList = directory.listFiles();

		if(filesList != null) {
			for(File file : filesList) {
				if(file.isFile()) {
					String name = file.getName();
					if(name.contains(".")) {
						name = name.split(".")[0];
					}

					list.add(name);
				}
			}
		}

		return list;
	}
}
