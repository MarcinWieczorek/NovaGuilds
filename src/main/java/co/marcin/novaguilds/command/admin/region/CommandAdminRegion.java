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

package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminRegion;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandAdminRegion implements Executor {
	private final Commands command = Commands.ADMIN_REGION_ACCESS;

	public static final HashMap<String, Commands> commandsMap = new HashMap<String, Commands>(){{
		put("bypass", Commands.ADMIN_REGION_BYPASS);
		put("bp", Commands.ADMIN_REGION_BYPASS);

		put("delete", Commands.ADMIN_REGION_DELETE);
		put("del", Commands.ADMIN_REGION_DELETE);

		put("list", Commands.ADMIN_REGION_LIST);

		put("teleport", Commands.ADMIN_REGION_TELEPORT);
		put("tp", Commands.ADMIN_REGION_TELEPORT);
	}};

	private static final List<Commands> noGuildCommands = new ArrayList<Commands>() {{
		add(Commands.ADMIN_REGION_BYPASS);
		add(Commands.ADMIN_REGION_LIST);
	}};

	public CommandAdminRegion() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(args.length == 0) {
			Message.CHAT_COMMANDS_ADMIN_REGION_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_REGION_ITEMS.send(sender);
			return;
		}

		NovaRegion region = null;
		String subCmd = args[args.length == 1 || noGuildCommands.contains(commandsMap.get(args[0])) ? 0: 1];
		Commands subCommand = commandsMap.get(subCmd.toLowerCase());

		if(!noGuildCommands.contains(subCommand)) {
			NovaGuild guild = plugin.getGuildManager().getGuildFind(args[0]);

			if(guild == null) {
				Message.CHAT_GUILD_COULDNOTFIND.send(sender);
				return;
			}

			if(!guild.hasRegion()) {
				Message.CHAT_GUILD_HASNOREGION.send(sender);
				return;
			}

			region = guild.getRegion();
		}

		Executor executor = plugin.getCommandManager().getExecutor(subCommand);

		if(executor == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		int subArgsCut = 1;

		if(executor instanceof ExecutorReversedAdminRegion) {
			((ExecutorReversedAdminRegion) executor).region(region);
			subArgsCut = 2;
		}

		executor.execute(sender, StringUtils.parseArgs(args, subArgsCut));
	}
}