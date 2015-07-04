package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.runnable.RunnableRaid;
import co.marcin.novaguilds.util.RegionUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RegionManager {
	private final NovaGuilds plugin;
	private final HashMap<String,NovaRegion> regions = new HashMap<>();
	
	public RegionManager(NovaGuilds pl) {
		plugin = pl;
	}

	//getters
	public NovaRegion getRegionByGuild(NovaGuild guild) {
		if(regions.containsKey(guild.getName().toLowerCase()))
			return regions.get(guild.getName().toLowerCase());
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
		return regions.values();
	}
	
	public void loadRegions() {
		plugin.mysqlReload();
    	
    	Statement statement;
		try {
			statement = plugin.c.createStatement();
			
			regions.clear();
			ResultSet res = statement.executeQuery("SELECT * FROM `"+plugin.getConfigManager().getDatabasePrefix()+"regions`");
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

					regions.put(res.getString("guild").toLowerCase(), novaRegion);
				}
				else {
					plugin.info("Failed loading region for guild "+res.getString("guild")+", world does not exist.");
				}
			}
		}
		catch (SQLException e) {
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
			
			String sql = "INSERT INTO `"+plugin.getConfigManager().getDatabasePrefix()+"regions` VALUES(0,'"+loc1+"','"+loc2+"','"+guild.getName()+"','"+guild.getSpawnPoint().getWorld().getName()+"');";
			statement.execute(sql);
			
			guild.setRegion(region);
			region.setGuildName(guild.getName());
			region.setUnChanged();
			regions.put(guild.getName().toLowerCase(), region);
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}
	
	public void saveRegion(NovaRegion region) {
		if(region != null) {
			if(region.isChanged()) {
				plugin.mysqlReload();
				Statement statement;
				try {
					statement = plugin.c.createStatement();

					String loc1 = StringUtils.parseDBLocationCoords2D(region.getCorner(0));
					String loc2 = StringUtils.parseDBLocationCoords2D(region.getCorner(1));

					String sql = "UPDATE `" + plugin.getConfigManager().getDatabasePrefix() + "regions` SET " +
							"`loc_1`='" + loc1 + "', " +
							"`loc_2`='" + loc2 + "', " +
							"`guild`='" + region.getGuildName() + "', " +
							"`world`='" + region.getWorld().getName() + "' " +
							"WHERE `id`=" + region.getId();
					statement.executeUpdate(sql);
					region.setUnChanged();
				}
				catch(SQLException e) {
					plugin.info(e.getMessage());
				}
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
		plugin.mysqlReload();

		try {
			Statement statement = plugin.c.createStatement();

			String sql = "DELETE FROM `" + plugin.getConfigManager().getDatabasePrefix() + "regions` WHERE `guild`='" + region.getGuildName() + "'";
			statement.executeUpdate(sql);

			regions.remove(region.getGuildName().toLowerCase());
		}
		catch(SQLException e) {
			plugin.info(e.getMessage());
		}
	}

	public void postCheckRegions() {
		Iterator<NovaRegion> iterator = getRegions().iterator();
		int i = 0;

		while(iterator.hasNext()) {
			NovaRegion region = iterator.next();
			boolean remove = false;

			if(region.getGuild() == null) {
				plugin.info("[RegionManager] ("+region.getGuildName()+") Guild is null");
				remove = true;
			}

			if(remove) {
				iterator.remove();
				i++;
			}
		}

		plugin.info("[RegionManager] PostCheck finished, unloaded "+i+" invalid regions");
	}
	
	public RegionValidity checkRegionSelect(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();
		
		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;
		plugin.debug(dif_x+","+dif_z);
		
		int minsize = plugin.getConfig().getInt("region.minsize");
		int maxsize = plugin.getConfig().getInt("region.maxsize");

		plugin.debug(minsize+","+maxsize);

		if(dif_x < minsize || dif_z < minsize) {
			return RegionValidity.TOOSMALL;
		}
		else if(dif_x > maxsize || dif_z > maxsize) {
			return RegionValidity.TOOBIG;
		}
		else if(!regionsInsideArea(l1,l2).isEmpty()) {
			return RegionValidity.OVERLAPS;
		}
		else if(!isFarEnough(l1,l2)) {
			return RegionValidity.TOOCLOSE;
		}
		else {
			return RegionValidity.VALID;
		}
	}
	
	public int checkRegionSize(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();
		
		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;
		
		return dif_x * dif_z;
	}

	public int checkRegionSize(NovaRegion region) {
		return checkRegionSize(region.getCorner(0),region.getCorner(1));
	}
	
	private List<NovaRegion> regionsInsideArea(Location l1, Location l2) {
		ArrayList<NovaRegion> list = new ArrayList<>();
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();
		
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
				list.add(region);
			}
		}
		
		return list;
	}

	public boolean canBuild(Player player, Location location) {
		NovaRegion region = getRegionAtLocation(location);

		if(region == null)
			return true;

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		if(nPlayer == null)
			return true;

		if(!nPlayer.hasGuild())
			return false;

		if(nPlayer.getBypass())
			return true;

		return region.getGuild().isMember(nPlayer);
	}

	public boolean isFarEnough(Location l1, Location l2) {
		int diagonal = 0;

		int min = diagonal + plugin.getConfig().getInt("region.mindistance");
		plugin.debug("min="+min);
		Location centerLocation = getCenterLocation(l1,l2);
		plugin.debug("center="+centerLocation.toString());

		for(NovaGuild guildLoop : plugin.getGuildManager().getGuilds()) {
			int diagonal2 = 0;
			plugin.debug("checking guild "+guildLoop.getName());

			if(guildLoop.hasRegion()) {
				diagonal2 = guildLoop.getRegion().getDiagonal();
				//spawnpointLocation = getCenterLocation(guildLoop.getRegion());
			}

			centerLocation.setY(guildLoop.getSpawnPoint().getY());
			//RegionUtils.setCorner(plugin.getServer().getPlayer("CTRL"),centerLocation,Material.WOOL);
			//RegionUtils.setCorner(plugin.getServer().getPlayer("CTRL"),spawnpointLocation,Material.GLOWSTONE);
			double distance = centerLocation.distance(guildLoop.getSpawnPoint());
			plugin.debug("distance="+distance);
			if(distance < min+diagonal2) {
				plugin.debug("too close "+guildLoop.getName());
				return false;
			}
		}
		return true;
	}

	//TODO fix
	public static Location getCenterLocation(Location l1, Location l2) {
		int width = Math.abs(l1.getBlockX()-l2.getBlockX());
		int height = Math.abs(l1.getBlockZ()-l2.getBlockZ());

		int newx = l1.getBlockX()<0 ? l1.getBlockX()+width/2 : l1.getBlockX()-width/2;
		int newz = l1.getBlockZ()>0 ? l1.getBlockZ()+height/2 : l1.getBlockZ()-height/2;

		return new Location(l1.getWorld(),newx,l1.getBlockY(),newz);
	}

	public static Location getCenterLocation(NovaRegion region) {
		return getCenterLocation(region.getCorner(0),region.getCorner(1));
	}

	public void playerEnteredRegion(Player player, Location toLocation) {
		NovaRegion region = getRegionAtLocation(toLocation);
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		//border particles
		if(plugin.getConfig().getBoolean("region.borderparticles")) {
			List<Block> blocks = RegionUtils.getBorderBlocks(region);
			for(Block block : blocks) {
				block.getLocation().setY(block.getLocation().getY() + 1);
				block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 100);
			}
		}

		//Chat message
		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",region.getGuildName());
		vars.put("PLAYERNAME",player.getName());
		plugin.getMessageManager().sendMessagesMsg(player, "chat.region.entered", vars);

		//Player is at region
		nPlayer.setAtRegion(region);

		//TODO add config
		if(nPlayer.hasGuild()) {
			if(!nPlayer.getGuild().getName().equalsIgnoreCase(region.getGuildName())) {
				NovaGuild guildDefender = plugin.getGuildManager().getGuildByRegion(region);

				if(nPlayer.getGuild().isWarWith(guildDefender)) {
					if(!guildDefender.isRaid()) {
						if(NovaGuilds.systemSeconds() - plugin.getConfigManager().getRaidTimeRest() > guildDefender.getTimeRest()) {
							guildDefender.createRaid(nPlayer.getGuild());
							plugin.guildRaids.add(guildDefender);
						}
						else {
							long timeWait = plugin.getConfigManager().getRaidTimeRest() - (NovaGuilds.systemSeconds() - guildDefender.getTimeRest());
							vars.put("TIMEREST", StringUtils.secondsToString(timeWait));

							plugin.getMessageManager().sendMessagesMsg(player, "chat.raid.resting", vars);
						}
					}

					if(guildDefender.isRaid()) {
						guildDefender.getRaid().addPlayerOccupying(nPlayer);
						Runnable task = new RunnableRaid(plugin);
						plugin.worker.schedule(task, 1, TimeUnit.SECONDS);
					}
				}

				//TODO: notify
				plugin.getMessageManager().broadcastGuild(plugin.getGuildManager().getGuildByRegion(region), "chat.region.notifyguild.entered", vars,true);
			}
		}
	}

	public void playerExitedRegion(Player player) {
		NovaRegion region = getRegionAtLocation(player.getLocation());
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);
		NovaGuild guild = region.getGuild();

		nPlayer.setAtRegion(null);
		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME", region.getGuildName());
		plugin.getMessageManager().sendMessagesMsg(player, "chat.region.exited", vars);

		if(nPlayer.hasGuild()) {
			if(nPlayer.getGuild().isWarWith(guild)) {
				if(guild.isRaid()) {
					guild.getRaid().removePlayerOccupying(nPlayer);

					if(guild.getRaid().getPlayersOccupyingCount() == 0) {
						guild.getRaid().resetProgress();
						plugin.resetWarBar(guild);
						plugin.resetWarBar(nPlayer.getGuild());
						plugin.debug("progress: " + guild.getRaid().getProgress());
						guild.getRaid().updateInactiveTime();
					}
				}
			}
		}
	}
}
