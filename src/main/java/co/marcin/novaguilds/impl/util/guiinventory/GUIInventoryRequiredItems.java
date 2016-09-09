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

package co.marcin.novaguilds.impl.util.guiinventory;

import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIInventoryRequiredItems extends AbstractGUIInventory {
	private final List<ItemStack> requiredItems = new ArrayList<>();

	public GUIInventoryRequiredItems(List<ItemStack> itemStackList) {
		super(ChestGUIUtils.getChestSize(itemStackList.size()), Message.INVENTORY_REQUIREDITEMS_NAME);
		requiredItems.addAll(itemStackList);
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void generateContent() {
		for(ItemStack item : requiredItems) {
			add(item);
		}
	}
}
