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
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChestGUIUtils {
	public static final List<NovaPlayer> guiContinueList = new ArrayList<>();

	public static int getChestSize(int count) {
		return (count/9)*9 + (count%9==0 ? 0 : 9);
	}

	public static void openGUIInventory(NovaPlayer nPlayer, GUIInventory guiInventory) {
		if(nPlayer.isOnline()) {
			nPlayer.setGuiInventory(guiInventory);

			if(!guiContinueList.contains(nPlayer)) {
				guiContinueList.add(nPlayer);
			}

			guiInventory.setViewer(nPlayer);
			guiInventory.generateContent();

			Inventory inventory = guiInventory.getInventory();

			ItemStack lastItem = inventory.getItem(inventory.getSize() - 1);
			if((lastItem == null || lastItem.getType() == Material.AIR) && nPlayer.getGuiInventoryHistory().size()>1) {
				inventory.setItem(inventory.getSize()-1, Message.INVENTORY_GUI_BACK.getItemStack());
			}

			nPlayer.getPlayer().openInventory(inventory);

			guiContinueList.remove(nPlayer);
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
