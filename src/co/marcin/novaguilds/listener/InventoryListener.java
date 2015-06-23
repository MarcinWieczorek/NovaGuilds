package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {
	private final NovaGuilds plugin;

	public InventoryListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getInventory().getName().equals(plugin.getMessageManager().getMessagesString("inventory.requireditems.name"))) {
			event.setCancelled(true);
		}
	}
}
