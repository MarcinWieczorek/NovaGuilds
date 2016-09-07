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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.ChatMessage;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.TagColor;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.ChatMessageImpl;
import co.marcin.novaguilds.impl.util.preparedtag.PreparedTagChatImpl;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener extends AbstractListener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		event.setCancelled(true);
		NovaGuild guild = nPlayer.getGuild();

		PreparedTag preparedTag = new PreparedTagChatImpl(nPlayer);

		String prefixChatGuild = Config.CHAT_GUILD_PREFIX.getString();
		String prefixChatAlly = Config.CHAT_ALLY_PREFIX.getString();

		String msg = event.getMessage();
		boolean isAllyPrefix = msg.startsWith(prefixChatAlly);
		boolean isGuildPrefix = msg.startsWith(prefixChatGuild) && !isAllyPrefix;

		if(nPlayer.hasGuild() && !isGuildPrefix && (isAllyPrefix || nPlayer.getChatMode() == ChatMode.ALLY)) { //ally chat
			if(Config.CHAT_ALLY_ENABLED.getBoolean()) {
				preparedTag.setLeaderPrefix(preparedTag.isLeaderPrefix() && Config.CHAT_ALLY_LEADERPREFIX.getBoolean());

				preparedTag.setColor(TagColor.NEUTRAL);
				String cFormat = Config.CHAT_ALLY_FORMAT.getString();
				cFormat = org.apache.commons.lang.StringUtils.replace(cFormat, "{TAG}", preparedTag.get());
				cFormat = org.apache.commons.lang.StringUtils.replace(cFormat, "{PLAYERNAME}", nPlayer.getName());
				cFormat = StringUtils.fixColors(cFormat);

				//Trim prefix
				if(nPlayer.getChatMode() != ChatMode.ALLY) {
					msg = msg.substring(prefixChatAlly.length(), msg.length());

					if(msg.length() == 0) {
						return;
					}
				}

				for(NovaPlayer nPlayerLoop : plugin.getPlayerManager().getOnlinePlayers()) {
					if(nPlayerLoop.getSpyMode() || (nPlayerLoop.hasGuild() && (nPlayerLoop.getGuild().isAlly(guild) || guild.isMember(nPlayerLoop)))) {
						nPlayerLoop.getPlayer().sendMessage(cFormat + msg);
					}
				}

				return;
			}
		}
		else if(nPlayer.hasGuild() && (isGuildPrefix || nPlayer.getChatMode() == ChatMode.GUILD)) { //guild chat
			if(Config.CHAT_GUILD_ENABLED.getBoolean()) {
				String rank = Config.CHAT_GUILD_LEADERPREFIX.getBoolean() && nPlayer.isLeader() ? Config.CHAT_LEADERPREFIX.getString() : "";

				String cFormat = Config.CHAT_GUILD_FORMAT.getString();
				cFormat = org.apache.commons.lang.StringUtils.replace(cFormat, "{LEADERPREFIX}", rank);
				cFormat = org.apache.commons.lang.StringUtils.replace(cFormat, "{PLAYERNAME}", nPlayer.getName());
				cFormat = StringUtils.fixColors(cFormat);

				//Trim prefix
				if(nPlayer.getChatMode() != ChatMode.GUILD) {
					msg = msg.substring(prefixChatGuild.length(), msg.length());

					if(msg.length() == 0) {
						return;
					}
				}

				for(NovaPlayer nPlayerLoop : plugin.getPlayerManager().getOnlinePlayers()) {
					if(guild.isMember(nPlayerLoop) || nPlayerLoop.getSpyMode()) {
						nPlayerLoop.getPlayer().sendMessage(cFormat + msg);
					}
				}

				return;
			}
		}

		//Message with a tag
		if(Config.CHAT_ADVANCED.getBoolean()) {
			ChatMessage chatMessage = new ChatMessageImpl(player);
			chatMessage.setTag(preparedTag);
			chatMessage.setFormat(event.getFormat());
			chatMessage.setMessage(event.getMessage());

			for(NovaPlayer onlineNovaPlayer : plugin.getPlayerManager().getOnlinePlayers()) {
				preparedTag.setTagColorFor(onlineNovaPlayer);
				preparedTag.setHidden(Permission.NOVAGUILDS_CHAT_NOTAG.has(player));

				chatMessage.send(onlineNovaPlayer);
			}
		}
		else {
			event.setCancelled(false);
			String format = event.getFormat();
			format = org.apache.commons.lang.StringUtils.replace(format, "{TAG}", preparedTag.get());
			event.setFormat(format);
		}
	}

	@EventHandler
	public void onCommandExecute(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().substring(1, event.getMessage().length());

		if(cmd.contains(" ")) {
			String[] split = org.apache.commons.lang.StringUtils.split(cmd, ' ');
			cmd = split[0];
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(event.getPlayer());
		if(!nPlayer.getBypass() && Config.REGION_BLOCKEDCMDS.getStringList().contains(cmd.toLowerCase())) {
			NovaRegion region = RegionManager.get(event.getPlayer());

			if(region != null) {
				if(nPlayer.hasGuild()) {
					if(!region.getGuild().isMember(nPlayer) && !region.getGuild().isAlly(nPlayer.getGuild())) {
						Message.CHAT_REGION_BLOCKEDCMD.send(event.getPlayer());
						event.setCancelled(true);
					}
				}
				else {
					Message.CHAT_REGION_BLOCKEDCMD.send(event.getPlayer());
					event.setCancelled(true);
				}
			}
		}

		if(plugin.getCommandManager().existsAlias(cmd)) {
			event.setMessage(event.getMessage().replaceFirst(cmd, plugin.getCommandManager().getMainCommand(cmd)));
		}

		//TODO: subCommands
	}
}
