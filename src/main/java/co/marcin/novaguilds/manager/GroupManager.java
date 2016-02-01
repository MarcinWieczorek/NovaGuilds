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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGroup;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<String, NovaGroup> groups = new HashMap<>();

	public GroupManager() {
		load();
		LoggerUtils.info("Enabled");
	}

	public void load() {
		groups.clear();
		Set<String> groupsNames = plugin.getConfig().getConfigurationSection("groups").getKeys(false);
		groupsNames.add("admin");

		for(String groupName : groupsNames) {
			groups.put(groupName, new NovaGroup(plugin, groupName));
		}
	}

	public NovaGroup getGroup(Player player) {
		String groupName = "default";

		if(player == null) {
			return getGroup(groupName);
		}

		if(player.hasPermission("novaguilds.group.admin")) {
			return getGroup("admin");
		}

		for(String name : groups.keySet()) {
			if(player.hasPermission("novaguilds.group." + name) && !name.equalsIgnoreCase("default")) {
				groupName = name;
				break;
			}
		}

		return getGroup(groupName);
	}

	public NovaGroup getGroup(CommandSender sender) {
		if(sender instanceof Player) {
			return getGroup((Player) sender);
		}
		else {
			return getGroup("admin");
		}
	}

	public NovaGroup getGroup(String groupName) {
		if(groups.containsKey(groupName)) {
			return groups.get(groupName);
		}
		return null;
	}
}
