package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
		if(event.getClickedBlock() != null) {
			List<String> denyinteract = plugin.getConfig().getStringList("region.denyinteract");
			List<String> denyuse = plugin.getConfig().getStringList("region.denyuse");
			
			String clickedblockname = event.getClickedBlock().getType().name();
			String useditemname = event.getPlayer().getItemInHand().getType().name();
			
			NovaRegion rgatploc = plugin.getRegionManager().getRegion(event.getClickedBlock().getLocation());
			
			if(rgatploc != null) {
				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
				NovaGuild guild = rgatploc.getGuild();

				//boolean isally = nPlayer.hasGuild() && guild.isAlly(nPlayer.getGuild());

				// (has no guild) OR (not his guild AND not ally)
				if(!nPlayer.hasGuild() || (!guild.isMember(nPlayer) && !guild.isAlly(nPlayer.getGuild()))) {
					if(!nPlayer.getBypass()) {
						if(denyinteract.contains(clickedblockname) || denyuse.contains(useditemname)) {
							event.setCancelled(true);
							
							if(!clickedblockname.contains("_PLATE")) { //Supress for plates
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
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
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
					NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

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
	public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
		Entity entity = event.getEntity();
		LoggerUtils.debug("hanging break by entity event");

		NovaRegion rgatploc = plugin.getRegionManager().getRegion(entity.getLocation());

		if(rgatploc != null) {
			LoggerUtils.debug("there is a region");

			if(entity instanceof ItemFrame) {
				LoggerUtils.debug("item frame destroyed");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		List<String> denyriding = plugin.getConfig().getStringList("region.denyriding");
		Entity mob = event.getRightClicked();
		NovaRegion rgatploc = plugin.getRegionManager().getRegion(mob.getLocation());
		//LoggerUtils.debug("PlayerInteractEntityEvent - "+event.getRightClicked().getType().name());
		
		if(rgatploc != null) {
			LoggerUtils.debug("There is a region");
			LoggerUtils.debug(denyriding.toString());
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
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
		NovaRegion rgatloc = plugin.getRegionManager().getRegion(loc);
		
		if(rgatloc != null) {
			Iterator<Block> iterator = event.blockList().iterator();
			while(iterator.hasNext()) {
				Block block = iterator.next();
				if(plugin.isBankBlock(block)) {
					if(!rgatloc.getGuild().isRaid()) {
						iterator.remove();
					}
				}
			}

			plugin.getMessageManager().broadcastGuild(rgatloc.getGuild(),"chat.guild.explosionatregion",true);
		}
	}

	@EventHandler
	public void onUnleash(PlayerUnleashEntityEvent event) {
		List<String> denyriding = plugin.getConfig().getStringList("region.denyriding");
		Entity mob = event.getEntity();
		NovaRegion rgatploc = plugin.getRegionManager().getRegion(mob.getLocation());

		if(rgatploc != null) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
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
}
