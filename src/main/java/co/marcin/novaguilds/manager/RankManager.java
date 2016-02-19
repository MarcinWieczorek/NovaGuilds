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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import com.google.common.collect.Lists;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RankManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final List<NovaRank> genericRanks = new ArrayList<>();
	private boolean loaded = false;

	public boolean isLoaded() {
		return loaded;
	}

	public void load() {
		int	count = plugin.getStorage().loadRanks().size();

		LoggerUtils.info("Loaded " + count + " ranks.");

		//Assing ranks to players
		assignRanks();

		loaded = true;
	}

	public void save() {
		long nanoTime = System.nanoTime();

		int count = plugin.getStorage().saveRanks();

		LoggerUtils.info("Ranks data saved in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " ranks)");
	}

	public void delete(NovaRank rank) {
		if(rank.isGeneric()) {
			return;
		}

		plugin.getStorage().remove(rank);

		rank.getGuild().removeRank(rank);

		for(NovaPlayer nPlayer : new ArrayList<>(rank.getMembers())) {
			rank.removeMember(nPlayer);
		}
	}

	public void delete(NovaGuild guild) {
		for(NovaRank rank : guild.getRanks()) {
			plugin.getStorage().remove(rank);
		}

		guild.setRanks(new ArrayList<NovaRank>());
	}

	public void loadDefaultRanks() {
		genericRanks.clear();
		NovaRank leaderRank = new NovaRank(Message.INVENTORY_GUI_RANKS_LEADERNAME.get());
		leaderRank.setPermissions(Lists.newArrayList(GuildPermission.values()));
		genericRanks.add(leaderRank);
		int count = 1;

		ConfigurationSection section = Config.GUILD_DEFAULTRANKS.getConfigurationSection();
		for(String rankName : section.getKeys(false)) {
			NovaRank rank = new NovaRank(rankName);

			for(String permName : section.getStringList(rankName)) {
				rank.addPermission(GuildPermission.valueOf(permName.toUpperCase()));
			}

			genericRanks.add(rank);
			count++;
		}

		LoggerUtils.info("Loaded " + count + " default (guild) ranks.");
	}

	public boolean isGenericRank(NovaRank rank) {
		return getGenericRanks().contains(rank);
	}

	public List<NovaRank> getGenericRanks() {
		return genericRanks;
	}

	private void assignRanks() {
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			assignRanks(guild);
		}
	}

	public void assignRanks(NovaGuild guild) {
		for(NovaPlayer nPlayer : guild.getPlayers()) {
			if(nPlayer.getGuildRank() == null) {
				NovaRank defaultRank = guild.getDefaultRank();
				NovaRank rank;

				if(nPlayer.isLeader()) {
					rank = getLeaderRank();
				}
				else {
					if(defaultRank == null) {
						rank = getGenericRanks().get(1);
					}
					else {
						rank = defaultRank;
					}
				}

				nPlayer.setGuildRank(rank);
			}
		}
	}

	public static NovaRank getLeaderRank() {
		return NovaGuilds.getInstance().getRankManager().getGenericRanks().get(0);
	}

	public static NovaRank getDefaultRank() {
		return plugin.getRankManager().getGenericRanks().get(1);
	}
}
