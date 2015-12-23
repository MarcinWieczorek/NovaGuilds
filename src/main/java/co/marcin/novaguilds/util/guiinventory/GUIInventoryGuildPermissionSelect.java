/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.util.guiinventory;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIInventoryGuildPermissionSelect implements GUIInventory {
	private final NovaRank rank;
	private final Inventory inventory;
	private final Map<Integer, GuildPermission> slotPermissionsMap = new HashMap<>();

	public GUIInventoryGuildPermissionSelect(NovaRank rank) {
		this.rank = rank;

		Map<String, String> vars = new HashMap<>();
		vars.put("RANKNAME", rank.getName());
		inventory = Bukkit.createInventory(null, ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PERMISSIONS_TITLE.vars(vars).get());

		generateContent();
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		event.setCancelled(true);
		int slot = event.getRawSlot();

		LoggerUtils.debug(String.valueOf("slot: "+slot));
		if(event.getInventory() == null || !slotPermissionsMap.containsKey(slot)) {
			return;
		}

		togglePermission(slotPermissionsMap.get(slot));
		refreshSlot(slot);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void open(NovaPlayer nPlayer) {
		ChestGUIUtils.openGUIInventory(nPlayer, this);
	}

	private void togglePermission(GuildPermission permission) {
		if(rank.hasPermission(permission)) {
			rank.removePermission(permission);
		}
		else {
			rank.addPermission(permission);
		}
	}

	private void generateContent() {
		int slot = 0;
		Map<String, String> vars = new HashMap<>();

		for(GuildPermission perm : GuildPermission.values()) {
			ItemStack itemStack;
			vars.clear();
			vars.put("PERMNAME", Message.valueOf("INVENTORY_GUI_PERMISSIONS_NAMES_"+perm.name()).get());

			if(rank.hasPermission(perm)) {
				itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PERMISSIONS_ITEM_ENABLED.vars(vars).get());
			}
			else {
				itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PERMISSIONS_ITEM_DISABLED.vars(vars).get());
			}

			inventory.addItem(itemStack);
			slotPermissionsMap.put(slot, perm);
			slot++;
		}
	}

	private void refreshSlot(int slot) {
		ItemStack itemStack;
		GuildPermission perm = slotPermissionsMap.get(slot);

		if(perm == null) {
			return;
		}

		Map<String, String> vars = new HashMap<>();
		vars.put("PERMNAME", Message.valueOf("INVENTORY_GUI_PERMISSIONS_NAMES_"+perm.name()).get());

		if(rank.hasPermission(perm)) {
			itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PERMISSIONS_ITEM_ENABLED.vars(vars).get());
		}
		else {
			itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PERMISSIONS_ITEM_DISABLED.vars(vars).get());
		}

		inventory.setItem(slot, itemStack);
	}
}
