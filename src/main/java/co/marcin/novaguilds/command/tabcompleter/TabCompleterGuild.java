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

package co.marcin.novaguilds.command.tabcompleter;

import co.marcin.novaguilds.command.admin.CommandAdmin;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuild;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologram;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegion;
import co.marcin.novaguilds.command.guild.CommandGuild;
import co.marcin.novaguilds.command.region.CommandRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class TabCompleterGuild implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();
		final Set<String> keys = new HashSet<>();

		if(args.length > 1) {
			switch(args[0].toLowerCase()) {
				case "rg":
				case "region":
					keys.addAll(CommandRegion.commandsMap.keySet());
					break;
			}
		}
		else {
			keys.addAll(CommandGuild.commandsMap.keySet());
		}

		for(String key : keys) {
			if(key.startsWith(args[args.length - 1])) {
				list.add(key);
			}
		}

		return list;
	}
}
