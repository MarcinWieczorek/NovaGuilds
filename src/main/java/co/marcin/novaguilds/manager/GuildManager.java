package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class GuildManager {
	private final NovaGuilds plugin;
	private final HashMap<String,NovaGuild> guilds = new HashMap<>();
	
	public GuildManager(NovaGuilds pl) {
		plugin = pl;
	}
	
	//getters
	public NovaGuild getGuildByName(String name) {
		return guilds.get(name.toLowerCase());
	}
	
	public NovaGuild getGuildByTag(String tag) {
		for(NovaGuild guild : getGuilds()) {
			if(StringUtils.removeColors(guild.getTag()).equalsIgnoreCase(tag)) {
				return guild;
			}
		}
		return null;
	}

	/*
	* Find by player/tag/guildname
	* @Param: String mixed
	* */
	public NovaGuild getGuildFind(String mixed) {
		NovaGuild guild = getGuildByTag(mixed);

		if(guild == null) {
			guild = getGuildByName(mixed);
		}
		
		if(guild == null) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(mixed);
			
			if(nPlayer == null) {
				return null;
			}
			
			guild = nPlayer.getGuild();
		}

		return guild;
	}

	public Collection<NovaGuild> getGuilds() {
		return guilds.values();
	}
	
	public boolean exists(String guildname) {
		return guilds.containsKey(guildname.toLowerCase());
	}

	public List<NovaGuild> nameListToGuildsList(List<String> namesList) {
		List<NovaGuild> invitedToList = new ArrayList<>();

		for(String guildName : namesList) {
			invitedToList.add(getGuildByName(guildName));
		}

		return invitedToList;
	}

	public void loadGuilds() {
		guilds.clear();

		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			for(String guildName : plugin.getFlatDataManager().getGuildList()) {
				FileConfiguration guildData = plugin.getFlatDataManager().getGuildData(guildName);
				NovaGuild guild = guildFromFlat(guildData);

				if(guild != null) {
					guilds.put(guildName.toLowerCase(),guild);
				}
				else {
					LoggerUtils.info("Loaded guild is null. name: "+guildName);
				}
			}
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				PreparedStatement statement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_SELECT);
				ResultSet res = statement.executeQuery();
				//ResultSet res = statement.executeQuery("SELECT * FROM `" + plugin.getConfigManager().getDatabasePrefix() + "guilds`");
				while(res.next()) {
					String spawnpoint_coord = res.getString("spawn");

					Location spawnpoint = null;
					if(!spawnpoint_coord.isEmpty()) {
						String[] spawnpoint_split = spawnpoint_coord.split(";");
						if(spawnpoint_split.length == 5) { //LENGTH
							String worldname = spawnpoint_split[0];

							if(plugin.getServer().getWorld(worldname) != null) {
								int x = Integer.parseInt(spawnpoint_split[1]);
								int y = Integer.parseInt(spawnpoint_split[2]);
								int z = Integer.parseInt(spawnpoint_split[3]);
								float yaw = Float.parseFloat(spawnpoint_split[4]);
								spawnpoint = new Location(plugin.getServer().getWorld(worldname), x, y, z);
								spawnpoint.setYaw(yaw);
							}
						}
					}

					String bankLocationString = res.getString("bankloc");
					Location bankLocation = null;
					if(!bankLocationString.isEmpty()) {
						String[] bankLocationSplit = bankLocationString.split(";");
						if(bankLocationSplit.length == 5) { //LENGTH
							String worldname = bankLocationSplit[0];

							if(plugin.getServer().getWorld(worldname) != null) {
								int x = Integer.parseInt(bankLocationSplit[1]);
								int y = Integer.parseInt(bankLocationSplit[2]);
								int z = Integer.parseInt(bankLocationSplit[3]);
								bankLocation = new Location(plugin.getServer().getWorld(worldname), x, y, z);
							}
						}
					}

					//load guild only if there is a spawnpoint.
					//error protection if a world has been deleted
					if(spawnpoint != null) {
						List<String> allies = new ArrayList<>();
						List<String> alliesinv = new ArrayList<>();
						List<String> wars = new ArrayList<>();
						List<String> nowarinv = new ArrayList<>();

						if(!res.getString("allies").isEmpty()) {
							allies = StringUtils.semicolonToList(res.getString("allies"));
						}

						if(!res.getString("alliesinv").isEmpty()) {
							alliesinv = StringUtils.semicolonToList(res.getString("alliesinv"));
						}

						if(!res.getString("war").isEmpty()) {
							wars = StringUtils.semicolonToList(res.getString("war"));
						}

						if(!res.getString("nowarinv").isEmpty()) {
							nowarinv = StringUtils.semicolonToList(res.getString("nowarinv"));
						}

						NovaGuild novaGuild = new NovaGuild();
						novaGuild.setId(res.getInt("id"));
						novaGuild.setMoney(res.getDouble("money"));
						novaGuild.setPoints(res.getInt("points"));
						novaGuild.setName(res.getString("name"));
						novaGuild.setTag(res.getString("tag"));
						novaGuild.setLeaderName(res.getString("leader"));
						novaGuild.setLives(res.getInt("lives"));
						novaGuild.setTimeRest(res.getLong("timerest"));
						novaGuild.setLostLiveTime(res.getLong("lostlive"));
						novaGuild.setSpawnPoint(spawnpoint);
						novaGuild.setRegion(plugin.getRegionManager().getRegion(novaGuild));
						novaGuild.setBankLocation(bankLocation);
						LoggerUtils.debug("regionnull=" + (novaGuild.getRegion() == null));

						novaGuild.setAllies(allies);
						novaGuild.setAllyInvitations(alliesinv);

						novaGuild.setWarsNames(wars);
						novaGuild.setNoWarInvitations(nowarinv);
						novaGuild.setInactiveTime(res.getLong("activity"));
						novaGuild.setUnchanged();

						//Done in NovaGuild.java (setRegion())
//						if(novaGuild.hasRegion()) {
//							novaGuild.getRegion().setGuild(novaGuild);
//						}

						//LoggerUtils.debug("id = " + novaGuild.getId());
						if(novaGuild.getId() > 0) {
							guilds.put(res.getString("name").toLowerCase(), novaGuild);
						}
						else {
							LoggerUtils.info("Failed to load guild " + res.getString("name") + ". Invalid ID");
						}
					}
					else {
						LoggerUtils.info("Failed loading guild " + res.getString("name") + ", world does not exist");
					}
				}
			}
			catch(SQLException e) {
				LoggerUtils.info("An error occured while loading guilds!");
				LoggerUtils.exception(e);
			}
		}

		LoggerUtils.info("Loaded "+guilds.size()+" guilds.");

		loadBankHolograms();
		LoggerUtils.info("Generated bank holograms.");
	}
	
	public void addGuild(NovaGuild guild) {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			plugin.getFlatDataManager().addGuild(guild);
			guilds.put(guild.getName().toLowerCase(), guild);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				String spawnpointcoords = "";
				if(guild.getSpawnPoint() != null) {
					spawnpointcoords = StringUtils.parseDBLocation(guild.getSpawnPoint());
				}

				//adding to MySQL
				//id,tag,name,leader,home,allies,alliesinv,wars,nowarinv,money,points,lives,timerest,lostlive,bankloc
//				String pSQL = "INSERT INTO `" + plugin.getConfigManager().getDatabasePrefix() + "guilds` VALUES(0,?,?,?,?,'','','','',?,?,?,0,0,0,'');";
//				PreparedStatement preparedStatement = plugin.getDatabaseManager().getConnection().prepareStatement(pSQL, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_INSERT);
				preparedStatement.setString(1, guild.getTag()); //tag
				preparedStatement.setString(2, guild.getName()); //name
				preparedStatement.setString(3, guild.getLeader().getName()); //leader
				preparedStatement.setString(4, spawnpointcoords); //home
				preparedStatement.setDouble(5, guild.getMoney()); //money
				preparedStatement.setInt(6, guild.getPoints()); //points
				preparedStatement.setInt(7, guild.getLives()); //lives

				preparedStatement.execute();
				ResultSet keys = preparedStatement.getGeneratedKeys();
				int id = 0;
				if(keys.next()) {
					id = keys.getInt(1);
				}

				if(id > 0) {
					guild.setId(id);
					guilds.put(guild.getName().toLowerCase(), guild);
					NovaPlayer leader = guild.getLeader();
					leader.setGuild(guild);
					guild.setUnchanged();
				}
			}
			catch(SQLException e) {
				LoggerUtils.info("SQLException while adding a guild!");
				LoggerUtils.exception(e);
			}
		}
	}
	
	public void saveGuild(NovaGuild guild) {
		if(guild.isChanged()) {
			if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
				plugin.getFlatDataManager().saveGuild(guild);
			}
			else {
				if(!plugin.getDatabaseManager().isConnected()) {
					LoggerUtils.info("Connection is not estabilished, stopping current action");
					return;
				}

				plugin.getDatabaseManager().mysqlReload();

				try {
					PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_UPDATE);

					String spawnpointcoords = "";
					if(guild.getSpawnPoint() != null) {
						spawnpointcoords = StringUtils.parseDBLocation(guild.getSpawnPoint());
					}

					//ALLIES
					String allies = "";
					String alliesinv = "";

					if(!guild.getAllies().isEmpty()) {
						for(NovaGuild ally : guild.getAllies()) {
							if(!allies.equals("")) {
								allies += ";";
							}

							allies += ally.getName();
						}
					}

					if(!guild.getAllyInvitations().isEmpty()) {
						for(String allyinv : guild.getAllyInvitations()) {
							if(!alliesinv.equals("")) {
								alliesinv += ";";
							}

							alliesinv = alliesinv + allyinv;
						}
					}

					//WARS
					String wars = "";
					String nowar_inv = "";

					if(!guild.getWars().isEmpty()) {
						for(NovaGuild war : guild.getWars()) {
							if(!wars.equals("")) {
								wars += ";";
							}

							wars += war.getName();
						}
					}

					if(!guild.getNoWarInvitations().isEmpty()) {
						for(String nowarinv : guild.getNoWarInvitations()) {
							if(!nowar_inv.equals("")) {
								nowar_inv += ";";
							}

							nowar_inv = nowar_inv + nowarinv;
						}
					}

					String bankLocationString = "";
					if(guild.getBankLocation() != null) {
						bankLocationString = StringUtils.parseDBLocation(guild.getBankLocation());
					}

					preparedStatement.setString(1, guild.getTag());
					preparedStatement.setString(2, guild.getName());
					preparedStatement.setString(3, guild.getLeader().getName());
					preparedStatement.setString(4, spawnpointcoords);
					preparedStatement.setString(5, allies);
					preparedStatement.setString(6, alliesinv);
					preparedStatement.setString(7, wars);
					preparedStatement.setString(8, nowar_inv);
					preparedStatement.setDouble(9, guild.getMoney());
					preparedStatement.setInt(10, guild.getPoints());
					preparedStatement.setInt(11, guild.getLives());
					preparedStatement.setLong(12, guild.getTimeRest());
					preparedStatement.setLong(13, guild.getLostLiveTime());
					preparedStatement.setLong(14, guild.getInactiveTime());
					preparedStatement.setString(15, bankLocationString);

					preparedStatement.setInt(16, guild.getId());

					preparedStatement.executeUpdate();
					guild.setUnchanged();
				}
				catch(SQLException e) {
					LoggerUtils.info("SQLException while saving a guild.");
					LoggerUtils.exception(e);
				}
			}
		}
	}
	
	public void saveAll() {
		for(Entry<String, NovaGuild> g : guilds.entrySet()) {
			saveGuild(g.getValue());
		}
	}

	public void deleteGuild(NovaGuild guild) {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			plugin.getFlatDataManager().deleteGuild(guild);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				//delete from database
				PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_DELETE);
				preparedStatement.setInt(1,guild.getId());
				preparedStatement.executeUpdate();
			}
			catch(SQLException e) {
				LoggerUtils.info("SQLException while deleting a guild.");
				LoggerUtils.exception(e);
			}
		}

