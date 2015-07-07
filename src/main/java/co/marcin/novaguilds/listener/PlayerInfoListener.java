package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInfoListener implements Listener {
	private final NovaGuilds plugin;

	public PlayerInfoListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onPlayerClickPlayer(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Player) {
			if(event.getPlayer().hasPermission("novaguilds.playerinfo")) { //TODO better permission node
				Player clickedPlayer = (Player) event.getRightClicked();
				//TODO code this!
			}
		}
	}
}
