/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.impl.util.bossbar.BossBarUtils;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RunnableRaid implements Runnable {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private static boolean running = false;

	public void run() {
		running = true;
		boolean renewTask = false;

		for(NovaGuild guildDefender : plugin.getGuildManager().getGuilds()) {
			if(!guildDefender.isRaid()) {
				continue;
			}

			NovaRaid raid = guildDefender.getRaid();

			LoggerUtils.debug(guildDefender.getName() + " raid scheduler working " + raid.getProgress());

			//stepping progress
			if(raid.getPlayersOccupying().size() > 0) {
				raid.addProgress((float) (raid.getPlayersOccupying().size() * Config.RAID_MULTIPLER.getDouble()));
			}
			else {
				raid.resetProgress();
			}

			//vars hashmap
			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.ATTACKER, raid.getGuildAttacker().getName());
			vars.put(VarKey.DEFENDER, guildDefender.getName());

			//players raiding, update inactive time
			if(raid.getPlayersOccupying().size() > 0) {
				raid.updateInactiveTime();
			}

			if(NumberUtils.systemSeconds() - raid.getInactiveTime() > Config.RAID_TIMEINACTIVE.getSeconds()) {
				raid.setResult(NovaRaid.Result.TIMEOUT);
			}

			if(raid.isProgressFinished()) {
				if(guildDefender.getLives() > 1) {
					raid.setResult(NovaRaid.Result.SUCCESS);
				}
				else {
					raid.setResult(NovaRaid.Result.DESTROYED);
				}
			}

			//finishing raid
			if(raid.getResult() != NovaRaid.Result.DURING) {
				int pointsTake = Config.RAID_POINTSTAKE.getInt();

				switch(raid.getResult()) {
					case DESTROYED:
						raid.getGuildAttacker().addPoints(pointsTake);

						GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guildDefender, AbandonCause.RAID);
						plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

						if(!guildAbandonEvent.isCancelled()) {
							vars.put(VarKey.GUILDNAME, guildDefender.getName());
							Message.BROADCAST_GUILD_DESTROYED.vars(vars).broadcast();
							plugin.getGuildManager().delete(guildDefender);
						}
						break;
					case SUCCESS:
						Message.BROADCAST_GUILD_RAID_FINISHED_ATTACKERWON.vars(vars).broadcast();
						guildDefender.takeLive();
						guildDefender.updateTimeRest();
						guildDefender.updateLostLive();
						guildDefender.takePoints(pointsTake);
						guildDefender.addPoints(pointsTake);
						break;
					case TIMEOUT:
						Message.BROADCAST_GUILD_RAID_FINISHED_DEFENDERWON.vars(vars).broadcast();
						break;
				}
			}
			else if(!renewTask) {
				renewTask = true;
			}

			raidBar(raid);
		}

		if(renewTask && plugin.isEnabled() && !running) {
			NovaGuilds.runTaskLater(this, 1, TimeUnit.SECONDS);
			running = true;
		}
	}

	private void raidBar(NovaRaid raid) {
		if(raid.getResult() != NovaRaid.Result.DURING) {
			raid.getGuildAttacker().removeRaidBar();
			raid.getGuildDefender().removeRaidBar();
		}
		else {
			List<Player> players = raid.getGuildAttacker().getOnlinePlayers();
			players.addAll(raid.getGuildDefender().getOnlinePlayers());

			for(Player player : players) {
				if(Config.BOSSBAR_ENABLED.getBoolean()) {
					BossBarUtils.setMessage(player, Message.BARAPI_WARPROGRESS.setVar(VarKey.DEFENDER, raid.getGuildDefender().getName()).get(), raid.getProgress());
				}
				else {
					//TODO
					if(raid.getProgress() == 0 || raid.getProgress() % 10 == 0 || raid.getProgress() >= 90) {
						String lines;
						if(raid.getProgress() == 0) {
							lines = "&f";
						}
						else {
							lines = "&4";
						}

						for(int i = 1; i <= 100; i++) {
							lines += "|";
							if(i == raid.getProgress()) {
								lines += "&f";
							}
						}

						MessageManager.sendPrefixMessage(player, lines);
					}
				}
			}
		}
	}

	public static boolean isRaidRunnableRunning() {
		return running;
	}

	public static void setRaidRunnableRunning(boolean raidRunnableRunning) {
		running = raidRunnableRunning;
	}
}
