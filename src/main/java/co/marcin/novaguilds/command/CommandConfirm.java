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

package co.marcin.novaguilds.command;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.runnable.CommandExecutorHandler;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandConfirm implements Executor, CommandExecutor {
	private static final Command command = Command.CONFIRM;

	public CommandConfirm() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
		CommandExecutorHandler handler = nPlayer.getCommandExecutorHandler();

		if(handler == null) {
			Message.CHAT_CONFIRM_NULLHANDLER.send(sender);
			return;
		}

		handler.confirm();
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
