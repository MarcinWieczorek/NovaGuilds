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
	/**
	 * Get group's name
	 *
	 * @return name
	 */
	String getName();

	/**
	 * Get teleport delay
	 *
	 * @return teleport delay in seconds
	 */
	int getGuildTeleportDelay();

	/**
	 * Get auto-region's size
	 * The size is one side's of a square length in blocks
	 *
	 * @return size
	 */
	int getRegionAutoSize();

	/**
	 * Get the amount of money required to create a guild
	 *
	 * @return money
	 */
	double getGuildCreateMoney();

	/**
	 * Get the price of one block of region's surface
	 *
	 * @return money
	 */
	double getRegionPricePerBlock();

	/**
	 * Get the price of creating a region (without surface cost)
	 *
	 * @return money
	 */
	double getRegionCreateMoney();

	/**
	 * Gets the schematic that gets pasted
	 * when a player creates a guild
	 *
	 * @return the schematic
	 */
	Schematic getCreateSchematic();

	/**
	 * Get the list of items required to create a guild
	 *
	 * @return list of items
	 */
	List<ItemStack> getGuildCreateItems();

	/**
	 * Get the price of teleporting to guild's home
	 *
	 * @return money
	 */
	double getGuildHomeMoney();

	/**
	 * Get the list of items required to teleport to guild's home
	 *
	 * @return list of items
	 */
	List<ItemStack> getGuildHomeItems();

	/**
	 * Get the price of joining a guild
	 *
	 * @return money
	 */
	double getGuildJoinMoney();

	/**
	 * Get the list of items required to join a guild
	 *
	 * @return list of items
	 */
	List<ItemStack> getGuildJoinItems();

	/**
	 * Get the price of buying a life
	 *
	 * @return money
	 */
	double getGuildBuylifeMoney();

	/**
	 * Get the list of items required to buy a life
	 *
	 * @return list of items
	 */
	List<ItemStack> getGuildBuylifeItems();

	/**
	 * Get the list of items required to buy a slot
	 *
	 * @return list of items
	 */
	List<ItemStack> getGuildBuySlotItems();

	/**
	 * Get the price of buying a slot
	 *
	 * @return money
	 */
	double getGuildBuySlotMoney();

	/**
	 * Get the list of items required to buy an effect
	 *
	 * @return list of items
	 */
	List<ItemStack> getGuildEffectItems();

	/**
	 * Get the price of buying an effect
	 *
	 * @return money
	 */
	double getGuildEffectPrice();

	/**
	 * Gets the list of items required to buy a banner
	 *
	 * @return list of items
	 */
	List<ItemStack> getGuildBuyBannerItems();

	/**
	 * Get the price of buying a banner
	 *
	 * @return money
	 */
	double getGuildBuyBannerMoney();
}
