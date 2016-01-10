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

package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandAdminGuildInactive implements Executor {
	private final Command command = Command.ADMIN_GUILD_INACTIVE;

	public CommandAdminGuildInactive() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		int page = 1;
		if(args.length == 1) {
			if(NumberUtils.isNumeric(args[0])) {
				page = Integer.parseInt(args[0]);
			}
			else if(args[0].equalsIgnoreCase("update")) {
				if(!Permission.NOVAGUILDS_ADMIN_GUILD_INACTIVE_UPDATE.has(sender)) {
					Message.CHAT_NOPERMISSIONS.send(sender);
					return;
				}

				int count = 0;
				for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
					guild.updateInactiveTime();
					count++;
				}
				Map<String, String> vars = new HashMap<>();
				vars.put("COUNT",count+"");
				Message.CHAT_ADMIN_GUILD_INACTIVE_UPDATED.vars(vars).send(sender);
				return;
			}
			else if(args[0].equalsIgnoreCase("clean")) {
				if(!Permission.NOVAGUILDS_ADMIN_GUILD_INACTIVE_CLEAN.has(sender)) {
					Message.CHAT_NOPERMISSIONS.send(sender);
					return;
				}

				//TODO cleaning guilds
				return;
			}
		}

		//list
		if(page < 1) {
			page = 1;
		}

		int perpage = 10;
		int size = plugin.getGuildManager().getGuilds().size();
		int pages_number = size / perpage;
		if(size % perpage > 0) {
			pages_number++;
		}

		Message.CHAT_ADMIN_GUILD_INACTIVE_LIST_HEADER.send(sender);
		String rowformat = Message.CHAT_ADMIN_GUILD_INACTIVE_LIST_ITEM.get();

		int i = 0;
		boolean display = false;

		if(size > perpage) {
			Map<String, String> vars = new HashMap<>();
			vars.put("PAGE", String.valueOf(page));
			vars.put("NEXT", String.valueOf(page+1));
			vars.put("PAGES", String.valueOf(pages_number));

			if(pages_number > page) {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_HASNEXT.vars(vars).send(sender);
			}
			else {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_NONEXT.vars(vars).send(sender);
			}
		}

		for(NovaGuild guild : plugin.getGuildManager().getMostInactiveGuilds()) {
			if((i + 1 > (page - 1) * perpage || page == 1) && !display) {
				display = true;
				i = 0;
			}

			if(!guild.getOnlinePlayers().isEmpty()) {
				guild.updateInactiveTime();
			}

			if(display) {
				String inactiveString = StringUtils.secondsToString(NumberUtils.systemSeconds() - guild.getInactiveTime(), TimeUnit.SECONDS);

				String agonow = Message.CHAT_ADMIN_GUILD_INACTIVE_LIST_AGO.get();
				if(inactiveString.isEmpty()) {
					agonow = Message.CHAT_ADMIN_GUILD_INACTIVE_LIST_NOW.get();
				}

				Map<String, String> vars = new HashMap<>();
				vars.put("GUILDNAME", guild.getName());
				vars.put("PLAYERNAME", guild.getLeader().getName());
				vars.put("TAG", guild.getTag());
				vars.put("PLAYERSCOUNT", guild.getPlayers().size() + "");
				vars.put("AGONOW",agonow);
				vars.put("INACTIVE", inactiveString);

				String rowmsg = StringUtils.replaceMap(rowformat, vars);
				sender.sendMessage(StringUtils.fixColors(rowmsg));

				if(i + 1 >= perpage) {
					break;
				}
			}

			i++;
		}
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
