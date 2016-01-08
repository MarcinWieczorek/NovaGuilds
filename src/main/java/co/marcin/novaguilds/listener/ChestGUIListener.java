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
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.concurrent.TimeUnit;

public class ChestGUIListener implements Listener {
	private final NovaGuilds plugin = NovaGuilds.getInstance();

	public ChestGUIListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null || event.getCurrentItem()==null) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		NovaPlayer nPlayer = NovaPlayer.get(player);
		GUIInventory guiInventory = nPlayer.getGuiInventory();

		if(guiInventory != null) {
			guiInventory.onClick(event);
			event.setCancelled(true);
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
