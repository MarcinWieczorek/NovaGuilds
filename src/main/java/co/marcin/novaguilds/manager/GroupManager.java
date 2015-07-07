package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGroup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class GroupManager {
	private final NovaGuilds plugin;
	private final HashMap<String,NovaGroup> groups = new HashMap<>();

	public GroupManager(NovaGuilds novaguilds) {
		plugin = novaguilds;

		loadGroups();
		plugin.info("Groups loaded");
	}

	public void loadGroups() {
		groups.clear();
		Set<String> groupsNames = plugin.getConfig().getConfigurationSection("guild.create.groups").getKeys(false);
		groupsNames.add("admin");

		for(String groupName : groupsNames) {
			groups.put(groupName, new NovaGroup(plugin, groupName));
		}
	}

	public NovaGroup getGroup(Player player) {
		String groupName = "default";

		if(player == null) {
			plugin.debug("Player is null, return is default group");
			return getGroup(groupName);
		}

		if(player.hasPermission("novaguilds.group.admin")) {
			return getGroup("admin");
		}

		for(String name : groups.keySet()) {
			if(player.hasPermission("novaguilds.group."+name) && !name.equalsIgnoreCase("default")) {
				groupName = name;
				break;
			}
		}

		return getGroup(groupName);
	}

	public NovaGroup getGroup(CommandSender sender) {
		if(sender instanceof Player) {
			return getGroup((Player)sender);
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
