package co.marcin.NovaGuilds.Manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class PlayerManager {
	public NovaGuilds plugin;
	
	public PlayerManager(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean exists(String playername) {
		return plugin.players.containsKey(playername.toLowerCase());
	}
	
	public NovaPlayer getPlayerByName(String playername) {
		if(exists(playername)) {
			return plugin.players.get(playername.toLowerCase());
		}
		return null;
	}
	
	public NovaPlayer getPlayerBySender(CommandSender sender) {
		return getPlayerByName(sender.getName());
	}
	
	public Set<Entry<String, NovaPlayer>> getPlayers() {
		return plugin.players.entrySet();
	}
	
	public void addInvitation(NovaPlayer nPlayer, NovaGuild guild) {
		nPlayer.addInvitation(guild);
		updateLocalPlayer(nPlayer);
	}
	
	public void updatePlayer(NovaPlayer nPlayer) {
		updateLocalPlayer(nPlayer);
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String guildname;
			if(!nPlayer.hasGuild()) {
				guildname = "";
			}
			else {
				guildname = nPlayer.getGuild().getName();
			}
			
			List<String> invitedto = nPlayer.getInvitedTo();
			String joined = Joiner.on(";").join(invitedto);
			
			String sql = "UPDATE `"+plugin.sqlp+"players` SET `invitedto`='"+joined+"', `guild`='"+guildname+"' WHERE `uuid`='"+nPlayer.getUUID()+"'";
			statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void updateLocalPlayer(NovaPlayer nPlayer) {
//		plugin.players.remove(nPlayer.getName().toLowerCase());
//		plugin.players.put(nPlayer.getName().toLowerCase(), nPlayer);

		plugin.players_changes.put(nPlayer.getName().toLowerCase(),nPlayer);
	}
	
	public void saveAll() {
		for(Entry<String, NovaPlayer> nP : getPlayers()) {
			updatePlayer(nP.getValue());
		}

		applyChanges();
	}
	
	//load
	public void loadPlayers() {
    	plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			plugin.players.clear();
			ResultSet res = statement.executeQuery("SELECT * FROM `"+plugin.sqlp+"players`");
			while(res.next()) {
				NovaPlayer novaplayer = new NovaPlayer();
				Player player = plugin.getServer().getPlayerExact(res.getString("name"));
				
				if(player instanceof Player) {
					if(player.isOnline()) {
						novaplayer.setPlayer(player);
						novaplayer.setOnline(true);
					}
				}
				
				String guildname = res.getString("guild").toLowerCase();
				
				String invitedTo = res.getString("invitedto");
				List<String> invitedToList = new ArrayList<String>();;
				if(!invitedTo.equals("")) {
					invitedToList.addAll(Arrays.asList(invitedTo.split(";")));
					//invitedToList = Arrays.asList(invitedTo.split(";"));
				}

				UUID uuid = UUID.fromString(res.getString("uuid"));

				if(guildname.isEmpty()) {
					novaplayer.setHasGuild(false);
				}

				novaplayer.setUUID(uuid);
				novaplayer.setName(res.getString("name"));
				novaplayer.setInvitedTo(invitedToList);

				NovaGuild guild = null;
				if(!guildname.isEmpty()) {
					guild = plugin.getGuildManager().getGuildByName(guildname);
					guild.addPlayer(novaplayer);
					plugin.getGuildManager().saveGuildLocal(guild);
					novaplayer.setGuild(guild);
				}

				plugin.players.put(res.getString("name").toLowerCase(), novaplayer);
			}

			for(Entry<String, NovaGuild> guild : plugin.getGuildManager().getGuilds()) {
				plugin.info(guild.getValue().players_nick.toString());
			}

			plugin.info("Players loaded from database");
		} catch (SQLException e) {
			plugin.info(e.getMessage());
		}	
    }
	
	//add a player
	public void addPlayer(Player player) {
		plugin.MySQLreload();
		Statement statement;
		
		try {
			statement = plugin.c.createStatement();
			
			UUID uuid = player.getUniqueId();
			String playername = player.getName();
			
			statement.executeUpdate("INSERT INTO `"+plugin.sqlp+"players` VALUES(0,'"+uuid+"','"+playername+"','','')");
			plugin.info("New player "+player.getName()+" added to the database");
			plugin.getPlayerManager().loadPlayers();
		}
		catch (SQLException e) {
			plugin.info("SQLException: "+e.getMessage());
		}
	}

	public void applyChanges() {
		for(Entry<String,NovaPlayer> entry : plugin.players_changes.entrySet()) {
			NovaPlayer nPlayer = entry.getValue();
			plugin.players.remove(nPlayer.getName().toLowerCase());
			plugin.players.put(nPlayer.getName().toLowerCase(),nPlayer);
		}

		plugin.players_changes.clear();
	}
}
