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

			if(args[0].equalsIgnoreCase("bypass")) { //togglebypass
				new CommandAdminRegionBypass(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) { //remove region
				new CommandAdminRegionDelete(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("list")) { //list regions
				new CommandAdminRegionList(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("tp")) { //list regions
				new CommandAdminRegionTeleport(plugin).onCommand(sender, cmd, label, newargs);
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.unknowncmd");
			}
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.commands.admin.region.header");
			
			for(String citem : plugin.getMessageManager().getMessages().getStringList("chat.commands.admin.region.items")) {
				sender.sendMessage(StringUtils.fixColors(citem));
			}
		}
		return true;
	}
}