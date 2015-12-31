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

import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAdminHologram implements Executor {
	private final Commands command = Commands.ADMIN_HOLOGRAM_ACCESS;

	private static final List<String> noHologramCommands = new ArrayList<String>() {{
		add("list");
		add("add");
		add("addtop");
	}};

	public static final Map<String, Commands> commandsMap = new HashMap<String, Commands>(){{
		put("list", Commands.ADMIN_HOLOGRAM_LIST);
		put("ls", Commands.ADMIN_HOLOGRAM_LIST);

		put("tp", Commands.ADMIN_HOLOGRAM_TELEPORT);
		put("teleport", Commands.ADMIN_HOLOGRAM_TELEPORT);

		put("add", Commands.ADMIN_HOLOGRAM_ADD);
		put("addtop", Commands.ADMIN_HOLOGRAM_ADDTOP);

		put("del", Commands.ADMIN_HOLOGRAM_DELETE);
		put("delete", Commands.ADMIN_HOLOGRAM_DELETE);

		put("tphere", Commands.ADMIN_HOLOGRAM_TELEPORT_HERE);
		put("teleporthere", Commands.ADMIN_HOLOGRAM_TELEPORT_HERE);
		put("movehere", Commands.ADMIN_HOLOGRAM_TELEPORT_HERE);
	}};

	public CommandAdminHologram() {
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

		if(args.length==0 || (args.length < 2 && !noHologramCommands.contains(args[0]))) {
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_ITEMS.send(sender);
			return;
		}

		Commands commands = commandsMap.get(args[noHologramCommands.contains(args[0]) ? 0 : 1].toLowerCase());

		if(commands == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		Executor executor = plugin.getCommandManager().getExecutor(commands);

		if(executor instanceof Executor.ReversedAdminHologram) {
			NovaHologram hologram = plugin.getHologramManager().getHologram(args[0]);

			if(hologram == null || hologram.isDeleted()) {
				Message.CHAT_ADMIN_HOLOGRAM_NOTFOUND.send(sender);
				return;
			}

			((Executor.ReversedAdminHologram) executor).hologram(hologram);
		}

		executor.execute(sender, StringUtils.parseArgs(args, 2));
	}
}
