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

package co.marcin.novaguilds.command.admin.region;


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAdminRegion extends AbstractCommandExecutor {
	private static final Command command = Command.ADMIN_REGION_ACCESS;

	public static final Map<String, Command> commandsMap = new HashMap<String, Command>() {{
		put("bypass", Command.ADMIN_REGION_BYPASS);
		put("bp", Command.ADMIN_REGION_BYPASS);

		put("spectate", Command.ADMIN_REGION_SPECTATE);

		put("delete", Command.ADMIN_REGION_DELETE);
		put("del", Command.ADMIN_REGION_DELETE);

		put("list", Command.ADMIN_REGION_LIST);

		put("teleport", Command.ADMIN_REGION_TELEPORT);
		put("tp", Command.ADMIN_REGION_TELEPORT);
	}};

	private static final List<Command> noGuildCommands = Arrays.asList(
			Command.ADMIN_REGION_BYPASS,
			Command.ADMIN_REGION_LIST,
			Command.ADMIN_REGION_SPECTATE
	);

	public CommandAdminRegion() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			Message.CHAT_COMMANDS_ADMIN_REGION_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_REGION_ITEMS.send(sender);
			return;
		}

		String subCmd = args[args.length == 1 || noGuildCommands.contains(commandsMap.get(args[0])) ? 0 : 1];
		Command subCommand = commandsMap.get(subCmd.toLowerCase());

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		if(!noGuildCommands.contains(subCommand)) {
			NovaGuild guild = GuildManager.getGuildFind(args[0]);

			if(guild == null) {
				Message.CHAT_GUILD_COULDNOTFIND.send(sender);
				return;
			}

			if(!guild.hasRegion()) {
				Message.CHAT_GUILD_HASNOREGION.send(sender);
				return;
			}

			subCommand.executorVariable(guild.getRegion());
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, noGuildCommands.contains(subCommand) ? 1 : 2));
	}
}