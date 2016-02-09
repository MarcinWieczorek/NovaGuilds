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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.AbstractListener;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class ChestGUIListener extends AbstractListener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = InventoryUtils.getClickedInventory(event);
		if(inventory == null || event.getCurrentItem() == null || !inventory.equals(event.getView().getTopInventory())) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		NovaPlayer nPlayer = NovaPlayer.get(player);
		GUIInventory guiInventory = nPlayer.getGuiInventory();

		if(guiInventory != null) {
			event.setCancelled(true);

			if(event.getSlot() == inventory.getSize() - 1 && event.getCurrentItem().equals(Message.INVENTORY_GUI_BACK.getItemStack())) {
				player.closeInventory();
				return;
			}

			guiInventory.onClick(event);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		final NovaPlayer nPlayer = NovaPlayer.get(event.getPlayer());
		if(nPlayer.getGuiInventory() != null && !ChestGUIUtils.guiContinueList.contains(nPlayer)) {
			if(nPlayer.getGuiInventoryHistory().size() == 1) {
				nPlayer.setGuiInventory(null);
			}
			else {
				nPlayer.removeLastGUIInventoryHistory();

				NovaGuilds.runTaskLater(new Runnable() {
					@Override
					public void run() {
						nPlayer.getGuiInventory().open(nPlayer);
					}
				}, 1, TimeUnit.MILLISECONDS);
			}
		}
	}
}
