package co.marcin.NovaGuilds.utils;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import javax.swing.text.html.HTML;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TagUtils {
	private final NovaGuilds plugin;

	public TagUtils(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public void updateTagPlayerToAll(Player p) {
		if(p == null)
			return;

		Set<Player> set = new HashSet<>(Arrays.asList(plugin.getServer().getOnlinePlayers()));
		TagAPI.refreshPlayer(p, set);
	}

	public void updateTagAll() {
		for(Player p: plugin.getServer().getOnlinePlayers()) {
			Set<Player> set = new HashSet<>(Arrays.asList(plugin.getServer().getOnlinePlayers()));
			TagAPI.refreshPlayer(p, set);
		}
	}

	public String getTag(Player namedplayer,Player player) {
		String tag;
		String guildTag;
		String rank = "";
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(namedplayer.getName());
		String tabName = namedplayer.getName();

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

			//TODO ally colors
			NovaPlayer nPlayerReceiver = plugin.getPlayerManager().getPlayerByPlayer(player);
			if(nPlayerReceiver.hasGuild()) {
				if(nPlayerReceiver.getGuild().isAlly(nPlayer.getGuild())) {
					if(plugin.getConfig().getBoolean("tagapi.allycolor.enabled")) {
						tabName = plugin.getConfig().getString("tagapi.allycolor.color") + tabName;
					}
				}
				else if(plugin.getPlayerManager().isGuildMate(player,namedplayer)) {
					if(plugin.getConfig().getBoolean("tagapi.guildcolor.enabled")) {
						tabName = plugin.getConfig().getString("tagapi.guildcolor.color") + tabName;
					}
				}
				else if(nPlayer.getGuild().isWarWith(nPlayerReceiver.getGuild())) {
					if(plugin.getConfig().getBoolean("tagapi.warcolor.enabled")) {
						tabName = plugin.getConfig().getString("tagapi.warcolor.color") + tabName;
					}
				}
			}

			tabName = tag + tabName;
		}

		return StringUtils.fixColors(tabName);
	}
}
