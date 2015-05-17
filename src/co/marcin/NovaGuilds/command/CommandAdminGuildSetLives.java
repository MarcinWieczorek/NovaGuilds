package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetLives implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetLives(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.lives")) {

			return true;
		}

		if(args.length == 0) {
			plugin.sendUsageMessage(sender,"nga.guild.lives");
			return true;
		}

		if(!StringUtils.isNumeric(args[0])) {

			return true;
		}

		int lives = Integer.parseInt(args[0]);
		guild.setLives(lives);
		//TODO message

		return true;
	}
}
