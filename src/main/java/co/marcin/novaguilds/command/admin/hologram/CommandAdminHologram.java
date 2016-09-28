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

package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAdminHologram extends AbstractCommandExecutor {
	private static final List<CommandWrapper> noHologramCommands = new ArrayList<>();
	public static final Map<String, CommandWrapper> commandsMap = new HashMap<>();

	public CommandAdminHologram() {

		commandsMap.put("list",         Command.ADMIN_HOLOGRAM_LIST);
		commandsMap.put("ls",           Command.ADMIN_HOLOGRAM_LIST);
		commandsMap.put("tp",           Command.ADMIN_HOLOGRAM_TELEPORT);
		commandsMap.put("teleport",     Command.ADMIN_HOLOGRAM_TELEPORT);
//		commandsMap.put("add",          Command.ADMIN_HOLOGRAM_ADD);
		commandsMap.put("addtop",       Command.ADMIN_HOLOGRAM_ADDTOP);
		commandsMap.put("del",          Command.ADMIN_HOLOGRAM_DELETE);
		commandsMap.put("delete",       Command.ADMIN_HOLOGRAM_DELETE);
		commandsMap.put("tphere",       Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
		commandsMap.put("teleporthere", Command.ADMIN_HOLOGRAM_TELEPORT_HERE);
		commandsMap.put("movehere",     Command.ADMIN_HOLOGRAM_TELEPORT_HERE);

		noHologramCommands.add(Command.ADMIN_HOLOGRAM_LIST);
//		noHologramCommands.add(Command.ADMIN_HOLOGRAM_ADD);
		noHologramCommands.add(Command.ADMIN_HOLOGRAM_ADDTOP);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(!Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_DISABLED.send(sender);
			return;
		}

		boolean isNoHologramCommand = args.length > 0 && noHologramCommands.contains(commandsMap.get(args[0]));

		if(args.length == 0 || (args.length < 2 && !isNoHologramCommand)) {
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_ITEMS.send(sender);
			return;
		}

		CommandWrapper subCommand = commandsMap.get(args[isNoHologramCommand || args.length == 1 ? 0 : 1].toLowerCase());

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		if(!noHologramCommands.contains(subCommand) && (args.length > 1 || !isNoHologramCommand)) {
			NovaHologram hologram = plugin.getHologramManager().getHologram(args[0]);

			if(hologram == null) {
				Message.CHAT_ADMIN_HOLOGRAM_NOTFOUND.send(sender);
				return;
			}

			subCommand.executorVariable(hologram);
		}

		subCommand.execute(sender, StringUtils.parseArgs(args, isNoHologramCommand ? 1 : 2));
	}
}
