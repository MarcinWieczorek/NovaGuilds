package co.marcin.NovaGuilds.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandAdminRegion implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandAdminRegion(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length>0) {
			String[] newargs = Utils.parseArgs(args,1);

			if(args[0].equalsIgnoreCase("bypass")) { //togglebypass
				new CommandAdminRegionBypass(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) { //remove region
				new CommandAdminRegionDelete(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("list")) { //list regions
				new CommandAdminRegionList(plugin).onCommand(sender, cmd, label, args);
			}
			else {
				plugin.sendMessagesMsg(sender, "chat.unknowncmd");
			}
		}
		else {
			plugin.sendMessagesMsg(sender, "chat.commands.admin.region.header");
			
			for(String citem : plugin.getMessages().getStringList("chat.commands.admin.region.items")) {
				sender.sendMessage(Utils.fixColors(citem));
			}
		}
		return true;
	}
}