package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.util.LoggerUtils;
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
	private static final List<InventoryAction> dissalowedActions = new ArrayList<>();

	public InventoryListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);

		//Add disallowed actions
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
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		String nameRequiredItems = plugin.getMessageManager().getMessagesString("inventory.requireditems.name");
		String nameGGUI = plugin.getMessageManager().getMessagesString("inventory.ggui.name");
		String nameBank = plugin.getConfigManager().getGuildBankItem().getItemMeta().getDisplayName();
		Player player = (Player) event.getWhoClicked();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		//1.8
		if(event.getClickedInventory() != null && event.getCurrentItem() != null && event.getCurrentItem().getType()!= Material.AIR) {
			if(event.getInventory().getName().equals(nameRequiredItems) || event.getInventory().getName().equals(nameGGUI) || event.getInventory().getName().equals(nameBank)) {
				if(event.getClickedInventory().equals(event.getView().getTopInventory()) || event.isShiftClick()) {
					//gui
					if(event.getInventory().getTitle().equals(nameGGUI)) {
						ItemStack clickedItem = event.getCurrentItem();

						String menuCommand = plugin.getCommandManager().getGuiCommand(clickedItem);

						player.chat("/" + menuCommand);
						event.setCancelled(true);
					}
					else if(nPlayer.hasGuild()) {
						LoggerUtils.debug(event.getAction().name());
						LoggerUtils.debug(nameBank);
						LoggerUtils.debug(plugin.getConfigManager().getGuildBankItem().toString());
						if(event.getInventory().getTitle().equals(nameBank)) {
							if(!nPlayer.isLeader() && plugin.getConfigManager().getGuildBankOnlyLeaderTake()) {
								if(dissalowedActions.contains(event.getAction())) {
									event.setCancelled(true);
								}
							}
						}
						else {
							event.setCancelled(true);
						}
					}
				}
			}
		}

		//1.7
//		if(event.getInventory() != null && event.getCurrentItem() != null && event.getCurrentItem().getType()!= Material.AIR) {
//			if(event.getInventory().getName().equals(nameRequiredItems) || event.getInventory().getName().equals(nameGGUI)) {
//				if(event.getInventory().getTitle().equals(nameGGUI)) {
//					ItemStack clickedItem = event.getCurrentItem();
//
//					String menuCommand = plugin.getCommandManager().getGuiCommand(clickedItem);
//					player.chat("/"+menuCommand);
//					event.setCancelled(true);
//					player.closeInventory();
//				}
//				else if(event.getInventory().getTitle().equals(nameBank)) {
//					if(!nPlayer.isLeader()) {
//						if(dissalowedActions.contains(event.getAction())) {
//							event.setCancelled(true);
//						}
//					}
//				}
//				else {
//					event.setCancelled(true);
//				}
//
//			}
//		}
	}
}
