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

package co.marcin.novaguilds.util.guiinventory;

import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.util.AbstractGUIInventory;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.ItemStackUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIInventoryGuildPermissionSelect extends AbstractGUIInventory {
	private final NovaRank rank;
	private final Map<Integer, GuildPermission> slotPermissionsMap = new HashMap<>();

	public GUIInventoryGuildPermissionSelect(NovaRank rank) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PERMISSIONS_TITLE.setVar(VarKey.RANKNAME, rank.getName()));
		this.rank = rank;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		event.setCancelled(true);
		int slot = event.getRawSlot();

		if(event.getInventory() == null || !slotPermissionsMap.containsKey(slot)) {
			return;
		}

		togglePermission(slotPermissionsMap.get(slot));
		refreshSlot(slot);
	}

	private void togglePermission(GuildPermission permission) {
		if(rank.hasPermission(permission)) {
			rank.removePermission(permission);
		}
		else {
			rank.addPermission(permission);
		}
	}

	@Override
	public void generateContent() {
		inventory.clear();
		int slot = 0;
		Map<VarKey, String> vars = new HashMap<>();

		for(GuildPermission perm : GuildPermission.values()) {
			ItemStack itemStack;
			vars.clear();
			vars.put(VarKey.PERMNAME, Message.valueOf("INVENTORY_GUI_PERMISSIONS_NAMES_" + perm.name()).get());

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

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PERMNAME, Message.valueOf("INVENTORY_GUI_PERMISSIONS_NAMES_" + perm.name()).get());

		if(rank.hasPermission(perm)) {
			itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PERMISSIONS_ITEM_ENABLED.vars(vars).get());
		}
		else {
			itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PERMISSIONS_ITEM_DISABLED.vars(vars).get());
		}

		inventory.setItem(slot, itemStack);
	}
}
