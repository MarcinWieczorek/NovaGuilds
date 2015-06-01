package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.NovaGuilds;

public class CommandAdminSave implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdminSave(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.save")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("players")) {
					plugin.getPlayerManager().saveAll();
					plugin.sendMessagesMsg(sender,"chat.admin.save.players");
					plugin.info("Saved players");
				}
				else if(args[0].equalsIgnoreCase("guilds")) {
					plugin.getGuildManager().saveAll();
					plugin.sendMessagesMsg(sender,"chat.admin.save.guilds");
					plugin.info("Saved guilds");
				}
				else if(args[0].equalsIgnoreCase("regions")) {
					plugin.getRegionManager().saveAll();
					plugin.sendMessagesMsg(sender,"chat.admin.save.regions");
					plugin.info("Saved regions");
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
				plugin.info("Saved all data");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}
