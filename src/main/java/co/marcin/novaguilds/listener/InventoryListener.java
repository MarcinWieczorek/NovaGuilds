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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.api.util.AbstractListener;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener extends AbstractListener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		String nameRequiredItems = Message.INVENTORY_REQUIREDITEMS_NAME.get();
		String nameGGUI = Message.INVENTORY_GGUI_NAME.get();
		Player player = (Player) event.getWhoClicked();

		Inventory clickedInventory = InventoryUtils.getClickedInventory(event);

		if(clickedInventory != null && clickedInventory.getTitle() != null && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			if(event.getInventory().getName().equals(nameRequiredItems) || event.getInventory().getName().equals(nameGGUI)) {
				if(clickedInventory.equals(event.getView().getTopInventory()) || event.isShiftClick()) {
					//gui
					if(event.getInventory().getTitle().equals(nameGGUI)) {
						ItemStack clickedItem = event.getCurrentItem();

						String menuCommand = plugin.getCommandManager().getGuiCommand(clickedItem);

						if(menuCommand != null && !menuCommand.isEmpty()) {
							player.chat("/" + menuCommand);
							player.closeInventory();
						}
					}

					event.setCancelled(true);
				}
			}
		}
	}
}
