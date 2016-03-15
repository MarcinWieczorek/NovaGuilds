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

package co.marcin.novaguilds.util.guiinventory.guild.rank;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIInventoryGuildRankList extends AbstractGUIInventory {
	private final NovaGuild guild;
	protected final Map<Integer, NovaRank> slotRanksMap = new HashMap<>();
	protected ItemStack addRankItem;

	public GUIInventoryGuildRankList(NovaGuild guild) {
		super(ChestGUIUtils.getChestSize(GuildPermission.values().length), Message.INVENTORY_GUI_RANKS_TITLE);
		this.guild = guild;
	}

	@Override
	public void generateContent() {
		inventory.clear();
		slotRanksMap.clear();

		int slot = 0;
		List<NovaRank> ranks = new ArrayList<>();
		ranks.addAll(NovaGuilds.getInstance().getRankManager().getGenericRanks());
		ranks.addAll(guild.getRanks());

		for(NovaRank rank : ranks) {
			NovaRank cloneOfGeneric = guild.getCloneOfGenericRank(rank);

			if(cloneOfGeneric != null) {
				continue;
			}

			ItemStack itemStack = Message.INVENTORY_GUI_RANKS_ROWITEM.setVar(VarKey.RANKNAME, StringUtils.replace(rank.getName(), " ", "_")).getItemStack();

			inventory.setItem(slot, itemStack);
			slotRanksMap.put(slot, rank);
			slot++;
		}

		addRankItem = Message.INVENTORY_GUI_RANKS_ADDITEM.getItemStack();
		inventory.addItem(addRankItem);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		NovaPlayer nPlayer = PlayerManager.getPlayer(event.getWhoClicked());

		if(!nPlayer.hasPermission(GuildPermission.RANK_EDIT)) {
			return;
		}

		ItemStack clickedItemStack = event.getCurrentItem();

		if(clickedItemStack.equals(addRankItem)) {
			String rankName = Message.INVENTORY_GUI_RANKS_DEFAULTNAME.get();
			for(NovaRank rank : guild.getRanks()) {
				if(rank.getName().equals(rankName)) {
					rankName = rankName + " " + NumberUtils.randInt(1, 999);
				}
			}

			NovaRank rank = new NovaRankImpl(rankName);
			guild.addRank(rank);
			generateContent();
			ChestGUIUtils.addBackItem(this);
		}
		else {
			NovaRank rank = slotRanksMap.get(event.getRawSlot());

			if(rank != null) {
				GUIInventoryGuildRankSettings guiInventory = new GUIInventoryGuildRankSettings(rank);
				guiInventory.setGuild(getGuild());
				guiInventory.open(nPlayer);
			}
		}
	}

	public NovaGuild getGuild() {
		return guild;
	}
}