//		//remove players
//		for(NovaPlayer nP : guild.getPlayers()) {
//			nP.setGuild(null);
//			LoggerUtils.debug(nP.getName());
//
//			//update tags
//			if(nP.isOnline()) {
//				plugin.tagUtils.updatePrefix(nP.getPlayer());
//			}
//		}
//
//		//remove guild invitations
//		for(NovaPlayer nPlayer : plugin.getPlayerManager().getPlayers()) {
//			if(nPlayer.isInvitedTo(guild)) {
//				nPlayer.deleteInvitation(guild);
//			}
//		}
//
//		//remove allies and wars
//		for(NovaGuild nGuild : guilds.values()) {
//			//ally
//			if(nGuild.isAlly(guild)) {
//				nGuild.removeAlly(guild);
//			}
//
//			//ally invitation
//			if(nGuild.isInvitedToAlly(guild)) {
//				nGuild.removeAllyInvitation(guild);
//			}
//
//			//war
//			if(nGuild.isWarWith(guild)) {
//				nGuild.removeWar(guild);
//			}
//
//			//no war invitation
//			if(nGuild.isNoWarInvited(guild)) {
//				nGuild.removeNoWarInvitation(guild);
//			}
//		}
//
//		//remove raid
//		//TODO
//
//		//remove region
//		if(guild.hasRegion()) {
//			plugin.getRegionManager().removeRegion(guild.getRegion());
//		}
//
//		//bank and hologram
//		if(guild.getBankHologram() != null) {
//			guild.getBankHologram().delete();
//		}
		guild.destroy();

		//remove region
		if(guild.hasRegion()) {
			plugin.getRegionManager().removeRegion(guild.getRegion());
		}

		guilds.remove(guild.getName().toLowerCase());
	}
	
	public void changeName(NovaGuild guild, String newname) {
		guilds.remove(guild.getName());
		guilds.put(newname, guild);
		guild.setName(newname);
		saveGuild(guild);
	}

	public List<NovaRaid> getRaidsTakingPart(NovaGuild guild) {
		List<NovaRaid> list = new ArrayList<>();
		for(NovaGuild raidGuild : plugin.guildRaids) {
			if(raidGuild.getRaid().getGuildAttacker().equals(guild)) {
				list.add(raidGuild.getRaid());
			}
		}

		return list;
	}

	public void postCheckGuilds() {
		Iterator<NovaGuild> it = getGuilds().iterator();
		int i=0;
		while(it.hasNext()) {
			NovaGuild guild = it.next();
			boolean remove = false;
			if(guild != null) {
				if(guild.getLeaderName() != null) {
					LoggerUtils.info("("+guild.getName()+") Leader's name is set. Probably leader is null");
				}

				if(guild.getLeader() == null) {
					LoggerUtils.info("("+guild.getName()+") Leader is null");
					remove = true;
				}

				if(guild.getPlayers().isEmpty()) {
					LoggerUtils.info("("+guild.getName()+") 0 players");
					remove = true;
				}

				if(guild.getSpawnPoint()==null) {
					LoggerUtils.info("("+guild.getName()+") Spawnpoint is null");
					remove = true;
				}

				if(guild.getId() <= 0 && plugin.getConfigManager().getDataStorageType()!=DataStorageType.FLAT) {
					LoggerUtils.info("("+guild.getName()+") ID <= 0 !");
					remove = true;
				}
			}
			else {
				LoggerUtils.info("guild is null!");
				remove = true;
			}

			if(remove) {
				LoggerUtils.info("Unloaded guild "+(guild==null ? "null" : guild.getName()));
				if(guild != null) {
					if(guild.hasRegion()) {
						guild.getRegion().setGuild(null);
					}
				}
				it.remove();
				i++;
			}
			else { //Add allies, wars etc
				//Allies
				for(String allyName : guild.getAlliesNames()) {
					NovaGuild allyGuild = getGuildByName(allyName);

					if(allyGuild != null) {
						guild.addAlly(allyGuild);
					}
				}

				//Wars
				for(String warName : guild.getWarsNames()) {
					NovaGuild warGuild = getGuildByName(warName);

					if(warGuild != null) {
						guild.addWar(warGuild);
					}
				}

				//No-war invitations
				//TODO

				//Ally invitations
				//TODO
			}
		}

		LoggerUtils.info("Postcheck finished. Found "+i+" invalid guilds");
	}

	public void createHomeFloor(NovaGuild guild) {
		Location sp = guild.getSpawnPoint();
		Material material = Material.getMaterial(plugin.getConfig().getString("guild.homefloor").toUpperCase());

		if(material != null) {
			sp.clone().add(1, -1, 0).getBlock().setType(material);
			sp.clone().add(0, -1, 0).getBlock().setType(material);
			sp.clone().add(1, -1, 1).getBlock().setType(material);
			sp.clone().add(0, -1, 1).getBlock().setType(material);
			sp.clone().add(-1, -1, -1).getBlock().setType(material);
			sp.clone().add(-1, -1, 0).getBlock().setType(material);
			sp.clone().add(0, -1, -1).getBlock().setType(material);
			sp.clone().add(1, -1, -1).getBlock().setType(material);
			sp.clone().add(-1, -1, 1).getBlock().setType(material);
		}
		else {
			LoggerUtils.info("Failed to create homefloor, invalid material.");
		}
	}

	//Pozdro dla Artura
	public List<NovaGuild> getTopGuildsByPoints(int count) {
		List<NovaGuild> guildsByPoints = new ArrayList<>(guilds.values());

		Collections.sort(guildsByPoints, new Comparator<NovaGuild>() {
			public int compare(NovaGuild o1, NovaGuild o2) {
				return o2.getPoints()- o1.getPoints();
			}
		});

		List<NovaGuild> guildsLimited = new ArrayList<>();

		int i=0;
		for(NovaGuild guild : guildsByPoints) {
			guildsLimited.add(guild);

			i++;
			if(i==count) {
				break;
			}
		}

		return guildsLimited;
	}

	public List<NovaGuild> getMostInactiveGuilds() {
		List<NovaGuild> guildsByInactive = new ArrayList<>(guilds.values());

		Collections.sort(guildsByInactive, new Comparator<NovaGuild>() {
			public int compare(NovaGuild o1, NovaGuild o2) {
				return (int)(NumberUtils.systemSeconds()-o2.getInactiveTime()) - (int)(NumberUtils.systemSeconds()-o1.getInactiveTime());
			}
		});

		return guildsByInactive;
	}

	public NovaGuild guildFromFlat(FileConfiguration guildData) {
		NovaGuild guild = null;

		if(guildData != null) {
			guild = new NovaGuild();
			guild.setId(guildData.getInt("id"));
			guild.setMoney(guildData.getDouble("money"));
			guild.setPoints(guildData.getInt("points"));
			guild.setName(guildData.getString("name"));
			guild.setTag(guildData.getString("tag"));
			guild.setLeaderName(guildData.getString("leader"));
			guild.setLives(guildData.getInt("lives"));

			guild.setAllies(guildData.getStringList("allies"));
			guild.setWarsNames(guildData.getStringList("wars"));
			guild.setNoWarInvitations(guildData.getStringList("nowar"));
			guild.setAllyInvitations(guildData.getStringList("alliesinv"));

			guild.setInactiveTime(guildData.getLong("activity"));
			guild.setTimeRest(guildData.getLong("timerest"));
			guild.setLostLiveTime(guildData.getLong("lostlive"));

			//home
			Location spawnpoint = null;
			World world = plugin.getServer().getWorld(guildData.getString("home.world"));

			if(world != null) {
				int x = guildData.getInt("home.x");
				int y = guildData.getInt("home.x");
				int z = guildData.getInt("home.x");
				float yaw = (float) guildData.getDouble("home.yaw");
				spawnpoint = new Location(world, x, y, z);
				spawnpoint.setYaw(yaw);
			}
			guild.setSpawnPoint(spawnpoint);
		}

		return guild;
	}

	public void loadBankHolograms() {
		for(NovaGuild guild : getGuilds()) {
			if(guild.getBankLocation() != null) {
				plugin.appendBankHologram(guild);
			}
		}
	}
}