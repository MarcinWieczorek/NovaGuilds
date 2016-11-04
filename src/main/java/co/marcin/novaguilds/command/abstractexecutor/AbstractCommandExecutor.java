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

package co.marcin.novaguilds.command.abstractexecutor;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.CommandExecutor;
import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.enums.Command;

import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractCommandExecutor implements CommandExecutor {
	protected final NovaGuilds plugin = NovaGuilds.getInstance();
	public final Map<String, CommandWrapper> commandsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	@Override
	public final CommandWrapper getCommand() {
		return Command.getCommand(this);
	}

	@Override
	public final Map<String, CommandWrapper> getCommandsMap() {
		return commandsMap;
	}

	protected CommandWrapper getSubCommand(String[] args) {
		int subCommandArg = 0;

		if(args.length >= 2
				&& getCommandsMap().get(args[1]) != null
				&& getCommandsMap().get(args[1]).isReversed()) {
			subCommandArg = 1;
		}

		return getCommandsMap().get(args[subCommandArg]);
	}

	public static abstract class Reversed<T> extends AbstractCommandExecutor implements CommandExecutor.Reversed<T> {
		protected T parameter;

		@Override
		public final void set(T parameter) {
			this.parameter = parameter;
		}

		@Override
		public final T getParameter() {
			return parameter;
		}
	}
}
