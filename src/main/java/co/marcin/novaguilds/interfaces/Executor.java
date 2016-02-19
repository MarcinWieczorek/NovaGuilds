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

package co.marcin.novaguilds.interfaces;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Command;
import org.bukkit.command.CommandSender;

public interface Executor {
	NovaGuilds plugin = NovaGuilds.getInstance();

	void execute(CommandSender sender, String[] args);

	Command getCommand();

	interface ReversedAdminGuild extends Executor {
		void guild(NovaGuild guild);
	}

	interface ReversedAdminRegion extends Executor {
		void region(NovaRegion guild);
	}

	interface ReversedAdminHologram extends Executor {
		void hologram(NovaHologram guild);
	}
}
