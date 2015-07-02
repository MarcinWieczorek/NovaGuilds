package co.marcin.novaguilds.listener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.runnable.RunnableRaid;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MoveListener implements Listener {
	private final NovaGuilds plugin;

	public MoveListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);
		Location from = event.getFrom();
		Location to = event.getTo();
		
		NovaRegion fromRegion = plugin.getRegionManager().getRegionAtLocation(from);
		NovaRegion toRegion = plugin.getRegionManager().getRegionAtLocation(to);

		//entering
		if(fromRegion == null && toRegion != null && nPlayer.getAtRegion() == null) {
			plugin.getRegionManager().playerEnteredRegion(player);
		}
		
		//exiting
		if(fromRegion != null && toRegion == null && nPlayer.getAtRegion() != null) {
			plugin.getRegionManager().playerExitedRegion(player);
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);
		Location from = event.getFrom();
		Location to = event.getTo();

		NovaRegion fromRegion = plugin.getRegionManager().getRegionAtLocation(from);
		NovaRegion toRegion = plugin.getRegionManager().getRegionAtLocation(to);

		//entering
		if(fromRegion == null && toRegion != null && nPlayer.getAtRegion() == null) {
			plugin.getRegionManager().playerEnteredRegion(player);
		}

		//exiting
		if(fromRegion != null && toRegion == null && nPlayer.getAtRegion() != null) {
			plugin.getRegionManager().playerExitedRegion(player);
		}
	}
}
