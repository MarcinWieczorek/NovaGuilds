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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGroup;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.runnable.RunnableTeleportRequest;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class GuildManager {
	private final NovaGuilds plugin;
	private final Map<String,NovaGuild> guilds = new HashMap<>();
	
	public GuildManager(NovaGuilds pl) {
		plugin = pl;
	}
	
	//getters
	public NovaGuild getGuildByName(String name) {
		return guilds.get(name.toLowerCase());
	}
	
	public NovaGuild getGuildByTag(String tag) {
		for(NovaGuild guild : getGuilds()) {
			if(StringUtils.removeColors(guild.getTag()).equalsIgnoreCase(tag)) {
				return guild;
			}
		}
		return null;
	}

	/*
	* Find by player/tag/guildname
	* @param: String mixed
	* */
	public NovaGuild getGuildFind(String mixed) {
		NovaGuild guild = getGuildByTag(mixed);

		if(guild == null) {
			guild = getGuildByName(mixed);
		}
		
		if(guild == null) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(mixed);
			
			if(nPlayer == null) {
				return null;
			}
			
			guild = nPlayer.getGuild();
		}

		return guild;
	}

	public Collection<NovaGuild> getGuilds() {
		return guilds.values();
	}
	
	public boolean exists(String guildname) {
		return guilds.containsKey(guildname.toLowerCase());
	}

	public List<NovaGuild> nameListToGuildsList(List<String> namesList) {
		List<NovaGuild> invitedToList = new ArrayList<>();

		for(String guildName : namesList) {
			NovaGuild guild = getGuildByName(guildName);
			if(guild != null) {
				invitedToList.add(guild);
			}
		}

		return invitedToList;
	}

	public void load() {
		guilds.clear();

		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			for(String guildName : plugin.getFlatDataManager().getGuildList()) {
				FileConfiguration guildData = plugin.getFlatDataManager().getGuildData(guildName);
				NovaGuild guild = guildFromFlat(guildData);

				if(guild != null) {
					guilds.put(guildName.toLowerCase(),guild);
				}
				else {
					LoggerUtils.info("Loaded guild is null. name: "+guildName);
				}
			}
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				PreparedStatement statement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_SELECT);
				ResultSet res = statement.executeQuery();
				//ResultSet res = statement.executeQuery("SELECT * FROM `" + plugin.getConfigManager().getDatabasePrefix() + "guilds`");
				while(res.next()) {
					String spawnpointCoords = res.getString("spawn");

					Location spawnpoint = null;
					if(!spawnpointCoords.isEmpty()) {
						String[] spawnpointSplit = spawnpointCoords.split(";");
						if(spawnpointSplit.length == 5) { //LENGTH
							String worldname = spawnpointSplit[0];

							if(plugin.getServer().getWorld(worldname) != null) {
								int x = Integer.parseInt(spawnpointSplit[1]);
								int y = Integer.parseInt(spawnpointSplit[2]);
								int z = Integer.parseInt(spawnpointSplit[3]);
								float yaw = Float.parseFloat(spawnpointSplit[4]);
								spawnpoint = new Location(plugin.getServer().getWorld(worldname), x, y, z);
								spawnpoint.setYaw(yaw);
							}
						}
					}

					String vaultLocationString = res.getString("bankloc");
					Location vaultLocation = null;
					if(!vaultLocationString.isEmpty()) {
						String[] vaultLocationSplit = vaultLocationString.split(";");
						if(vaultLocationSplit.length == 5) { //LENGTH
							String worldname = vaultLocationSplit[0];

							if(plugin.getServer().getWorld(worldname) != null) {
								int x = Integer.parseInt(vaultLocationSplit[1]);
								int y = Integer.parseInt(vaultLocationSplit[2]);
								int z = Integer.parseInt(vaultLocationSplit[3]);
								vaultLocation = new Location(plugin.getServer().getWorld(worldname), x, y, z);
							}
						}
					}

					//load guild only if there is a spawnpoint.
					//error protection if a world has been deleted
					if(spawnpoint != null) {
						List<String> allies = new ArrayList<>();
						List<String> alliesinv = new ArrayList<>();
						List<String> wars = new ArrayList<>();
						List<String> nowarinv = new ArrayList<>();

						if(!res.getString("allies").isEmpty()) {
							allies = StringUtils.semicolonToList(res.getString("allies"));
						}

						if(!res.getString("alliesinv").isEmpty()) {
							alliesinv = StringUtils.semicolonToList(res.getString("alliesinv"));
						}

						if(!res.getString("war").isEmpty()) {
							wars = StringUtils.semicolonToList(res.getString("war"));
						}

						if(!res.getString("nowarinv").isEmpty()) {
							nowarinv = StringUtils.semicolonToList(res.getString("nowarinv"));
						}

						NovaGuild novaGuild = new NovaGuild();
						novaGuild.setId(res.getInt("id"));
						novaGuild.setMoney(res.getDouble("money"));
						novaGuild.setPoints(res.getInt("points"));
						novaGuild.setName(res.getString("name"));
						novaGuild.setTag(res.getString("tag"));
						novaGuild.setLeaderName(res.getString("leader"));
						novaGuild.setLives(res.getInt("lives"));
						novaGuild.setTimeRest(res.getLong("timerest"));
						novaGuild.setLostLiveTime(res.getLong("lostlive"));
						novaGuild.setSpawnPoint(spawnpoint);
						novaGuild.setRegion(plugin.getRegionManager().getRegion(novaGuild));
						novaGuild.setVaultLocation(vaultLocation);
						novaGuild.setSlots(res.getInt("slots"));

						novaGuild.setAlliesNames(allies);
						novaGuild.setAllyInvitations(alliesinv);

						novaGuild.setWarsNames(wars);
						novaGuild.setNoWarInvitations(nowarinv);
						novaGuild.setInactiveTime(res.getLong("activity"));
						novaGuild.setTimeCreated(res.getLong("created"));
						novaGuild.setOpenInvitation(res.getBoolean("openinv"));

						//set unchanged
						novaGuild.setUnchanged();

						//Fix slots amount
						if(novaGuild.getSlots() <= 0) {
							novaGuild.setSlots(Config.GUILD_STARTSLOTS.getInt());
						}

						if(novaGuild.getId() > 0) {
							if(guilds.containsKey(res.getString("name").toLowerCase())) {
								if(Config.DELETEINVALID.getBoolean()) {
									delete(novaGuild, false);
								}

								LoggerUtils.error("Removed guild with doubled name ("+res.getString("name")+")");
								continue;
							}

							guilds.put(res.getString("name").toLowerCase(), novaGuild);
						}
						else {
							LoggerUtils.info("Failed to load guild " + res.getString("name") + ". Invalid ID");
						}
					}
					else {
						LoggerUtils.info("Failed loading guild " + res.getString("name") + ", world does not exist");
					}
				}
			}
			catch(SQLException e) {
				LoggerUtils.info("An error occured while loading guilds!");
				LoggerUtils.exception(e);
			}
		}

		LoggerUtils.info("Loaded "+guilds.size()+" guilds.");

		loadVaultHolograms();
		LoggerUtils.info("Generated bank holograms.");
	}
	
	public void add(NovaGuild guild) {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			plugin.getFlatDataManager().add(guild);
			guilds.put(guild.getName().toLowerCase(), guild);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				String spawnpointcoords = "";
				if(guild.getSpawnPoint() != null) {
					spawnpointcoords = StringUtils.parseDBLocation(guild.getSpawnPoint());
				}

				//adding to MySQL
				//id,tag,name,leader,home,allies,alliesinv,wars,nowarinv,money,points,lives,timerest,lostlive,bankloc
				PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_INSERT);
				preparedStatement.setString(1, guild.getTag()); //tag
				preparedStatement.setString(2, guild.getName()); //name
				preparedStatement.setString(3, guild.getLeader().getName()); //leader
				preparedStatement.setString(4, spawnpointcoords); //home
				preparedStatement.setDouble(5, guild.getMoney()); //money
				preparedStatement.setInt(6, guild.getPoints()); //points
				preparedStatement.setInt(7, guild.getLives()); //lives
				preparedStatement.setLong(8, guild.getTimeCreated()); //created
				preparedStatement.setInt(9, guild.getSlots()); //created

				preparedStatement.execute();
				ResultSet keys = preparedStatement.getGeneratedKeys();
				int id = 0;
				if(keys.next()) {
					id = keys.getInt(1);
				}

				if(id > 0) {
					guild.setId(id);
					guilds.put(guild.getName().toLowerCase(), guild);
					guild.setUnchanged();
				}
			}
			catch(SQLException e) {
				LoggerUtils.info("SQLException while adding a guild!");
				LoggerUtils.exception(e);
			}
		}
	}
	
	public void save(NovaGuild guild) {
		if(guild.isChanged()) {
			if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
				plugin.getFlatDataManager().save(guild);
			}
			else {
				if(!plugin.getDatabaseManager().isConnected()) {
					LoggerUtils.info("Connection is not estabilished, stopping current action");
					return;
				}

				plugin.getDatabaseManager().mysqlReload();

				try {
					PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_UPDATE);

					String spawnpointcoords = "";
					if(guild.getSpawnPoint() != null) {
						spawnpointcoords = StringUtils.parseDBLocation(guild.getSpawnPoint());
					}

					//ALLIES
					String allies = "";
					String alliesinv = "";

					if(!guild.getAllies().isEmpty()) {
						for(NovaGuild ally : guild.getAllies()) {
							if(!allies.equals("")) {
								allies += ";";
							}

							allies += ally.getName();
						}
					}

					if(!guild.getAllyInvitations().isEmpty()) {
						for(String allyinv : guild.getAllyInvitations()) {
							if(!alliesinv.equals("")) {
								alliesinv += ";";
							}

							alliesinv = alliesinv + allyinv;
						}
					}

					//WARS
					String wars = "";
					String nowar_inv = "";

					if(!guild.getWars().isEmpty()) {
						for(NovaGuild war : guild.getWars()) {
							if(!wars.equals("")) {
								wars += ";";
							}

							wars += war.getName();
						}
					}

					if(!guild.getNoWarInvitations().isEmpty()) {
						for(String nowarinv : guild.getNoWarInvitations()) {
							if(!nowar_inv.equals("")) {
								nowar_inv += ";";
							}

							nowar_inv = nowar_inv + nowarinv;
						}
					}

					String vaultLocationString = "";
					if(guild.getVaultLocation() != null) {
						vaultLocationString = StringUtils.parseDBLocation(guild.getVaultLocation());
					}

					preparedStatement.setString(1, guild.getTag());
					preparedStatement.setString(2, guild.getName());
					preparedStatement.setString(3, guild.getLeader().getName());
					preparedStatement.setString(4, spawnpointcoords);
					preparedStatement.setString(5, allies);
					preparedStatement.setString(6, alliesinv);
					preparedStatement.setString(7, wars);
					preparedStatement.setString(8, nowar_inv);
					preparedStatement.setDouble(9, guild.getMoney());
					preparedStatement.setInt(10, guild.getPoints());
					preparedStatement.setInt(11, guild.getLives());
					preparedStatement.setLong(12, guild.getTimeRest());
					preparedStatement.setLong(13, guild.getLostLiveTime());
					preparedStatement.setLong(14, guild.getInactiveTime());
					preparedStatement.setString(15, vaultLocationString);
					preparedStatement.setInt(16, guild.getSlots());
					preparedStatement.setBoolean(17, guild.isOpenInvitation());

					preparedStatement.setInt(18, guild.getId());

					preparedStatement.executeUpdate();
					guild.setUnchanged();
				}
				catch(SQLException e) {
					LoggerUtils.info("SQLException while saving a guild.");
					LoggerUtils.exception(e);
				}
			}
		}
	}
	
	public void save() {
		for(Entry<String, NovaGuild> g : guilds.entrySet()) {
			save(g.getValue());
		}
	}

	public void delete(NovaGuild guild) {
		delete(guild, true);
	}

	public void delete(NovaGuild guild, boolean removeFromMap) {
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			plugin.getFlatDataManager().delete(guild);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				//delete from database
				PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.GUILDS_DELETE);
				preparedStatement.setInt(1,guild.getId());
				preparedStatement.executeUpdate();
			}
			catch(SQLException e) {
				LoggerUtils.info("SQLException while deleting a guild.");
				LoggerUtils.exception(e);
			}
		}

		//remove region
		if(guild.hasRegion()) {
			plugin.getRegionManager().remove(guild.getRegion());
		}

		if(removeFromMap) {
			guilds.remove(guild.getName().toLowerCase());
		}

		guild.destroy();
	}
	
	public void changeName(NovaGuild guild, String newname) {
		guilds.remove(guild.getName());
		guilds.put(newname, guild);
		guild.setName(newname);
		save(guild);
	}

	public List<NovaRaid> getRaidsTakingPart(NovaGuild guild) {
		List<NovaRaid> list = new ArrayList<>();
		for(NovaGuild raidGuild : plugin.guildRaids) {
			if(raidGuild.getRaid().getGuildAttacker().equals(guild)) {
				list.add(raidGuild.getRaid());
			}
		}

		return list;
	}

	public void postCheck() {
		Iterator<NovaGuild> it = getGuilds().iterator();
		int i=0;
		while(it.hasNext()) {
			NovaGuild guild = it.next();
			boolean remove = false;
			if(guild != null) {
				if(guild.getLeaderName() != null) {
					LoggerUtils.info("("+guild.getName()+") Leader's name is set. Probably leader is null");
				}

				if(guild.getLeader() == null) {
					LoggerUtils.info("("+guild.getName()+") Leader is null");
					remove = true;
				}

				if(guild.getPlayers().isEmpty()) {
					LoggerUtils.info("("+guild.getName()+") 0 players");
					remove = true;
				}

				if(guild.getSpawnPoint()==null) {
					LoggerUtils.info("("+guild.getName()+") Spawnpoint is null");
					remove = true;
				}

				if(guild.getId() <= 0 && plugin.getConfigManager().getDataStorageType()!=DataStorageType.FLAT) {
					LoggerUtils.info("("+guild.getName()+") ID <= 0 !");
					remove = true;
				}
			}
			else {
				LoggerUtils.info("guild is null!");
				remove = true;
			}

			if(remove) {
				LoggerUtils.info("Unloaded guild " + (guild == null ? "null" : guild.getName()));
				if(Config.DELETEINVALID.getBoolean()) {
					delete(guild, false);
					LoggerUtils.info("DELETED guild " + (guild == null ? "null" : guild.getName()));
				}

				if(guild != null) {
					if(guild.hasRegion()) {
						guild.getRegion().setGuild(null);
					}
				}
				it.remove();
				i++;
			}
			else { //Add allies, wars etc
				//Allies
				List<NovaGuild> allies = new ArrayList<>();
				for(String allyName : guild.getAlliesNames()) {
					NovaGuild allyGuild = getGuildByName(allyName);

					if(allyGuild != null) {
						allies.add(allyGuild);
					}
				}
				guild.setAllies(allies);

				//Wars
				List<NovaGuild> wars = new ArrayList<>();
				for(String warName : guild.getWarsNames()) {
					NovaGuild warGuild = getGuildByName(warName);

					if(warGuild != null) {
						wars.add(warGuild);
					}
				}
				guild.setWars(wars);

				guild.setUnchanged();

				//No-war invitations
				//TODO

				//Ally invitations
				//TODO
			}
		}

		LoggerUtils.info("Postcheck finished. Found " + i + " invalid guilds");
	}

	public static void createHomeFloor(NovaGuild guild) {
		if(Config.GUILD_HOMEFLOOR_ENABLED.getBoolean()) {
			Location sp = guild.getSpawnPoint();
			Material material = Config.GUILD_HOMEFLOOR_MATERIAL.getMaterial();

			if(material != null) {
				sp.clone().add(1, -1, 0).getBlock().setType(material);
				sp.clone().add(0, -1, 0).getBlock().setType(material);
				sp.clone().add(1, -1, 1).getBlock().setType(material);
				sp.clone().add(0, -1, 1).getBlock().setType(material);
				sp.clone().add(-1, -1, -1).getBlock().setType(material);
				sp.clone().add(-1, -1, 0).getBlock().setType(material);
				sp.clone().add(0, -1, -1).getBlock().setType(material);
				sp.clone().add(1, -1, -1).getBlock().setType(material);
				sp.clone().add(-1, -1, 1).getBlock().setType(material);
			}
			else {
				LoggerUtils.error("Failed creating homefloor, invalid material.");
			}
		}
	}

	public List<NovaGuild> getTopGuildsByPoints(int count) {
		List<NovaGuild> guildsByPoints = new ArrayList<>(guilds.values());

		Collections.sort(guildsByPoints, new Comparator<NovaGuild>() {
			public int compare(NovaGuild o1, NovaGuild o2) {
				return o2.getPoints()- o1.getPoints();
			}
		});

		List<NovaGuild> guildsLimited = new ArrayList<>();

		int i=0;
		for(NovaGuild guild : guildsByPoints) {
			guildsLimited.add(guild);

			i++;
			if(i==count) {
				break;
			}
		}

		return guildsLimited;
	}

	public List<NovaGuild> getMostInactiveGuilds() {
		List<NovaGuild> guildsByInactive = new ArrayList<>(guilds.values());

		Collections.sort(guildsByInactive, new Comparator<NovaGuild>() {
			public int compare(NovaGuild o1, NovaGuild o2) {
				return (int) (NumberUtils.systemSeconds() - o2.getInactiveTime()) - (int) (NumberUtils.systemSeconds() - o1.getInactiveTime());
			}
		});

		return guildsByInactive;
	}

	private NovaGuild guildFromFlat(FileConfiguration guildData) {
		NovaGuild guild = null;

		if(guildData != null) {
			guild = new NovaGuild();
			guild.setId(guildData.getInt("id"));
			guild.setName(guildData.getString("name"));
			guild.setTag(guildData.getString("tag"));
			guild.setLeaderName(guildData.getString("leader"));

			guild.setAlliesNames(guildData.getStringList("allies"));
			guild.setWarsNames(guildData.getStringList("wars"));
			guild.setNoWarInvitations(guildData.getStringList("nowar"));
			guild.setAllyInvitations(guildData.getStringList("alliesinv"));

			guild.setMoney(guildData.getDouble("money"));
			guild.setPoints(guildData.getInt("points"));
			guild.setLives(guildData.getInt("lives"));
			guild.setSlots(guildData.getInt("slots"));

			guild.setTimeRest(guildData.getLong("timerest"));
			guild.setLostLiveTime(guildData.getLong("lostlive"));
			guild.setInactiveTime(guildData.getLong("activity"));
			guild.setTimeCreated(guildData.getLong("created"));
			guild.setOpenInvitation(guildData.getBoolean("openinv"));

			//home
			World homeWorld = plugin.getServer().getWorld(guildData.getString("home.world"));
			if(homeWorld != null) {
				int x = guildData.getInt("home.x");
				int y = guildData.getInt("home.y");
				int z = guildData.getInt("home.z");
				float yaw = (float) guildData.getDouble("home.yaw");
				Location spawnpoint = new Location(homeWorld, x, y, z);
				spawnpoint.setYaw(yaw);
				guild.setSpawnPoint(spawnpoint);
			}

			//bankloc
			if(guildData.isConfigurationSection("bankloc")) {
				World vaultWorld = plugin.getServer().getWorld(guildData.getString("bankloc.world"));
				if(vaultWorld != null) {
					int x = guildData.getInt("bankloc.x");
					int y = guildData.getInt("bankloc.y");
					int z = guildData.getInt("bankloc.z");
					Location vaultLocation = new Location(vaultWorld, x, y, z);
					guild.setVaultLocation(vaultLocation);
				}
			}

			//region
			guild.setRegion(plugin.getRegionManager().getRegion(guild));

			guild.setUnchanged();

			//Fix slots amount
			if(guild.getSlots() <= 0) {
				guild.setSlots(Config.GUILD_STARTSLOTS.getInt());
			}
		}

		return guild;
	}

	private void loadVaultHolograms() {
		for(NovaGuild guild : getGuilds()) {
			if(guild.getVaultLocation() != null) {
				appendVaultHologram(guild);
			}
		}
	}

	public boolean isVaultItemStack(ItemStack itemStack) {
		return itemStack.equals(Config.VAULT_ITEM.getItemStack());
	}

	public void appendVaultHologram(NovaGuild guild) {
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			if(Config.VAULT_HOLOGRAM_ENABLED.getBoolean()) {
				checkVaultDestroyed(guild);
				if(guild.getVaultLocation() != null) {
					if(guild.getVaultHologram() == null) {
						Location hologramLocation = guild.getVaultLocation().clone();
						hologramLocation.add(0.5, 2, 0.5);
						Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
						hologram.getVisibilityManager().setVisibleByDefault(false);
						for(String hologramLine : Config.VAULT_HOLOGRAM_LINES.getStringList()) {
							if(hologramLine.startsWith("[ITEM]")) {
								hologramLine = hologramLine.substring(6);
								ItemStack itemStack = ItemStackUtils.stringToItemStack(hologramLine);
								if(itemStack != null) {
									hologram.appendItemLine(itemStack);
								}
							}
							else {
								hologram.appendTextLine(StringUtils.fixColors(hologramLine));
							}
						}

						guild.setVaultHologram(hologram);

						for(Player player : guild.getOnlinePlayers()) {
							guild.showVaultHologram(player);
						}
					}
				}
			}
		}
	}

	public boolean isVaultBlock(Block block) {
		if(block.getType() == Config.VAULT_ITEM.getItemStack().getType()) {
			for(NovaGuild guild : getGuilds()) {
				checkVaultDestroyed(guild);
				if(guild.getVaultLocation() != null) {
					if(guild.getVaultLocation().distance(block.getLocation()) < 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void checkVaultDestroyed(NovaGuild guild) {
		if(guild.getVaultLocation() != null) {
			if(guild.getVaultLocation().getBlock().getType() != Material.CHEST) {
				guild.setVaultLocation(null);
				Hologram hologram = guild.getVaultHologram();

				if(hologram != null) {
					hologram.delete();
				}
				guild.setVaultHologram(null);
			}
		}
	}

	public void delayedTeleport(Player player, Location location, Message message) {
		Runnable task = new RunnableTeleportRequest(player,location, message);
		int delay = NovaGroup.get(player)==null ? 0 : NovaGroup.get(player).getGuildTeleportDelay();

		if(delay > 0) {
			Map<String, String> vars = new HashMap<>();
			vars.put("DELAY", plugin.getGroupManager().getGroup(player).getGuildTeleportDelay()+"");
			NovaGuilds.runTaskLater(task, delay, TimeUnit.SECONDS);
			Message.CHAT_DELAYEDTELEPORT.vars(vars).send(player);
		}
		else {
			task.run();
		}
	}

	public List<String> getTopGuilds() {
		int limit = Integer.parseInt(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_TOPROWS.get()); //TODO move to config
		int i=1;

		List<String> list = new ArrayList<>();
		Map<String, String> vars = new HashMap<>();

		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			vars.clear();
			vars.put("GUILDNAME", guild.getName());
			vars.put("N", String.valueOf(i));
			vars.put("POINTS", String.valueOf(guild.getPoints()));
			list.add(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.vars(vars).get());
			i++;
		}

		return list;
	}
}