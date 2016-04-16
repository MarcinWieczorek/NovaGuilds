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

import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.impl.util.guiinventory.guild.player.GUIInventoryGuildPlayersList;
import co.marcin.novaguilds.impl.util.guiinventory.guild.rank.GUIInventoryGuildRankList;
import co.marcin.novaguilds.impl.util.guiinventory.guild.settings.GUIInventoryGuildSettings;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIInventoryGuildMenu extends AbstractGUIInventory {
	private ItemStack ranksItem;
	private ItemStack playersItem;
	private ItemStack settingsItem;
	private ItemStack homeTeleportItem;
	private ItemStack guildTopItem;

	public GUIInventoryGuildMenu() {
		super(9, Message.INVENTORY_GGUI_NAME);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		ItemStack clickedItemStack = event.getCurrentItem();

		if(clickedItemStack.equals(ranksItem)) {
			new GUIInventoryGuildRankList(getViewer().getGuild()).open(getViewer());
		}
		else if(clickedItemStack.equals(playersItem)) {
			new GUIInventoryGuildPlayersList(getViewer().getGuild()).open(getViewer());
		}
		else if(clickedItemStack.equals(settingsItem)) {
			new GUIInventoryGuildSettings().open(getViewer());
		}
		else if(clickedItemStack.equals(homeTeleportItem)) {
			Bukkit.dispatchCommand(getViewer().getPlayer(), "g home");
		}
	}

	@Override
	public void generateContent() {
		inventory.clear();

		ranksItem = Message.INVENTORY_GUI_RANKS_ICONITEM.getItemStack();
		playersItem = Message.INVENTORY_GUI_PLAYERSLIST_ICONITEM.getItemStack();
		settingsItem = Message.INVENTORY_GUI_SETTINGS_ITEM_ICON.getItemStack();
		homeTeleportItem = Message.INVENTORY_GUI_HOMETP.getItemStack();
		guildTopItem = Message.INVENTORY_GUI_GUILDTOP.getItemStack();

		if(guildTopItem != null) {
			add(guildTopItem);
			updateGuiTop();
		}

		if(getViewer().hasGuild()) {
			add(homeTeleportItem);
			add(playersItem);

			if(getViewer().hasPermission(GuildPermission.RANK_EDIT)) {
				add(ranksItem);
			}

			add(settingsItem);
		}
	}

	protected void updateGuiTop() {
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(guildTopItem.getType());
		meta.setDisplayName(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		meta.setLore(plugin.getGuildManager().getTopGuilds());
		guildTopItem.setItemMeta(meta);
		getInventory().setItem(0, guildTopItem);
	}
}
