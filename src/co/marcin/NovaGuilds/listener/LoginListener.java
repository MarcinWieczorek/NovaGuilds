package co.marcin.NovaGuilds.listener;

import java.sql.Statement;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class LoginListener implements Listener {
	private final NovaGuilds plugin;
	
	public LoginListener(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.MySQLreload();
		Statement statement;
		Player player = event.getPlayer();

		plugin.getPlayerManager().addPlayer(player);
		
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
