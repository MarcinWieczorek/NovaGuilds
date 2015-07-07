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
		if(!sender.hasPermission("novaguilds.admin.guild.setpoints")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length != 1) { //no new name
			plugin.getMessageManager().sendUsageMessage(sender, "nga.guild.setpoints");
			return true;
		}

		String points = args[0];

		if(!NumberUtils.isNumeric(points)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		int pointsInteger = Integer.parseInt(points);

		if(pointsInteger < 0) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.basic.negativenumber");
			return true;
		}

		guild.setPoints(pointsInteger);

		plugin.getMessageManager().sendMessagesMsg(sender, "chat.admin.guild.setpoints");
		return true;
	}
}
