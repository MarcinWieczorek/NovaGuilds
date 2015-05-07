package co.marcin.NovaGuilds.listener;

import java.util.List;

import org.bukkit.Location;
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

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.basic.NovaRegion;
import co.marcin.NovaGuilds.utils.StringUtils;

public class RegionInteractListener implements Listener {
	private final NovaGuilds plugin;
	
	public RegionInteractListener(NovaGuilds pl) {
		plugin = pl;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getClickedBlock() != null) {
			List<String> denyinteract = plugin.getConfig().getStringList("region.denyinteract");
			List<String> denyuse = plugin.getConfig().getStringList("region.denyuse");
			
			String clickedblockname = event.getClickedBlock().getType().name();
			String useditemname = event.getPlayer().getItemInHand().getType().name();
			
			NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getClickedBlock().getLocation());
			
			if(rgatploc != null) {
				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
				NovaGuild guild = plugin.getGuildManager().getGuildByRegion(rgatploc);

				boolean isally = guild.isAlly(nPlayer.getGuild());
				if(plugin.DEBUG) plugin.info(isally+" "+guild.getName()+" "+nPlayer.getGuild().getName());

				// (has no guild) OR (has guild AND (not his guild AND not ally)
				if(!nPlayer.hasGuild() || (nPlayer.hasGuild() && (!nPlayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()) && !isally))) {
					if(!nPlayer.getBypass()) {
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
								event.getPlayer().sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.region.cannotinteract")));
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
		
		if(rgatploc != null) {
			NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
			
			if(!novaplayer.getBypass()) {
				if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.region.cannotinteract")));
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getBlock().getLocation());
		
		if(rgatploc != null) {
			NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
			
			if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
				if(!novaplayer.getBypass()) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.region.cannotinteract")));
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getEntity().getLocation());
		
		if(rgatploc != null) {
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
								player.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.region.cannotattackmob")));
								
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
		List<String> denyriding = plugin.getConfig().getStringList("region.denyriding");
		Entity mob = event.getRightClicked();
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(mob.getLocation());
		
		if(rgatploc != null) {
			NovaPlayer novaplayer = plugin.getPlayerManager().getPlayerByName(event.getPlayer().getName());
			if(!novaplayer.hasGuild() || (novaplayer.hasGuild() && !novaplayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
				if(!novaplayer.getBypass()) {
					if(denyriding.contains(mob.getType().name())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.region.cannotridemob")));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		Location loc = event.getLocation();
		NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(loc);
		
		if(rgatloc != null) {
			NovaGuild guild = plugin.getGuildManager().getGuildByName(rgatloc.getGuildName());
			
			for(NovaPlayer nP : guild.getPlayers()) {
				if(nP.isOnline()) {
					plugin.sendMessagesMsg(nP.getPlayer(),"chat.guild.explosionatregion");
				}
			}
		}
	}
}
