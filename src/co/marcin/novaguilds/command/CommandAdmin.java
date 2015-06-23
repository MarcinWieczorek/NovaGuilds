package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.utils.StringUtils;

public class CommandAdmin implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdmin(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String[] newArgs = StringUtils.parseArgs(args, 1);
		if(sender.hasPermission("novaguilds.admin.access")) {
			if(args.length>0) {
				if(args[0].equalsIgnoreCase("guild") || args[0].equalsIgnoreCase("g")) { //guilds
					new CommandAdminGuild(plugin).onCommand(sender, cmd, label, newArgs);
				}
				else if(args[0].equalsIgnoreCase("region") || args[0].equalsIgnoreCase("rg")) { //regions
					new CommandAdminRegion(plugin).onCommand(sender, cmd, label, newArgs);
				}
				else if(args[0].equalsIgnoreCase("reload")) { //reload
					new CommandAdminReload(plugin).onCommand(sender, cmd, label, args);
				}
				else if(args[0].equalsIgnoreCase("save")) { //reload
					new CommandAdminSave(plugin).onCommand(sender, cmd, label, newArgs);
				}
				else {
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.unknowncmd");
				}
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.commands.admin.main.header");
				
				for(String cItem : plugin.getMessageManager().getMessages().getStringList("chat.commands.admin.main.items")) {
					sender.sendMessage(StringUtils.fixColors(cItem));
				}
			}
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.nopermissions");
		}
		
		return true;
	}

}
