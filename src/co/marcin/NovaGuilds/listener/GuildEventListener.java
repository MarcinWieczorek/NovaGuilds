package co.marcin.NovaGuilds.listener;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.event.GuildCreateEvent;
import co.marcin.NovaGuilds.event.GuildRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GuildEventListener implements Listener {
	private final NovaGuilds plugin;

	public GuildEventListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onGuildRemoval(GuildRemoveEvent event) {
		//plugin.tagUtils.refreshAll();
	}

	@EventHandler
	public void onGuildCreate(GuildCreateEvent event) {
		//plugin.tagUtils.refreshAll();
	}
}
