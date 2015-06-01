package co.marcin.novaguilds.manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RegionManager {
	private final NovaGuilds plugin;
	
	public RegionManager(NovaGuilds pl) {
		plugin = pl;
	}
	
	//getters
	public NovaRegion getRegionByGuild(NovaGuild guild) {
		if(plugin.regions.containsKey(guild.getName().toLowerCase()))
			return plugin.regions.get(guild.getName().toLowerCase());
		return null;
	}
	
	public NovaRegion getRegionAtLocation(Location l) {
		int x = l.getBlockX();
		int z = l.getBlockZ();
		
		for(NovaRegion r : getRegions()) {
			
			Location c1 = r.getCorner(0);
			Location c2 = r.getCorner(1);
			
			if((x >= c1.getBlockX() && x <= c2.getBlockX()) || (x <= c1.getBlockX() && x >= c2.getBlockX())) {
				if((z >= c1.getBlockZ() && z <= c2.getBlockZ()) || (z <= c1.getBlockZ() && z >= c2.getBlockZ())) {
					return r;
				}
			}
		}
		
		return null;
	}
	
	public Collection<NovaRegion> getRegions() {
		return plugin.regions.values();
	}
	
	public void loadRegions() {
		plugin.mysqlReload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			plugin.regions.clear();
			ResultSet res = statement.executeQuery("SELECT * FROM `"+plugin.sqlp+"regions`");
			while(res.next()) {
				World world = plugin.getServer().getWorld(res.getString("world"));

				if(world != null) {
					NovaRegion novaRegion = new NovaRegion();

					String loc1 = res.getString("loc_1");
					String[] loc1_split = loc1.split(";");

					String loc2 = res.getString("loc_2");
					String[] loc2_split = loc2.split(";");

					Location c1 = new Location(world, Integer.parseInt(loc1_split[0]), 0, Integer.parseInt(loc1_split[1]));
					Location c2 = new Location(world, Integer.parseInt(loc2_split[0]), 0, Integer.parseInt(loc2_split[1]));

					novaRegion.setCorner(0, c1);
					novaRegion.setCorner(1, c2);
					novaRegion.setWorld(world);
					novaRegion.setId(res.getInt("id"));

					novaRegion.setGuildName(res.getString("guild"));
					novaRegion.setUnChanged();

					plugin.regions.put(res.getString("guild").toLowerCase(), novaRegion);
				}
				else {
					plugin.info("Failed loading region for guild "+res.getString("guild")+", world does not exist.");
				}
			}
			plugin.info("Regions loaded from database");
		} catch (SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void addRegion(NovaRegion region, NovaGuild guild) {
		plugin.mysqlReload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String loc1 = StringUtils.parseDBLocationCoords2D(region.getCorner(0));
			String loc2 = StringUtils.parseDBLocationCoords2D(region.getCorner(1));
			
			if(guild == null) {
				plugin.info("addRegion w/o guild attempt");
				return;
			}

			if(region.getWorld() == null) {
				region.setWorld(plugin.getServer().getWorlds().get(0));
			}
			
			String sql = "INSERT INTO `"+plugin.sqlp+"regions` VALUES(0,'"+loc1+"','"+loc2+"','"+guild.getName()+"','"+guild.getSpawnPoint().getWorld().getName()+"');";
			statement.execute(sql);
			
			guild.setRegion(region);
			region.setGuildName(guild.getName());
			region.setUnChanged();
			plugin.regions.put(guild.getName().toLowerCase(), region);
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void saveRegion(NovaRegion region) {
		plugin.mysqlReload();

		if(region != null) {
			Statement statement;
			try {
				statement = plugin.c.createStatement();

				String loc1 = StringUtils.parseDBLocationCoords2D(region.getCorner(0));
				String loc2 = StringUtils.parseDBLocationCoords2D(region.getCorner(1));

				String sql = "UPDATE `" + plugin.sqlp + "regions` SET " +
						"`loc_1`='" + loc1 + "', " +
						"`loc_2`='" + loc2 + "', " +
						"`guild`='" + region.getGuildName() + "', " +
						"`world`='" + region.getWorld().getName() + "' " +
						"WHERE `id`=" + region.getId();
				statement.executeUpdate(sql);
			}
			catch(SQLException e) {
				plugin.info(e.getMessage());
			}
		}
	}
	
	public void saveAll() {
		for(NovaRegion r : getRegions()) {
			saveRegion(r);
		}
	}
	
	//delete region
	public void removeRegion(NovaRegion region) {
		if(region.isChanged()) {
			plugin.mysqlReload();

			try {
				Statement statement = plugin.c.createStatement();

				String sql = "DELETE FROM `" + plugin.sqlp + "regions` WHERE `guild`='" + region.getGuildName() + "'";
				statement.executeUpdate(sql);

				plugin.regions.remove(region.getGuildName().toLowerCase());
			}
			catch(SQLException e) {
				plugin.info(e.getMessage());
			}
		}
	}
	
	public void highlightRegion(Player player, NovaRegion region) {
		Location loc1 = region.getCorner(0);
		Location loc2 = region.getCorner(1);
		
		loc1.setY(player.getWorld().getHighestBlockAt(loc1.getBlockX(),loc1.getBlockZ()).getY()-1);
		loc2.setY(player.getWorld().getHighestBlockAt(loc2.getBlockX(),loc2.getBlockZ()).getY()-1);
		
		setCorner(player, loc1, Material.DIAMOND_BLOCK);
		setCorner(player, loc2, Material.DIAMOND_BLOCK);
	}
	
	public void resetHighlightRegion(Player player, NovaRegion region) {
		Location loc1 = region.getCorner(0);
		Location loc2 = region.getCorner(1);
		
		loc1.setY(player.getWorld().getHighestBlockAt(loc1.getBlockX(),loc1.getBlockZ()).getY()-1);
		loc2.setY(player.getWorld().getHighestBlockAt(loc2.getBlockX(),loc2.getBlockZ()).getY()-1);
		
		setCorner(player, loc1, loc1.getBlock().getType());
		setCorner(player, loc2, loc2.getBlock().getType());
	}
	
	@SuppressWarnings("deprecation")
	public void setCorner(Player player, Location location) {
		player.sendBlockChange(location, Material.EMERALD_BLOCK, (byte) 0);
	}
	
	//set corner with material
	@SuppressWarnings("deprecation")
	private void setCorner(Player player, Location location, Material material) {
		if(material == null) {
			material = player.getWorld().getBlockAt(location).getType();
		}

		player.sendBlockChange(location,material,(byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void resetCorner(Player player, Location location) {
		player.sendBlockChange(location,player.getWorld().getBlockAt(location).getType(),(byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void sendSquare(Player player, Location l1, Location l2,Material material,byte data) {
		Material material1 = null;
		Material material2 = null;
		
		Byte data1 = null;
		Byte data2 = null;
		
		if(material != null) {
			material1 = material2 = material;
			data1 = data2 = data;
		}
		
		int x;
		int z;
		
		int xs;
		int zs;
		
		int x1 = StringUtils.fixX(l1.getBlockX());
		int x2 = StringUtils.fixX(l2.getBlockX());
		int z1 = StringUtils.fixX(l1.getBlockZ());
		int z2 = StringUtils.fixX(l2.getBlockZ());
		
		int t;
		
		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;
		
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
		
		for(t=0;t<dif_x;t++) {
			x = xs + t;
			int highest1 = player.getWorld().getHighestBlockYAt(x,z1)-1;
			int highest2 = player.getWorld().getHighestBlockYAt(x,z2)-1;
			Location loc1 = player.getWorld().getBlockAt(x,highest1,z1).getLocation();
			Location loc2 = player.getWorld().getBlockAt(x,highest2,z2).getLocation();
			
			if(material == null) {
				material1 = player.getWorld().getBlockAt(loc1).getType();
				material2 = player.getWorld().getBlockAt(loc2).getType();
				
				data1 = player.getWorld().getBlockAt(loc1).getData();
				data2 = player.getWorld().getBlockAt(loc2).getData();
			}
			
			player.sendBlockChange(loc1,material1,data1);
			player.sendBlockChange(loc2,material2,data2);
		}

		
		for(t=0;t<dif_z;t++) {
			z = zs + t;
			int highest1 = player.getWorld().getHighestBlockYAt(x1,z)-1;
			int highest2 = player.getWorld().getHighestBlockYAt(x2,z)-1;
			Location loc1 = player.getWorld().getBlockAt(x1,highest1,z).getLocation();
			Location loc2 = player.getWorld().getBlockAt(x2,highest2,z).getLocation();
			
			if(material == null) {
				material1 = player.getWorld().getBlockAt(loc1).getType();
				material2 = player.getWorld().getBlockAt(loc2).getType();

				data1 = player.getWorld().getBlockAt(loc1).getData();
				data2 = player.getWorld().getBlockAt(loc2).getData();
			}
			
			player.sendBlockChange(loc1,material1,data1);
			player.sendBlockChange(loc2,material2,data2);
		}
	}
	
	public String checkRegionSelect(Location l1, Location l2) {
		int i;
		String[] returns = {
			"invalid", //0
			"valid", //1
			"toobig", //2
			"toosmall", //3
			"overlaps", //4
		};
		
		int x1 = StringUtils.fixX(l1.getBlockX());
		int x2 = StringUtils.fixX(l2.getBlockX());
		int z1 = StringUtils.fixX(l1.getBlockZ());
		int z2 = StringUtils.fixX(l2.getBlockZ());
		
		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;
		
		int minsize = plugin.getConfig().getInt("region.minsize");
		int maxsize = plugin.getConfig().getInt("region.maxsize");

		if(dif_x < minsize || dif_z < minsize) {
			i = 3;
		}
		else if(dif_x > maxsize || dif_z > maxsize) {
			i = 2;
		}
		else if(regionInsideArea(l1,l2) != null) {
			i = 4;
		}
		else {
			i = 1;
		}
		
		return returns[i];
	}
	
	public int checkRegionSize(Location l1, Location l2) {
		int x1 = StringUtils.fixX(l1.getBlockX());
		int x2 = StringUtils.fixX(l2.getBlockX());
		int z1 = StringUtils.fixX(l1.getBlockZ());
		int z2 = StringUtils.fixX(l2.getBlockZ());
		
		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;
		
		return dif_x * dif_z;
	}
	
	private NovaRegion regionInsideArea(Location l1, Location l2) {
		int x1 = StringUtils.fixX(l1.getBlockX());
		int x2 = StringUtils.fixX(l2.getBlockX());
		int z1 = StringUtils.fixX(l1.getBlockZ());
		int z2 = StringUtils.fixX(l2.getBlockZ());
		
		boolean i1;
		boolean i2;
		boolean i3;
		boolean i4;
		
		boolean ov1;
		boolean ov2;
		boolean overlaps;
		
		for(NovaRegion region: getRegions()) {
			Location c1 = region.getCorner(0);
			Location c2 = region.getCorner(1);
			
			//c1
			i1 = (c1.getBlockX() <= x1 && c1.getBlockX() >= x2) || (c1.getBlockX() >= x1 && c1.getBlockX() <= x2);
			i2 = (c1.getBlockZ() <= z1 && c1.getBlockZ() >= z2) || (c1.getBlockZ() >= z1 && c1.getBlockZ() <= z2);

			//c2
			i3 = (c2.getBlockX() <= x1 && c2.getBlockX() >= x2) || (c2.getBlockX() >= x1 && c2.getBlockX() <= x2);
			i4 = (c2.getBlockZ() <= z1 && c2.getBlockZ() >= z2) || (c2.getBlockZ() >= z1 && c2.getBlockZ() <= z2);
			
			ov1 = i1 && i2;
			ov2 = i3 && i4;
			
			overlaps = ov1 || ov2;
			
			if(overlaps) {
				return region;
			}
		}
		
		return null;
	}

	public boolean canBuild(Player player, Location location) {
		NovaRegion region = getRegionAtLocation(location);

		if(region == null)
			return true;

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(player.getName());

		if(nPlayer == null)
			return true;

		if(!nPlayer.hasGuild())
			return true;

		if(nPlayer.getBypass())
			return true;

		return nPlayer.getGuild().getName().equalsIgnoreCase(region.getGuildName());
	}

	@SuppressWarnings("deprecation")
	public List<Block> getBorderBlocks(NovaRegion region) {
		List<Block> blocks = new ArrayList<>();

		Location l1 = region.getCorner(0);
		Location l2 = region.getCorner(1);
		World world = region.getWorld();

		int x;
		int z;

		int xs;
		int zs;

		int x1 = StringUtils.fixX(l1.getBlockX());
		int x2 = StringUtils.fixX(l2.getBlockX());
		int z1 = StringUtils.fixX(l1.getBlockZ());
		int z2 = StringUtils.fixX(l2.getBlockZ());

		int t;

		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;

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

		for(t=0;t<dif_x;t++) {
			x = xs + t;
			int highest1 = world.getHighestBlockYAt(x, z1)-1;
			int highest2 = world.getHighestBlockYAt(x, z2)-1;

			blocks.add(world.getBlockAt(x, highest1, z1));
			blocks.add(world.getBlockAt(x,highest2,z2));
		}


		for(t=0;t<dif_z;t++) {
			z = zs + t;
			int highest1 = world.getHighestBlockYAt(x1, z)-1;
			int highest2 = world.getHighestBlockYAt(x2, z)-1;

			blocks.add(world.getBlockAt(x1, highest1, z));
			blocks.add(world.getBlockAt(x2,highest2,z));
		}

		return blocks;
	}
}
