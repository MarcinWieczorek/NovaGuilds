package co.marcin.NovaGuilds.utils;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagUtils {
	private final NovaGuilds plugin;

	public TagUtils(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public String getTag(Player namedplayer) { //TODO deleted second arg Player player
		String tag = "";
		String guildTag;
		String rank = "";
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(namedplayer.getName());

		if(nPlayer.hasGuild()) {
			tag = plugin.getConfig().getString("guild.tag");
			guildTag = nPlayer.getGuild().getTag();

			if(!plugin.getConfig().getBoolean("tabapi.colortags")) {
				guildTag = StringUtils.removeColors(guildTag);
			}

			tag = StringUtils.replace(tag, "{TAG}", guildTag);

			if(plugin.getConfig().getBoolean("tabapi.rankprefix")) {
				if(nPlayer.getGuild().getLeaderName().equalsIgnoreCase(namedplayer.getName())) {
					rank = plugin.getMessages().getString("chat.guildinfo.leaderprefix");
				}
			}

			tag = StringUtils.replace(tag, "{RANK}", rank);

			//TODO: ally/war colors
//			NovaPlayer nPlayerReceiver = plugin.getPlayerManager().getPlayerByPlayer(player);
//			if(nPlayerReceiver.hasGuild()) {
//				if(nPlayerReceiver.getGuild().isAlly(nPlayer.getGuild())) {
//					if(plugin.getConfig().getBoolean("tagapi.allycolor.enabled")) {
//						tabName = plugin.getConfig().getString("tagapi.allycolor.color") + tabName;
//					}
//				}
//				else if(plugin.getPlayerManager().isGuildMate(player,namedplayer)) {
//					if(plugin.getConfig().getBoolean("tagapi.guildcolor.enabled")) {
//						tabName = plugin.getConfig().getString("tagapi.guildcolor.color") + tabName;
//					}
//				}
//				else if(nPlayer.getGuild().isWarWith(nPlayerReceiver.getGuild())) {
//					if(plugin.getConfig().getBoolean("tagapi.warcolor.enabled")) {
//						tabName = plugin.getConfig().getString("tagapi.warcolor.color") + tabName;
//					}
//				}
//			}

			//TODO: using chat permissions
			if(namedplayer.hasPermission("novaguilds.chat.notag")) {
				tag = "";
			}
		}

		return StringUtils.fixColors(tag);
	}

	private static void setPrefix(OfflinePlayer player, String tag, Player p) {
		Scoreboard board = p.getScoreboard();
		Team team;
		if (board.getPlayerTeam(player) == null) {
			team = board.registerNewTeam(player.getName());
			team.addPlayer(player);
		} else {
			team = board.getPlayerTeam(player);
		}
		team.setPrefix(StringUtils.fixColors(tag));
	}

	public void updatePrefix(Player p) {
		for(Player of : Bukkit.getOnlinePlayers()) {
				setPrefix(of, getTag(of), p);
		}
	}

	public void refreshAll() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			updatePrefix(player);
		}
	}
}
