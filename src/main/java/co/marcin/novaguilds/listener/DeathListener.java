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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.TabUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class DeathListener implements Listener {
	private final NovaGuilds plugin;
	
	public DeathListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(event.getEntity().getKiller() == null) {
			return;
		}

		Player victim = event.getEntity();
		Player attacker = event.getEntity().getKiller();

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(victim);
		NovaPlayer nPlayerAttacker = plugin.getPlayerManager().getPlayer(attacker);

		nPlayerAttacker.addKill();
		nPlayer.addDeath();

		if(nPlayer.isPartRaid() && nPlayerAttacker.isPartRaid() && nPlayer.getPartRaid().equals(nPlayerAttacker.getPartRaid()) && !nPlayer.getGuild().isMember(nPlayerAttacker)) {
			NovaRaid raid = nPlayer.getPartRaid();

			if(raid.getPlayersOccupying().contains(nPlayerAttacker)) {
				raid.addKillAttacker();
			}
			else {
				raid.addKillDefender();
			}
		}

		String tag1 = "";
		String tag2 = "";
		String tagscheme = Config.GUILD_TAG.getString();
		tagscheme = StringUtils.replace(tagscheme, "{RANK}", "");

		if(nPlayer.hasGuild()) {
			tag1 = StringUtils.replace(tagscheme, "{TAG}", nPlayer.getGuild().getTag());
		}

		if(nPlayerAttacker.hasGuild()) {
			tag2 = StringUtils.replace(tagscheme, "{TAG}", nPlayerAttacker.getGuild().getTag());
		}

		Map<String, String> vars = new HashMap<>();
		vars.put("PLAYER1", victim.getName());
		vars.put("PLAYER2", attacker.getName());
		vars.put("TAG1", tag1);
		vars.put("TAG2", tag2);
		Message.BROADCAST_PVP_KILLED.vars(vars).broadcast();

		//guildpoints and money
		if(nPlayerAttacker.canGetKillPoints(victim)) {
			//Guild points
			if(nPlayer.hasGuild()) {
				NovaGuild guildVictim = nPlayer.getGuild();
				guildVictim.takePoints(Config.GUILD_DEATHPOINTS.getInt());
			}

			if(nPlayerAttacker.hasGuild()) {
				NovaGuild guildAttacker = nPlayerAttacker.getGuild();
				guildAttacker.addPoints(Config.GUILD_KILLPOINTS.getInt());
			}

			//Raid bonus percent
			double bonusPercentMoney = 0;
			double bonusPercentPoints = 0;
			if(nPlayer.isPartRaid()) {
				bonusPercentMoney = Config.RAID_PVP_BONUSPERCENT_MONEY.getPercent();
				bonusPercentPoints = Config.RAID_PVP_BONUSPERCENT_POINTS.getPercent();
			}

			//player points
			int points = (int) Math.round(nPlayer.getPoints() * (Config.KILLING_RANKPERCENT.getPercent() + bonusPercentPoints));
			nPlayer.takePoints(points);
			nPlayerAttacker.addPoints(points);
			nPlayerAttacker.addKillHistory(victim);

			//money
			vars.clear();
			vars.put("PLAYERNAME", victim.getName());
			double money;
			if(nPlayer.canGetKillPoints(attacker)) {
				money = (Config.KILLING_MONEYFORKILL.getPercent() + bonusPercentMoney) * nPlayer.getMoney();

				if(money > 0) {
					vars.put("MONEY", String.valueOf(money));
					Message.CHAT_PLAYER_PVPMONEY_KILL.vars(vars).send(attacker);
				}
			}
			else {
				money = (Config.KILLING_MONEYFORREVENGE.getPercent() + bonusPercentMoney) * nPlayer.getMoney();

				if(money > 0) {
					vars.put("MONEY", String.valueOf(money));
					Message.CHAT_PLAYER_PVPMONEY_REVENGE.vars(vars).send(attacker);
				}
			}

			if(money > 0) {
				nPlayer.takeMoney(money);
				nPlayerAttacker.addMoney(money);
			}
		}

		//disable death message
		event.setDeathMessage(null);
		TabUtils.refresh(attacker);
		TabUtils.refresh(victim);
	}
}
