package co.marcin.NovaGuilds.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;

public class CommandAdminSave implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandAdminSave(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.save")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("players")) {
					plugin.getPlayerManager().saveAll();
					plugin.sendMessagesMsg(sender,"chat.admin.save.players");
				}
				else if(args[0].equalsIgnoreCase("guilds")) {
					plugin.getGuildManager().saveAll();
					plugin.sendMessagesMsg(sender,"chat.admin.save.guilds");
				}
				else if(args[0].equalsIgnoreCase("regions")) {
					plugin.getRegionManager().saveAll();
					plugin.sendMessagesMsg(sender,"chat.admin.save.regions");
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.invalidparam");
				}
			}
			else { //save all
				plugin.getRegionManager().saveAll();
				plugin.getGuildManager().saveAll();
				plugin.getPlayerManager().saveAll();
				plugin.sendMessagesMsg(sender,"chat.admin.save.all");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}
