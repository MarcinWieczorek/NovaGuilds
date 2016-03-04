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
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.enums.Command;

public abstract class AbstractCommandExecutor implements CommandExecutor {
	protected final NovaGuilds plugin = NovaGuilds.getInstance();
	protected Command command;

	protected AbstractCommandExecutor(Command command) {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public final Command getCommand() {
		return command;
	}

	public static abstract class ReversedAdminGuild extends AbstractCommandExecutor implements CommandExecutor.ReversedAdminGuild {
		protected NovaGuild guild;

		protected ReversedAdminGuild(Command command) {
			super(command);
		}

		@Override
		public final void guild(NovaGuild guild) {
			this.guild = guild;
		}
	}

	public static abstract class ReversedAdminRegion extends AbstractCommandExecutor implements CommandExecutor.ReversedAdminRegion {
		protected NovaRegion region;

		protected ReversedAdminRegion(Command command) {
			super(command);
		}

		@Override
		public final void region(NovaRegion region) {
			this.region = region;
		}
	}

	public static abstract class ReversedAdminHologram extends AbstractCommandExecutor implements CommandExecutor.ReversedAdminHologram {
		protected NovaHologram hologram;

		protected ReversedAdminHologram(Command command) {
			super(command);
		}

		@Override
		public final void hologram(NovaHologram hologram) {
			this.hologram = hologram;
		}
	}
}
