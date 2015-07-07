package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.event.GuildRemoveEvent;

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
			plugin.setWarBar(guild, raid.getProgress(), raid.getGuildDefender());
			NovaPlayer nPlayer = raid.getPlayersOccupying().get(0);
			plugin.setWarBar(nPlayer.getGuild(), raid.getProgress(), raid.getGuildDefender());
			plugin.debug(guild.getName() + " scheduler working " + plugin.guildRaids.size());

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
			if(NovaGuilds.systemSeconds() - raid.getInactiveTime() > plugin.getConfigManager().getRaidTimeInactive()) {
				raid.finish();
				plugin.debug("inactive for 10 seconds, removing.");
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
					GuildRemoveEvent guildRemoveEvent = new GuildRemoveEvent(guild);
					guildRemoveEvent.setCause(AbandonCause.RAID);
					plugin.getServer().getPluginManager().callEvent(guildRemoveEvent);

					//if event is not cancelled
					if(!guildRemoveEvent.isCancelled()) {
						vars.put("GUILDNAME", raid.getGuildDefender().getName());
						plugin.getMessageManager().broadcastMessage("broadcast.guild.destroyed", vars);

						NovaGuild guildDefender = raid.getGuildDefender();
						plugin.getGuildManager().deleteGuild(guildDefender);
					}
				}
			}
		}

		if(plugin.guildRaids.size() > 0 && plugin.isEnabled()) {
			plugin.worker.schedule(this, 1, TimeUnit.SECONDS);
		}
		else {
			plugin.debug("size: " + plugin.guildRaids.size());
			plugin.debug("enabled: " + plugin.isEnabled());
		}
	}
}
