package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		if(guilds.containsKey(name.toLowerCase()))
			return guilds.get(name.toLowerCase());
		return null;
	}
	
	public NovaGuild getGuildByTag(String tag) {
		for(NovaGuild guild : getGuilds()) {
			plugin.debug(StringUtils.removeColors(guild.getTag())+" = "+tag);
			if(StringUtils.removeColors(guild.getTag()).equalsIgnoreCase(tag)) {
				return guild;
			}
		}
		return null;
	}

	public NovaGuild getGuildByRegion(NovaRegion rgatploc) {
		return getGuildByName(rgatploc.getGuildName());
	}
	
	public NovaGuild getGuildByPlayer(NovaPlayer nPlayer) {
		if(nPlayer.hasGuild()) {
			return getGuildByName(nPlayer.getGuild().getName());
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
			
			guild = getGuildByPlayer(nPlayer);
		}

		return guild;
	}

	public Collection<NovaGuild> getGuilds() {
		return guilds.values();
	}
	
	public boolean exists(String guildname) {
		return guilds.containsKey(guildname.toLowerCase());
	}
	
	public void loadGuilds() {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			for(String guildName : plugin.getFlatDataManager().getGuildList()) {
				FileConfiguration guildData = plugin.getFlatDataManager().getGuildData(guildName);
				NovaGuild guild = guildFromFlat(guildData);

				if(guild != null) {
					guilds.put(guildName.toLowerCase(),guild);
				}
				else {
					plugin.info("Loaded guild is null. name: "+guildName);
				}
			}
		}
		else {
			plugin.mysqlReload();

			Statement statement;
			try {
				statement = plugin.c.createStatement();

				guilds.clear();
				ResultSet res = statement.executeQuery("SELECT * FROM `" + plugin.getConfigManager().getDatabasePrefix() + "guilds`");
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
						//novaGuild.setRegion(plugin.getRegionManager().getRegionsMap().get(novaGuild.getName().toLowerCase()));
						plugin.debug("regionnull=" + (novaGuild.getRegion() == null));

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

						//plugin.debug("id = " + novaGuild.getId());
						if(novaGuild.getId() > 0) {
							guilds.put(res.getString("name").toLowerCase(), novaGuild);
						}
						else {
							plugin.info("Failed to load guild " + res.getString("name") + ". Invalid ID");
						}
					}
					else {
						plugin.info("Failed loading guild " + res.getString("name") + ", world does not exist");
					}
				}
			}
			catch(SQLException e) {
				plugin.info("An error occured while loading guilds!");
				plugin.info(e.getMessage());
			}
		}

		plugin.info("[GuildManager] Loaded "+guilds.size()+" guilds.");
	}
	
	public void addGuild(NovaGuild guild) {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			plugin.getFlatDataManager().addGuild(guild);
			guilds.put(guild.getName().toLowerCase(), guild);
		}
		else {
			plugin.mysqlReload();

			try {
				String spawnpointcoords = "";
				if(guild.getSpawnPoint() != null) {
					spawnpointcoords = StringUtils.parseDBLocation(guild.getSpawnPoint());
				}

				//adding to MySQL
				//id,tag,name,leader,home,allies,alliesinv,wars,nowarinv,money,points,lives,timerest,lostlive
				String pSQL = "INSERT INTO `" + plugin.getConfigManager().getDatabasePrefix() + "guilds` VALUES(0,?,?,?,?,'','','','',?,?,?,0,0,0);";
				PreparedStatement preparedStatement = plugin.c.prepareStatement(pSQL, Statement.RETURN_GENERATED_KEYS);
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
					plugin.debug("id=" + id);
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
				plugin.info("SQLException while adding a guild!");
				plugin.info(e.getMessage());
			}
		}
	}
	
	public void saveGuild(NovaGuild guild) {
		if(guild.isChanged()) {
			if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
				plugin.getFlatDataManager().saveGuild(guild);
			}
			else {
				plugin.mysqlReload();

				try {
					Statement statement = plugin.c.createStatement();

					String spawnpointcoords = "";
					if(guild.getSpawnPoint() != null) {
						spawnpointcoords = StringUtils.parseDBLocation(guild.getSpawnPoint());
					}

					//ALLIES
					String allies = "";
					String alliesinv = "";

					if(guild.getAllies().size() > 0) {
						for(NovaGuild ally : guild.getAllies()) {
							if(!allies.equals("")) {
								allies += ";";
							}

							allies += ally.getName();
						}
					}

					if(guild.getAllyInvitations().size() > 0) {
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

					if(guild.getWars().size() > 0) {
						for(NovaGuild war : guild.getWars()) {
							if(!wars.equals("")) {
								wars += ";";
							}

							wars += war.getName();
						}
					}

					if(guild.getNoWarInvitations().size() > 0) {
						for(String nowarinv : guild.getNoWarInvitations()) {
							if(!nowar_inv.equals("")) {
								nowar_inv += ";";
							}

							nowar_inv = nowar_inv + nowarinv;
						}
					}

					String sql = "UPDATE `" + plugin.getConfigManager().getDatabasePrefix() + "guilds` SET " +
							"`tag`='" + guild.getTag() + "', " +
							"`name`='" + guild.getName() + "', " +
							"`leader`='" + guild.getLeader().getName() + "', " +
							"`spawn`='" + spawnpointcoords + "', " +
							"`allies`='" + allies + "', " +
							"`alliesinv`='" + alliesinv + "', " +
							"`war`='" + wars + "', " +
							"`nowarinv`='" + nowar_inv + "', " +
							"`money`='" + guild.getMoney() + "', " +
							"`points`=" + guild.getPoints() + ", " +
							"`lives`=" + guild.getLives() + ", " +
							"`timerest`=" + guild.getTimeRest() + ", " +
							"`lostlive`=" + guild.getLostLiveTime() + ", " +
							"`activity`=" + guild.getInactiveTime() +
							" WHERE `id`=" + guild.getId();

					statement.executeUpdate(sql);
					guild.setUnchanged();
				}
				catch(SQLException e) {
					plugin.info("SQLException while saving a guild.");
					plugin.info(e.getMessage());
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
			plugin.mysqlReload();

			Statement statement;
			try {
				statement = plugin.c.createStatement();

				//delete from database
				String sql = "DELETE FROM `" + plugin.getConfigManager().getDatabasePrefix() + "guilds` WHERE `id`=" + guild.getId();
				plugin.debug(sql);
				plugin.debug("id=" + guild.getId());
				statement.executeUpdate(sql);
			}
			catch(SQLException e) {
				plugin.info("SQLException while deleting a guild.");
				plugin.info(e.getMessage());
			}
		}

		//remove players
		for(NovaPlayer nP : guild.getPlayers()) {
			nP.setGuild(null);
			plugin.debug(nP.getName());

			//update tags
			if(nP.isOnline()) {
				plugin.tagUtils.updatePrefix(nP.getPlayer());
			}
		}

		//remove guild invitations
		for(NovaPlayer nPlayer : plugin.getPlayerManager().getPlayers()) {
			if(nPlayer.isInvitedTo(guild)) {
				nPlayer.deleteInvitation(guild);
			}
		}

		//remove allies and wars
		for(NovaGuild nGuild : guilds.values()) {
			//ally
			if(nGuild.isAlly(guild)) {
				nGuild.removeAlly(guild);
			}

			//ally invitation
			if(nGuild.isInvitedToAlly(guild)) {
				nGuild.removeAllyInvitation(guild);
			}

			//war
			if(nGuild.isWarWith(guild)) {
				nGuild.removeWar(guild);
			}

			//no war invitation
			if(nGuild.isNoWarInvited(guild)) {
				nGuild.removeNoWarInvitation(guild);
			}
		}

		//remove raid
		//TODO

		//remove region
		if(guild.hasRegion()) {
			plugin.getRegionManager().removeRegion(guild.getRegion());
		}

		plugin.debug(guild.getName());
		plugin.debug("exists=" + exists(guild.getName()));
		guilds.remove(guild.getName().toLowerCase());
		plugin.debug("exists=" + exists(guild.getName()));
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
					plugin.info("("+guild.getName()+") Leader's name is set. Probably leader is null");
				}

				if(guild.getLeader() == null) {
					plugin.info("("+guild.getName()+") Leader is null");
					remove = true;
				}

				if(guild.getPlayers().size() == 0) {
					plugin.info("("+guild.getName()+") 0 players");
					remove = true;
				}

				if(guild.getSpawnPoint()==null) {
					plugin.info("("+guild.getName()+") Spawnpoint is null");
					remove = true;
				}

				if(guild.getId() <= 0 && plugin.getConfigManager().getDataStorageType()!=DataStorageType.FLAT) {
					plugin.info("("+guild.getName()+") ID <= 0 !");
					remove = true;
				}
			}
			else {
				plugin.info("guild is null!");
				remove = true;
			}

			if(remove) {
				plugin.info("Unloaded guild "+(guild==null ? "null" : guild.getName()));
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

		plugin.info("[GuildManager] Postcheck finished. Found "+i+" invalid guilds");
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
			plugin.info("Failed to create homefloor, invalid material.");
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
				return (int)(NovaGuilds.systemSeconds()-o2.getInactiveTime()) - (int)(NovaGuilds.systemSeconds()-o1.getInactiveTime());
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
}