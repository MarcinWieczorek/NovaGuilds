package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

public class VanishListener implements Listener {
	private final NovaGuilds plugin;

	public VanishListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onVanishStatusChange(VanishStatusChangeEvent event) {
		if(event.isVanishing()) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());

			if(nPlayer.getAtRegion() != null) {
				plugin.getRegionManager().playerExitedRegion(event.getPlayer());
			}
		}
		else if(plugin.getRegionManager().getRegion(event.getPlayer().getLocation()) != null) {
			plugin.getRegionManager().playerEnteredRegion(event.getPlayer(), event.getPlayer().getLocation());
		}
	}
}
