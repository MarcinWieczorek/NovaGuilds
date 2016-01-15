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

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIInventoryGuildPlayerSettings implements GUIInventory {
	private final Inventory inventory;
	private final NovaPlayer nPlayer;
	private NovaPlayer viewer;
	private ItemStack kickItem;
	private ItemStack rankItem;

	public GUIInventoryGuildPlayerSettings(NovaPlayer nPlayer) {
		this.nPlayer = nPlayer;

		Map<String, String> vars = new HashMap<>();
		vars.put("PLAYERNAME", nPlayer.getName());
		inventory = ChestGUIUtils.createInventory(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PLAYERSETTINGS_TITLE.vars(vars));
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem().equals(rankItem)) {
			if(NovaPlayer.get(event.getWhoClicked()).hasPermission(GuildPermission.RANK_SET)) {
				new GUIInventoryGuildPlayerSettingsRank(nPlayer).open(NovaPlayer.get(event.getWhoClicked()));
			}
		}
		else if(event.getCurrentItem().equals(kickItem)) {
			nPlayer.getPlayer().performCommand("g kick " + nPlayer.getName());
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

		Map<String, String> vars = new HashMap<>();
		vars.put("RANKNAME", nPlayer.getGuildRank() == null ? "Invalid_rank" : StringUtils.replace(nPlayer.getGuildRank().getName(), " ", "_"));

		kickItem = Message.INVENTORY_GUI_PLAYERSETTINGS_ITEM_KICK.getItemStack();
		rankItem = Message.INVENTORY_GUI_PLAYERSETTINGS_ITEM_RANK.vars(vars).getItemStack();

		if(kickItem != null && (!nPlayer.equals(viewer) || Config.DEBUG.getBoolean())) {
			inventory.addItem(kickItem);
		}

		if(rankItem != null && (!nPlayer.equals(viewer) || Config.DEBUG.getBoolean())) {
			inventory.addItem(rankItem);
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
