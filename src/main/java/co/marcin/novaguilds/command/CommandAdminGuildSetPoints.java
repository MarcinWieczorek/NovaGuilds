package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetPoints implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetPoints(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/*
		* args:
		* 0 - points
		* */

		if(!sender.hasPermission("novaguilds.admin.guild.setpoints")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length == 0) { //no new name
			plugin.getMessageManager().sendUsageMessage(sender, "nga.guild.setpoints");
			return true;
		}

		String points = args[0];

		if(!NumberUtils.isNumeric(points)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		int pointsInteger = Integer.parseInt(points);
		guild.setPoints(pointsInteger);

		plugin.getMessageManager().sendMessagesMsg(sender, "chat.admin.guild.setpoints.success");
		return true;
	}
}
