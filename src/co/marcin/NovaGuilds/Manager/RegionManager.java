package co.marcin.NovaGuilds.Manager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaRegion;
import co.marcin.NovaGuilds.Utils;

public class RegionManager {
	private NovaGuilds plugin;
	
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
		
		for(Entry<String, NovaRegion> e : getRegions()) {
			NovaRegion r = e.getValue();
			
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
	
	public Set<Entry<String, NovaRegion>> getRegions() {
		return plugin.regions.entrySet();
	}
	
	public void loadRegions() {
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			plugin.regions.clear();
			ResultSet res = statement.executeQuery("SELECT * FROM `"+plugin.sqlp+"regions`");
			while(res.next()) {
				NovaRegion novaRegion = new NovaRegion();
				
				String loc1 = res.getString("loc_1");
				String[] loc1_split = loc1.split(";");

				String loc2 = res.getString("loc_2");
				String[] loc2_split = loc2.split(";");
				
				novaRegion.setX1(Integer.parseInt(loc1_split[0]));
				novaRegion.setZ1(Integer.parseInt(loc1_split[1]));
				novaRegion.setX2(Integer.parseInt(loc2_split[0]));
				novaRegion.setZ2(Integer.parseInt(loc2_split[1]));
				
				World world = plugin.getServer().getWorld(res.getString("world"));
				Location c1 = new Location(world,Integer.parseInt(loc1_split[0]),0,Integer.parseInt(loc1_split[1]));
				Location c2 = new Location(world,Integer.parseInt(loc2_split[0]),0,Integer.parseInt(loc2_split[1]));
				
				novaRegion.setCorner(0,c1);
				novaRegion.setCorner(1,c2);
				novaRegion.setWorld(world);
				novaRegion.setId(res.getInt("id"));
				
				novaRegion.setGuildName(res.getString("guild"));
				
				plugin.regions.put(res.getString("guild").toLowerCase(), novaRegion);
			}
			plugin.info("Regions loaded from database");
		} catch (SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void addRegion(NovaRegion region, NovaGuild guild) {
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String loc1 = Utils.parseDBLocationCoords2D(region.getCorner(0));
			String loc2 = Utils.parseDBLocationCoords2D(region.getCorner(1));
			
			if(guild == null) {
				plugin.info("addRegion w/o guild attempt");
				return;
			}
			
			String sql = "INSERT INTO `"+plugin.sqlp+"regions` VALUES(0,'"+loc1+"','"+loc2+"','"+guild.getName()+"','"+guild.getSpawnPoint().getWorld().getName()+"');";
			statement.execute(sql);
			
			guild.setRegion(region);
			plugin.getGuildManager().saveGuildLocal(guild);
			region.setGuildName(guild.getName());
			plugin.regions.put(guild.getName().toLowerCase(),region);
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void saveRegion(NovaRegion region) {
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String loc1 = Utils.parseDBLocationCoords2D(region.getCorner(0));
			String loc2 = Utils.parseDBLocationCoords2D(region.getCorner(1));
			
			String sql = "UPDATE `"+plugin.sqlp+"regions` SET `loc_1`='"+loc1+"', `loc_2`='"+loc2+"', `guild`='"+region.getGuildName()+"', `world`='"+region.getWorld().getName()+"' WHERE `id`="+region.getId();
			statement.executeUpdate(sql);
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void saveAll() {
		for(Entry<String, NovaRegion> r : getRegions()) {
			saveRegion(r.getValue());
		}
	}
	
	//delete region
	public void removeRegion(NovaRegion region) {
		plugin.MySQLreload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			String sql = "DELETE FROM `"+plugin.sqlp+"regions` WHERE `guild`='"+region.getGuildName()+"'";
			statement.executeUpdate(sql);
			
			plugin.regions.remove(region.getGuildName().toLowerCase());
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
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
	public void setCorner(Player player, Location location, Material material) {
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
		
		int x1 = Utils.fixX(l1.getBlockX());
		int x2 = Utils.fixX(l2.getBlockX());
		int z1 = Utils.fixX(l1.getBlockZ());
		int z2 = Utils.fixX(l2.getBlockZ());
		
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
		int i = 0;
		String[] returns = {
			"invalid", //0
			"valid", //1
			"toobig", //2
			"toosmall", //3
			"overlaps", //4
		};
		
		int x1 = Utils.fixX(l1.getBlockX());
		int x2 = Utils.fixX(l2.getBlockX());
		int z1 = Utils.fixX(l1.getBlockZ());
		int z2 = Utils.fixX(l2.getBlockZ());
		
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
		int x1 = Utils.fixX(l1.getBlockX());
		int x2 = Utils.fixX(l2.getBlockX());
		int z1 = Utils.fixX(l1.getBlockZ());
		int z2 = Utils.fixX(l2.getBlockZ());
		
		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;
		
		return dif_x * dif_z;
	}
	
	public NovaRegion regionInsideArea(Location l1, Location l2) {
		int x1 = Utils.fixX(l1.getBlockX());
		int x2 = Utils.fixX(l2.getBlockX());
		int z1 = Utils.fixX(l1.getBlockZ());
		int z2 = Utils.fixX(l2.getBlockZ());
		
		boolean i1;
		boolean i2;
		boolean i3;
		boolean i4;
		
		boolean ov1;
		boolean ov2;
		boolean overlaps;
		
		for(Entry<String, NovaRegion> r: getRegions()) {
			Location c1 = r.getValue().getCorner(0);
			Location c2 = r.getValue().getCorner(1);
			
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
				return r.getValue();
			}
		}
		
		return null;
	}
}
