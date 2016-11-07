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

package co.marcin.novaguilds.command.tabcompleter;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabCompleterGuild implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		final List<String> list = new ArrayList<>();
		final Set<String> keys = new HashSet<>();
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(cmd.getName().equalsIgnoreCase("gi")) {
			args = new String[] {
					"info",
					args.length == 0 ? "" : args[0]
			};
		}

		if(args.length > 1) {
			switch(args[0].toLowerCase()) {
				case "rg":
				case "region":
					keys.addAll(Command.REGION_ACCESS.getExecutor().getCommandsMap().keySet());
					break;
				case "c":
				case "chat":
				case "chatmode":
					for(ChatMode chatMode : ChatMode.valuesEnabled()) {
						keys.add(chatMode.name().toLowerCase());
					}
					break;
				case "war":
				case "ally":
					for(NovaGuild guild : NovaGuilds.getInstance().getGuildManager().getGuilds()) {
						if(!nPlayer.hasGuild() || !guild.equals(nPlayer.getGuild())) {
							keys.add(guild.getTag().toLowerCase());
							keys.add(guild.getName().toLowerCase());
						}
					}
					break;
				case "join":
					for(NovaGuild guild : nPlayer.getInvitedTo()) {
						keys.add(guild.getTag().toLowerCase());
						keys.add(guild.getName().toLowerCase());
					}
					break;
				case "info":
					for(NovaGuild guild : NovaGuilds.getInstance().getGuildManager().getGuilds()) {
						keys.add(guild.getTag().toLowerCase());
						keys.add(guild.getName().toLowerCase());
					}

					int limit = 0;
					for(NovaPlayer nPlayerLoop : NovaGuilds.getInstance().getPlayerManager().getPlayers()) {
						if(limit > 100) {
							break;
						}

						if(!nPlayerLoop.getName().startsWith(args[args.length - 1])) {
							continue;
						}

						if(!nPlayerLoop.hasGuild()) {
							continue;
						}

						keys.add(nPlayerLoop.getName());
						limit++;
					}
					break;
				case "leader":
					if(nPlayer.hasGuild()) {
						for(NovaPlayer guildMember : nPlayer.getGuild().getPlayers()) {
							if(!guildMember.isLeader()) {
								keys.add(guildMember.getName());
							}
						}
					}
					break;
				case "kick":
					if(nPlayer.hasGuild()) {
						for(NovaPlayer guildMember : nPlayer.getGuild().getPlayers()) {
							if(!guildMember.isLeader() && !guildMember.equals(nPlayer)) {
								keys.add(guildMember.getName());
							}
						}
					}
					break;
			}
		}
		else {
			keys.addAll(Command.GUILD_ACCESS.getExecutor().getCommandsMap().keySet());
		}

		for(String key : keys) {
			if(key.startsWith(args[args.length - 1])) {
				list.add(key);
			}
		}

		return list;
	}
}
