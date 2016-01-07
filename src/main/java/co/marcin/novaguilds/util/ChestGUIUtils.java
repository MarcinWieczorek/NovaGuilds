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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class ChestGUIUtils {
	public static int getChestSize(int count) {
		return (count/9)*9 + (count%9==0 ? 0 : 9);
	}

	public static void openGUIInventory(NovaPlayer nPlayer, GUIInventory guiInventory) {
		if(nPlayer.isOnline()) {
			nPlayer.getPlayer().openInventory(guiInventory.getInventory());
			nPlayer.setGuiInventory(guiInventory);
		}
	}

	public static Inventory createInventory(int size, String title) {
		if(title.length() > 32) {
			title = title.substring(0, 32);
		}

		return Bukkit.createInventory(null, size, title);
	}

	public static Inventory createInventory(int size, Message title) {
		return createInventory(size, title.get());
	}

	public static Inventory createInventory(int size) {
		return createInventory(size, "");
	}
}
