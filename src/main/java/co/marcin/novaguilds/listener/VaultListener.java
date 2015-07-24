package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.ItemStackUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.List;

public class VaultListener implements Listener {
	private final NovaGuilds plugin;
	private final List<InventoryAction> dissalowedActions = new ArrayList<>();
	private final BlockFace[] doubleChestFaces;

	public VaultListener(NovaGuilds novaGuilds) {
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

		//double chest faces
		doubleChestFaces = new BlockFace[] {
				BlockFace.EAST,
				BlockFace.NORTH,
				BlockFace.SOUTH,
				BlockFace.WEST
		};

	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(plugin.getGuildManager().isBankItemStack(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
			Message.CHAT_GUILD_VAULT_DROP.send(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer((Player) event.getWhoClicked());
		String nameBank = plugin.getConfigManager().getGuildBankItem().getItemMeta().getDisplayName();

		if(event.getInventory().getName().equals(nameBank)) {
			if(nPlayer.hasGuild()) {
				if(!nPlayer.isLeader() && plugin.getConfigManager().getGuildBankOnlyLeaderTake()) {
					if(dissalowedActions.contains(event.getAction())) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		if(plugin.getGuildManager().isBankBlock(event.getBlock())) {
			Chest chest = (Chest) event.getBlock().getState();
			if(ItemStackUtils.isEmpty(chest.getInventory())) {
				if(nPlayer.isLeader()) {
					if(nPlayer.getGuild().getBankHologram() != null) {
						nPlayer.getGuild().getBankHologram().delete();
						nPlayer.getGuild().setBankHologram(null);
					}

					if(player.getGameMode() != GameMode.CREATIVE) {
						event.setCancelled(true);
						event.getBlock().setType(Material.AIR);
						event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), plugin.getConfigManager().getGuildBankItem());
					}

					nPlayer.getGuild().setBankLocation(null);
					Message.CHAT_GUILD_VAULT_BREAK_SUCCESS.send(player);
				}
				else {
					event.setCancelled(true);
					Message.CHAT_GUILD_VAULT_BREAK_NOTLEADER.send(player);
				}
			}
			else {
				event.setCancelled(true);
				Message.CHAT_GUILD_VAULT_BREAK_NOTEMPTY.send(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		Player player = event.getPlayer();

		if(plugin.getRegionManager().canBuild(player,event.getBlock().getLocation())) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);
			if(event.getPlayer().getItemInHand().getType() == Config.BANK_ITEM.getItemStack().getType()) {
				for(BlockFace face : doubleChestFaces) {
					if(event.getBlock().getRelative(face) != null) {
						if(plugin.getGuildManager().isBankBlock(event.getBlock().getRelative(face))) {
							event.setCancelled(true);
							Message.CHAT_GUILD_VAULT_PLACE_DOUBLECHEST.send(player);
							return;
						}
					}
				}

				if(plugin.getGuildManager().isBankItemStack(event.getItemInHand())) {
					if(nPlayer.hasGuild()) {
						if(nPlayer.isLeader()) {
							if(nPlayer.getGuild().getBankLocation() == null) {
								NovaRegion region = plugin.getRegionManager().getRegion(event.getBlockPlaced().getLocation());
								if(region != null && region.getGuild().isMember(nPlayer)) {
									nPlayer.getGuild().setBankLocation(event.getBlockPlaced().getLocation());
									plugin.getGuildManager().appendVaultHologram(nPlayer.getGuild());
									Message.CHAT_GUILD_VAULT_PLACE_SUCCESS.send(player);
								}
								else {
									Message.CHAT_GUILD_VAULT_OUTSIDEREGION.send(player);
									event.setCancelled(true);
								}
							}
							else {
								Message.CHAT_GUILD_VAULT_PLACE_EXISTS.send(player);
								event.setCancelled(true);
							}
						}
						else {
							Message.CHAT_GUILD_VAULT_PLACE_NOTLEADER.send(player);
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
