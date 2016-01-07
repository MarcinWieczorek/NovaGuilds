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

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.ItemStackUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class GUIInventoryGuildPlayersList implements GUIInventory {
	private final Inventory inventory;
	private final Map<Integer, NovaPlayer> slotPlayersMap = new HashMap<>();
	private final NovaGuild guild;

	public GUIInventoryGuildPlayersList(NovaGuild guild) {
		this.guild = guild;

		inventory = ChestGUIUtils.createInventory(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PLAYERSLIST_TITLE);

		generateContent();
	}

	private void generateContent() {
		int slot = 0;
		slotPlayersMap.clear();
		for(NovaPlayer nPlayer : guild.getPlayers()) {
//			ItemStack itemStack = InventoryUtils.getPlayerHead(nPlayer.getName());

			Map<String, String> vars = new HashMap<>();
			vars.put("PLAYERNAME", nPlayer.getName());

			ItemStack itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PLAYERSLIST_ROWITEM.vars(vars).get());

			if(itemStack == null) {
				continue;
			}

			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(nPlayer.getName());

			itemStack.setItemMeta(meta);
			inventory.addItem(itemStack);
			slotPlayersMap.put(slot, nPlayer);
			slot++;
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(slotPlayersMap.get(event.getRawSlot()) == null) {
			return;
		}

		new GUIInventoryGuildPlayerSettings(slotPlayersMap.get(event.getRawSlot())).open(NovaPlayer.get(event.getWhoClicked()));
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void open(NovaPlayer nPlayer) {
		ChestGUIUtils.openGUIInventory(nPlayer, this);
	}
}
