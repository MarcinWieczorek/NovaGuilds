package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
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
				switch(args[0].toLowerCase()) {
					case "players":
						plugin.getPlayerManager().saveAll();
						plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.save.players");
						LoggerUtils.info("Saved players");
						break;
					case "guilds":
						plugin.getGuildManager().saveAll();
						plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.save.guilds");
						LoggerUtils.info("Saved guilds");
						break;
					case "regions":
						plugin.getRegionManager().saveAll();
						plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.save.regions");
						LoggerUtils.info("Saved regions");
						break;
					default:
						plugin.getMessageManager().sendMessagesMsg(sender,"chat.invalidparam");
						break;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
		}
		return true;
	}
}
