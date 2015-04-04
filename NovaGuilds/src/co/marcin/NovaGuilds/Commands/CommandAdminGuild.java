package co.marcin.NovaGuilds.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandAdminGuild implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandAdminGuild(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//TODO permissions
		if(args.length>0) {
			if(args[0].equalsIgnoreCase("list")) { //some command
				new CommandGuildList(plugin).onCommand(sender, cmd, label, Utils.parseArgs(args,1));
			}
			else {
				plugin.sendMessagesMsg(sender, "chat.unknowncmd");
			}
		}
		else {
			plugin.sendMessagesMsg(sender, "chat.commands.admin.guild.header");
			
			for(String citem : plugin.getMessages().getStringList("chat.commands.admin.guild.items")) {
				sender.sendMessage(Utils.fixColors(citem));
			}
		}
		return true;
	}
}
