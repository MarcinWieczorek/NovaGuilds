package co.marcin.NovaGuilds.manager;

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

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;

public class PlayerManager {
	private final NovaGuilds plugin;
	
	public PlayerManager(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean exists(String playername) {
		return plugin.players.containsKey(playername.toLowerCase());
	}
	
	public NovaPlayer getPlayerByName(String playername) {
		addIfNotExists(playername);

		return plugin.players.get(playername.toLowerCase());
	}
	
	public NovaPlayer getPlayerBySender(CommandSender sender) {
		addIfNotExists(sender.getName());

		return getPlayerByName(sender.getName());
	}

	public NovaPlayer getPlayerByPlayer(Player player) {
		addIfNotExists(player.getName());

		return getPlayerByName(player.getName());
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
		if(plugin.DEBUG) return;
		plugin.players_changes.put(nPlayer.getName().toLowerCase(), nPlayer);
	}
	
	public void saveAll() {
		for(Entry<String, NovaPlayer> nP : getPlayers()) {
			updatePlayer(nP.getValue());
		}
	}
	
	//load
	public void loadPlayers() {
    	plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			plugin.players.clear();
			ResultSet res = statement.executeQuery("SELECT * FROM `" + plugin.sqlp + "players`");
			while(res.next()) {
				NovaPlayer novaplayer = new NovaPlayer();
				Player player = plugin.getServer().getPlayerExact(res.getString("name"));
				
				if(player != null) {
					if(player.isOnline()) {
						novaplayer.setPlayer(player);
						novaplayer.setOnline(true);
					}
				}
				
				String guildname = res.getString("guild").toLowerCase();
				
				String invitedTo = res.getString("invitedto");
				List<String> invitedToList = new ArrayList<>();
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
					novaplayer.setGuild(guild);

					if(guild.isLeader(novaplayer.getName())) {
						novaplayer.setLeader(true);
					}
				}

				plugin.players.put(res.getString("name").toLowerCase(), novaplayer);
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

	public void addIfNotExists(String playername) {
		Player player = plugin.getServer().getPlayerExact(playername);

		if(player != null) {
			if(!plugin.players.containsKey(player.getName().toLowerCase())) {
				addPlayer(player);
			}
		}
	}

	public boolean isGuildMate(Player player1, Player player2) {
		NovaPlayer nPlayer1 = getPlayerByPlayer(player1);
		NovaPlayer nPlayer2 = getPlayerByPlayer(player2);

		return nPlayer1.getGuild().getName().equalsIgnoreCase(nPlayer2.getGuild().getName());
	}
}
