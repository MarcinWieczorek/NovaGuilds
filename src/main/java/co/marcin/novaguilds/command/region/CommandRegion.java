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

package co.marcin.novaguilds.command.region;

import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.manager.PlayerManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandRegion implements Executor {
	private static final Command command = Command.REGION_ACCESS;

	public static final Map<String, Command> commandsMap = new HashMap<String, Command>() {{
		put("buy", Command.REGION_BUY);
		put("create", Command.REGION_BUY);

		put("delete", Command.REGION_DELETE);
		put("del", Command.REGION_DELETE);
	}};

	public CommandRegion() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length > 0) {
			Command subCommand = commandsMap.get(args[0].toLowerCase());

			if(subCommand == null) {
				Message.CHAT_UNKNOWNCMD.send(sender);
				return;
			}

			subCommand.execute(sender, null);
		}
		else {
			if(PlayerManager.getPlayer(sender).isLeader()) {
				Message.CHAT_COMMANDS_REGION_HEADER.send(sender);
				Message.CHAT_COMMANDS_REGION_ITEMS.send(sender);
			}
		}
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
