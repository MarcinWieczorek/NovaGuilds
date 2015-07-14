package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
					plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.save.players");
					LoggerUtils.info("Saved players");
				}
				else if(args[0].equalsIgnoreCase("guilds")) {
					plugin.getGuildManager().saveAll();
					plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.save.guilds");
					LoggerUtils.info("Saved guilds");
				}
				else if(args[0].equalsIgnoreCase("regions")) {
					plugin.getRegionManager().saveAll();
					plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.save.regions");
					LoggerUtils.info("Saved regions");
				}
				else {
					plugin.getMessageManager().sendMessagesMsg(sender,"chat.invalidparam");
				}
			}
			else { //save all
				plugin.getRegionManager().saveAll();
				plugin.getGuildManager().saveAll();
				plugin.getPlayerManager().saveAll();
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.save.all");
				LoggerUtils.info("Saved all data");
			}
		}
		else {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
		}
		return true;
	}
}
