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

import co.marcin.novaguilds.api.basic.CommandWrapper;
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
	public static final Map<String, CommandWrapper> commandsMap = new HashMap<>();
	private static final List<CommandWrapper> noGuildCommands = new ArrayList<>();

	public CommandAdminGuild() {
		commandsMap.put("tp",            Command.ADMIN_GUILD_TELEPORT);
		commandsMap.put("teleport",      Command.ADMIN_GUILD_TELEPORT);
		commandsMap.put("abandon",       Command.ADMIN_GUILD_ABANDON);
		commandsMap.put("setname",       Command.ADMIN_GUILD_SET_NAME);
		commandsMap.put("name",          Command.ADMIN_GUILD_SET_NAME);
		commandsMap.put("settag",        Command.ADMIN_GUILD_SET_TAG);
		commandsMap.put("tag",           Command.ADMIN_GUILD_SET_TAG);
		commandsMap.put("setpoints",     Command.ADMIN_GUILD_SET_POINTS);
		commandsMap.put("points",        Command.ADMIN_GUILD_SET_POINTS);
		commandsMap.put("setslots",      Command.ADMIN_GUILD_SET_SLOTS);
		commandsMap.put("slots",         Command.ADMIN_GUILD_SET_SLOTS);
		commandsMap.put("promote",       Command.ADMIN_GUILD_SET_LEADER);
		commandsMap.put("leader",        Command.ADMIN_GUILD_SET_LEADER);
		commandsMap.put("setleader",     Command.ADMIN_GUILD_SET_LEADER);
		commandsMap.put("invite",        Command.ADMIN_GUILD_INVITE);
		commandsMap.put("pay",           Command.ADMIN_GUILD_BANK_PAY);
		commandsMap.put("withdraw",      Command.ADMIN_GUILD_BANK_WITHDRAW);
		commandsMap.put("timerest",      Command.ADMIN_GUILD_SET_TIMEREST);
		commandsMap.put("liveregentime", Command.ADMIN_GUILD_SET_LIVEREGENERATIONTIME);
		commandsMap.put("lives",         Command.ADMIN_GUILD_SET_LIVES);
		commandsMap.put("purge",         Command.ADMIN_GUILD_PURGE);
		commandsMap.put("list",          Command.ADMIN_GUILD_LIST);
		commandsMap.put("inactive",      Command.ADMIN_GUILD_INACTIVE);
		commandsMap.put("kick",          Command.ADMIN_GUILD_KICK);
		commandsMap.put("resetpoints",   Command.ADMIN_GUILD_RESET_POINTS);

		noGuildCommands.add(Command.ADMIN_GUILD_LIST);
		noGuildCommands.add(Command.ADMIN_GUILD_KICK);
		noGuildCommands.add(Command.ADMIN_GUILD_SET_LEADER);
		noGuildCommands.add(Command.ADMIN_GUILD_PURGE);
		noGuildCommands.add(Command.ADMIN_GUILD_INACTIVE);
		noGuildCommands.add(Command.ADMIN_GUILD_RESET_POINTS);
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
		CommandWrapper subCommand = commandsMap.get(subCmd.toLowerCase());

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
