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

import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryGuildRankSettings extends AbstractGUIInventory {
	private final NovaRank rank;
	private NovaGuild guild;

	private ItemStack editPermissionsItem;
	private ItemStack setDefaultItem;
	private ItemStack cloneItem;
	private ItemStack renameItem;
	private ItemStack deleteItem;
	private ItemStack memberListItem;

	public GUIInventoryGuildRankSettings(NovaRank rank) {
		super(9, Message.INVENTORY_GUI_RANKS_TITLE);
		this.rank = rank;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		ItemStack clickedItemStack = event.getCurrentItem();

		if(clickedItemStack.equals(editPermissionsItem)) {
			new GUIInventoryGuildPermissionSelect(rank).open(getViewer());
		}
		else if(clickedItemStack.equals(setDefaultItem)) {
			NovaRank clonedRank = rank;
			if(rank.isGeneric() && !RankManager.getDefaultRank().equals(rank)) {
				clonedRank = cloneRank();
			}

			if(!getGuild().getDefaultRank().isGeneric()) {
				getGuild().getDefaultRank().setDefault(false);
			}

			clonedRank.setDefault(true);
			generateContent();
		}
		else if(clickedItemStack.equals(cloneItem)) {
			cloneRank();
		}
		else if(clickedItemStack.equals(renameItem)) {
			//TODO renaming
		}
		else if(clickedItemStack.equals(deleteItem)) {
			rank.delete();
			close();
		}
		else if(clickedItemStack.equals(memberListItem)) {
			new GUIInventoryGuildRankMembers(getGuild(), rank).open(getViewer());
		}
	}

	@Override
	public void generateContent() {
		inventory.clear();

		editPermissionsItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_EDITPERMISSIONS.get());
		setDefaultItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_SETDEFAULT.get());
		cloneItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_CLONE.get());
		renameItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_RENAME.get());
		deleteItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_DELETE.get());
		memberListItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_MEMBERLIST.get());

		if(editPermissionsItem != null && !rank.isGeneric()) {
			inventory.addItem(editPermissionsItem);
		}

		if(setDefaultItem != null && !rank.equals(getGuild().getDefaultRank()) && !RankManager.getLeaderRank().equals(rank)) {
			inventory.addItem(setDefaultItem);
		}

		if(cloneItem != null && !RankManager.getLeaderRank().equals(rank)) {
			inventory.addItem(cloneItem);
		}

		if(renameItem != null && !rank.isGeneric()) {
			inventory.addItem(renameItem);
		}

		if(deleteItem != null && !rank.isGeneric()) {
			inventory.addItem(deleteItem);
		}

		if(memberListItem != null && !GUIInventoryGuildRankMembers.getMembers(getGuild(), rank).isEmpty()) {
			inventory.addItem(memberListItem);
		}

		ChestGUIUtils.addBackItem(this);
	}

	public NovaGuild getGuild() {
		return guild;
	}

	public void setGuild(NovaGuild guild) {
		this.guild = guild;
	}

	private NovaRank cloneRank() {
		String clonePrefix = Message.INVENTORY_GUI_RANK_SETTINGS_CLONEPREFIX.get();
		String cloneName = rank.getName().startsWith(clonePrefix) || rank.isGeneric() ? rank.getName() : clonePrefix + rank.getName();

		if(StringUtils.contains(cloneName, ' ')) {
			String[] split = StringUtils.split(cloneName, ' ');

			if(NumberUtils.isNumeric(split[split.length-1])) {
				cloneName = cloneName.substring(0, cloneName.length() - split[split.length-1].length() - 1);
			}
		}

		NovaRank clone = new NovaRankImpl(cloneName);
		clone.setClone(rank.isGeneric());
		NovaGuild guild;

		if(rank.isGeneric()) {
			GUIInventory previousGui = getViewer().getGuiInventoryHistory().get(getViewer().getGuiInventoryHistory().size() - 2);
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
		return clone;
	}
}
