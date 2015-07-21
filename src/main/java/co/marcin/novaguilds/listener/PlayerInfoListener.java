package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;

public class PlayerInfoListener implements Listener {
	private final NovaGuilds plugin;

	public PlayerInfoListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickPlayer(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if(event.getRightClicked() instanceof Player) {
			if(event.getPlayer().hasPermission("novaguilds.playerinfo")) { //TODO better permission node
				NovaPlayer nCPlayer = plugin.getPlayerManager().getPlayer((Player) event.getRightClicked());
				plugin.getPlayerManager().sendPlayerInfo(player, nCPlayer);
			}
		}
	}
}
