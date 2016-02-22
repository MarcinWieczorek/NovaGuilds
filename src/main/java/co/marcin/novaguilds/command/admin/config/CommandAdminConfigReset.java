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
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class CommandAdminConfigReset extends AbstractCommandExecutor {
	private static final Command command = Command.ADMIN_CONFIG_RESET;

	public CommandAdminConfigReset() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		try {
			plugin.getConfigManager().backupFile();
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}

		if(plugin.getConfigManager().getConfigFile().delete()) {
			plugin.getConfigManager().reload();
		}
		else {
			Message.CHAT_ERROROCCURED.send(sender);
			return;
		}

		Message.CHAT_ADMIN_CONFIG_RESET.send(sender);
	}
}
