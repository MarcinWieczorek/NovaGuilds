package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
		String nameBank = plugin.getMessageManager().getMessagesString("inventory.bank.name");
		Player player = (Player) event.getWhoClicked();

		if(event.getClickedInventory() != null && event.getCurrentItem() != null && event.getCurrentItem().getType()!= Material.AIR) {
			if(event.getInventory().getName().equals(nameRequiredItems) || event.getInventory().getName().equals(nameGGUI)) {
				//1.8
				if(event.getClickedInventory().equals(event.getView().getTopInventory()) || event.isShiftClick()) {
					//gui
					if(event.getClickedInventory().getTitle().equals(nameGGUI)) {
						ItemStack clickedItem = event.getCurrentItem();

						String menuCommand = plugin.getCommandManager().getGuiCommand(clickedItem);

						player.chat("/" + menuCommand);
						event.setCancelled(true);
					}
					else if(event.getInventory().getTitle().equals(nameBank)) {
						NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);
						List<InventoryAction> dissalowedActions = new ArrayList<>();
						dissalowedActions.add(InventoryAction.CLONE_STACK);
						dissalowedActions.add(InventoryAction.COLLECT_TO_CURSOR);
						dissalowedActions.add(InventoryAction.HOTBAR_MOVE_AND_READD);
						dissalowedActions.add(InventoryAction.HOTBAR_SWAP);
						dissalowedActions.add(InventoryAction.MOVE_TO_OTHER_INVENTORY);
						dissalowedActions.add(InventoryAction.PICKUP_ALL);
						dissalowedActions.add(InventoryAction.PICKUP_HALF);
						dissalowedActions.add(InventoryAction.PICKUP_ONE);
						dissalowedActions.add(InventoryAction.PICKUP_SOME);
						dissalowedActions.add(InventoryAction.SWAP_WITH_CURSOR);
						dissalowedActions.add(InventoryAction.UNKNOWN);

						if(!nPlayer.isLeader()) {
							if(dissalowedActions.contains(event.getAction())) {
								event.setCancelled(true);
							}
						}
					}
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
