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

package co.marcin.novaguilds.command.tabcompleter;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.command.admin.CommandAdmin;
import co.marcin.novaguilds.command.admin.config.CommandAdminConfig;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuild;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologram;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TabCompleterAdmin implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		final List<String> list = new ArrayList<>();
		Set<String> keys = null;

		if(args.length > 1) {
			switch(args[0].toLowerCase()) {
				case "g":
				case "guild":
					keys = CommandAdminGuild.commandsMap.keySet();
					break;

				case "rg":
				case "region":
					keys = CommandAdminRegion.commandsMap.keySet();
					break;

				case "h":
				case "hologram":
					keys = CommandAdminHologram.commandsMap.keySet();
					break;

				case "config":
					if(args.length == 3 && (args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("set"))) {
						keys = NovaGuilds.getInstance().getConfigManager().getConfig().getKeys(true);
					}
					else if(args.length == 2) {
						keys = CommandAdminConfig.commandsMap.keySet();
					}
					break;
			}
		}
		else {
			keys = CommandAdmin.commandsMap.keySet();
		}

		if(keys != null) {
			for(String key : keys) {
				if(key.startsWith(args[args.length - 1])) {
					list.add(key);
				}
			}
		}

		return list;
	}
}
