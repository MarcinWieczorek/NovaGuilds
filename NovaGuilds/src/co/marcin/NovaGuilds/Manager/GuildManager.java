package co.marcin.NovaGuilds.Manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class GuildManager {
	private NovaGuilds plugin;
	
	public GuildManager(NovaGuilds pl) {
		plugin = pl;
	}
	
	//getters
	public NovaGuild getGuildByName(String name) {
		if(plugin.guilds.containsKey(name.toLowerCase()))
			return plugin.guilds.get(name.toLowerCase());
		return null;
	}
	
	public NovaGuild getGuildByTag(String tag) {
		for(Entry<String, NovaGuild> e : getGuilds()) {
			plugin.info(Utils.removeColors(e.getValue().getTag())+" = "+tag);
			if(Utils.removeColors(e.getValue().getTag()).equalsIgnoreCase(tag)) {
				return e.getValue();
			}
		}
		return null;
	}
	
	public Set<Entry<String, NovaGuild>> getGuilds() {
		return plugin.guilds.entrySet();
	}
	
	public boolean exists(String guildname) {
		return plugin.guilds.containsKey(guildname.toLowerCase());
	}
	
	public void loadGuilds() {
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			plugin.players.clear();
			ResultSet res = statement.executeQuery("SELECT * FROM `"+plugin.sqlp+"guilds`");
			while(res.next()) {
				String spawnpoint_coord = res.getString("spawn");
				
				Location spawnpoint = null;
				if(!spawnpoint_coord.equals("")) {
					String[] spawnpoint_split = spawnpoint_coord.split(";");
					if(spawnpoint_split.length==5) { //LENGTH
						String worldname = spawnpoint_split[0];
						int x = Integer.parseInt(spawnpoint_split[1]);
						int y = Integer.parseInt(spawnpoint_split[2]);
						int z = Integer.parseInt(spawnpoint_split[3]);
						float yaw = Float.parseFloat(spawnpoint_split[4]);
						spawnpoint = new Location(plugin.getServer().getWorld(worldname),x,y,z);
						spawnpoint.setYaw(yaw);
					}
				}
				
				List<String> allies = Utils.semicolonToList(res.getString("allies"));
				List<String> alliesinv = Utils.semicolonToList(res.getString("alliesinv"));
				
				NovaGuild novaGuild = new NovaGuild();
				novaGuild.setId(res.getInt("id"));
				novaGuild.setMoney(res.getDouble("money"));
				novaGuild.setName(res.getString("name"));
				novaGuild.setTag(res.getString("tag"));
				novaGuild.setLeaderName(res.getString("leader"));
				novaGuild.setSpawnPoint(spawnpoint);
				novaGuild.setRegion(plugin.regions.get(novaGuild.getName().toLowerCase()));
				novaGuild.setAllies(allies);
				novaGuild.setAllyInvitations(alliesinv);
				
				plugin.guilds.put(res.getString("name").toLowerCase(), novaGuild);
			}
			plugin.info("Guilds loaded from database");
		} catch (SQLException e) {
			plugin.info(e.getMessage());
		}	
	}
	
	public void addGuild(NovaGuild guild) {
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String spawnpointcoords = "";			
			if(guild.getSpawnPoint() != null) {
				spawnpointcoords = Utils.parseDBLocation(guild.getSpawnPoint());
			}
			
			String sql = "INSERT INTO `"+plugin.sqlp+"guilds` VALUES(0,'"+guild.getTag()+"','"+guild.getName()+"','"+guild.getLeaderName()+"','"+spawnpointcoords+"','','',0);";
			statement.execute(sql);
			
			plugin.guilds.put(guild.getName().toLowerCase(),guild);
			plugin.getPlayerManager().getPlayerByName(guild.getLeaderName()).setGuild(guild);
			plugin.getPlayerManager().updateLocalPlayer(plugin.getPlayerManager().getPlayerByName(guild.getLeaderName()));
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void saveGuild(NovaGuild guild) {
		plugin.MySQLreload();
    	saveGuildLocal(guild);
		
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String spawnpointcoords = "";			
			if(guild.getSpawnPoint() != null) {
				spawnpointcoords = Utils.parseDBLocation(guild.getSpawnPoint());
			}
			
			String allies = "";
			String alliesinv = "";
			
			if(guild.getAllies().size() > 0) {
				for(String ally : guild.getAllies()) {
					if(!allies.equals("")) allies += ";";
					allies += ally;
				}
			}
			
			if(plugin.DEBUG) plugin.info(guild.getAllyInvitations().toString());
			if(guild.getAllyInvitations().size() > 0) {
				for(String allyinv : guild.getAllyInvitations()) {
					if(!alliesinv.equals("")) alliesinv += ";";
					alliesinv = alliesinv + allyinv;
				}
			}
			
			String sql = "UPDATE `"+plugin.sqlp+"guilds` SET `tag`='"+guild.getTag()+"', `name`='"+guild.getName()+"', `leader`='"+guild.getLeaderName()+"', `spawn`='"+spawnpointcoords+"', money='"+guild.getMoney()+"', `allies`='"+allies+"', `alliesinv`='"+alliesinv+"' WHERE `id`="+guild.getId();
			statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void saveGuildLocal(NovaGuild guild) {
		if(plugin.guilds.containsValue(guild)) {
			plugin.guilds.replace(guild.getName().toLowerCase(),guild);
		}
	}
	
	public void saveAll() {
		for(Entry<String, NovaGuild> g : getGuilds()) {
			saveGuild(g.getValue());
		}
	}
	
	public void deleteGuild(NovaGuild guild) {
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String sql = "DELETE FROM `"+plugin.sqlp+"guilds` WHERE `id`="+guild.getId();
			statement.executeUpdate(sql);
			
			List<NovaPlayer> players = guild.getPlayers();
			int i=0;
			
			for(i=0;i<players.size();i++) {
				NovaPlayer np = players.get(i);
				np.setGuild(null);
				np.setHasGuild(false);
				plugin.getPlayerManager().updateLocalPlayer(np);
				plugin.players.remove(np.getName().toLowerCase());
				plugin.players.put(np.getName().toLowerCase(),np);
			}
			
			plugin.guilds.remove(guild.getName().toLowerCase());
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
}
