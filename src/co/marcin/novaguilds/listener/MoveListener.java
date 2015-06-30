package co.marcin.novaguilds.listener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.runnable.RunnableRaid;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MoveListener implements Listener {
	private final NovaGuilds plugin;

	public MoveListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(player);
		Location from = event.getFrom();
		Location to = event.getTo();
		
		NovaRegion fromRegion = plugin.getRegionManager().getRegionAtLocation(from);
		NovaRegion toRegion = plugin.getRegionManager().getRegionAtLocation(to);


		
		//entering
		if(fromRegion == null && toRegion != null && nPlayer.getAtRegion() == null) {
			//border particles
			if(plugin.getConfig().getBoolean("region.borderparticles")) {
				List<Block> blocks = plugin.getRegionManager().getBorderBlocks(toRegion);
				for(Block block : blocks) {
					block.getLocation().setY(block.getLocation().getY() + 1);
					block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 100);
				}
			}

			//Chat message
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME",toRegion.getGuildName());
			vars.put("PLAYERNAME",player.getName());
			plugin.getMessageManager().sendMessagesMsg(player, "chat.region.entered", vars);

			//Player is at region
			nPlayer.setAtRegion(toRegion);

			//TODO add config
			if(nPlayer.hasGuild()) {
				if(!nPlayer.getGuild().getName().equalsIgnoreCase(toRegion.getGuildName())) {
					NovaGuild guildDefender = plugin.getGuildManager().getGuildByRegion(toRegion);

					if(nPlayer.getGuild().isWarWith(guildDefender)) {
						if(!guildDefender.isRaid()) {
							if(NovaGuilds.systemSeconds() - plugin.timeRest > guildDefender.getTimeRest()) {
								guildDefender.createRaid(nPlayer.getGuild());
								plugin.guildRaids.add(guildDefender);
							}
							else {
								long timeWait = plugin.timeRest - (NovaGuilds.systemSeconds() - guildDefender.getTimeRest());
								vars.put("TIMEREST", StringUtils.secondsToString(timeWait));

								plugin.getMessageManager().sendMessagesMsg(player, "chat.raid.resting", vars);
							}
						}

						if(guildDefender.isRaid()) {
							guildDefender.getRaid().addPlayerOccupying(nPlayer);
							Runnable task = new RunnableRaid(plugin);
							plugin.worker.schedule(task, 1, TimeUnit.SECONDS);
						}
					}

					//TODO: notify
					plugin.getMessageManager().broadcastGuild(plugin.getGuildManager().getGuildByRegion(toRegion), "chat.region.notifyguild.entered", vars);
				}
			}
		}
		
		//exiting
		if(fromRegion != null && toRegion == null && nPlayer.getAtRegion() != null) {
			NovaGuild guild = plugin.getGuildManager().getGuildByRegion(fromRegion);
			nPlayer.setAtRegion(null);
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME", fromRegion.getGuildName());
			plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.exited", vars);

			if(nPlayer.hasGuild()) {
				if(nPlayer.getGuild().isWarWith(guild)) {
					if(guild.isRaid()) {
						guild.getRaid().removePlayerOccupying(nPlayer);

						if(guild.getRaid().getPlayersOccupyingCount() == 0) {
							guild.getRaid().resetProgress();
							plugin.resetWarBar(guild);
							plugin.resetWarBar(nPlayer.getGuild());
							plugin.debug("progress: " + guild.getRaid().getProgress());
							guild.getRaid().updateInactiveTime();
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(player);
		Location from = event.getFrom();
		Location to = event.getTo();

		NovaRegion fromRegion = plugin.getRegionManager().getRegionAtLocation(from);
		NovaRegion toRegion = plugin.getRegionManager().getRegionAtLocation(to);



		//entering
		if(fromRegion == null && toRegion != null && nPlayer.getAtRegion() == null) {
			//border particles
			if(plugin.getConfig().getBoolean("region.borderparticles")) {
				List<Block> blocks = plugin.getRegionManager().getBorderBlocks(toRegion);
				for(Block block : blocks) {
					block.getLocation().setY(block.getLocation().getY() + 1);
					block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 100);
				}
			}

			//Chat message
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME",toRegion.getGuildName());
			vars.put("PLAYERNAME",player.getName());
			plugin.getMessageManager().sendMessagesMsg(player, "chat.region.entered", vars);

			//Player is at region
			nPlayer.setAtRegion(toRegion);

			//TODO add config
			if(nPlayer.hasGuild()) {
				if(!nPlayer.getGuild().getName().equalsIgnoreCase(toRegion.getGuildName())) {
					NovaGuild guildDefender = plugin.getGuildManager().getGuildByRegion(toRegion);

					if(nPlayer.getGuild().isWarWith(guildDefender)) {
						if(!guildDefender.isRaid()) {
							if(NovaGuilds.systemSeconds() - plugin.timeRest > guildDefender.getTimeRest()) {
								guildDefender.createRaid(nPlayer.getGuild());
								plugin.guildRaids.add(guildDefender);
							}
							else {
								long timeWait = plugin.timeRest - (NovaGuilds.systemSeconds() - guildDefender.getTimeRest());
								vars.put("TIMEREST", StringUtils.secondsToString(timeWait));

								plugin.getMessageManager().sendMessagesMsg(player, "chat.raid.resting", vars);
							}
						}

						if(guildDefender.isRaid()) {
							guildDefender.getRaid().addPlayerOccupying(nPlayer);
							Runnable task = new RunnableRaid(plugin);
							plugin.worker.schedule(task, 1, TimeUnit.SECONDS);
						}
					}

					//TODO: notify
					plugin.getMessageManager().broadcastGuild(plugin.getGuildManager().getGuildByRegion(toRegion), "chat.region.notifyguild.entered", vars);
				}
			}
		}

		//exiting
		if(fromRegion != null && toRegion == null && nPlayer.getAtRegion() != null) {
			NovaGuild guild = plugin.getGuildManager().getGuildByRegion(fromRegion);
			nPlayer.setAtRegion(null);

			//message
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME", fromRegion.getGuildName());
			plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.exited", vars);

			if(nPlayer.hasGuild()) {
				if(nPlayer.getGuild().isWarWith(guild)) {
					if(guild.isRaid()) {
						guild.getRaid().removePlayerOccupying(nPlayer);

						if(guild.getRaid().getPlayersOccupyingCount() == 0) {
							guild.getRaid().resetProgress();
							plugin.resetWarBar(guild);
							plugin.resetWarBar(nPlayer.getGuild());
							plugin.debug("progress: " + guild.getRaid().getProgress());
							guild.getRaid().updateInactiveTime();
						}
					}
				}
			}
		}
	}
}
