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
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.event.PlayerInteractEntityEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.ArrayList;
import java.util.List;

public class RegionInteractListener implements Listener {
	private final NovaGuilds plugin;
	
	public RegionInteractListener(NovaGuilds pl) {
		plugin = pl;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getClickedBlock() == null) {
			return;
		}

		List<String> denyInteract = Config.REGION_DENYINTERACT.getStringList();
		List<String> denyUse = Config.REGION_DENYUSE.getStringList();

		String clickedBlockName = event.getClickedBlock().getType().name();
		String usedItemName = event.getPlayer().getItemInHand().getType().name();

		NovaRegion region = plugin.getRegionManager().getRegion(event.getClickedBlock().getLocation());

		if(region == null) {
			return;
		}

		NovaPlayer nPlayer = NovaPlayer.get(event.getPlayer());
		NovaGuild guild = region.getGuild();

		if(nPlayer.getBypass()) {
			return;
		}

		if(!denyInteract.contains(clickedBlockName) && !denyUse.contains(usedItemName)) {
			return;
		}

		if(nPlayer.hasGuild()) {
			if(guild.isMember(nPlayer)) {
				if(nPlayer.hasPermission(GuildPermission.INTERACT)) {
					if(event.getAction() != Action.RIGHT_CLICK_BLOCK || (!plugin.getGuildManager().isVaultBlock(event.getClickedBlock()) || nPlayer.hasPermission(GuildPermission.VAULT_ACCESS))) {
						return;
					}
				}
			}
			else if(guild.isAlly(nPlayer.getGuild()) && Config.REGION_ALLYINTERACT.getBoolean()) {
				return;
			}
		}


		event.setCancelled(true);

		if(clickedBlockName.contains("_PLATE")) { //Supress for plates
			return;
		}

