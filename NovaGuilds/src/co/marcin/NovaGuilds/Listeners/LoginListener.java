package co.marcin.NovaGuilds.Listeners;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class LoginListener implements Listener {
	private NovaGuilds plugin;
	
	public LoginListener(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.MySQLreload();
		Statement statement;
		Player player = event.getPlayer();
		
		if(!plugin.getPlayerManager().exists(player.getName())) {
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
		
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(player.getName());
		
		if(player instanceof Player) {
			nPlayer.setPlayer(player);
			nPlayer.setOnline(true);
			plugin.getPlayerManager().updateLocalPlayer(nPlayer);
		}
		
		
		//TabAPI
		plugin.updateTabAll();
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
		nPlayer.setOnline(false);
		plugin.getPlayerManager().updateLocalPlayer(nPlayer);
		plugin.updateTabAll(event.getPlayer());
	}
	
	//TagAPI
	@EventHandler
	public void onNameTag(AsyncPlayerReceiveNameTagEvent event) {
		event.setTag(plugin.getTag(event.getNamedPlayer()));
		//plugin.updateTagPlayerToAll(event.getNamedPlayer());
	}
}
