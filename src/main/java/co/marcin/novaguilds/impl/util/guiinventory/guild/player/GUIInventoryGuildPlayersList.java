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

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIInventoryGuildPlayersList extends AbstractGUIInventory {
	private final Map<Integer, NovaPlayer> slotPlayersMap = new HashMap<>();
	protected final NovaGuild guild;

	/**
	 * The constructor
	 *
	 * @param guild the guild
	 */
	public GUIInventoryGuildPlayersList(NovaGuild guild) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_PLAYERSLIST_TITLE);
		this.guild = guild;
	}

	@Override
	public void generateContent() {
		generateContent(guild.getPlayers());
	}

	public void generateContent(List<NovaPlayer> playerList) {
		inventory.clear();
		int slot = 0;
		slotPlayersMap.clear();
		for(NovaPlayer nPlayer : playerList) {
			ItemStack itemStack = Message.INVENTORY_GUI_PLAYERSLIST_ROWITEM.setVar(VarKey.PLAYERNAME, nPlayer.getName()).getItemStack();

			if(itemStack == null) {
				continue;
			}

			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(nPlayer.getName());

			itemStack.setItemMeta(meta);
			add(itemStack);
			slotPlayersMap.put(slot, nPlayer);
			slot++;
		}
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(slotPlayersMap.get(event.getRawSlot()) == null) {
			return;
		}

		new GUIInventoryGuildPlayerSettings(slotPlayersMap.get(event.getRawSlot())).open(PlayerManager.getPlayer(event.getWhoClicked()));
	}
}
