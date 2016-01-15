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

package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAdminHologram implements Executor {
	private final Command command = Command.ADMIN_HOLOGRAM_ACCESS;

	private static final List<String> noHologramCommands = new ArrayList<String>() {{
		add("list");
		add("add");
		add("addtop");
	}};

	public static final Map<String, Command> commandsMap = new HashMap<String, Command>(){{
		put("list", Command.ADMIN_HOLOGRAM_LIST);
		put("ls", Command.ADMIN_HOLOGRAM_LIST);

		put("tp", Command.ADMIN_HOLOGRAM_TELEPORT);
		put("teleport", Command.ADMIN_HOLOGRAM_TELEPORT);

		put("add", Command.ADMIN_HOLOGRAM_ADD);
		put("addtop", Command.ADMIN_HOLOGRAM_ADDTOP);

		put("del", Command.ADMIN_HOLOGRAM_DELETE);
		put("delete", Command.ADMIN_HOLOGRAM_DELETE);

		put("tphere", Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
		put("teleporthere", Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
		put("movehere", Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
	}};

	public CommandAdminHologram() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length==0 || (args.length < 2 && !noHologramCommands.contains(args[0]))) {
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_ITEMS.send(sender);
			return;
		}

		Command subCommand = commandsMap.get(args[noHologramCommands.contains(args[0]) ? 0 : 1].toLowerCase());

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, noHologramCommands.contains(args[0]) ? 1 : 2));
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
