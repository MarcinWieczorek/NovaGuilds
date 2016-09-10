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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.api.util.Schematic;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface NovaGroup {
	enum Key {
		CREATE_MONEY(Type.DOUBLE),
		CREATE_ITEMS(Type.ITEMSTACKLIST),

		HOME_DELAY(Type.INTEGER),
		HOME_MONEY(Type.DOUBLE),
		HOME_ITEMS(Type.ITEMSTACKLIST),

		JOIN_MONEY(Type.DOUBLE),
		JOIN_ITEMS(Type.ITEMSTACKLIST),

		EFFECT_MONEY(Type.DOUBLE),
		EFFECT_ITEMS(Type.ITEMSTACKLIST),

		BUY_LIFE_MONEY(Type.DOUBLE),
		BUY_LIFE_ITEMS(Type.ITEMSTACKLIST),

		BUY_SLOT_MONEY(Type.DOUBLE),
		BUY_SLOT_ITEMS(Type.ITEMSTACKLIST),

		BUY_BANNER_MONEY(Type.DOUBLE),
		BUY_BANNER_ITEMS(Type.ITEMSTACKLIST),

		REGION_CREATE_MONEY(Type.DOUBLE),
		REGION_PRICEPERBLOCK(Type.DOUBLE),
		REGION_AUTOSIZE(Type.INTEGER);

		private final Type type;

		public enum Type {
			ITEMSTACKLIST,
			DOUBLE,
			INTEGER
		}

		/**
		 * The constructor
		 *
		 * @param type variable type
		 */
		Key(Type type) {
			this.type = type;
		}

		/**
		 * Gets variable type
		 *
		 * @return the type
		 */
		public Type getType() {
			return type;
		}
	}

	/**
	 * Get group's name
	 *
	 * @return name
	 */
	String getName();

	/**
	 * Gets the schematic that gets pasted
	 * when a player creates a guild
	 *
	 * @return the schematic
	 */
	Schematic getCreateSchematic();

	/**
	 * Gets a double
	 *
	 * @param key the key
	 * @return double value
	 */
	double getDouble(Key key);

	/**
	 * Gets an int
	 *
	 * @param key the key
	 * @return integer value
	 */
	int getInt(Key key);

	/**
	 * Gets an ItemStack list
	 *
	 * @param key the key
	 * @return ItemStack list
	 */
	List<ItemStack> getItemStackList(Key key);
}
