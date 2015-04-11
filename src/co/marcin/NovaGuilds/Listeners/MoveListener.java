package co.marcin.NovaGuilds.Listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaRegion;

public class MoveListener implements Listener {
	private NovaGuilds plugin;

	public MoveListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		
		NovaRegion fromRegion = plugin.getRegionManager().getRegionAtLocation(from);
		NovaRegion toRegion = plugin.getRegionManager().getRegionAtLocation(to);
		
		//entering
		if(fromRegion == null) {
			if(toRegion != null) {
				HashMap<String,String> vars = new HashMap<String,String>();
				vars.put("GUILDNAME",toRegion.getGuildName());
				plugin.sendMessagesMsg(event.getPlayer(),"chat.region.entered", vars);
			}
		}
		
		//exiting
		if(fromRegion != null) {
			if(toRegion == null) {
				HashMap<String,String> vars = new HashMap<String,String>();
				vars.put("GUILDNAME",fromRegion.getGuildName());
				plugin.sendMessagesMsg(event.getPlayer(),"chat.region.exited", vars);
			}
		}
	}
}
