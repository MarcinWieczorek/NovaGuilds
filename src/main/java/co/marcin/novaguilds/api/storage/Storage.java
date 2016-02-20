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

package co.marcin.novaguilds.api.storage;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.basic.NovaRegion;

import java.util.List;

public interface Storage {
	/**
	 * Set up the directories, connect to database etc.
	 * @return true if successful
	 */
	boolean setUp();

	/**
	 * Loads players
	 *
	 * @return List of players
	 */
	List<NovaPlayer> loadPlayers();

	/**
	 * Loads Guilds
	 *
	 * @return List of guilds
	 */
	List<NovaGuild> loadGuilds();

	/**
	 * Loads regions
	 *
	 * @return List of regions
	 */
	List<NovaRegion> loadRegions();

	/**
	 * Loads ranks
	 *
	 * @return List of ranks
	 */
	List<NovaRank> loadRanks();

	/**
	 * Saves all data
	 */
	void save();

	/**
	 * Saves all players
	 * @return The amount of saved items
	 */
	Integer savePlayers();

	/**
	 * Saves all guilds
	 * @return The amount of saved items
	 */
	Integer saveGuilds();

	/**
	 * Saves all regions
	 * @return The amount of saved items
	 */
	Integer saveRegions();

	/**
	 * Saves all ranks
	 * @return The amount of saved items
	 */
	Integer saveRanks();

	/**
	 * Adds a player
	 *
	 * @param nPlayer the player
	 */
	void add(NovaPlayer nPlayer);

	/**
	 * Adds a guild
	 *
	 * @param guild the guild
	 */
	void add(NovaGuild guild);

	/**
	 * Adds a region
	 *
	 * @param region the region
	 */
	void add(NovaRegion region);

	/**
	 * Adds a rank
	 *
	 * @param rank the rank
	 */
	void add(NovaRank rank);

	/**
	 * Saves a specific player
	 *
	 * @param nPlayer the player
	 */
	void save(NovaPlayer nPlayer);

	/**
	 * Saves a specific guild
	 *
	 * @param guild the guild
	 */
	void save(NovaGuild guild);

	/**
	 * Saves a specific region
	 *
	 * @param region the region
	 */
	void save(NovaRegion region);

	/**
	 * Saves a specific rank
	 *
	 * @param rank the rank
	 */
	void save(NovaRank rank);

	/**
	 * Removes a player
	 *
	 * @param nPlayer the player
	 */
	void remove(NovaPlayer nPlayer);

	/**
	 * Removes a guild
	 *
	 * @param guild the guild
	 */
	void remove(NovaGuild guild);

	/**
	 * Removes a region
	 *
	 * @param region the region
	 */
	void remove(NovaRegion region);

	/**
	 * Removes a rank
	 *
	 * @param rank the rank
	 */
	void remove(NovaRank rank);

	/**
	 * Removes players from a list
	 *
	 * @param list the list
	 */
	void removePlayers(List<NovaPlayer> list);

	/**
	 * Removes guilds from a list
	 *
	 * @param list the list
	 */
	void removeGuilds(List<NovaGuild> list);

	/**
	 * Removes regions from a list
	 *
	 * @param list the list
	 */
	void removeRegions(List<NovaRegion> list);

	/**
	 * Removes rank from a list
	 *
	 * @param list the list
	 */
	void removeRanks(List<NovaRank> list);
}
