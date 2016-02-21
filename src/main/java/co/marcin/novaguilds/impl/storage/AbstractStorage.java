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

package co.marcin.novaguilds.impl.storage;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.basic.NovaRank;

import java.util.List;

public abstract class AbstractStorage implements Storage {
	protected static final NovaGuilds plugin = NovaGuilds.getInstance();

	@Override
	public void save() {
		saveGuilds();
		saveRanks();
		saveRegions();
		savePlayers();
	}

	@Override
	public Integer savePlayers() {
		int count = 0;

		for(NovaPlayer nPlayer : plugin.getPlayerManager().getPlayers()) {
			if(nPlayer.isChanged()) {
				count++;
			}

			save(nPlayer);
		}

		return count;
	}

	@Override
	public Integer saveGuilds() {
		int count = 0;

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			if(guild.isChanged()) {
				count++;
			}

			save(guild);
		}

		return count;
	}

	@Override
	public Integer saveRegions() {
		int count = 0;

		for(NovaRegion region : plugin.getRegionManager().getRegions()) {
			if(region.isChanged()) {
				count++;
			}

			save(region);
		}

		return count;
	}

	@Override
	public Integer saveRanks() {
		int count = 0;

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			for(NovaRank rank : guild.getRanks()) {
				if(rank.isChanged() || rank.isNew()) {
					count++;
				}

				save(rank);
			}
		}

		return count;
	}

	@Override
	public void removePlayers(List<NovaPlayer> list) {
		for(NovaPlayer nPlayer : list) {
			remove(nPlayer);
		}
	}

	@Override
	public void removeGuilds(List<NovaGuild> list) {
		for(NovaGuild guild : list) {
			remove(guild);
		}
	}

	@Override
	public void removeRegions(List<NovaRegion> list) {
		for(NovaRegion region : list) {
			remove(region);
		}
	}

	@Override
	public void removeRanks(List<NovaRank> list) {
		for(NovaRank rank : list) {
			remove(rank);
		}
	}
}
