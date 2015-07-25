package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.*;
import co.marcin.novaguilds.runnable.RunnableRaid;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.RegionUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RegionManager {
	private final NovaGuilds plugin;
	private final HashMap<String,NovaRegion> regions = new HashMap<>();
	
	public RegionManager(NovaGuilds pl) {
		plugin = pl;
	}

	//getters
	public NovaRegion getRegion(NovaGuild guild) {
		return regions.get(guild.getName().toLowerCase());
	}
	
	public NovaRegion getRegion(Location l) {
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
		regions.clear();

		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			for(String guildName : plugin.getFlatDataManager().getRegionList()) {
				FileConfiguration regionData = plugin.getFlatDataManager().getRegionData(guildName);
				NovaRegion region = regionFromFlat(regionData);

				if(region != null) {
					regions.put(guildName.toLowerCase(), region);
				}
				else {
					LoggerUtils.info("Loaded region is null. name: " + guildName);
				}
			}
		}
		else {
			plugin.getDatabaseManager().mysqlReload();

			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			try {
				PreparedStatement statement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.REGIONS_SELECT);

				ResultSet res = statement.executeQuery();
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
						LoggerUtils.info("Failed loading region for guild " + res.getString("guild") + ", world does not exist.");
					}
				}
			}
			catch(SQLException e) {
				LoggerUtils.exception(e);
			}
		}

		LoggerUtils.info("Loaded "+regions.size()+" regions.");
	}
	
	public void addRegion(NovaRegion region, NovaGuild guild) {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			plugin.getFlatDataManager().addRegion(region);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				String loc1 = StringUtils.parseDBLocationCoords2D(region.getCorner(0));
				String loc2 = StringUtils.parseDBLocationCoords2D(region.getCorner(1));

				if(guild == null) {
					LoggerUtils.info("addRegion w/o guild attempt");
					return;
				}

				if(region.getWorld() == null) {
					region.setWorld(plugin.getServer().getWorlds().get(0));
				}

				PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.REGIONS_INSERT);

				//"INSERT INTO `" + prefix + "regions` VALUES(0,'" + loc1 + "','" + loc2 + "','" + guild.getName() + "','" + guild.getSpawnPoint().getWorld().getName() + "');";

				preparedStatement.setString(1, loc1);
				preparedStatement.setString(2, loc2);
				preparedStatement.setString(3, guild.getName());
				preparedStatement.setString(4, region.getWorld().getName());
				preparedStatement.executeUpdate();

				guild.setRegion(region);
				region.setGuildName(guild.getName());
				region.setUnChanged();
				regions.put(guild.getName().toLowerCase(), region);
			}
			catch(SQLException e) {
				LoggerUtils.exception(e);
			}
		}
	}
	
	public void saveRegion(NovaRegion region) {
		if(region != null) {
			if(region.isChanged()) {
				if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
					plugin.getFlatDataManager().saveRegion(region);
				}
				else {
					if(!plugin.getDatabaseManager().isConnected()) {
						LoggerUtils.info("Connection is not estabilished, stopping current action");
						return;
					}

					plugin.getDatabaseManager().mysqlReload();
					try {
						PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.REGIONS_UPDATE);

						String loc1 = StringUtils.parseDBLocationCoords2D(region.getCorner(0));
						String loc2 = StringUtils.parseDBLocationCoords2D(region.getCorner(1));

						preparedStatement.setString(1, loc1);
						preparedStatement.setString(2, loc2);
						preparedStatement.setString(3, region.getGuild().getName());
						preparedStatement.setString(4, region.getWorld().getName());
						preparedStatement.setInt(5, region.getId());
						preparedStatement.executeUpdate();

						region.setUnChanged();
					}
					catch(SQLException e) {
						LoggerUtils.exception(e);
					}
				}
			}
		}
		else {
			LoggerUtils.info("null found while saving a region!");
		}
	}
	
	public void saveAll() {
		for(NovaRegion r : getRegions()) {
			saveRegion(r);
		}
	}
	
	//delete region
	public void removeRegion(NovaRegion region) {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			plugin.getFlatDataManager().deleteRegion(region);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.REGIONS_DELETE);
				preparedStatement.setString(1,region.getGuild().getName());
				preparedStatement.executeUpdate();

				regions.remove(region.getGuildName().toLowerCase());
			}
			catch(SQLException e) {
				LoggerUtils.info("An error occured while deleting a guild's region ("+region.getGuild().getName()+")");
				LoggerUtils.exception(e);
			}
		}
	}

	public void postCheckRegions() {
		Iterator<NovaRegion> iterator = getRegions().iterator();
		int i = 0;

		while(iterator.hasNext()) {
			NovaRegion region = iterator.next();
			boolean remove = false;

			if(region.getGuild() == null) {
				LoggerUtils.info("("+region.getGuildName() + ") Guild is null");
				remove = true;
			}

			if(remove) {
				iterator.remove();
				i++;
			}
		}

		LoggerUtils.info("PostCheck finished, unloaded " + i + " invalid regions");
	}
	
	public RegionValidity checkRegionSelect(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();
		
		int dif_x = Math.abs(x1 - x2) +1;
		int dif_z = Math.abs(z1 - z2) +1;
		LoggerUtils.debug(dif_x+","+dif_z);
		
		int minsize = plugin.getConfig().getInt("region.minsize");
		int maxsize = plugin.getConfig().getInt("region.maxsize");

		LoggerUtils.debug(minsize + "," + maxsize);

		if(dif_x < minsize || dif_z < minsize) {
			return RegionValidity.TOOSMALL;
		}
		else if(dif_x > maxsize || dif_z > maxsize) {
			return RegionValidity.TOOBIG;
		}
		else if(!getRegionsInsideArea(l1,l2).isEmpty()) {
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
	
	public List<NovaRegion> getRegionsInsideArea(Location l1, Location l2) {
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
		NovaRegion region = getRegion(location);
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);
		return region == null || nPlayer.getBypass() || (nPlayer.hasGuild() && region.getGuild().isMember(nPlayer));
	}

	private boolean isFarEnough(Location l1, Location l2) {
		int width = Math.abs(l1.getBlockX() - l2.getBlockX()) + 1;
		int height = Math.abs(l1.getBlockZ() - l2.getBlockZ()) + 1;
		int diagonal = Math.round((int)Math.sqrt((int)(Math.pow(width,2) + Math.pow(height,2))));
		LoggerUtils.debug(String.valueOf(width + " " + height + " " + diagonal));

		int min = diagonal + plugin.getConfig().getInt("region.mindistance");
		LoggerUtils.debug("min="+min);
		Location centerLocation = getCenterLocation(l1, l2);
		LoggerUtils.debug("center="+centerLocation.toString());

		for(NovaGuild guildLoop : plugin.getGuildManager().getGuilds()) {
			int diagonal2 = 0;
			LoggerUtils.debug("checking guild "+guildLoop.getName());

			if(guildLoop.hasRegion()) {
				diagonal2 = guildLoop.getRegion().getDiagonal();
				LoggerUtils.debug(String.valueOf(guildLoop.getRegion().getWidth()+" "+guildLoop.getRegion().getHeight()+" "+diagonal2));
			}

			centerLocation.setY(guildLoop.getSpawnPoint().getY());

			RegionUtils.setCorner(plugin.getServer().getPlayer("CTRL"), centerLocation, Material.WOOL);
			RegionUtils.setCorner(plugin.getServer().getPlayer("CTRL"), guildLoop.getSpawnPoint(), Material.GLOWSTONE);

			RegionUtils.setCorner(plugin.getServer().getPlayer("Kennar"), centerLocation, Material.WOOL);
			RegionUtils.setCorner(plugin.getServer().getPlayer("Kennar"), guildLoop.getSpawnPoint(), Material.GLOWSTONE);

			double distance = centerLocation.distance(guildLoop.getSpawnPoint());
			LoggerUtils.debug("distance="+distance);
			if(distance < min+diagonal2) {
				LoggerUtils.debug("too close "+guildLoop.getName());
				return false;
			}
		}
		return true;
	}

	//TODO fix
	private static Location getCenterLocation(Location l1, Location l2) {
		int width = Math.abs(l1.getBlockX() - l2.getBlockX());
		int height = Math.abs(l1.getBlockZ() - l2.getBlockZ());

		//int newx = l1.getBlockX()<0 ? l1.getBlockX()+width/2 : l1.getBlockX()-width/2;
		int newx = l1.getBlockX()+width/2;
		int newz = l1.getBlockZ()>0 ? l1.getBlockZ()+height/2 : l1.getBlockZ()-height/2;

		return new Location(l1.getWorld(),newx,l1.getBlockY(),newz);
	}

	public void playerEnteredRegion(Player player, Location toLocation) {
		NovaRegion region = getRegion(toLocation);
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
		vars.put("PLAYERNAME", player.getName());
		Message.CHAT_REGION_ENTERED.vars(vars).send(player);

		//Player is at region
		nPlayer.setAtRegion(region);

		//TODO add config
		if(!region.getGuild().isMember(nPlayer)) {
			if(nPlayer.hasGuild()) {
				NovaGuild guildDefender = region.getGuild();

				//RAIDS
				if(Config.RAID_ENABLED.getBoolean()) {
					//raid
					if(nPlayer.getGuild().isWarWith(guildDefender)) {
						if(!guildDefender.isRaid()) {
							if(NumberUtils.systemSeconds() - plugin.getConfigManager().getRaidTimeRest() > guildDefender.getTimeRest()) {
								if(guildDefender.getOnlinePlayers().size() >= Config.RAID_MINONLINE.getInt()) {
									if(NumberUtils.systemSeconds()-guildDefender.getTimeCreated() > Config.GUILD_CREATEPROTECTION.getSeconds()) {
										guildDefender.createRaid(nPlayer.getGuild());
										plugin.guildRaids.add(guildDefender);
									}
									else {
										Message.CHAT_RAID_PROTECTION.send(player);
									}
								}
							}
							else {
								long timeWait = plugin.getConfigManager().getRaidTimeRest() - (NumberUtils.systemSeconds() - guildDefender.getTimeRest());
								vars.put("TIMEREST", StringUtils.secondsToString(timeWait));

								Message.CHAT_RAID_RESTING.vars(vars).send(player);
							}
						}

						if(guildDefender.isRaid()) {
							nPlayer.setPartRaid(guildDefender.getRaid());
							guildDefender.getRaid().addPlayerOccupying(nPlayer);
							Runnable task = new RunnableRaid(plugin);
							plugin.worker.schedule(task, 1, TimeUnit.SECONDS);
						}
					}
				}

			}

			plugin.getMessageManager().broadcastGuild(region.getGuild(), "chat.region.notifyguild.entered", vars,true);
		}
	}

	public void playerExitedRegion(Player player) {
		NovaRegion region = getRegion(player.getLocation());
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		if(region == null) {
			return;
		}

		NovaGuild guild = region.getGuild();

		nPlayer.setAtRegion(null);
		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME", region.getGuildName());
		Message.CHAT_REGION_EXITED.vars(vars).send(player);

		if(nPlayer.hasGuild()) {
			if(nPlayer.getGuild().isWarWith(guild)) {
				if(guild.isRaid()) {
					guild.getRaid().removePlayerOccupying(nPlayer);

					if(guild.getRaid().getPlayersOccupyingCount() == 0) {
						guild.getRaid().resetProgress();
						plugin.resetWarBar(guild);
						plugin.resetWarBar(nPlayer.getGuild());
						LoggerUtils.debug("progress: " + guild.getRaid().getProgress());
						guild.getRaid().updateInactiveTime();
					}
				}
			}
		}
	}

	private NovaRegion regionFromFlat(FileConfiguration regionData) {
		NovaRegion region = null;

		if(regionData != null) {
			World world = plugin.getServer().getWorld(regionData.getString("world"));

			if(world != null) {
				region = new NovaRegion();
				region.setGuildName(regionData.getString("guild"));
				region.setWorld(world);

				Location c1 = new Location(world, regionData.getInt("corner1.x"), 0, regionData.getInt("corner1.z"));
				Location c2 = new Location(world, regionData.getInt("corner2.x"), 0, regionData.getInt("corner2.z"));

				region.setCorner(0, c1);
				region.setCorner(1, c2);
				region.setUnChanged();
			}
		}

		return region;
	}
}
