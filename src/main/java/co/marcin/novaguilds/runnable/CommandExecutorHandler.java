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

package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.CommandExecutorHandlerState;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommandExecutorHandler implements Runnable {
	private final CommandSender sender;
	private final Command command;
	private final String[] args;
	private CommandExecutorHandlerState state = CommandExecutorHandlerState.WAITING;
	private final Executor executor;
	private final ScheduledFuture scheduledFuture;

	public CommandExecutorHandler(Command command, CommandSender sender, String[] args) {
		this.command = command;
		this.sender = sender;
		this.args = args;

		executor = NovaGuilds.getInstance().getCommandManager().getExecutor(command);
		scheduledFuture = NovaGuilds.getInstance().getWorker().schedule(this, Config.CHAT_CONFIRMTIMEOUT.getSeconds(), TimeUnit.SECONDS);
	}

	public void execute() {
		if(!scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
			executor.execute(sender, args);
		}
	}

	public void cancel() {
		state = CommandExecutorHandlerState.CANCELED;
		scheduledFuture.cancel(false);
		NovaPlayer.get(sender).removeCommandExecutorHandler();
	}

	public void confirm() {
		if(state != CommandExecutorHandlerState.CANCELED) {
			state = CommandExecutorHandlerState.CONFIRMED;
			execute();
		}
	}

	@Override
	public void run() {
		if(state == CommandExecutorHandlerState.WAITING) {
			cancel();
			Message.CHAT_CONFIRM_TIMEOUT.send(sender);
		}
	}

	public Command getCommand() {
		return command;
	}
}
