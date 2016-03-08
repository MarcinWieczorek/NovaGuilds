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
	public static void removeItems(Player player, List<ItemStack> items) {
		if(player.getGameMode() != GameMode.CREATIVE) {
			for(ItemStack item : items) {
				player.getInventory().removeItem(item);
			}
		}
	}

	public static boolean containsItems(Inventory inventory, List<ItemStack> items) {
		return getMissingItems(inventory, items).isEmpty();
	}

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

	public static int getTotalAmountOfItemStackInInventory(Inventory inventory, ItemStack itemStack) {
		int amount = 0;

		for(ItemStack item : inventory.getContents()) {
			if(item != null && item.getType() != Material.AIR && ItemStackUtils.isSimilar(itemStack, item)) {
				amount += item.getAmount();
			}
		}

		return amount;
	}

	public static boolean isEmpty(Inventory inventory) {
		for(ItemStack itemStack : inventory.getContents()) {
			if(itemStack != null && itemStack.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}

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

	public static boolean containsAtLeast(Inventory inventory, ItemStack itemStack, int amount) {
		return getTotalAmountOfItemStackInInventory(inventory, itemStack) >= amount;
	}
}
