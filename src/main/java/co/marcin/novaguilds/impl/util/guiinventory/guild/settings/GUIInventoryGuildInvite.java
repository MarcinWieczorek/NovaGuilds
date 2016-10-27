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

package co.marcin.novaguilds.impl.util.guiinventory.guild.settings;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIInventoryGuildInvite extends AbstractGUIInventory {
	private final Map<Integer, NovaPlayer> playerMap = new HashMap<>();

	/**
	 * The constructor
	 */
	public GUIInventoryGuildInvite() {
		super(ChestGUIUtils.getChestSize(NovaGuilds.getOnlinePlayers().size()), Message.INVENTORY_GUI_SETTINGS_INVITE_TITLE);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		getViewer().getPlayer().performCommand("g invite " + playerMap.get(event.getRawSlot()).getName());
	}

	@Override
	public void generateContent() {
		int index = 0;
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			NovaPlayer nPlayer = PlayerManager.getPlayer(player);

			if(nPlayer.hasGuild()) {
				continue;
			}

			ItemStack itemStack = Message.INVENTORY_GUI_SETTINGS_INVITE_ITEM.clone().setVar(VarKey.PLAYERNAME, nPlayer.getName()).getItemStack();

			add(itemStack);
			playerMap.put(index, nPlayer);
			index++;
		}
	}
}
