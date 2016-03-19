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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.enums.Command;
import org.bukkit.command.CommandSender;

public interface CommandExecutor {
	/**
	 * Execute the command
	 *
	 * @param sender sender
	 * @param args arguments
	 * @throws Exception bugs occur sometimes...
	 */
	void execute(CommandSender sender, String[] args) throws Exception;

	/**
	 * Gets the command
	 *
	 * @return the command
	 */
	Command getCommand();

	interface ReversedAdminGuild extends CommandExecutor {
		/**
		 * Sets the parameter
		 *
		 * @param guild the parameter
		 */
		void guild(NovaGuild guild);
	}

	interface ReversedAdminRegion extends CommandExecutor {
		/**
		 * Sets the parameter
		 *
		 * @param region the parameter
		 */
		void region(NovaRegion region);
	}

	interface ReversedAdminHologram extends CommandExecutor {
		/**
		 * Sets the parameter
		 *
		 * @param hologram the parameter
		 */
		void hologram(NovaHologram hologram);
	}
}
