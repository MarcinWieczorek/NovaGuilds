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

package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdmin implements CommandExecutor {
	private final NovaGuilds plugin;

	public static final Map<String, Commands> commandsMap = new HashMap<String, Commands>(){{
		put("guild", Commands.ADMIN_GUILD_ACCESS);
		put("g", Commands.ADMIN_GUILD_ACCESS);

		put("region", Commands.ADMIN_REGION_ACCESS);
		put("rg", Commands.ADMIN_REGION_ACCESS);

		put("hologram", Commands.ADMIN_HOLOGRAM_ACCESS);
		put("h", Commands.ADMIN_HOLOGRAM_ACCESS);

		put("reload", Commands.ADMIN_RELOAD);
		put("save", Commands.ADMIN_SAVE);
	}};
	
	public CommandAdmin(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!Commands.GUILD_ACCESS.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length == 0) {
			Message.CHAT_COMMANDS_ADMIN_MAIN_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_MAIN_ITEMS.send(sender);
			return true;
		}

		Commands subCommand = commandsMap.get(args[0]);

		if(subCommand == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return true;
		}

		plugin.getCommandManager().getExecutor(subCommand).execute(sender, StringUtils.parseArgs(args, 1));
		return true;
	}

}
