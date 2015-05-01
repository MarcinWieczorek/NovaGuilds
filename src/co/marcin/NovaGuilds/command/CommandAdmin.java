package co.marcin.NovaGuilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.utils.StringUtils;

public class CommandAdmin implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdmin(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String[] newArgs = StringUtils.parseArgs(args, 1);
		if(sender.hasPermission("NovaGuilds.admin.access")) {
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
					plugin.sendMessagesMsg(sender,"chat.unknowncmd");
				}
			}
			else {
				plugin.sendMessagesMsg(sender, "chat.commands.admin.main.header");
				
				for(String cItem : plugin.getMessages().getStringList("chat.commands.admin.main.items")) {
					sender.sendMessage(StringUtils.fixColors(cItem));
				}
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		
		return true;
	}

}
