package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
	private final NovaGuilds plugin;

	public InventoryListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		String nameRequiredItems = plugin.getMessageManager().getMessagesString("inventory.requireditems.name");
		String nameGGUI = plugin.getMessageManager().getMessagesString("inventory.ggui.name");

		if(event.getClickedInventory() != null) {
			if(event.getInventory().getName().equals(nameRequiredItems) || event.getInventory().getName().equals(nameGGUI)) {
				//1.8
				if(event.getClickedInventory().equals(event.getView().getTopInventory()) || event.isShiftClick()) {
					//gui
					if(event.getClickedInventory().getTitle().equals(nameGGUI)) {
						ItemStack clickedItem = event.getCurrentItem();

						String menuCommand = plugin.getCommandManager().getGuiCommand(clickedItem);
						Player player = (Player) event.getWhoClicked();
						player.chat("/" + menuCommand);
					}

					event.setCancelled(true);
				}

				//1.7
//				if(event.getInventory().getTitle().equals(nameGGUI)) {
//					ItemStack clickedItem = event.getCurrentItem();
//
//					String menuCommand = plugin.getCommandManager().getGuiCommand(clickedItem);
//					Player player = (Player)event.getWhoClicked();
//					player.chat("/"+menuCommand);
//				}
//
//				event.setCancelled(true);
			}
		}
	}
}
