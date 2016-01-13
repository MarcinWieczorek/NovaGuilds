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
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.enums.GuildPermission;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIInventoryGuildPlayerSettingsRank extends GUIInventoryGuildRankList {
	private final NovaPlayer nPlayer;

	public GUIInventoryGuildPlayerSettingsRank(NovaPlayer nPlayer) {
		super(nPlayer.getGuild());
		this.nPlayer = nPlayer;
		getInventory().remove(addRankItem);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(getViewer().hasPermission(GuildPermission.RANK_SET)) {
			NovaRank rank = slotRanksMap.get(event.getRawSlot());
			nPlayer.setGuildRank(rank);
		}
	}
}
