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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.event.PlayerInteractEntityEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import java.util.Iterator;
import java.util.List;

public class RegionInteractListener implements Listener {
	private final NovaGuilds plugin;
	
	public RegionInteractListener(NovaGuilds pl) {
		plugin = pl;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getClickedBlock() != null) {
			List<String> denyinteract = Config.REGION_DENYINTERACT.getStringList();
			List<String> denyuse = Config.REGION_DENYUSE.getStringList();
			
			String clickedblockname = event.getClickedBlock().getType().name();
			String useditemname = event.getPlayer().getItemInHand().getType().name();
			
			NovaRegion rgatploc = plugin.getRegionManager().getRegion(event.getClickedBlock().getLocation());
			
			if(rgatploc != null) {
				NovaPlayer nPlayer = NovaPlayer.get(event.getPlayer());
				NovaGuild guild = rgatploc.getGuild();

				// (has no guild) OR (not his guild AND !(ally AND allyinteract) )
				if(!nPlayer.hasGuild() || (!guild.isMember(nPlayer) && !(guild.isAlly(nPlayer.getGuild()) && Config.REGION_ALLYINTERACT.getBoolean()))) {
					if(!nPlayer.getBypass()) {
						if(denyinteract.contains(clickedblockname) || denyuse.contains(useditemname)) {
							event.setCancelled(true);
							
							if(!clickedblockname.contains("_PLATE")) { //Supress for plates
								Message.CHAT_REGION_DENY_INTERACT.send(player);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) { //BREAKING
		Player player = event.getPlayer();
		if(!plugin.getRegionManager().canInteract(player, event.getBlock())) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		Player player = event.getPlayer();
		if(!plugin.getRegionManager().canInteract(player, event.getBlock())) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) { //Entity Damage
		List<String> denymobdamage = Config.REGION_DENYMOBDAMAGE.getStringList();

		if(denymobdamage.contains(event.getEntityType().name())) {
			boolean playerCaused = false;
			Player player = null;
			Arrow arrow = null;

			if(event.getCause() == DamageCause.PROJECTILE && event.getDamager() instanceof Arrow) {
				arrow = (Arrow) event.getDamager();

				if(arrow.getShooter() instanceof Player) {
					playerCaused = true;
					player = (Player)arrow.getShooter();
				}
			}

			if(event.getDamager() instanceof Player) {
				playerCaused = true;
				player = (Player)event.getDamager();
			}

			if(playerCaused && !plugin.getRegionManager().canInteract(player, event.getEntity())) {
				if(!(event.getEntity().getPassenger() instanceof Player)) {
					event.setCancelled(true);
					Message.CHAT_REGION_DENY_ATTACKMOB.send(player);

					//remove the arrow so it wont bug
					if(arrow != null) {
						arrow.remove();
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerClickEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity mob = event.getEntity();

		if(!plugin.getRegionManager().canInteract(player, mob)) {
			List<String> denyDamage = Config.REGION_DENYMOBDAMAGE.getStringList();
			List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();

			if(event.getAction() == EntityUseAction.ATTACK) {
				if(denyDamage.contains(mob.getType().name())) {
					if(!(mob instanceof LivingEntity)) {
						event.setCancelled(true);
						Message.CHAT_REGION_DENY_ATTACKMOB.send(player);
					}
				}
			}
			else {
				boolean sheep = mob.getType() == EntityType.SHEEP && player.getItemInHand().getType() == Material.SHEARS;

				if(denyRiding.contains(mob.getType().name()) || sheep) {
					event.setCancelled(true);
					Message.CHAT_REGION_DENY_RIDEMOB.send(player);
				}
			}
		}
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		Location loc = event.getLocation();
		NovaRegion rgatloc = plugin.getRegionManager().getRegion(loc);
		
		if(rgatloc != null) {
			Iterator<Block> iterator = event.blockList().iterator();
			while(iterator.hasNext()) {
				Block block = iterator.next();
				if(plugin.getGuildManager().isVaultBlock(block)) {
					if(!rgatloc.getGuild().isRaid()) {
						iterator.remove();
					}
				}
			}

			Message.CHAT_GUILD_EXPLOSIONATREGION.broadcast(rgatloc.getGuild());
		}
	}

	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
		List<String> denyriding = Config.REGION_DENYRIDING.getStringList();

		if(denyriding.contains(event.getEntityType().name())) {
			if(!plugin.getRegionManager().canInteract(event.getPlayer(), event.getEntity())) {
				event.setCancelled(true);
				Message.CHAT_REGION_DENY_UNLEASH.send(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());

		if(!plugin.getRegionManager().canInteract(event.getPlayer(), block)) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(event.getPlayer());
		}
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if(!Config.REGION_WATERFLOW.getBoolean()) {
			Material type = event.getBlock().getType();
			if(type==Material.WATER || type==Material.STATIONARY_WATER || type==Material.LAVA || type==Material.STATIONARY_LAVA) {
				if(plugin.getRegionManager().getRegion(event.getBlock().getLocation()) == null) {
					if(plugin.getRegionManager().getRegion(event.getToBlock().getLocation()) != null) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
