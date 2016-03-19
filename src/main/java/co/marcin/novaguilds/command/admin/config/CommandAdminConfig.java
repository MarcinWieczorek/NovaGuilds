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

package co.marcin.novaguilds.command.admin.config;

import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminConfig extends AbstractCommandExecutor {
	private static final Command command = Command.ADMIN_CONFIG_ACCESS;

	public CommandAdminConfig() {
		super(command);
	}

	public static final Map<String, Command> commandsMap = new HashMap<String, Command>() {{
		put("get", Command.ADMIN_CONFIG_GET);
		put("reload", Command.ADMIN_CONFIG_RELOAD);
		put("reset", Command.ADMIN_CONFIG_RESET);
		put("save", Command.ADMIN_CONFIG_SAVE);
		put("set", Command.ADMIN_CONFIG_SET);
	}};

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			Message.send(Message.CHAT_USAGE_NGA_CONFIG_ACCESS.getNeighbours(), sender);
			return;
		}

		Command subCommand = commandsMap.get(args[0]);

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, 1));
	}
}
