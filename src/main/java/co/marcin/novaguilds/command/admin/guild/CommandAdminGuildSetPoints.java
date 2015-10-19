package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetPoints implements Executor, ExecutorReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command;

	public CommandAdminGuildSetPoints(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(args.length != 1) { //no new name
			Message.CHAT_USAGE_NGA_GUILD_SET_POINTS.send(sender);
			return;
		}

		String points = args[0];

		if(!NumberUtils.isNumeric(points)) {
			Message.CHAT_ENTERINTEGER.send(sender);
			return;
		}

		int pointsInteger = Integer.parseInt(points);

		if(pointsInteger < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		guild.setPoints(pointsInteger);

		Message.CHAT_ADMIN_GUILD_SET_POINTS.send(sender);
	}
}
