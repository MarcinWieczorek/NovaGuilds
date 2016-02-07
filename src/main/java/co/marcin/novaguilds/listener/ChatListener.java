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
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.PreparedTagImpl;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {
	private final NovaGuilds plugin;
	
	public ChatListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String msg = event.getMessage();
		
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		String format = event.getFormat();
		NovaGuild guild = nPlayer.getGuild();
		String tagString = "";
		String rank = "";

		PreparedTag tag = new PreparedTagImpl(nPlayer);

		if(Config.CHAT_DISPLAYNAMETAGS.getBoolean()) {
			format = tag.get() + format;
		}
		else {
			format = org.apache.commons.lang.StringUtils.replace(format, "{TAG}", tag.get());
		}

		event.setFormat(format);

		if(!nPlayer.hasGuild()) {
			return;
		}

		String prefixChatGuild = Config.CHAT_GUILD_PREFIX.getString();
		String prefixChatAlly = Config.CHAT_ALLY_PREFIX.getString();

		boolean isAllyPrefix = msg.startsWith(prefixChatAlly);
		boolean isGuildPrefix = msg.startsWith(prefixChatGuild) && !isAllyPrefix;

		if(!isGuildPrefix && (isAllyPrefix || nPlayer.getChatMode() == ChatMode.ALLY)) { //ally chat
			if(Config.CHAT_ALLY_ENABLED.getBoolean()) {
				if(!Config.CHAT_ALLY_COLORTAGS.getBoolean()) {
					tagString = StringUtils.removeColors(tagString);
				}

				if(!Config.CHAT_ALLY_LEADERPREFIX.getBoolean()) {
					rank = "";
				}

				tagString = org.apache.commons.lang.StringUtils.replace(tagString, "{RANK}", rank);

				String cFormat = Config.CHAT_ALLY_FORMAT.getString();
				cFormat = org.apache.commons.lang.StringUtils.replace(cFormat, "{TAG}", tagString);
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
					if(nPlayerLoop.equals(nPlayer) || nPlayerLoop.getSpyMode() || (nPlayerLoop.hasGuild() && nPlayerLoop.getGuild().isAlly(guild))) {
						nPlayerLoop.getPlayer().sendMessage(cFormat + msg);
					}
				}

				event.setCancelled(true);
			}
		}
		else if(isGuildPrefix || nPlayer.getChatMode() == ChatMode.GUILD) { //guild chat
			if(Config.CHAT_GUILD_ENABLED.getBoolean()) {
				if(!Config.CHAT_GUILD_LEADERPREFIX.getBoolean()) {
					rank = "";
				}

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

				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCommandExecute(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().substring(1, event.getMessage().length());

		if(cmd.contains(" ")) {
			String[] split = org.apache.commons.lang.StringUtils.split(cmd, ' ');
			cmd = split[0];
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
		if(!nPlayer.getBypass() && Config.REGION_BLOCKEDCMDS.getStringList().contains(cmd.toLowerCase())) {
			NovaRegion region = plugin.getRegionManager().getRegion(event.getPlayer().getLocation());

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
