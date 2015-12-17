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

package co.marcin.novaguilds.command.region;

import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

public class CommandRegion implements Executor {
	private static final Commands command = Commands.REGION_ACCESS;

	public CommandRegion() {
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

		if(args.length>0) {
			String subCommand = args[0];

			switch(subCommand) {
				case "buy":
				case "create":
					Commands.REGION_BUY.getExecutor().execute(sender, null);
					break;
				case "delete":
				case "del":
					Commands.REGION_DELETE.getExecutor().execute(sender, null);
					break;
				default:
					Message.CHAT_UNKNOWNCMD.send(sender);
					break;
			}
		}
		else {
			if(plugin.getPlayerManager().getPlayer(sender).isLeader()) {
				Message.CHAT_COMMANDS_REGION_HEADER.send(sender);
				Message.CHAT_COMMANDS_REGION_ITEMS.send(sender);
			}
		}
	}
}
