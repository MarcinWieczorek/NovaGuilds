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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.util.PreparedTagImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class TagUtils {
	@SuppressWarnings("deprecation")
	public static void refresh(Player p) {
		if(!Config.TAGAPI_ENABLED.getBoolean()) {
			return;
		}

		for(Player player : Bukkit.getOnlinePlayers()) {
			PreparedTag tag = new PreparedTagImpl(NovaPlayer.get(player));

			Scoreboard board = p.getScoreboard();
			Team team = board.getPlayerTeam(player);

			if(team == null) {
				String tName = "ng_" + player.getName();
				if(tName.length() > 16) {
					tName = tName.substring(0, 16);
				}

				team = board.registerNewTeam(tName);
				team.addPlayer(player);
			}

			team.setPrefix(tag.get());
		}
	}

	public static void refresh() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			refresh(player);
		}
	}

	public static void refresh(NovaGuild guild) {
		if(guild != null) {
			for(Player player : guild.getOnlinePlayers()) {
				refresh(player);
			}
		}
	}
}
