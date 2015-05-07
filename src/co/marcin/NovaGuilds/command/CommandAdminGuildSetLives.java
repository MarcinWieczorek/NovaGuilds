package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuilds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetLives implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminGuildSetLives(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.lives")) {

			return true;
		}

		if(args.length != 2) {
			plugin.sendUsageMessage(sender,"nga.guild.lives");
			return true;
		}

		return true;
	}
}
