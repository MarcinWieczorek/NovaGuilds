package co.marcin.NovaGuilds.runnable;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.basic.NovaRaid;

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
			if(plugin.DEBUG) plugin.info(guild.getName() + " scheduler working " + plugin.guildRaids.size());

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
			if(raid.systemSeconds() - raid.getInactiveTime() > plugin.timeInactive) {
				raid.finish();
				if(plugin.DEBUG) plugin.info("inactive for 10 seconds, removing.");
				plugin.broadcastMessage("broadcast.guild.raid.finished.defenderwon", vars);
			}

			if(raid.isProgressFinished()) {
				raid.finish();
			}

			//finishing raid
			if(raid.getFinished()) {

				plugin.broadcastMessage("broadcast.guild.raid.finished.attackerwon", vars);
				plugin.resetWarBar(guild);
				plugin.resetWarBar(nPlayer.getGuild());
				guild.takeLive();
				guild.updateTimeRest();
				plugin.guildRaids.remove(guild);
				guild.isNotRaid();

				if(guild.getLives() == 0) {
					vars.put("GUILDNAME", raid.getGuildDefender().getName());
					plugin.broadcastMessage("broadcast.guild.destroyed", vars);

					NovaGuild guildDefender = raid.getGuildDefender();
					guildDefender.setLives(1);
					plugin.getGuildManager().deleteGuild(guildDefender);
				}
			}
		}

		if(plugin.guildRaids.size() > 0 && plugin.isEnabled()) {
			plugin.worker.schedule(this, 1, TimeUnit.SECONDS);
		}
		else {
			if(plugin.DEBUG) plugin.info("size: " + plugin.guildRaids.size());
			if(plugin.DEBUG) plugin.info("enabled: " + plugin.isEnabled());
		}
	}
}
