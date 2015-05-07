package co.marcin.NovaGuilds.listener;

import co.marcin.NovaGuilds.NovaGuilds;
import org.bukkit.event.Listener;

public class ReceiveNameTagListener implements Listener {
	private final NovaGuilds plugin;

	public ReceiveNameTagListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;

		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	//TODO probably remove
//	@EventHandler
//	public void onNameTag(AsyncPlayerReceiveNameTagEvent event) {
//		event.setTag(plugin.tagUtils.getTag(event.getNamedPlayer(),event.getPlayer()));
//		//plugin.updateTagPlayerToAll(event.getNamedPlayer());
//	}
}