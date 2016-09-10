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

package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAdminGuild extends AbstractCommandExecutor {
	private static final Command command = Command.ADMIN_GUILD_ACCESS;

	public static final Map<String, Command> commandsMap = new HashMap<String, Command>() {{
		put("tp", Command.ADMIN_GUILD_TELEPORT);
		put("teleport", Command.ADMIN_GUILD_TELEPORT);
		put("abandon", Command.ADMIN_GUILD_ABANDON);

		put("setname", Command.ADMIN_GUILD_SET_NAME);
		put("name", Command.ADMIN_GUILD_SET_NAME);

		put("settag", Command.ADMIN_GUILD_SET_TAG);
		put("tag", Command.ADMIN_GUILD_SET_TAG);

		put("setpoints", Command.ADMIN_GUILD_SET_POINTS);
		put("points", Command.ADMIN_GUILD_SET_POINTS);

		put("setslots", Command.ADMIN_GUILD_SET_SLOTS);
		put("slots", Command.ADMIN_GUILD_SET_SLOTS);

		put("promote", Command.ADMIN_GUILD_SET_LEADER);
		put("leader", Command.ADMIN_GUILD_SET_LEADER);
		put("setleader", Command.ADMIN_GUILD_SET_LEADER);

		put("invite", Command.ADMIN_GUILD_INVITE);
		put("pay", Command.ADMIN_GUILD_BANK_PAY);
		put("withdraw", Command.ADMIN_GUILD_BANK_WITHDRAW);
		put("timerest", Command.ADMIN_GUILD_SET_TIMEREST);
		put("liveregentime", Command.ADMIN_GUILD_SET_LIVEREGENERATIONTIME);
		put("lives", Command.ADMIN_GUILD_SET_LIVES);
		put("purge", Command.ADMIN_GUILD_PURGE);
		put("list", Command.ADMIN_GUILD_LIST);
		put("inactive", Command.ADMIN_GUILD_INACTIVE);
		put("kick", Command.ADMIN_GUILD_KICK);
		put("resetpoints", Command.ADMIN_GUILD_RESET_POINTS);
	}};

	private static final List<Command> noGuildCommands = new ArrayList<Command>() {{
		add(Command.ADMIN_GUILD_LIST);
		add(Command.ADMIN_GUILD_KICK);
		add(Command.ADMIN_GUILD_SET_LEADER);
		add(Command.ADMIN_GUILD_PURGE);
		add(Command.ADMIN_GUILD_INACTIVE);
		add(Command.ADMIN_GUILD_RESET_POINTS);
	}};

	public CommandAdminGuild() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		//command list
		if(args.length == 0) {
			Message.CHAT_COMMANDS_ADMIN_GUILD_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_GUILD_ITEMS.send(sender);
			return;
		}

		String subCmd = args[args.length == 1 || noGuildCommands.contains(commandsMap.get(args[0])) ? 0 : 1];
		Command subCommand = commandsMap.get(subCmd.toLowerCase());

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		if(!noGuildCommands.contains(subCommand) && (args.length > 1 || !noGuildCommands.contains(subCommand))) {
			NovaGuild guild = GuildManager.getGuildFind(args[0]);

			if(guild == null) {
				Message.CHAT_GUILD_COULDNOTFIND.send(sender);
				return;
			}

			subCommand.executorVariable(guild);
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, noGuildCommands.contains(subCommand) ? 1 : 2));
	}
}
