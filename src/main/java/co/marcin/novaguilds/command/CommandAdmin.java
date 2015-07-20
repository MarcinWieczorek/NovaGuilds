package co.marcin.novaguilds.command;

import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.StringUtils;

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
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.unknowncmd");
					break;
			}
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.commands.admin.main.header");
			plugin.getMessageManager().sendMessagesList(sender,"chat.commands.admin.main.items",null,false);
		}
		
		return true;
	}

}
