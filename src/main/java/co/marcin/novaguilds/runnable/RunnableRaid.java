/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RunnableRaid implements Runnable {
	private final NovaGuilds plugin;

	public RunnableRaid(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public void run() {
		NovaGuilds.setRaidRunnableRunning(false);

		for(NovaGuild guild : plugin.guildRaids) {
			NovaRaid raid = guild.getRaid();
			plugin.showRaidBar(raid);
			NovaGuild guildDefender = raid.getGuildDefender();

			LoggerUtils.debug(guild.getName() + " scheduler working " + plugin.guildRaids.size());

			//stepping progress
			for(int count = 0; count < raid.getPlayersOccupyingCount(); count++) {
				raid.stepProgress();
			}

			//vars hashmap
			Map<String, String> vars = new HashMap<>();
			vars.put("ATTACKER",raid.getGuildAttacker().getName());
			vars.put("DEFENDER", guildDefender.getName());

			//players raiding, update inactive time
			if(raid.getPlayersOccupyingCount() > 0) {
				raid.updateInactiveTime();
			}

			if(NumberUtils.systemSeconds() - raid.getInactiveTime() > Config.RAID_TIMEINACTIVE.getSeconds()) {
				Message.BROADCAST_GUILD_RAID_FINISHED_DEFENDERWON.vars(vars).broadcast();
				guild.removeRaidBar();
				raid.getGuildAttacker().removeRaidBar();
				plugin.guildRaids.remove(guild);
				return;
			}

			if(raid.isProgressFinished()) {
				raid.finish();
			}

			//finishing raid
			if(raid.getFinished()) {
				Message.BROADCAST_GUILD_RAID_FINISHED_ATTACKERWON.vars(vars).broadcast();
				guild.removeRaidBar();
				raid.getGuildAttacker().removeRaidBar();
				guild.takeLive();
				guild.updateTimeRest();
				guild.updateLostLive();
				guild.isNotRaid();
				plugin.guildRaids.remove(guild);

				int pointsTake = Config.RAID_POINTSTAKE.getInt();
				if(pointsTake > 0) {
					guild.takePoints(pointsTake);
					guildDefender.addPoints(pointsTake);
				}

				if(guild.getLives() == 0) {
					//fire event
					GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.RAID);
					plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

					//if event is not cancelled
					if(!guildAbandonEvent.isCancelled()) {
						vars.put("GUILDNAME", guildDefender.getName());
						Message.BROADCAST_GUILD_DESTROYED.vars(vars).broadcast();
						plugin.getGuildManager().delete(guildDefender);
					}
				}
			}
		}

		if(!plugin.guildRaids.isEmpty() && plugin.isEnabled() && !NovaGuilds.isRaidRunnableRunning()) {
			plugin.worker.schedule(this, 1, TimeUnit.SECONDS);
			NovaGuilds.setRaidRunnableRunning(true);
		}
		else {
			LoggerUtils.debug("size: " + plugin.guildRaids.size());
			LoggerUtils.debug("enabled: " + plugin.isEnabled());
		}
	}
}
