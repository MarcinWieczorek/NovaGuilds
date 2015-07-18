package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.StringUtils;

public class CommandAdminRegion implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdminRegion(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length>0) {
			String[] newargs = StringUtils.parseArgs(args, 1);

			switch(args[0].toLowerCase()) {
				case "bypass":
					new CommandAdminRegionBypass(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "delete":
				case "del":
					new CommandAdminRegionDelete(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "list":
					new CommandAdminRegionList(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "tp":
				case "teleport":
					new CommandAdminRegionTeleport(plugin).onCommand(sender, cmd, label, newargs);
					break;
				default:
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.unknowncmd");
					break;
			}
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.commands.admin.region.header");
			plugin.getMessageManager().sendMessagesList(sender,"chat.commands.admin.region.items",null,false);
		}
		return true;
	}
}