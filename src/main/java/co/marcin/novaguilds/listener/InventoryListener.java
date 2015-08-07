package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
	private final NovaGuilds plugin;

	public InventoryListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		String nameRequiredItems = Message.INVENTORY_REQUIREDITEMS_NAME.get();
		String nameGGUI = Message.INVENTORY_GGUI_NAME.get();
		Player player = (Player) event.getWhoClicked();

		Inventory clickedInventory = InventoryUtils.getClickedInventory(event);

		if(clickedInventory != null && event.getCurrentItem() != null && event.getCurrentItem().getType()!= Material.AIR) {
			if(event.getInventory().getName().equals(nameRequiredItems) || event.getInventory().getName().equals(nameGGUI)) {
				if(clickedInventory.equals(event.getView().getTopInventory()) || event.isShiftClick()) {
					//gui
					if(event.getInventory().getTitle().equals(nameGGUI)) {
						ItemStack clickedItem = event.getCurrentItem();

						String menuCommand = plugin.getCommandManager().getGuiCommand(clickedItem);

						player.chat("/" + menuCommand);
					}

					event.setCancelled(true);
				}
			}
		}
	}
}
