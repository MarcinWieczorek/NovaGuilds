package co.marcin.NovaGuilds.Listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.NovaRegion;
import co.marcin.NovaGuilds.Utils;

public class RegionInteractListener implements Listener {
	private NovaGuilds plugin;
	
	public RegionInteractListener(NovaGuilds pl) {
		plugin = pl;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getClickedBlock() instanceof Block) {
			List<String> denyinteract = plugin.config.getStringList("region.denyinteract");
			List<String> denyuse = plugin.config.getStringList("region.denyuse");
			
			String clickedblockname = event.getClickedBlock().getType().name();
			String useditemname = event.getPlayer().getItemInHand().getType().name();
			
			NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getClickedBlock().getLocation());
			
			if(rgatploc instanceof NovaRegion) {
				NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
				
				if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
					if(!novaplayer.getBypass()) {
						boolean isok = true;
						
						if(denyinteract.contains(clickedblockname)) {
							isok = false;
						}

						if(denyuse.contains(useditemname)) {
							isok = false;
						}
						
						if(!isok) {
							event.setCancelled(true);
							
							if(!clickedblockname.contains("_PLATE")) {
								event.getPlayer().sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.region.cannotinteract")));
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) { //BREAKING
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getBlock().getLocation());
		
		if(rgatploc instanceof NovaRegion) {
			NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
			
			if(!novaplayer.getBypass()) {
				if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.region.cannotinteract")));
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getBlock().getLocation());
		
		if(rgatploc instanceof NovaRegion) {
			NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
			
			if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
				if(!novaplayer.getBypass()) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.region.cannotinteract")));
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getEntity().getLocation());
		
		if(rgatploc instanceof NovaRegion) {
			DamageCause cause = event.getCause();
			boolean playercaused = false;
			Player player = null;
			Arrow arrow = null;
			
			if(cause.equals(DamageCause.PROJECTILE)) {
				arrow = (Arrow)event.getDamager();
				
				if(arrow.getShooter() instanceof Player) {
					playercaused = true;
					player = (Player)arrow.getShooter();
				}
			}
			
			if(event.getDamager() instanceof Player) {
				playercaused = true;
				player = (Player) event.getDamager();
			}
			
			if(playercaused) {
				if(player != null) {
					NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(player.getName());
				
					if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
						if(!novaplayer.getBypass()) {
							if(!(event.getEntity().getPassenger() instanceof Player)) {
								event.setCancelled(true);
								player.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.region.cannotattackmob")));
								
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
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		List<String> denyriding = plugin.config.getStringList("region.denyriding");
		Entity mob = event.getRightClicked();
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(mob.getLocation());
		
		if(rgatploc instanceof NovaRegion) {
			NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
			if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
				if(!novaplayer.getBypass()) {
					if(denyriding.contains(mob.getType().name())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.region.cannotridemob")));
					}
				}
			}
		}
		
		if(event.getRightClicked().getType().name().equals("ENDER_CRYSTAL")) {
			if(plugin.progress >= 10) {
				plugin.progress = 0;
				plugin.sendPrefixMessage(event.getPlayer(),"Success!");
				EnderCrystal crystal = (EnderCrystal) event.getRightClicked();
				crystal.getWorld().createExplosion(crystal.getLocation(),100L);
				crystal.remove();
				return;
			}
			
			plugin.sendPrefixMessage(event.getPlayer(),"Progress: "+plugin.progress);
			plugin.progress++;
		}
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		Location loc = event.getLocation();
		NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(loc);
		
		if(rgatloc instanceof NovaRegion) {
			plugin.info("Explosion at region!");
		}
	}
}
