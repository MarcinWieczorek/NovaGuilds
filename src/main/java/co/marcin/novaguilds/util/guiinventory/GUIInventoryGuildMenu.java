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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.ItemStackUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryGuildMenu implements GUIInventory {
	private final Inventory inventory;
	private final NovaGuilds plugin = NovaGuilds.getInstance();
	private final NovaPlayer nPlayer;
	private ItemStack ranksItem;
	private ItemStack playersItem;
	private NovaPlayer viewer;

	public GUIInventoryGuildMenu(NovaPlayer nPlayer) {
		this.nPlayer = nPlayer;
		inventory = ChestGUIUtils.createInventory(9, Message.INVENTORY_GGUI_NAME);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		ItemStack clickedItemStack = event.getCurrentItem();

		if(clickedItemStack.equals(ranksItem)) {
			new GUIInventoryGuildRankList(nPlayer.getGuild()).open(nPlayer);
		}
		else if(clickedItemStack.equals(playersItem)) {
			new GUIInventoryGuildPlayersList(nPlayer.getGuild()).open(nPlayer);
		}
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void open(NovaPlayer nPlayer) {
		ChestGUIUtils.openGUIInventory(nPlayer, this);
	}

	@Override
	public void generateContent() {
		inventory.clear();
		plugin.getCommandManager().updateGuiTop();

		for(ItemStack item : plugin.getCommandManager().getGuiItems()) {
			inventory.addItem(item);
		}

		ranksItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANKS_ICONITEM.get());
		playersItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_PLAYERSLIST_ICONITEM.get());

		if(nPlayer.hasGuild()) {
			if(playersItem != null) {
				inventory.addItem(playersItem);
			}

			if(ranksItem != null && nPlayer.hasPermission(GuildPermission.RANK_EDIT)) {
				inventory.addItem(ranksItem);
			}
		}
	}

	@Override
	public NovaPlayer getViewer() {
		return viewer;
	}

	@Override
	public void setViewer(NovaPlayer nPlayer) {
		this.viewer = nPlayer;
	}
}
