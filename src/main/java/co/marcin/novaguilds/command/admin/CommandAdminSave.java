package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminSave implements Executor {
	private final Commands command;

	public CommandAdminSave(Commands command) {
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

		if(args.length == 1) {
			switch(args[0].toLowerCase()) {
				case "players":
					plugin.getPlayerManager().save();
					Message.CHAT_ADMIN_SAVE_PLAYERS.send(sender);
					LoggerUtils.info("Saved players");
					break;
				case "guilds":
					plugin.getGuildManager().save();
					Message.CHAT_ADMIN_SAVE_GUILDS.send(sender);
					LoggerUtils.info("Saved guilds");
					break;
				case "regions":
					plugin.getRegionManager().save();
					Message.CHAT_ADMIN_SAVE_REGIONS.send(sender);
					LoggerUtils.info("Saved regions");
					break;
				default:
					Message.CHAT_INVALIDPARAM.send(sender);
					break;
			}
		}
		else { //save all
			plugin.getRegionManager().save();
			plugin.getGuildManager().save();
			plugin.getPlayerManager().save();
			Message.CHAT_ADMIN_SAVE_ALL.send(sender);
			LoggerUtils.info("Saved all data");
		}
	}
}
