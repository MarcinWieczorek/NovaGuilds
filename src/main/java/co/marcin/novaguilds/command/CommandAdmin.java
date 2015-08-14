package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdmin implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdmin(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.access")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length>0) {
			String[] newArgs = StringUtils.parseArgs(args, 1);

			switch(args[0].toLowerCase()) {
				case "guild":
				case "g":
					new CommandAdminGuild(plugin).onCommand(sender, cmd, label, newArgs);
					break;
				case "region":
				case "rg":
					new CommandAdminRegion(plugin).onCommand(sender, cmd, label, newArgs);
					break;
				case "reload":
					new CommandAdminReload(plugin).onCommand(sender, cmd, label, args);
					break;
				case "save":
					new CommandAdminSave(plugin).onCommand(sender, cmd, label, newArgs);
					break;
				default:
					Message.CHAT_UNKNOWNCMD.send(sender);
					break;
			}
		}
		else {
			Message.CHAT_COMMANDS_ADMIN_MAIN_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_MAIN_ITEMS.send(sender);
		}
		
		return true;
	}

}
