package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.NovaGuilds;

public class CommandAdminReload implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminReload(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.reload")) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.reload.reloading");
			
			plugin.saveDefaultConfig();
			plugin.reloadConfig();

			//plugin's vars from config
			plugin.savePeriod = plugin.getConfig().getInt("saveperiod");

			plugin.timeRest = plugin.getConfig().getLong("raid.timerest");
			plugin.distanceFromSpawn = plugin.getConfig().getLong("guild.fromspawn");
			plugin.timeInactive = plugin.getConfig().getLong("raid.timeinactive");

			plugin.getMessageManager().sendMessagesMsg(sender,"chat.reload.config");

			//MySQL
			plugin.sqlp = plugin.getConfig().getString("mysql.prefix");
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
			plugin.loadGroups();
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.reload.groups");

			//all done
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.reload.reloaded");
			
			return true;
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}