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

package co.marcin.novaguilds.command.admin.config;

import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminConfigSet implements Executor {
	private static final Command command = Command.ADMIN_CONFIG_SET;

	public CommandAdminConfigSet() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length != 2) {
			command.getUsageMessage().send(sender);
			return;
		}

		Config config = Config.fromPath(args[0]);

		if(config == null) {
			Message.CHAT_INVALIDPARAM.send(sender);
			return;
		}

		String valueString = args[1];
		Object value = valueString;

		if(valueString.toLowerCase().equals("true")) {
			value = true;
		}
		else if(valueString.toLowerCase().equals("false")) {
			value = false;
		}
		else if(NumberUtils.isNumeric(valueString)) {
			value = Integer.parseInt(valueString);
		}

		plugin.getConfigManager().set(config, value);
		Message.CHAT_ADMIN_CONFIG_SET.send(sender);
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
