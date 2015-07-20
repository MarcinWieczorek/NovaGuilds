package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminReload implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminReload(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.reload")) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.reload.reloading");

			//plugin's vars from config
			plugin.getConfigManager().reload();

			plugin.getMessageManager().sendMessagesMsg(sender,"chat.reload.config");

			//MySQL
			//TODO reload mysql
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.reload.mysql");

			//messages
			plugin.getMessageManager().loadMessages();

			plugin.getMessageManager().sendMessagesMsg(sender, "chat.reload.messages");

			//regions
			plugin.getRegionManager().loadRegions();
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.reload.regions");

			//guilds
			plugin.getGuildManager().loadGuilds();
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.reload.guilds");

			//players
			plugin.getPlayerManager().loadPlayers();
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.reload.players");

			//groups
			plugin.getGroupManager().loadGroups();
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.reload.groups");

			LoggerUtils.info("Post checks running");
			plugin.getGuildManager().postCheckGuilds();
			plugin.getRegionManager().postCheckRegions();

			//all done
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.reload.reloaded");
			
			return true;
		}
		else {
			Message.CHAT_NOPERMISSIONS.send(sender);
		}
		return true;
	}
}