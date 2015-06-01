package co.marcin.novaguildss.command;

import co.marcin.novaguildss.NovaGuilds;
import co.marcin.novaguildss.basic.NovaGuild;
import co.marcin.novaguildss.utils.StringUtils;
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
