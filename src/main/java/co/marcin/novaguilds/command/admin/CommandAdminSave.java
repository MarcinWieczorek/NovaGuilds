package co.marcin.novaguilds.command.admin;

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
		if(!sender.hasPermission("novaguilds.admin.save")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length == 1) {
			switch(args[0].toLowerCase()) {
				case "players":
					plugin.getPlayerManager().save();
					Message.CHAT_ADMIN_SAVE_PLAYERS.send(sender);
					LoggerUtils.info("Saved players");
					break;
				case "guilds":
					plugin.getGuildManager().save();
					Message.CHAT_ADMIN_SAVE_GUILDS.send(sender);
					LoggerUtils.info("Saved guilds");
					break;
				case "regions":
					plugin.getRegionManager().save();
					Message.CHAT_ADMIN_SAVE_REGIONS.send(sender);
					LoggerUtils.info("Saved regions");
					break;
				default:
					Message.CHAT_INVALIDPARAM.send(sender);
					break;
			}
		}
		else { //save all
			plugin.getRegionManager().save();
			plugin.getGuildManager().save();
			plugin.getPlayerManager().save();
			Message.CHAT_ADMIN_SAVE_ALL.send(sender);
			LoggerUtils.info("Saved all data");
		}
		return true;
	}
}
