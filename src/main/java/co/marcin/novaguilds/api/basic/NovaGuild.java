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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.basic.NovaRegion;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface NovaGuild {
	/**
	 * Gets the UUID
	 *
	 * @return the UUID
	 */
	UUID getUUID();

	/**
	 * Gets the name
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the points
	 *
	 * @return the points
	 */
	int getPoints();

	/**
	 * Gets the allies
	 *
	 * @return the list of allies
	 */
	List<NovaGuild> getAllies();

	/**
	 * Gets the names of allies
	 *
	 * @return the list of allies
	 */
	List<String> getAlliesNames();

	/**
	 * Gets the names of ally-invitations
	 *
	 * @return the list
	 */
	List<String> getAllyInvitationNames();

	/**
	 * Gets the ally invitations
	 *
	 * @return the list of ally invitations
	 */
	List<NovaGuild> getAllyInvitations();

	/**
	 * Gets the wars (names)
	 *
	 * @return the list of wars
	 */
	List<String> getWarsNames();

	/**
	 * Gets the wars
	 *
	 * @return the list of wars
	 */
	List<NovaGuild> getWars();

	/**
	 * Gets the no-war invitations (names)
	 *
	 * @return the list of names
	 */
	List<String> getNoWarInvitationNames();

	/**
	 * Gets the no-war invitations
	 *
	 * @return the list of no-war invitations
	 */
	List<NovaGuild> getNoWarInvitations();

	/**
	 * Gets the list of players
	 *
	 * @return the list of players
	 */
	List<NovaPlayer> getPlayers();

	/**
	 * Gets the list of online NovaPlayers
	 *
	 * @return the list
	 */
	List<NovaPlayer> getOnlineNovaPlayers();

	/**
	 * Gets the list of online players
	 *
	 * @return the list
	 */
	List<Player> getOnlinePlayers();

	/**
	 * Gets invited players
	 *
	 * @return the list
	 */
	List<NovaPlayer> getInvitedPlayers();

	/**
	 * Gets the ranks
	 *
	 * @return the list
	 */
	List<NovaRank> getRanks();

	/**
	 * Gets the tag
	 *
	 * @return the tag string
	 */
	String getTag();

	/**
	 * Gets the region
	 *
	 * @return the region instance
	 */
	NovaRegion getRegion();

	/**
	 * Gets the vault hologram
	 *
	 * @return the hologram (HolographicDisplays hologram instance)
	 */
	Hologram getVaultHologram();

	/**
	 * Gets the vault location
	 *
	 * @return the Location instance
	 */
	Location getVaultLocation();

	/**
	 * Gets the guild's home
	 *
	 * @return the Location instace
	 */
	Location getHome();

	/**
	 * Gets the leader
	 *
	 * @return the leader
	 */
	NovaPlayer getLeader();

	/**
	 * Gets the name of the leader
	 *
	 * @return the name of the leader
	 */
	String getLeaderName();

	/**
	 * Gets the ID
	 *
	 * @return the ID
	 */
	int getId();

	/**
	 * Gets the amount of money
	 *
	 * @return the amount
	 */
	double getMoney();

	/**
	 * Get the raid
	 *
	 * @return the NovaRaid instance
	 */
	NovaRaid getRaid();

	/**
	 * Gets the amount of lives
	 *
	 * @return the amount
	 */
	int getLives();

	/**
	 * Get the resting time
	 *
	 * @return the time (unixtime)
	 */
	long getTimeRest();

	/**
	 * Get the time of losing a live
	 *
	 * @return the time (unixtime)
	 */
	long getLostLiveTime();

	/**
	 * Get inactive time
	 *
	 * @return the time (unixtime)
	 */
	long getInactiveTime();

	/**
	 * Gets time of creating a guild
	 *
	 * @return the time (unixtime)
	 */
	long getTimeCreated();

	/**
	 * Gets the friendly pvp flag
	 *
	 * @return the flag boolean
	 */
	boolean getFriendlyPvp();

	/**
	 * Gets the amount of slots
	 *
	 * @return the amount
	 */
	int getSlots();

	/**
	 * Gets the default rank
	 *
	 * @return the rank
	 */
	NovaRank getDefaultRank();

	/**
	 * Gets a clone of a generic rank
	 *
	 * @param genericRank generic rank instance
	 * @return the rank
	 */
	NovaRank getCloneOfGenericRank(NovaRank genericRank);

	/**
	 * Sets the vault hologram
	 *
	 * @param hologram the hologram instance
	 */
	void setVaultHologram(Hologram hologram);

	/**
	 * Marks the guild as unchanged
	 */
	void setUnchanged();

	/**
	 * Marks the guild as changed
	 */
	void changed();

	/**
	 * Sets the name
	 *
	 * @param name the name string
	 */
	void setName(String name);

	/**
	 * Sets the tag
	 *
	 * @param tag the tag string
	 */
	void setTag(String tag);

	/**
	 * Sets the region
	 *
	 * @param region the region instance
	 */
	void setRegion(NovaRegion region);

	/**
	 * Sets the leader name
	 *
	 * @param name the name string
	 */
	void setLeaderName(String name);

	/**
	 * Sets vault's location
	 *
	 * @param location the Location instance
	 */
	void setVaultLocation(Location location);

	/**
	 * Sets the home location
	 *
	 * @param location the Location instance
	 */
	void setSpawnPoint(Location location);

	/**
	 * Sets the leader
	 *
	 * @param nPlayer the player
	 */
	void setLeader(NovaPlayer nPlayer);

	/**
	 * Sets the id
	 *
	 * @param id the ID
	 */
	void setId(int id);

	/**
	 * Sets the money
	 *
	 * @param money the amount
	 */
	void setMoney(double money);

	/**
	 * Sets the allies
	 *
	 * @param list the list
	 */
	void setAllies(List<NovaGuild> list);

	/**
	 * Sets the allies (names)
	 *
	 * @param list the list
	 */
	void setAlliesNames(List<String> list);

	/**
	 * Sets ally invitations (names)
	 *
	 * @param list the list
	 */
	void setAllyInvitationNames(List<String> list);

	/**
	 * Sets the wars
	 *
	 * @param list the list
	 */
	void setWars(List<NovaGuild> list);

	/**
	 * Sets the wars (names)
	 *
	 * @param list the list
	 */
	void setWarsNames(List<String> list);

	/**
	 * Sets no-war invitations
	 *
	 * @param list the list
	 */
	void setNoWarInvitations(List<String> list);

	/**
	 * Sets the amount of points
	 *
	 * @param points the amount
	 */
	void setPoints(int points);

	/**
	 * Set open-invitation flag
	 *
	 * @param openInvitation the flag boolean
	 */
	void setOpenInvitation(boolean openInvitation);

	/**
	 * Updates resting time
	 */
	void updateTimeRest();

	/**
	 * Updates lost live time
	 */
	void updateLostLive();

	/**
	 * Updates inactive time
	 */
	void updateInactiveTime();

	/**
	 * Sets lost live time
	 *
	 * @param time the time (unixtime)
	 */
	void setLostLiveTime(long time);

	/**
	 * Sets inactive time
	 *
	 * @param time the time (unixtime)
	 */
	void setInactiveTime(long time);

	/**
	 * Resets lost live time
	 */
	void resetLostLiveTime();

	/**
	 * Sets the resting time
	 *
	 * @param time the time (unixtime)
	 */
	void setTimeRest(long time);

	/**
	 * Sets the time of being created
	 *
	 * @param time the time (unixtime)
	 */
	void setTimeCreated(long time);

	/**
	 * Sets the amount of lives
	 *
	 * @param lives the amount
	 */
	void setLives(int lives);

	/**
	 * Sets friendly-pvp flag
	 *
	 * @param pvp the flag boolean
	 */
	void setFriendlyPvp(boolean pvp);

	/**
	 * Sets the amount of slots
	 *
	 * @param slots the amount
	 */
	void setSlots(int slots);

	/**
	 * Sets the ranks
	 *
	 * @param list the list
	 */
	void setRanks(List<NovaRank> list);

	/**
	 * Checks if the guild is ally with a guild
	 *
	 * @param guild 'ally' guild
	 * @return boolean
	 */
	boolean isAlly(NovaGuild guild);

	/**
	 * Checks if the guild is invited to an ally
	 *
	 * @param guild 'ally invited' guild
	 * @return boolean
	 */
	boolean isInvitedToAlly(NovaGuild guild);

	/**
	 * Checks if the guild is in war with a guild
	 *
	 * @param guild 'war' guild
	 * @return boolean
	 */
	boolean isWarWith(NovaGuild guild);

	/**
	 * Checks if the guild is invited to stop the war with a guild
	 *
	 * @param guild 'no-war invited' guild
	 * @return boolean
	 */
	boolean isNoWarInvited(NovaGuild guild);

	/**
	 * Checks if a player is the leader
	 *
	 * @param nPlayer the player
	 * @return boolean
	 */
	boolean isLeader(NovaPlayer nPlayer);

	/**
	 * Checks if the guild has a region
	 *
	 * @return boolean
	 */
	boolean hasRegion();

	/**
	 * Checks if the guild is being raid
	 *
	 * @return boolean
	 */
	boolean isRaid();

	/**
	 * Checks if the guild is marked as changed
	 *
	 * @return boolean
	 */
	boolean isChanged();

	/**
	 * Checks if the guild is full (slot limit)
	 *
	 * @return boolean
	 */
	boolean isFull();

	/**
	 * Checks if the guild has open-invitation flag
	 *
	 * @return the flag boolean
	 */
	boolean isOpenInvitation();

	/**
	 * Checks if a player is a member of the guild
	 *
	 * @param nPlayer the player
	 * @return boolean
	 */
	boolean isMember(NovaPlayer nPlayer);

	/**
	 * Checks if the guild has enough money
	 *
	 * @param money the amount
	 * @return boolean
	 */
	boolean hasMoney(double money);

	/**
	 * Adds an ally
	 *
	 * @param guild the guild
	 */
	void addAlly(NovaGuild guild);

	/**
	 * Adds an ally invitation
	 *
	 * @param guild the guild
	 */
	void addAllyInvitation(NovaGuild guild);

	/**
	 * Adds a war
	 *
	 * @param guild the guild
	 */
	void addWar(NovaGuild guild);

	/**
	 * Adds a no-war invitation
	 *
	 * @param guild the guild
	 */
	void addNoWarInvitation(NovaGuild guild);

	/**
	 * Adds a player
	 *
	 * @param nPlayer the player
	 */
	void addPlayer(NovaPlayer nPlayer);

	/**
	 * Adds money
	 *
	 * @param money the amount
	 */
	void addMoney(double money);

	/**
	 * Adds points
	 *
	 * @param points the amount
	 */
	void addPoints(int points);

	/**
	 * Adds a slot
	 */
	void addSlot();

	/**
	 * Adds a rank
	 *
	 * @param rank the rank
	 */
	void addRank(NovaRank rank);

	/**
	 * Adds a live
	 */
	void addLive();

	/**
	 * Removes a player
	 *
	 * @param nPlayer the player
	 */
	void removePlayer(NovaPlayer nPlayer);

	/**
	 * Removes an ally
	 *
	 * @param guild the guild
	 */
	void removeAlly(NovaGuild guild);

	/**
	 * Removes a war
	 *
	 * @param guild the guild
	 */
	void removeWar(NovaGuild guild);

	/**
	 * Removes a no-war invitation
	 *
	 * @param guild the guild
	 */
	void removeNoWarInvitation(NovaGuild guild);

	/**
	 * Removes a rank
	 *
	 * @param rank the rank
	 */
	void removeRank(NovaRank rank);

	/**
	 * Removes the raid
	 */
	void removeRaid();

	/**
	 * Takes money
	 *
	 * @param money the amount
	 */
	void takeMoney(double money);

	/**
	 * Takes points
	 *
	 * @param points the amount
	 */
	void takePoints(int points);

	/**
	 * Removes a live
	 */
	void takeLive();

	/**
	 * Removes an ally invitation
	 *
	 * @param guild the guild
	 */
	void removeAllyInvitation(NovaGuild guild);

	/**
	 * Creates a raid
	 * @param attacker the guild
	 *
	 */
	void createRaid(NovaGuild attacker);

	/**
	 * Destroys the guild
	 */
	void destroy();

	/**
	 * Shows the vault hologram to a player
	 *
	 * @param player the player
	 */
	void showVaultHologram(Player player);

	/**
	 * Hides the vault hologram from a player
	 *
	 * @param player the player
	 */
	void hideVaultHologram(Player player);

	/**
	 * Removes the raid bar from all members of the guild
	 */
	void removeRaidBar();
}
