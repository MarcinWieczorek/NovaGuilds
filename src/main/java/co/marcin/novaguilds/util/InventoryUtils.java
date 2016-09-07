/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class InventoryUtils {
	private InventoryUtils() {
	}

	/**
	 * Removes a list of items from player's inventory
	 *
	 * @param player the player
	 * @param items  list of items
	 */
	public static void removeItems(Player player, List<ItemStack> items) {
		if(player.getGameMode() != GameMode.CREATIVE) {
			for(ItemStack item : items) {
				player.getInventory().removeItem(item);
			}
		}
	}

	/**
	 * Checks if an inventory contains the list of items
	 *
	 * @param inventory the inventory
	 * @param items     list of items
	 * @return boolean
	 */
	public static boolean containsItems(Inventory inventory, List<ItemStack> items) {
		return getMissingItems(inventory, items).isEmpty();
	}

	/**
	 * Gets a list of missing items
	 *
	 * @param inventory inventory
	 * @param items     list of items
	 * @return list of missing items
	 */
	public static List<ItemStack> getMissingItems(Inventory inventory, List<ItemStack> items) {
		List<ItemStack> missing = new ArrayList<>();

		if(items != null && inventory.getType() != InventoryType.CREATIVE) {
			for(ItemStack item : items) {
				if(!containsAtLeast(inventory, item, item.getAmount())) {
					ItemStack missingItemStack = item.clone();
					missingItemStack.setAmount(item.getAmount() - getTotalAmountOfItemStackInInventory(inventory, item));
					missing.add(missingItemStack);
				}
			}
		}

		return missing;
	}

	/**
	 * Gets total amount of item
	 *
	 * @param inventory inventory
	 * @param itemStack item
	 * @return the amount
	 */
	public static int getTotalAmountOfItemStackInInventory(Inventory inventory, ItemStack itemStack) {
		int amount = 0;

		for(ItemStack item : inventory.getContents()) {
			if(item != null && item.getType() != Material.AIR && ItemStackUtils.isSimilar(itemStack, item)) {
				amount += item.getAmount();
			}
		}

		return amount;
	}

	/**
	 * Checks if an inventory is empty
	 *
	 * @param inventory inventory
	 * @return boolean
	 */
	public static boolean isEmpty(Inventory inventory) {
		for(ItemStack itemStack : inventory.getContents()) {
			if(itemStack != null && itemStack.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets clicked inventory
	 * For API older than 1.8
	 *
	 * @param event inventory click event
	 * @return inventory
	 */
	public static Inventory getClickedInventory(InventoryClickEvent event) {
		int slot = event.getRawSlot();
		InventoryView view = event.getView();

		if(slot < 0) {
			return null;
		}
		else if(view.getTopInventory() != null && slot < view.getTopInventory().getSize()) {
			return view.getTopInventory();
		}
		else {
			return view.getBottomInventory();
		}
	}

	/**
	 * Checks if an inventory contains required amount of an item
	 *
	 * @param inventory inventory
	 * @param itemStack item
	 * @param amount    amount
	 * @return boolean
	 */
	public static boolean containsAtLeast(Inventory inventory, ItemStack itemStack, int amount) {
		return getTotalAmountOfItemStackInInventory(inventory, itemStack) >= amount;
	}

	/**
	 * Gets item in player's hand
	 * Fixes issues with 2 hands introduced in 1.9
	 *
	 * @param player player
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_9_R1)) {
			return player.getItemInHand();
		}
		else {
			return player.getInventory().getItemInMainHand();
		}
	}
}
