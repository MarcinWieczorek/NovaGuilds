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

package co.marcin.novaguilds.api.util;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.RegionValidity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public interface RegionSelection {
	enum Type {
		HIGHLIGHT,
		HIGHLIGHT_RESIZE,
		CREATE,
		RESIZE,
		NONE
	}

	/**
	 * Sends the selection
	 */
	void send();

	/**
	 * Removes the selection
	 */
	void reset();

	/**
	 * Removes the selection for one player
	 *
	 * @param nPlayer the player
	 */
	void reset(NovaPlayer nPlayer);

	/**
	 * Gets selection's corner
	 *
	 * @param index index (0/1)
	 * @return corner's location
	 */
	Location getCorner(Integer index);

	/**
	 * Gets selection type
	 *
	 * @return the type
	 */
	Type getType();

	/**
	 * Gets the list of blocks modified
	 *
	 * @return the list
	 */
	List<Block> getBlocks();

	/**
	 * Gets the player
	 * (The owner of the selection)
	 *
	 * @return the player
	 */
	NovaPlayer getPlayer();

	/**
	 * Gets the list of players
	 * (spectators and owner)
	 *
	 * @return the list
	 */
	List<NovaPlayer> getPlayerList();

	/**
	 * Gets region validy enum if present
	 *
	 * @return the validity enum
	 */
	RegionValidity getValidity();

	/**
	 * Chcecks if the region is valid
	 *
	 * @return boolean
	 */
	boolean isValid();

	/**
	 * Sets a corner
	 *
	 * @param index index (0/1)
	 * @param location the location
	 */
	void setCorner(Integer index, Location location);

	/**
	 * Sets region validity
	 *
	 * @param regionValidity the enum
	 */
	void setValidity(RegionValidity regionValidity);

	/**
	 * Adds a spectator
	 *
	 * @param nPlayer the player
	 */
	 void addSpectator(NovaPlayer nPlayer);

	/**
	 * Removes a spectator
	 *
	 * @param nPlayer the player
	 */
	void removeSpectator(NovaPlayer nPlayer);

	/**
	 * Gets border material
	 *
	 * @return the material
	 */
	Material getBorderMaterial();

	/**
	 * Gets border data
	 *
	 * @return the data byte
	 */
	byte getBorderData();

	/**
	 * Gets corner material
	 *
	 * @return the material
	 */
	Material getCornerMaterial();

	/**
	 * Gets corner data
	 *
	 * @return the data byte
	 */
	byte getCornerData();
}
