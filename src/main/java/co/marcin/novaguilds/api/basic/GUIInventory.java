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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface GUIInventory {
	/**
	 * Handles the event of a slot being clicked
	 *
	 * @param event inventory click event
	 */
	void onClick(InventoryClickEvent event);

	/**
	 * Gets the inventory
	 *
	 * @return the inventory
	 */
	Inventory getInventory();

	/**
	 * Opens a GUI
	 *
	 * @param nPlayer the player
	 */
	void open(NovaPlayer nPlayer);

	/**
	 * Generates the content
	 */
	void generateContent();

	/**
	 * Gets the viewer
	 *
	 * @return the viewer
	 */
	NovaPlayer getViewer();

	/**
	 * Sets the viewer
	 *
	 * @param nPlayer the viewer
	 */
	void setViewer(NovaPlayer nPlayer);

	/**
	 * Closes the GUI
	 */
	void close();
}
