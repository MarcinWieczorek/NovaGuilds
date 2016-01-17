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
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryGuildRankSettings implements GUIInventory {
	private final Inventory inventory;
	private final NovaRank rank;
	private NovaPlayer viewer;

	private ItemStack editPermissionsItem;
	private ItemStack setDefaultItem;
	private ItemStack cloneItem;
	private ItemStack renameItem;
	private ItemStack deleteItem;

	public GUIInventoryGuildRankSettings(NovaRank rank) {
		inventory = ChestGUIUtils.createInventory(9, Message.INVENTORY_GUI_RANKS_TITLE);
		this.rank = rank;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		ItemStack clickedItemStack = event.getCurrentItem();

		if(clickedItemStack.equals(editPermissionsItem)) {
			new GUIInventoryGuildPermissionSelect(rank).open(viewer);
		}
		else if(clickedItemStack.equals(setDefaultItem)) {
			rank.setDef(true);
		}
		else if(clickedItemStack.equals(cloneItem)) {
			String clonePrefix = Message.INVENTORY_GUI_RANK_SETTINGS_CLONEPREFIX.get();
			String cloneName = rank.getName().startsWith(clonePrefix) || rank.isGeneric() ? rank.getName() : clonePrefix + rank.getName();

			if(StringUtils.contains(cloneName, ' ')) {
				String[] split = StringUtils.split(cloneName, ' ');

				if(NumberUtils.isNumeric(split[split.length-1])) {
					cloneName = cloneName.substring(0, cloneName.length() - split[split.length-1].length() - 1);
				}
			}

			NovaRank clone = new NovaRank(cloneName);
			clone.setClone(rank.isGeneric());
			NovaGuild guild;

			if(rank.isGeneric()) {
				GUIInventory previousGui = getViewer().getGuiInventoryHistory().get(getViewer().getGuiInventoryHistory().size() - 2);

				if(previousGui == null) {
					return;
				}

				if(!(previousGui instanceof GUIInventoryGuildRankList)) {
					return;
				}

				guild = ((GUIInventoryGuildRankList) previousGui).getGuild();
			}
			else {
				guild = rank.getGuild();
			}

			boolean doubleName;
			int i = 1;
			do {
				if(i > 999) {
					break;
				}

				doubleName = false;
				for(NovaRank loopRank : guild.getRanks()) {
					if(!loopRank.isGeneric() && loopRank.getName().equalsIgnoreCase(clone.getName())) {
						doubleName = true;
					}
				}

				if(doubleName) {
					clone.setName(cloneName + " " + i);
				}

				i++;
			} while(doubleName);

			clone.setPermissions(rank.getPermissions());
			guild.addRank(clone);
		}
		else if(clickedItemStack.equals(renameItem)) {
			//TODO renaming
		}
		else if(clickedItemStack.equals(deleteItem)) {
			rank.delete();
			close();
		}
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void open(NovaPlayer nPlayer) {
		viewer = nPlayer;
		ChestGUIUtils.openGUIInventory(nPlayer, this);
	}

	@Override
	public void generateContent() {
		inventory.clear();

		editPermissionsItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_EDITPERMISSIONS.get());
		setDefaultItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_SETDEFAULT.get());
		cloneItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_CLONE.get());
		renameItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_RENAME.get());
		deleteItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_DELETE.get());

		if(editPermissionsItem != null && !rank.isGeneric()) {
			inventory.addItem(editPermissionsItem);
		}

		if(setDefaultItem != null && !rank.isGeneric() && rank.isDef()) {
			inventory.addItem(setDefaultItem);
		}

		if(cloneItem != null && !NovaGuilds.getInstance().getRankManager().getDefaultRanks().get(0).equals(rank)) {
			inventory.addItem(cloneItem);
		}

		if(renameItem != null && !rank.isGeneric()) {
			inventory.addItem(renameItem);
		}

		if(deleteItem != null && !rank.isGeneric()) {
			inventory.addItem(deleteItem);
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

	@Override
	public void close() {
		getViewer().getPlayer().closeInventory();
	}
}
