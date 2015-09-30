package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminHologram implements Executor {
	private final Commands command;

	public CommandAdminHologram(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
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

		if(args.length == 0) {
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_ITEMS.send(sender);
		}
		else {
			String[] args1 = StringUtils.parseArgs(args, 1);

			switch(args[0].toLowerCase()) {
				case "list":
					plugin.getCommandManager().getExecutor(Commands.ADMIN_HOLOGRAM_LIST).execute(sender, args1);
					break;
				case "tp":
				case "teleport":
					plugin.getCommandManager().getExecutor(Commands.ADMIN_HOLOGRAM_TELEPORT).execute(sender, args1);
					break;
				case "add":
					plugin.getCommandManager().getExecutor(Commands.ADMIN_HOLOGRAM_ADD).execute(sender, args1);
					break;
				case "addtop":
					plugin.getCommandManager().getExecutor(Commands.ADMIN_HOLOGRAM_ADDTOP).execute(sender, args1);
					break;
				case "del":
				case "delete":
					plugin.getCommandManager().getExecutor(Commands.ADMIN_HOLOGRAM_DELETE).execute(sender, args1);
					break;
			}
		}
	}
}
