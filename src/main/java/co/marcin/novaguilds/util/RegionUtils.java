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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.api.basic.NovaRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class RegionUtils {

	public static List<Block> getBorderBlocks(NovaRegion region) {
		return getBorderBlocks(region.getCorner(0), region.getCorner(1));
	}

	public static List<Block> getBorderBlocks(Location l1, Location l2) {
		List<Block> blocks = new ArrayList<>();

		//World world = region.getWorld();
		World world = l1.getWorld() == null ? Bukkit.getWorlds().get(0) : l1.getWorld();

		int x;
		int z;

		int xs;
		int zs;

		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();

		int t;

		int difX = Math.abs(x1 - x2) + 1;
		int difZ = Math.abs(z1 - z2) + 1;

		if(l1.getBlockX() < l2.getBlockX()) {
			xs = l1.getBlockX();
		}
		else {
			xs = l2.getBlockX();
		}

		if(l1.getBlockZ() < l2.getBlockZ()) {
			zs = l1.getBlockZ();
		}
		else {
			zs = l2.getBlockZ();
		}

		for(t = 0; t < difX; t++) {
			x = xs + t;

			Block highestBlock1 = world.getHighestBlockAt(x, z1);
			Block highestBlock2 = world.getHighestBlockAt(x, z2);
			int highest1 = highestBlock1.getY() - (highestBlock1.getType() == Material.SNOW ? 0 : 1);
			int highest2 = highestBlock2.getY() - (highestBlock2.getType() == Material.SNOW ? 0 : 1);

			blocks.add(world.getBlockAt(x, highest1, z1));
			blocks.add(world.getBlockAt(x, highest2, z2));
		}


		for(t = 0; t < difZ; t++) {
			z = zs + t;

			Block highestBlock1 = world.getHighestBlockAt(x1, z);
			Block highestBlock2 = world.getHighestBlockAt(x2, z);
			int highest1 = highestBlock1.getY() - (highestBlock1.getType() == Material.SNOW ? 0 : 1);
			int highest2 = highestBlock2.getY() - (highestBlock2.getType() == Material.SNOW ? 0 : 1);

			blocks.add(world.getBlockAt(x1, highest1, z));
			blocks.add(world.getBlockAt(x2, highest2, z));
		}

		return blocks;
	}

	@SuppressWarnings("deprecation")
	public static void resetBlock(Player player, Block block) {
		Material material = block.getWorld().getBlockAt(block.getLocation()).getType();
		byte data = block.getWorld().getBlockAt(block.getLocation()).getData();

		player.sendBlockChange(block.getLocation(), material, data);
	}

	public static Location getCenterLocation(Location l1, Location l2) {
		int width = Math.abs(l1.getBlockX() - l2.getBlockX());
		int height = Math.abs(l1.getBlockZ() - l2.getBlockZ());

		int newX = l1.getBlockX() + (l1.getBlockX() < l2.getBlockX() ? width : -width) / 2;
		int newZ = l1.getBlockZ() + (l1.getBlockZ() < l2.getBlockZ() ? height : -height) / 2;

		return new Location(l1.getWorld(), newX, l1.getBlockY(), newZ);
	}

	public static int checkRegionSize(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();

		int difX = Math.abs(x1 - x2) + 1;
		int difZ = Math.abs(z1 - z2) + 1;

		return difX * difZ;
	}

	public static Location sectionToLocation(ConfigurationSection section) {
		World world = Bukkit.getWorld(section.getString("world"));
		double x = section.getDouble("x");
		double y = section.getDouble("y");
		double z = section.getDouble("z");
		float yaw = (float) section.getDouble("yaw");
		float pitch = (float) section.getDouble("pitch");

		if(world == null) {
			return null;
		}

		return new Location(world, x, y, z, yaw, pitch);
	}
}