package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Message;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length != 1) { //no new name
			Message.CHAT_USAGE_NGA_GUILD_SETPOINTS.send(sender);
			return true;
		}

		String points = args[0];

		if(!NumberUtils.isNumeric(points)) {
			Message.CHAT_ENTERINTEGER.send(sender);
			return true;
		}

		int pointsInteger = Integer.parseInt(points);

		if(pointsInteger < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return true;
		}

		guild.setPoints(pointsInteger);

		Message.CHAT_ADMIN_GUILD_SET_POINTS.send(sender);
		return true;
	}
}
