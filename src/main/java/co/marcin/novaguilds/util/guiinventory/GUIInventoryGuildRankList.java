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
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIInventoryGuildRankList implements GUIInventory {
	private final Inventory inventory;
	private final NovaGuild guild;
	protected Map<Integer, NovaRank> slotRanksMap = new HashMap<>();
	protected ItemStack addRankItem;

	public GUIInventoryGuildRankList(NovaGuild guild) {
		this.guild = guild;
		inventory = Bukkit.createInventory(null, ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_RANKS_TITLE.get());
	}

	@Override
	public void generateContent() {
		inventory.clear();
		slotRanksMap.clear();

		int slot = 0;
		Map<String, String> vars = new HashMap<>();
		List<NovaRank> ranks = new ArrayList<>();
		ranks.addAll(NovaGuilds.getInstance().getRankManager().getDefaultRanks());
		ranks.addAll(guild.getRanks());

		for(NovaRank rank : ranks) {
			vars.clear();
			vars.put("RANKNAME", StringUtils.replace(rank.getName(), " ", "_"));
			ItemStack itemStack = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANKS_ROWITEM.vars(vars).get());
			inventory.addItem(itemStack);
			slotRanksMap.put(slot, rank);
			slot++;
		}

		addRankItem = ItemStackUtils.stringToItemStack(Message.INVENTORY_GUI_RANKS_ADDITEM.get());
		inventory.addItem(addRankItem);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		NovaPlayer nPlayer = NovaPlayer.get(event.getWhoClicked());

		if(!nPlayer.hasPermission(GuildPermission.RANK_EDIT)) {
			return;
		}

		ItemStack clickedItemStack = event.getCurrentItem();

		if(clickedItemStack.equals(addRankItem)) {
			String rankName = "rank"; //TODO name
			for(NovaRank rank : guild.getRanks()) {
				if(rank.getName().equals(rankName)) {
					rankName = rankName + " " + NumberUtils.randInt(1,999);
				}
			}

			NovaRank rank = new NovaRank(rankName);
			guild.addRank(rank);
			generateContent();
		}
		else {
			NovaRank rank = slotRanksMap.get(event.getRawSlot());

			if(rank != null) {
				new GUIInventoryGuildPermissionSelect(rank).open(nPlayer);
			}
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
}
