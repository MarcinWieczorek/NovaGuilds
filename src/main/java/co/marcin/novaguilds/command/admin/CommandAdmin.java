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

package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdmin extends AbstractCommandExecutor implements CommandExecutor {
	private static final Command command = Command.ADMIN_ACCESS;

	public static final Map<String, Command> commandsMap = new HashMap<String, Command>() {{
		put("guild", Command.ADMIN_GUILD_ACCESS);
		put("g", Command.ADMIN_GUILD_ACCESS);

		put("region", Command.ADMIN_REGION_ACCESS);
		put("rg", Command.ADMIN_REGION_ACCESS);

		put("hologram", Command.ADMIN_HOLOGRAM_ACCESS);
		put("h", Command.ADMIN_HOLOGRAM_ACCESS);

		put("reload", Command.ADMIN_RELOAD);
		put("save", Command.ADMIN_SAVE);

		put("spy", Command.ADMIN_CHATSPY);
		put("chatspy", Command.ADMIN_CHATSPY);

		put("config", Command.ADMIN_CONFIG_ACCESS);
	}};

	public CommandAdmin() {
		super(command);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			Message.CHAT_COMMANDS_ADMIN_MAIN_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_MAIN_ITEMS.send(sender);
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
