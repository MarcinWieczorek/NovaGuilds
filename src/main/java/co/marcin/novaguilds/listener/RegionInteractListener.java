package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
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
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
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
		if(!plugin.getRegionManager().canBuild(player ,event.getBlock().getLocation())) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		Player player = event.getPlayer();
		if(!plugin.getRegionManager().canBuild(player ,event.getBlock().getLocation())) {
			event.setCancelled(true);
			Message.CHAT_REGION_DENY_INTERACT.send(player);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) { //Entity Damage
		List<String> denymobdamage = Config.REGION_DENYMOBDAMAGE.getStringList();
		NovaRegion rgatploc = plugin.getRegionManager().getRegion(event.getEntity().getLocation());
		
		if(rgatploc != null) {
			if(denymobdamage.contains(event.getEntity().getType().name())) {
				DamageCause cause = event.getCause();
				boolean playercaused = false;
				Player player = null;
				Arrow arrow = null;

				if(cause == DamageCause.PROJECTILE && event.getDamager() instanceof Arrow) {
					arrow = (Arrow) event.getDamager();

					if(arrow.getShooter() instanceof Player) {
						playercaused = true;
						player = (Player) arrow.getShooter();
					}
				}

				if(event.getDamager() instanceof Player) {
					playercaused = true;
					player = (Player) event.getDamager();
				}

				if(playercaused) {
					NovaPlayer nPlayer = NovaPlayer.get(player);

					if(!nPlayer.hasGuild() || (nPlayer.hasGuild() && !nPlayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
						if(!nPlayer.getBypass()) {
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
			}
		}
	}

	@EventHandler
	public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
		if(event.getRemover() instanceof Player) {
			if(!plugin.getRegionManager().canBuild((Player) event.getRemover(), event.getEntity().getLocation())) {
				event.setCancelled(true);
				Message.CHAT_REGION_DENY_INTERACT.send((Player) event.getRemover());
			}
		}
	}

	@EventHandler
	public void onPlayerClickEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity mob = event.getEntity();

		if(!plugin.getRegionManager().canBuild(player, mob.getLocation())) {
			List<String> denyDamage = Config.REGION_DENYMOBDAMAGE.getStringList();
			List<String> denyRiding = Config.REGION_DENYRIDING.getStringList();

			if(event.getAction() == PlayerInteractEntityEvent.EntityUseAction.ATTACK) {
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
				if(plugin.getGuildManager().isBankBlock(block)) {
					if(!rgatloc.getGuild().isRaid()) {
						iterator.remove();
					}
				}
			}

			Message.CHAT_GUILD_EXPLOSIONATREGION.broadcast(rgatloc.getGuild());
		}
	}

	@EventHandler
	public void onUnleash(PlayerUnleashEntityEvent event) {
		List<String> denyriding = Config.REGION_DENYRIDING.getStringList();
		Entity mob = event.getEntity();
		NovaRegion rgatploc = plugin.getRegionManager().getRegion(mob.getLocation());

		Player player = event.getPlayer();
		if(rgatploc != null) {
			NovaPlayer nPlayer = NovaPlayer.get(player);

			if(!nPlayer.hasGuild() || (nPlayer.hasGuild() && !nPlayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
				if(!nPlayer.getBypass()) {
					if(denyriding.contains(mob.getType().name())) {
						event.setCancelled(true);
						Message.CHAT_REGION_DENY_UNLEASH.send(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());

		if(!plugin.getRegionManager().canBuild(event.getPlayer(), block.getLocation())) {
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
