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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.basic.NovaRegion;
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
	@SuppressWarnings("deprecation")
	public static void setCorner(Player player, Location location, Material material, byte data) {
		if(player == null || location == null) {
			return;
		}

		if(material == null) {
			material = location.getBlock().getType();
			data = location.getBlock().getData();
		}

		player.sendBlockChange(location, material, data);
	}

	public static void setCorner(Player player, Location location, Material material) {
		setCorner(player, location, material, (byte) 0);
	}

	@SuppressWarnings("deprecation")
	public static void highlightRegion(Player player, NovaRegion region, Material material) {
		Location loc1 = region.getCorner(0).clone();
		Location loc2 = region.getCorner(1).clone();

		Block highest1 = player.getWorld().getHighestBlockAt(loc1.getBlockX(), loc1.getBlockZ());
		Block highest2 = player.getWorld().getHighestBlockAt(loc2.getBlockX(), loc2.getBlockZ());

		loc1.setY(highest1.getY() - (highest1.getType()==Material.SNOW ? 0 : 1));
		loc2.setY(highest2.getY() - (highest2.getType()==Material.SNOW ? 0 : 1));

		Material material1;
		Material material2;

		byte data1 = 0;
		byte data2 = 0;

		if(material != null) {
			material1 = material2 = material;
		}
		else {
			material1 = loc1.getBlock().getType();
			material2 = loc2.getBlock().getType();

			data1 = loc1.getBlock().getData();
			data2 = loc2.getBlock().getData();
		}

		setCorner(player, loc1, material1, data1);
		setCorner(player, loc2, material2, data2);
	}

	@SuppressWarnings("deprecation")
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

		int dif_x = Math.abs(x1 - x2) + 1;
		int dif_z = Math.abs(z1 - z2) + 1;

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

		for(t = 0; t < dif_x; t++) {
			x = xs + t;
			int highest1 = world.getHighestBlockYAt(x, z1) - 1;
			int highest2 = world.getHighestBlockYAt(x, z2) - 1;

			blocks.add(world.getBlockAt(x, highest1, z1));
			blocks.add(world.getBlockAt(x, highest2, z2));
		}


		for(t = 0; t < dif_z; t++) {
			z = zs + t;
			int highest1 = world.getHighestBlockYAt(x1, z) - 1;
			int highest2 = world.getHighestBlockYAt(x2, z) - 1;

			blocks.add(world.getBlockAt(x1, highest1, z));
			blocks.add(world.getBlockAt(x2, highest2, z));
		}

		return blocks;
	}

	@SuppressWarnings("deprecation")
	public static void sendSquare(Player player, Location l1, Location l2, Material material, byte data) {
		if(player == null || l1 == null || l2 == null) {
			return;
		}

		Material useMaterial;
		byte useData;

		for(Block block : getBorderBlocks(l1, l2)) {
			if(material == null) {
				useMaterial = player.getWorld().getBlockAt(block.getLocation()).getType();
				useData = player.getWorld().getBlockAt(block.getLocation()).getData();
			}
			else {
				useMaterial = material;
				useData = data;
			}

			player.sendBlockChange(block.getLocation(), useMaterial, useData);
		}
	}

	//TODO fix
	public static Location getCenterLocation(Location l1, Location l2) {
		int width = Math.abs(l1.getBlockX() - l2.getBlockX());
		int height = Math.abs(l1.getBlockZ() - l2.getBlockZ());

		//int newx = l1.getBlockX()<0 ? l1.getBlockX()+width/2 : l1.getBlockX()-width/2;
		int newx = l1.getBlockX()+width/2;
		int newz = l1.getBlockZ()>0 ? l1.getBlockZ()+height/2 : l1.getBlockZ()-height/2;

		return new Location(l1.getWorld(),newx,l1.getBlockY(),newz);
	}

	public static int checkRegionSize(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();

		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;

		return dif_x * dif_z;
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