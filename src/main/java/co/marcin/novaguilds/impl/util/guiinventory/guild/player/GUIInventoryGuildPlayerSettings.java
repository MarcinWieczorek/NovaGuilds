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

package co.marcin.novaguilds.impl.util.guiinventory.guild.player;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIInventoryGuildPlayerSettings extends AbstractGUIInventory {
	private final NovaPlayer nPlayer;
	private ItemStack kickItem;
	private ItemStack rankItem;

	/**
	 * The constructor
	 *
	 * @param nPlayer the player who's settings are being edited
	 */
	public GUIInventoryGuildPlayerSettings(NovaPlayer nPlayer) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PLAYERSETTINGS_TITLE.clone().setVar(VarKey.PLAYERNAME, nPlayer.getName()));
		this.nPlayer = nPlayer;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem().equals(rankItem)) {
			if(PlayerManager.getPlayer(event.getWhoClicked()).hasPermission(GuildPermission.RANK_SET)) {
				new GUIInventoryGuildPlayerSettingsRank(nPlayer).open(getViewer());
			}
		}
		else if(event.getCurrentItem().equals(kickItem)) {
			getViewer().getPlayer().performCommand("g kick " + nPlayer.getName());
		}
	}

	@Override
	public void generateContent() {
		inventory.clear();

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.RANKNAME, nPlayer.getGuildRank() == null ? "Invalid_rank" : StringUtils.replace(nPlayer.getGuildRank().getName(), " ", "_"));

		kickItem = Message.INVENTORY_GUI_PLAYERSETTINGS_ITEM_KICK.getItemStack();
		rankItem = Message.INVENTORY_GUI_PLAYERSETTINGS_ITEM_RANK.clone().vars(vars).getItemStack();

		if(!nPlayer.equals(getViewer()) || Config.DEBUG.getBoolean()) {
			add(kickItem);
		}

		if(!nPlayer.equals(getViewer()) || Config.DEBUG.getBoolean()) {
			add(rankItem);
		}
	}
}
