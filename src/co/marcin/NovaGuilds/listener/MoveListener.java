package co.marcin.NovaGuilds.listener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.basic.NovaRaid;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaRegion;

public class MoveListener implements Listener {
	private final NovaGuilds plugin;

	public MoveListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		
		NovaRegion fromRegion = plugin.getRegionManager().getRegionAtLocation(from);
		NovaRegion toRegion = plugin.getRegionManager().getRegionAtLocation(to);

		final Player player = event.getPlayer();
		
		//entering
		if(fromRegion == null) {
			if(toRegion != null) {
				HashMap<String,String> vars = new HashMap<>();
				vars.put("GUILDNAME",toRegion.getGuildName());
				vars.put("PLAYERNAME",player.getName());
				plugin.sendMessagesMsg(player, "chat.region.entered", vars);

				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(player);
				nPlayer.setAtRegion(toRegion);
				plugin.getPlayerManager().updateLocalPlayer(nPlayer);

				//TODO add config
				if(!nPlayer.getGuild().getName().equalsIgnoreCase(toRegion.getGuildName())) {
					NovaGuild guild = plugin.getGuildManager().getGuildByRegion(toRegion);

					if(nPlayer.getGuild().isWarWith(guild)) {
						if(!guild.isRaid()) {
							if(NovaGuilds.systemSeconds() - plugin.timeRest > guild.getTimeRest()) {
								guild.createRaid(nPlayer.getGuild());
								plugin.guildRaids.add(guild);
							}
							else {
								long timeWait = plugin.timeRest - (NovaGuilds.systemSeconds() - guild.getTimeRest());
								vars.put("TIMEREST", StringUtils.secondsToString(timeWait));

								plugin.sendMessagesMsg(player, "chat.raid.resting", vars);
							}
						}

						if(guild.isRaid()) {
							guild.getRaid().addPlayerOccupying(nPlayer);
							Runnable task = new Runnable() {
								public void run() {
									for(NovaGuild guild : plugin.guildRaids) {
										NovaRaid raid = guild.getRaid();
										plugin.setWarBar(guild, raid.getProgress());
										NovaPlayer nPlayer = guild.getRaid().getPlayersOccupying().get(0);
										plugin.setWarBar(nPlayer.getGuild(), guild.getRaid().getProgress());
										plugin.info(guild.getName() + " scheduler working " + plugin.guildRaids.size());

										//stepping progress
										for(int count = 0; count < raid.getPlayersOccupyingCount(); count++) {
											raid.stepProgress();
										}

										if(guild.getRaid().getPlayersOccupyingCount() > 0) {
											raid.updateInactiveTime();
										}

										//TODO: can be done better
										if(raid.systemSeconds() - raid.getInactiveTime() > 10) {
											raid.finish();
											plugin.info("inactive for 10 seconds, removing.");
										}

										if(raid.isProgressFinished()) {
											raid.finish();
										}

										if(raid.getFinished()) {
											plugin.broadcast("Raid finished! " + raid.getGuildAttacker().getName() + " vs " + guild.getName());
											plugin.resetWarBar(guild);
											plugin.resetWarBar(nPlayer.getGuild());
											guild.takeLive();
											guild.updateTimeRest();
											plugin.guildRaids.remove(guild);
											guild.isNotRaid();
										}
									}

									if(plugin.guildRaids.size() > 0 && plugin.isEnabled()) {
										plugin.worker.schedule(this, 1, TimeUnit.SECONDS);
									} else {
										plugin.info("size: " + plugin.guildRaids.size());
										plugin.info("enabled: " + plugin.isEnabled());
									}
								}
							};
							plugin.worker.schedule(task, 1, TimeUnit.SECONDS);
						}
					}

					plugin.broadcastGuild(plugin.getGuildManager().getGuildByRegion(toRegion), "chat.region.notifyguild.entered", vars);
				}
			}
		}
		
		//exiting
		if(fromRegion != null) {
			if(toRegion == null) {
				HashMap<String,String> vars = new HashMap<>();
				vars.put("GUILDNAME", fromRegion.getGuildName());
				plugin.sendMessagesMsg(event.getPlayer(), "chat.region.exited", vars);

				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByPlayer(event.getPlayer());
				NovaGuild guild = plugin.getGuildManager().getGuildByRegion(fromRegion);

				if(nPlayer.getGuild().isWarWith(guild)) {
					if(guild.isRaid()) {
						guild.getRaid().removePlayerOccupying(nPlayer);

						if(guild.getRaid().getPlayersOccupyingCount() == 0) {
							guild.getRaid().resetProgress();
							plugin.resetWarBar(guild);
							plugin.resetWarBar(nPlayer.getGuild());
							plugin.info("progress: "+guild.getRaid().getProgress());
							guild.getRaid().updateInactiveTime();
						}
					}
				}
			}
		}
	}
}
