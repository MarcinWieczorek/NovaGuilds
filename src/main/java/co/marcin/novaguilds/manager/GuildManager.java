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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.runnable.RunnableTeleportRequest;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class GuildManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<String, NovaGuild> guilds = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	//getters
	public static NovaGuild getGuildByName(String name) {
		return plugin.getGuildManager().guilds.get(name);
	}
	
	public static NovaGuild getGuildByTag(String tag) {
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			if(StringUtils.removeColors(guild.getTag()).equalsIgnoreCase(tag)) {
				return guild;
			}
		}
		return null;
	}

	/**
	 * Find by player/tag/guildname
	 *
	 * @param mixed mixed string
	 * @return guild instance
	 */
	public static NovaGuild getGuildFind(String mixed) {
		NovaGuild guild = getGuildByTag(mixed);

		if(guild == null) {
			guild = getGuildByName(mixed);
		}
		
		if(guild == null) {
			NovaPlayer nPlayer = PlayerManager.getPlayer(mixed);
			
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
	
	public boolean exists(String guildName) {
		return guilds.containsKey(guildName);
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
		for(NovaGuild guild : getResourceManager().load()) {
			if(guilds.containsKey(guild.getName())) {
				if(Config.DELETEINVALID.getBoolean()) {
					getResourceManager().remove(guild);
				}

				LoggerUtils.error("Removed guild with doubled name (" + guild.getName() + ")");
				continue;
			}

			guilds.put(guild.getName(), guild);
		}

		LoggerUtils.info("Loaded " + guilds.size() + " guilds.");

		loadVaultHolograms();
		LoggerUtils.info("Generated bank holograms.");
	}
	
	public void add(NovaGuild guild) {
		guilds.put(guild.getName(), guild);
	}
	
	public void save(NovaGuild guild) {
		getResourceManager().save(guild);
	}

	public void save() {
		long startTime = System.nanoTime();

		int count = getResourceManager().save(getGuilds());

		LoggerUtils.info("Guilds data saved in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " guilds)");
	}

	public void delete(NovaGuild guild) {
		getResourceManager().remove(guild);

		//remove region
		if(guild.hasRegion()) {
			plugin.getRegionManager().remove(guild.getRegion());
		}

		guilds.remove(guild.getName());
		guild.destroy();
	}
	
	public void changeName(NovaGuild guild, String newName) {
		guilds.remove(guild.getName());
		guilds.put(newName, guild);
		guild.setName(newName);
	}

	public List<NovaRaid> getRaidsTakingPart(NovaGuild guild) {
		List<NovaRaid> list = new ArrayList<>();
		for(NovaGuild raidGuild : getGuilds()) {
			if(raidGuild.isRaid() && raidGuild.getRaid().getGuildAttacker().equals(guild)) {
				list.add(raidGuild.getRaid());
			}
		}

		return list;
	}

	public void postCheck() {
		int i = 0;
		for(NovaGuild guild : new ArrayList<>(getGuilds())) {
			boolean remove = false;
			if(guild != null) {
				if(((NovaGuildImpl) guild).getLeaderName() != null) {
					LoggerUtils.info("(" + guild.getName() + ") Leader's name is set. Probably leader is null");
				}

				if(guild.getLeader() == null) {
					LoggerUtils.info("(" + guild.getName() + ") Leader is null");
					remove = true;
				}

				if(guild.getPlayers().isEmpty()) {
					LoggerUtils.info("(" + guild.getName() + ") 0 players");
					remove = true;
				}

				if(guild.getHome() == null) {
					LoggerUtils.info("(" + guild.getName() + ") Spawnpoint is null");
					remove = true;
				}

				if(guild.getId() <= 0 && plugin.getConfigManager().getDataStorageType() != DataStorageType.FLAT) {
					LoggerUtils.info("(" + guild.getName() + ") ID <= 0 !");
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
					delete(guild);
					LoggerUtils.info("DELETED guild " + (guild == null ? "null" : guild.getName()));
				}
				else if(guild != null) {
					guilds.remove(guild.getName());
					guild.destroy();
				}

				i++;
			}
			else { //Add allies, wars etc
				guild.postSetUp();
			}
		}

		LoggerUtils.info("Postcheck finished. Found " + i + " invalid guilds");
	}

	public List<NovaGuild> getTopGuildsByPoints(int count) {
		List<NovaGuild> guildsByPoints = new ArrayList<>(guilds.values());

		Collections.sort(guildsByPoints, new Comparator<NovaGuild>() {
			public int compare(NovaGuild o1, NovaGuild o2) {
				return o2.getPoints() - o1.getPoints();
			}
		});

		List<NovaGuild> guildsLimited = new ArrayList<>();

		int i = 0;
		for(NovaGuild guild : guildsByPoints) {
			guildsLimited.add(guild);

			i++;
			if(i == count) {
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

	private void loadVaultHolograms() {
		for(NovaGuild guild : getGuilds()) {
			if(guild.getVaultLocation() != null) {
				appendVaultHologram(guild);
			}
		}
	}

	public boolean isVaultItemStack(ItemStack itemStack) {
		return ItemStackUtils.isSimilar(itemStack, Config.VAULT_ITEM.getItemStack());
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
					if(guild.getVaultLocation().getWorld().equals(block.getWorld())) {
						if(guild.getVaultLocation().distance(block.getLocation()) < 1) {
							return true;
						}
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
		Runnable task = new RunnableTeleportRequest(player, location, message);
		int delay = GroupManager.getGroup(player) == null ? 0 : GroupManager.getGroup(player).getGuildTeleportDelay();

		if(delay > 0) {
			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.DELAY, String.valueOf(GroupManager.getGroup(player).getGuildTeleportDelay()));
			NovaGuilds.runTaskLater(task, delay, TimeUnit.SECONDS);
			Message.CHAT_DELAYEDTELEPORT.vars(vars).send(player);
		}
		else {
			task.run();
		}
	}

	public List<String> getTopGuilds() {
		int limit = Integer.parseInt(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_TOPROWS.get()); //TODO move to config
		int i = 1;

		List<String> list = new ArrayList<>();
		Map<VarKey, String> vars = new HashMap<>();

		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			vars.clear();
			vars.put(VarKey.GUILDNAME, guild.getName());
			vars.put(VarKey.N, String.valueOf(i));
			vars.put(VarKey.POINTS, String.valueOf(guild.getPoints()));
			list.add(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.vars(vars).get());
			i++;
		}

		return list;
	}

	public void cleanInactiveGuilds() {
		int count = 0;

		for(NovaGuild guild : plugin.getGuildManager().getMostInactiveGuilds()) {
			if(NumberUtils.systemSeconds() - guild.getInactiveTime() < Config.CLEANUP_INACTIVETIME.getSeconds()) {
				break;
			}

			//fire event
			GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.INACTIVE);
			plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

			if(!guildAbandonEvent.isCancelled()) {
				Map<VarKey, String> vars = new HashMap<>();
				vars.put(VarKey.GUILDNAME, guild.getName());
				Message.BROADCAST_ADMIN_GUILD_CLEANUP.vars(vars).broadcast();
				LoggerUtils.info("Abandoned guild " + guild.getName() + " due to inactivity.");
				count++;

				plugin.getGuildManager().delete(guild);
			}
		}

		LoggerUtils.info("Guilds cleanup finished, removed " + count + " guilds.");
	}

	private ResourceManager<NovaGuild> getResourceManager() {
		return plugin.getStorage().getResourceManager(NovaGuild.class);
	}
}
