package co.marcin.novaguilds.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;

import co.marcin.novaguilds.*;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;

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
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(mixed);
			
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
		plugin.mysqlReload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			guilds.clear();
			ResultSet res = statement.executeQuery("SELECT * FROM `" + plugin.sqlp + "guilds`");
			while(res.next()) {
				String spawnpoint_coord = res.getString("spawn");
				
				Location spawnpoint = null;
				if(!spawnpoint_coord.isEmpty()) {
					String[] spawnpoint_split = spawnpoint_coord.split(";");
					if(spawnpoint_split.length==5) { //LENGTH
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

					if(!res.getString("allies").isEmpty()) {
						allies = StringUtils.semicolonToList(res.getString("allies"));
					}

					if(!res.getString("alliesinv").isEmpty()) {
						alliesinv = StringUtils.semicolonToList(res.getString("alliesinv"));
					}

					List<String> wars = new ArrayList<>();
					List<String> nowarinv = new ArrayList<>();

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
					novaGuild.setRegion(plugin.getRegionManager().getRegionsMap().get(novaGuild.getName().toLowerCase()));

					novaGuild.setAllies(allies);
					novaGuild.setAllyInvitations(alliesinv);

					novaGuild.setWars(wars);
					novaGuild.setNoWarInvitations(nowarinv);
					novaGuild.setUnchanged();


					plugin.debug("id = "+novaGuild.getId());
					if(novaGuild.getId()>0) {
						guilds.put(res.getString("name").toLowerCase(), novaGuild);
					}
					else {
						plugin.info("Failed to load guild "+res.getString("name")+". Invalid ID");
					}
				}
				else {
					plugin.info("Failed loading guild "+res.getString("name")+", world does not exist");
				}
			}
		}
		catch (SQLException e) {
			plugin.info("An error occured while loading guilds!");
			plugin.info(e.getMessage());
		}	
	}
	
	public void addGuild(NovaGuild guild) {
		plugin.mysqlReload();

		try {
			String spawnpointcoords = "";			
			if(guild.getSpawnPoint() != null) {
				spawnpointcoords = StringUtils.parseDBLocation(guild.getSpawnPoint());
			}

			int startpoints = plugin.getConfig().getInt("guild.startpoints");
			double startmoney = plugin.getConfig().getDouble("guild.startmoney");
			int startlives = plugin.getConfig().getInt("guild.startlives");

			//adding to MySQL
			//id,tag,name,leader,home,allies,alliesinv,wars,nowarinv,money,points,lives,timerest,lostlive
			String pSQL = "INSERT INTO `"+plugin.sqlp+"guilds` VALUES(0,?,?,?,?,'','','','',?,?,?,0,0);";
			PreparedStatement preparedStatement = plugin.c.prepareStatement(pSQL,Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1,guild.getTag()); //tag
			preparedStatement.setString(2,guild.getName()); //name
			preparedStatement.setString(3,guild.getLeaderName()); //leader
			preparedStatement.setString(4,spawnpointcoords); //home
			preparedStatement.setDouble(5, startmoney); //money
			preparedStatement.setInt(6, startpoints); //points
			preparedStatement.setInt(7,startlives); //lives

			preparedStatement.execute();
			ResultSet keys = preparedStatement.getGeneratedKeys();
			int id = 0;
			if(keys.next()){
				id = keys.getInt(1);
				plugin.debug("id="+id);
			}

			if(id > 0) {
				guild.setId(id);
				guilds.put(guild.getName().toLowerCase(), guild);
				NovaPlayer leader = plugin.getPlayerManager().getPlayerByName(guild.getLeaderName());
				leader.setGuild(guild);
				leader.setLeader(true);
				guild.setUnchanged();
			}
		}
		catch(SQLException e) {
			plugin.info("SQLException while adding a guild!");
			plugin.info(e.getMessage());
		}
	}
	
	public void saveGuild(NovaGuild guild) {
		if(guild.isChanged()) {
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
					for(String ally : guild.getAllies()) {
						if(!allies.equals("")) {
							allies += ";";
						}

						allies += ally;
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
					for(String war : guild.getWars()) {
						if(!wars.equals("")) {
							wars += ";";
						}

						wars += war;
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

				String sql = "UPDATE `" + plugin.sqlp + "guilds` SET " +
						"`tag`='" + guild.getTag() + "', " +
						"`name`='" + guild.getName() + "', " +
						"`leader`='" + guild.getLeaderName() + "', " +
						"`spawn`='" + spawnpointcoords + "', " +
						"`allies`='" + allies + "', " +
						"`alliesinv`='" + alliesinv + "', " +
						"`war`='" + wars + "', " +
						"`nowarinv`='" + nowar_inv + "', " +
						"`money`='" + guild.getMoney() + "', " +
						"`points`=" + guild.getPoints() + ", " +
						"`lives`=" + guild.getLives() + ", " +
						"`timerest`=" + guild.getTimeRest() + ", " +
						"`lostlive`=" + guild.getLostLiveTime() +
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
	
	public void saveAll() {
		for(Entry<String, NovaGuild> g : guilds.entrySet()) {
			saveGuild(g.getValue());
		}
	}

	public void deleteGuild(NovaGuild guild) {
		plugin.mysqlReload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();

			//delete from database
			String sql = "DELETE FROM `"+plugin.sqlp+"guilds` WHERE `id`="+guild.getId();
			plugin.debug(sql);
			plugin.debug("id="+guild.getId());
			statement.executeUpdate(sql);
			
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

			//remove region
			if(guild.hasRegion()) {
				plugin.getRegionManager().removeRegion(guild.getRegion());
			}

			plugin.debug(guild.getName());
			plugin.debug("exists="+exists(guild.getName()));
			guilds.remove(guild.getName().toLowerCase());
			plugin.debug("exists="+exists(guild.getName()));
		}
		catch(SQLException e) {
			plugin.info("SQLException while deleting a guild.");
			plugin.info(e.getMessage());
		}
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

	public List<NovaGuild> getTopGuildsByPointsFromDatabase(int count) {
		List<NovaGuild> list = new ArrayList<>();

		plugin.mysqlReload();

		try {
			Statement statement = plugin.c.createStatement();

			ResultSet res = statement.executeQuery("SELECT `name` FROM `"+plugin.sqlp+"guilds` ORDER BY `points` DESC LIMIT "+count);

			while(res.next()) {
				String name = res.getString("name");

				NovaGuild guild = getGuildByName(name);
				if(guild != null) {
					list.add(guild);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
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
}