package co.marcin.novaguilds.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class RegionInteractListener implements Listener {
	private final NovaGuilds plugin;
	
	public RegionInteractListener(NovaGuilds pl) {
		plugin = pl;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
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
				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(event.getPlayer());
				NovaGuild guild = plugin.getGuildManager().getGuildByRegion(rgatploc);

				boolean isally = guild.isAlly(nPlayer.getGuild());

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
								plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.cannotinteract");
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) { //BREAKING
		if(!plugin.getRegionManager().canBuild(event.getPlayer(),event.getBlock().getLocation())) {
			event.setCancelled(true);
			plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.cannotinteract");
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) { //PLACING
		if(!plugin.getRegionManager().canBuild(event.getPlayer(),event.getBlock().getLocation())) {
			event.setCancelled(true);
			plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.cannotinteract");
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) { //Entity Damage
		List<String> denymobdamage = plugin.getConfig().getStringList("region.denymobdamage");
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(event.getEntity().getLocation());
		plugin.debug("EntityDamageByEntity "+event.getEntityType().name());
		
		if(rgatploc != null) {
			plugin.debug("There is a region");
			plugin.debug(denymobdamage.toString());
			if(denymobdamage.contains(event.getEntity().getType().name())) {
				DamageCause cause = event.getCause();
				boolean playercaused = false;
				Player player = null;
				Arrow arrow = null;

				if(cause.equals(DamageCause.PROJECTILE) && event.getDamager() instanceof Arrow) {
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
					NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(player);

					if(!nPlayer.hasGuild() || (nPlayer.hasGuild() && !nPlayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
						if(!nPlayer.getBypass()) {
							if(!(event.getEntity().getPassenger() instanceof Player)) {
								event.setCancelled(true);
								plugin.getMessageManager().sendMessagesMsg(player, "chat.region.cannotattackmob");

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
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		List<String> denyriding = plugin.getConfig().getStringList("region.denyriding");
		Entity mob = event.getRightClicked();
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(mob.getLocation());
		plugin.debug("PlayerInteractEntityEvent - "+event.getRightClicked().getType().name());
		
		if(rgatploc != null) {
			plugin.debug("There is a region");
			plugin.debug(denyriding.toString());
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(event.getPlayer());
			if(!nPlayer.hasGuild() || (nPlayer.hasGuild() && !nPlayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
				if(!nPlayer.getBypass()) {
					//TODO: fix messages and names for sheep and all
					boolean sheep = mob.getType() == EntityType.SHEEP && event.getPlayer().getItemInHand().getType() == Material.SHEARS;

					if(denyriding.contains(mob.getType().name()) || sheep) {
						event.setCancelled(true);
						plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.cannotridemob");
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

			plugin.getMessageManager().broadcastGuild(guild,"chat.guild.explosionatregion");
		}
	}

	@EventHandler
	public void onUnleash(PlayerUnleashEntityEvent event) {
		List<String> denyriding = plugin.getConfig().getStringList("region.denyriding");
		Entity mob = event.getEntity();
		NovaRegion rgatploc = plugin.getRegionManager().getRegionAtLocation(mob.getLocation());

		if(rgatploc != null) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(event.getPlayer());
			if(!nPlayer.hasGuild() || (nPlayer.hasGuild() && !nPlayer.getGuild().getName().equalsIgnoreCase(rgatploc.getGuildName()))) {
				if(!nPlayer.getBypass()) {
					if(denyriding.contains(mob.getType().name())) {
						event.setCancelled(true);
						plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.cannotunleash");
					}
				}
			}
		}
	}

//	@EventHandler
//	public void onEntityClick(EntityInteractEvent)
}
