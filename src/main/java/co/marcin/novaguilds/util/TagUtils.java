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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class TagUtils {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();

	public static String getTag(Player namedplayer) { //TODO deleted second arg Player player
		String tag = Config.GUILD_TAG.getString();
		String guildTag;
		String rank = "";
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(namedplayer);

		if(Permission.NOVAGUILDS_CHAT_NOTAG.has(namedplayer) || !nPlayer.hasGuild()) {
			return "";
		}

		guildTag = nPlayer.getGuild().getTag();

		if(!Config.TAGAPI_COLORTAGS.getBoolean()) {
			guildTag = StringUtils.removeColors(guildTag);
		}

		tag = org.apache.commons.lang.StringUtils.replace(tag, "{TAG}", guildTag);

		if(Config.TABAPI_RANKPREFIX.getBoolean()) {
			if(nPlayer.isLeader()) {
				rank = Message.CHAT_GUILDINFO_LEADERPREFIX.get();
			}
		}

		tag = org.apache.commons.lang.StringUtils.replace(tag, "{RANK}", rank);

		return StringUtils.fixColors(tag);
	}

	@SuppressWarnings("deprecation")
	private static void setPrefix(OfflinePlayer player, String tag, Player p) {
		Scoreboard board = p.getScoreboard();
		Team team = board.getPlayerTeam(player);
		if(team == null) {
			String tName = "ng_"+player.getName();
			if(tName.length() > 16) {
				tName = tName.substring(0, 16);
			}

			team = board.registerNewTeam(tName);
			team.addPlayer(player);
		}

		team.setPrefix(StringUtils.fixColors(tag));
	}

	public static void updatePrefix(Player p) {
		for(Player of : Bukkit.getOnlinePlayers()) {
			setPrefix(of, getTag(of), p);
		}
	}

	public static void refreshAll() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			updatePrefix(player);
		}
	}

	public static void refreshGuild(NovaGuild guild) {
		if(guild != null) {
			for(Player player : guild.getOnlinePlayers()) {
				updatePrefix(player);
			}
		}
	}
}