		Message.CHAT_REGION_DENY_INTERACT.send(player);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) { //BREAKING
		Player player = event.getPlayer();
		NovaPlayer nPlayer = NovaPlayer.get(player);

		if(NovaRegion.get(event.getBlock()) != null && (!plugin.getRegionManager().canInteract(player, event.getBlock()) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_BREAK)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		Player player = event.getPlayer();
		NovaPlayer nPlayer = NovaPlayer.get(player);

		if(NovaRegion.get(event.getBlock()) != null && (!plugin.getRegionManager().canInteract(player, event.getBlock()) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_PLACE)))) {
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
					player = (Player) arrow.getShooter();
				}
			}

			if(event.getDamager() instanceof Player) {
				playerCaused = true;
				player = (Player) event.getDamager();
			}

			if(!playerCaused) {
				return;
			}

			NovaPlayer nPlayer = NovaPlayer.get(player);

			if(NovaRegion.get(event.getEntity()) != null && (!plugin.getRegionManager().canInteract(player, event.getEntity()) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_ATTACK)))) {
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
		NovaPlayer nPlayer = NovaPlayer.get(player);
		Entity entity = event.getEntity();
		List<String> denyDamage = Config.REGION_DENYMOBDAMAGE.getStringList();

		if(event.getAction() == EntityUseAction.ATTACK) {
			if(NovaRegion.get(entity) != null && (!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_ATTACK)))) {
				if(denyDamage.contains(entity.getType().name())) {
					if(!(entity instanceof LivingEntity)) {
						event.setCancelled(true);
						Message.CHAT_REGION_DENY_ATTACKMOB.send(player);
					}
				}
			}
		}
		else {
			if(NovaRegion.get(entity) != null && (!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_RIDE)))) {
				if(entity.getType() == EntityType.SHEEP && player.getItemInHand().getType() == Material.SHEARS) {
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
			for(Block block : new ArrayList<>(event.blockList())) {
				if(plugin.getGuildManager().isVaultBlock(block)) {
					if(!rgatloc.getGuild().isRaid()) {
						event.blockList().remove(block);
					}
				}
			}

			Message.CHAT_GUILD_EXPLOSIONATREGION.broadcast(rgatloc.getGuild());
		}
	}

	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
		List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();
		Player player = event.getPlayer();
		Entity entity = event.getEntity();
		NovaPlayer nPlayer = NovaPlayer.get(player);

		if(denyRiding.contains(entity.getType().name())) {
			if(NovaRegion.get(entity) != null && (!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_LEASH)))) {
				if(!(entity instanceof Vehicle) || !NovaPlayer.get(player).isVehicleListed((Vehicle) event.getEntity())) {
					event.setCancelled(true);
					Message.CHAT_REGION_DENY_UNLEASH.send(player);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
		List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();
		Player player = event.getPlayer();
		Entity entity = event.getEntity();
		NovaPlayer nPlayer = NovaPlayer.get(player);

		if(denyRiding.contains(entity.getType().name())) {
			if(NovaRegion.get(entity) != null && (!plugin.getRegionManager().canInteract(player, entity) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_LEASH)))) {
				if(!(entity instanceof Vehicle) || !NovaPlayer.get(player).isVehicleListed((Vehicle) event.getEntity())) {
					event.setCancelled(true);
					Message.CHAT_REGION_DENY_LEASH.send(event.getPlayer());
				}
			}
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		Player player = event.getPlayer();
		NovaPlayer nPlayer = NovaPlayer.get(player);

		if(NovaRegion.get(block) != null && (!plugin.getRegionManager().canInteract(player, block) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_PLACE)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(event.getPlayer());
		}
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if(!Config.REGION_WATERFLOW.getBoolean()) {
			Material type = event.getBlock().getType();
			if(type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA) {
				if(plugin.getRegionManager().getRegion(event.getBlock().getLocation()) == null) {
					if(plugin.getRegionManager().getRegion(event.getToBlock().getLocation()) != null) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		Vehicle vehicle = event.getVehicle();

		if(!(event.getEntered() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntered();
		NovaPlayer nPlayer = NovaPlayer.get(player);

		List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();

		if(denyRiding.contains(vehicle.getType().name())) {
			if(NovaRegion.get(vehicle) != null && (!plugin.getRegionManager().canInteract(player, vehicle) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.MOB_RIDE)))) {
				if(!NovaPlayer.get(event.getEntered()).isVehicleListed(vehicle)) {
					event.setCancelled(true);
					Message.CHAT_REGION_DENY_RIDEMOB.send(event.getEntered());
				}
			}
		}
	}

	/**
	 * Handles breaking pantings, item frames, leashes
	 * @param event The event
	 */
	@EventHandler
	public void onHangingEntityBreak(HangingBreakByEntityEvent event) {
		if(!(event.getRemover() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getRemover();
		NovaPlayer nPlayer = NovaPlayer.get(player);
		boolean isLeash = event.getEntity() instanceof LeashHitch;

		if(NovaRegion.get(event.getEntity()) != null && (!plugin.getRegionManager().canInteract(player, event.getEntity()) || (!nPlayer.getBypass() && !nPlayer.hasPermission(isLeash ? GuildPermission.MOB_LEASH : GuildPermission.BLOCK_BREAK)))) {
			event.setCancelled(true);
			(isLeash ? Message.CHAT_REGION_DENY_UNLEASH : Message.CHAT_REGION_DENY_INTERACT).send(player);
		}
	}

	/**
	 * Handles placing paintings, item frames, leashes
	 * @param event The event
	 */
	@EventHandler
	public void onHangingPlace(HangingPlaceEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = NovaPlayer.get(player);
		Location location = event.getEntity().getLocation();

		if(NovaRegion.get(location) != null && (!plugin.getRegionManager().canInteract(player, location) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.BLOCK_PLACE)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}

	/**
	 * Handles editing items on an ArmorStand
	 * @param event The event
	 */
	@EventHandler
	public void onPlayerManipulateArmorStand(PlayerArmorStandManipulateEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = NovaPlayer.get(player);
		Location location = event.getRightClicked().getLocation();

		if(NovaRegion.get(location) != null && (!plugin.getRegionManager().canInteract(player, location) || (!nPlayer.getBypass() && !nPlayer.hasPermission(GuildPermission.INTERACT)))) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}
}
