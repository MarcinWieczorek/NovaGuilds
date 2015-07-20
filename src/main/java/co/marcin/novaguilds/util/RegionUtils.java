package co.marcin.novaguilds.util;

import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {
	@SuppressWarnings("deprecation")
	public static void setCorner(Player player, Location location, Material material, byte data) {
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

		loc1.setY(player.getWorld().getHighestBlockAt(loc1.getBlockX(), loc1.getBlockZ()).getY() - 1);
		loc2.setY(player.getWorld().getHighestBlockAt(loc2.getBlockX(), loc2.getBlockZ()).getY() - 1);

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

	//public static int distanceBetweenRegions(NovaRegion region1, NovaRegion region2) {
	public static int distanceBetweenRegions(NovaRegion region1, Location l1, Location l2) {
		int distance = 0;

		int[] x1 = new int[2];
		int[] x2 = new int[2];

		int[] z1 = new int[2];
		int[] z2 = new int[2];

		Location[] c1 = new Location[2];
		Location[] c2 = new Location[2];

		c1[0] = region1.getCorner(0);
		c1[1] = region1.getCorner(1);

//		c2[0] = region2.getCorner(0);
//		c2[1] = region2.getCorner(1);

		c2[0] = l1;
		c2[1] = l2;

		x1[0] = c1[0].getBlockX();
		x1[1] = c1[1].getBlockX();

		x2[0] = c2[0].getBlockX();
		x2[1] = c2[1].getBlockX();

		z1[0] = c1[0].getBlockZ();
		z1[1] = c1[1].getBlockZ();

		z2[0] = c2[0].getBlockZ();
		z2[1] = c2[1].getBlockZ();

		boolean xr1 = x1[0] < x2[0];
		boolean xr2 = x1[1] < x2[1];
		boolean xr3 = x2[0] < x1[1];

		//boolean x_left = (x2[0] < x1[0] && x1[0] < x2[1]);// && !x_between;
		boolean x_left = x2[0] < x1[0] && x2[1] < x1[1] && x1[0] < x2[1];
		//boolean x_right = x1[0] < x2[0] && x1[1] < x2[1] && x2[0] < x1[1];
		boolean x_right = xr1 && xr2 && xr3;
		boolean x_between = (x1[0] < x2[0] && x1[1] > x2[1]) && !(x_left || x_right);
		boolean x_over = x2[0] < x1[0] && x1[1] < x2[1];

		boolean x_inline = x_left || x_right || x_between || x_over;

		Bukkit.getLogger().info("-----------");
		Bukkit.getLogger().info("x_left=" + x_left);
		Bukkit.getLogger().info("x_right=" + x_right + "(" + xr1 + "|" + xr2 + "|" + xr3 + ")");
		Bukkit.getLogger().info("x_between=" + x_between);
		Bukkit.getLogger().info("x_over=" + x_over);

		Bukkit.getLogger().info("x_inline=" + x_inline);

		return distance;
	}

	public static int distanceBetweenRegions2(NovaRegion region1, Location l1, Location l2) {
		float millis = System.nanoTime();
		double distance = -1;

		int[] x1 = new int[2];
		int[] x2 = new int[2];

		int[] z1 = new int[2];
		int[] z2 = new int[2];

		Location[] c1 = new Location[2];
		Location[] c2 = new Location[2];

		c1[0] = region1.getCorner(0);
		c1[1] = region1.getCorner(1);

//		c2[0] = region2.getCorner(0);
//		c2[1] = region2.getCorner(1);

		c2[0] = l1;
		c2[1] = l2;

		x1[0] = c1[0].getBlockX();
		x1[1] = c1[1].getBlockX();

		x2[0] = c2[0].getBlockX();
		x2[1] = c2[1].getBlockX();

		z1[0] = c1[0].getBlockZ();
		z1[1] = c1[1].getBlockZ();

		z2[0] = c2[0].getBlockZ();
		z2[1] = c2[1].getBlockZ();

		boolean x_out_left = x2[0] < x1[0] && x2[1] < x1[0];
		boolean x_out_right = x1[1] < x2[0] && x1[1] < x2[1];

		boolean x_out = x_out_left || x_out_right;
		Bukkit.getLogger().info("-----");
		Bukkit.getLogger().info("x_out=" + x_out);

		boolean z_out_left = z2[0] < z1[0] && z2[1] < z1[0];
		boolean z_out_right = z1[1] < z2[0] && z1[1] < z2[1];

		boolean z_out = z_out_left || z_out_right;
		Bukkit.getLogger().info("z_out=" + z_out);

		boolean out = x_out && z_out;
		Bukkit.getLogger().info("out=" + out);

		Bukkit.getLogger().info("x1|0=" + x1[0]);
		Bukkit.getLogger().info("x1|1=" + x1[1]);
		Bukkit.getLogger().info("z1|0=" + z1[0]);
		Bukkit.getLogger().info("z1|1=" + z1[1]);
		Bukkit.getLogger().info("x2|0=" + x2[0]);
		Bukkit.getLogger().info("x2|1=" + x2[1]);
		Bukkit.getLogger().info("z2|0=" + z2[0]);
		Bukkit.getLogger().info("z2|1=" + z2[1]);

		World world = region1.getWorld();
		List<Location> corners1 = new ArrayList<>();
		List<Location> corners2 = new ArrayList<>();

		corners1.add(new Location(world, x1[0], 0, x1[0]));
		corners1.add(new Location(world, x1[0], 0, x1[1]));
		corners1.add(new Location(world, x1[1], 0, x1[1]));
		corners1.add(new Location(world, x1[1], 0, x1[0]));

		corners2.add(new Location(world, x2[0], 0, x2[0]));
		corners2.add(new Location(world, x2[0], 0, x2[1]));
		corners2.add(new Location(world, x2[1], 0, x2[1]));
		corners2.add(new Location(world, x2[1], 0, x2[0]));

		//corners distances
		if(out) {
			Bukkit.getLogger().info("rectangle1 corners=" + corners1.size());
			Bukkit.getLogger().info("rectangle2 corners=" + corners2.size());

			for(Location corner1 : corners1) {
				//setCorner(region1.getGuild().getPlayers().get(0).getPlayer(),corner1,Material.BRICK);

				for(Location corner2 : corners2) {
					//Bukkit.getLogger().info("2("+cx2+","+cz2+")");
					//setCorner(region1.getGuild().getPlayers().get(0).getPlayer(),corner2,Material.BRICK);

					double cacheDistance = corner2.distance(corner1);

					if(distance > cacheDistance || distance == -1) {
						Bukkit.getLogger().info("Changed distance. " + distance + " -> " + cacheDistance);
						distance = cacheDistance;
					}
				}
			}
		}
		else {
			Bukkit.getLogger().info("side by side!");

			boolean x_inside = x1[0] < x2[0] && x2[1] < x1[1];

			if(x_inside) {
				Bukkit.getLogger().info("x_inside");
				int dif = Math.abs(x1[0] - x2[0]);
				Bukkit.getLogger().info("dif=" + dif);
				distance = dif;

				dif = Math.abs(x1[1] - x2[1]);
				Bukkit.getLogger().info("dif=" + dif);
				if(distance > dif || distance == -1) {
					distance = dif;
				}
			}
			else {
				Bukkit.getLogger().info("z_inside");
				int dif = Math.abs(z1[0] - z2[0]);
				Bukkit.getLogger().info("dif=" + dif);
				distance = dif;

				dif = Math.abs(z1[1] - z2[1]);
				Bukkit.getLogger().info("dif=" + dif);
				if(distance > dif || distance == -1) {
					distance = dif;
				}
			}
		}

		Bukkit.getLogger().info("distance=" + Math.round(distance));
		Bukkit.getLogger().info("Time: " + ((System.nanoTime() - millis)) + "ns");
		return Integer.parseInt(Math.round(distance) + "");
	}

	public static int distanceBetweenRegionsSide(NovaRegion region1, Location l1, Location l2) {
		int distance;

		int[] x1 = new int[2];
		int[] x2 = new int[2];

		int[] z1 = new int[2];
		int[] z2 = new int[2];

		Location[] c1 = new Location[2];
		Location[] c2 = new Location[2];

		c1[0] = region1.getCorner(0);
		c1[1] = region1.getCorner(1);

//		c2[0] = region2.getCorner(0);
//		c2[1] = region2.getCorner(1);

		c2[0] = l1;
		c2[1] = l2;

		x1[0] = c1[0].getBlockX();
		x1[1] = c1[1].getBlockX();

		x2[0] = c2[0].getBlockX();
		x2[1] = c2[1].getBlockX();

		z1[0] = c1[0].getBlockZ();
		z1[1] = c1[1].getBlockZ();

		z2[0] = c2[0].getBlockZ();
		z2[1] = c2[1].getBlockZ();

		boolean x_inside = x1[0] < x2[0] && x2[1] < x1[1];

		if(x_inside) {
			int dif = Math.abs(x1[0] - x2[0]);
			Bukkit.getLogger().info("dif=" + dif);
			distance = dif;

			dif = Math.abs(x1[1] - x2[1]);
			Bukkit.getLogger().info("dif=" + dif);
			if(distance > dif || distance == -1) {
				distance = dif;
			}
		}
		else {
			int dif = Math.abs(z1[0] - z2[0]);
			Bukkit.getLogger().info("dif=" + dif);
			distance = dif;

			dif = Math.abs(z1[1] - z2[1]);
			Bukkit.getLogger().info("dif=" + dif);
			if(distance > dif || distance == -1) {
				distance = dif;
			}
		}

		return distance;
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

		for(Block block : getBorderBlocks(l1, l2)) {
			if(material == null) {
				material = player.getWorld().getBlockAt(block.getLocation()).getType();
				data = player.getWorld().getBlockAt(block.getLocation()).getData();
			}

			player.sendBlockChange(block.getLocation(), material, data);
		}
	}
}