package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RunnableRaid implements Runnable {
	private final NovaGuilds plugin;

	public RunnableRaid(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public void run() {
		for(NovaGuild guild : plugin.guildRaids) {
			NovaRaid raid = guild.getRaid();
			plugin.showRaidBar(raid);

			NovaPlayer nPlayer = raid.getPlayersOccupying().get(0);
			LoggerUtils.debug(guild.getName() + " scheduler working " + plugin.guildRaids.size());

			//stepping progress
			for(int count = 0; count < raid.getPlayersOccupyingCount(); count++) {
				raid.stepProgress();
			}

			//vars hashmap
			HashMap<String,String> vars = new HashMap<>();
			vars.put("ATTACKER",raid.getGuildAttacker().getName());
			vars.put("DEFENDER", raid.getGuildDefender().getName());

			//players raiding, update inactive time
			if(raid.getPlayersOccupyingCount() > 0) {
				raid.updateInactiveTime();
			}

			//TODO: can be done better
			//TODO: not working at all
			if(NumberUtils.systemSeconds() - raid.getInactiveTime() > plugin.getConfigManager().getRaidTimeInactive()) {
				raid.finish();
				LoggerUtils.debug("inactive for 10 seconds, removing.");
				plugin.getMessageManager().broadcastMessage("broadcast.guild.raid.finished.defenderwon", vars);
			}

			if(raid.isProgressFinished()) {
				raid.finish();
			}

			//finishing raid
			if(raid.getFinished()) {
				plugin.getMessageManager().broadcastMessage("broadcast.guild.raid.finished.attackerwon", vars);
				plugin.resetWarBar(guild);
				plugin.resetWarBar(nPlayer.getGuild());
				plugin.guildRaids.remove(guild);
				guild.takeLive();
				guild.updateTimeRest();
				guild.updateLostLive();
				guild.isNotRaid();

				if(guild.getLives() == 0) {
					//fire event
					GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.RAID);
					plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

					//if event is not cancelled
					if(!guildAbandonEvent.isCancelled()) {
						vars.put("GUILDNAME", raid.getGuildDefender().getName());
						plugin.getMessageManager().broadcastMessage("broadcast.guild.destroyed", vars);

						NovaGuild guildDefender = raid.getGuildDefender();
						plugin.getGuildManager().deleteGuild(guildDefender);
					}
				}
			}
		}

		if(!plugin.guildRaids.isEmpty() && plugin.isEnabled()) {
			plugin.worker.schedule(this, 1, TimeUnit.SECONDS);
		}
		else {
			LoggerUtils.debug("size: " + plugin.guildRaids.size());
			LoggerUtils.debug("enabled: " + plugin.isEnabled());
		}
	}
}
