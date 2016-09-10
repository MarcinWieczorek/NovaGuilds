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
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.basic.GenericRankImpl;
import co.marcin.novaguilds.impl.storage.managers.database.AbstractDatabaseResourceManager;
import co.marcin.novaguilds.util.LoggerUtils;
import com.google.common.collect.Lists;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RankManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final List<NovaRank> genericRanks = new ArrayList<>();
	private boolean loaded = false;

	/**
	 * Checks if the ranks are loaded
	 *
	 * @return true if loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Loads the ranks
	 */
	public void load() {
		int count = getResourceManager().load().size();

		LoggerUtils.info("Loaded " + count + " ranks.");

		//Assign ranks to players
		assignRanks();

		loaded = true;
	}

	/**
	 * Saves all ranks
	 */
	public void save() {
		long nanoTime = System.nanoTime();

		if(getResourceManager() instanceof AbstractDatabaseResourceManager) {
			AbstractDatabaseResourceManager<NovaRank> databaseResourceManager = (AbstractDatabaseResourceManager<NovaRank>) getResourceManager();
			int count = databaseResourceManager.executeUpdateUUID();
			LoggerUtils.info("Rank UUIDs updated in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " ranks)");
		}

		nanoTime = System.nanoTime();
		int count = getResourceManager().executeSave() + getResourceManager().save(get());
		LoggerUtils.info("Ranks data saved in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " ranks)");

		nanoTime = System.nanoTime();
		count = getResourceManager().executeRemoval();
		LoggerUtils.info("Ranks removed in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " ranks)");
	}

	/**
	 * Deletes a rank
	 *
	 * @param rank the rank
	 */
	public void delete(NovaRank rank) {
		if(rank.isGeneric()) {
			return;
		}

		getResourceManager().addToRemovalQueue(rank);

		rank.getGuild().removeRank(rank);

		for(NovaPlayer nPlayer : new ArrayList<>(rank.getMembers())) {
			rank.removeMember(nPlayer);
		}
	}

	/**
	 * Deletes all ranks of a guild
	 *
	 * @param guild the guild
	 */
	public void delete(NovaGuild guild) {
		for(NovaRank rank : guild.getRanks()) {
			getResourceManager().addToRemovalQueue(rank);
		}

		guild.setRanks(new ArrayList<NovaRank>());
	}

	/**
	 * Loads generic ranks
	 */
	public void loadDefaultRanks() {
		genericRanks.clear();
		NovaRank leaderRank = new GenericRankImpl(Message.INVENTORY_GUI_RANKS_LEADERNAME.get());
		leaderRank.setPermissions(Lists.newArrayList(GuildPermission.values()));
		genericRanks.add(leaderRank);
		int count = 1;

		ConfigurationSection section = Config.GUILD_DEFAULTRANKS.getConfigurationSection();
		for(String rankName : section.getKeys(false)) {
			NovaRank rank = new GenericRankImpl(rankName);

			for(String permName : section.getStringList(rankName)) {
				rank.addPermission(GuildPermission.valueOf(permName.toUpperCase()));
			}

			genericRanks.add(rank);
			count++;
		}

		LoggerUtils.info("Loaded " + count + " default (guild) ranks.");
	}

	/**
	 * Gets generic ranks
	 *
	 * @return list with generic ranks
	 */
	public List<NovaRank> getGenericRanks() {
		return genericRanks;
	}

	/**
	 * Gets all the ranks
	 *
	 * @return collection of ranks
	 */
	public Collection<NovaRank> get() {
		Collection<NovaRank> collection = new HashSet<>();

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			for(NovaRank rank : guild.getRanks()) {
				if(!rank.isGeneric()) {
					collection.add(rank);
				}
			}
		}

		return collection;
	}

	/**
	 * Assigns ranks to all players
	 */
	private void assignRanks() {
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			assignRanks(guild);
		}
	}

	/**
	 * Assigns ranks in a guild
	 *
	 * @param guild the guild
	 */
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

	/**
	 * Gets leader rank
	 *
	 * @return the rank
	 */
	public static NovaRank getLeaderRank() {
		return NovaGuilds.getInstance().getRankManager().getGenericRanks().get(0);
	}

	/**
	 * Gets default generic rank
	 *
	 * @return the rank
	 */
	public static NovaRank getDefaultRank() {
		return plugin.getRankManager().getGenericRanks().get(1);
	}

	/**
	 * Gets the resource manager
	 *
	 * @return the manager
	 */
	private ResourceManager<NovaRank> getResourceManager() {
		return plugin.getStorage().getResourceManager(NovaRank.class);
	}
}
