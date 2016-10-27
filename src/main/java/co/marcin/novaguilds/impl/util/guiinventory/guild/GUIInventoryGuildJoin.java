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

package co.marcin.novaguilds.impl.util.guiinventory.guild;

import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIInventoryGuildJoin extends AbstractGUIInventory {
	private final Map<Integer, NovaGuild> slotGuildsMap = new HashMap<>();
	private final List<NovaGuild> guildList = new ArrayList<>();

	public GUIInventoryGuildJoin(List<NovaGuild> guilds) {
		super(ChestGUIUtils.getChestSize(guilds.size()), Message.INVENTORY_GUI_JOIN_TITLE);
		guildList.addAll(guilds);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		NovaGuild guild = slotGuildsMap.get(event.getRawSlot());

		if(guild == null) {
			return;
		}

		Bukkit.dispatchCommand(getViewer().getPlayer(), "g join " + guild.getName());
	}

	@Override
	public void generateContent() {
		inventory.clear();
		int slot = 0;
		slotGuildsMap.clear();
		for(NovaGuild guild : guildList) {
			MessageWrapper msg = Message.INVENTORY_GUI_JOIN_ROWITEM.clone()
					.setVar(VarKey.GUILDNAME, guild.getName())
					.setVar(VarKey.TAG, guild.getTag())
					.setVar(VarKey.PLAYERNAME, guild.getLeader().getName());
			LoggerUtils.debug(msg.get());
			ItemStack itemStack = msg.getItemStack();

			if(itemStack == null) {
				continue;
			}

			add(itemStack);
			slotGuildsMap.put(slot, guild);
			close();
			slot++;
		}
	}
}
