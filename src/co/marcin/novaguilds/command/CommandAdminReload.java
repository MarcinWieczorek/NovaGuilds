package co.marcin.novaguilds.command;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.NovaGuilds;

public class CommandAdminReload implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminReload(NovaGuilds plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.reload")) {
			plugin.sendMessagesMsg(sender,"chat.reload.reloading");
			
			plugin.saveDefaultConfig();
			plugin.reloadConfig();

			//plugin's vars from config
			plugin.sqlp = plugin.getConfig().getString("mysql.prefix");
			plugin.savePeriod = plugin.getConfig().getInt("saveperiod");
			plugin.lang = plugin.getConfig().getString("lang");

			plugin.timeRest = plugin.getConfig().getLong("raid.timerest");
			plugin.distanceFromSpawn = plugin.getConfig().getLong("guild.fromspawn");
			plugin.timeInactive = plugin.getConfig().getLong("raid.timeinactive");
			//TODO

			plugin.useTabAPI = plugin.getConfig().getBoolean("tabapi.enabled");
			plugin.useTagAPI = plugin.getConfig().getBoolean("tagapi.enabled");
			plugin.useHolographicDisplays = plugin.getConfig().getBoolean("holographicdisplays.enabled");

			plugin.sendMessagesMsg(sender,"chat.reload.config");

			plugin.sqlp = plugin.getConfig().getString("mysql.prefix");
			plugin.sendMessagesMsg(sender, "chat.reload.mysql");

			plugin.loadMessages();

			//TODO: check and remove
			File msgFile = new File(plugin.getDataFolder()+"/lang",plugin.lang+".yml");
	        plugin.loadMessagesFile(msgFile);

			plugin.setPrefix(plugin.getMessages().getString("chat.prefix"));
			plugin.sendPrefixMessage(sender, "chat.reload.messages");

			//regions
			plugin.getRegionManager().loadRegions();
			plugin.sendPrefixMessage(sender, "chat.reload.regions");

			//guilds
			plugin.getGuildManager().loadGuilds();
			plugin.sendPrefixMessage(sender, "chat.reload.guilds");

			//players
			plugin.getPlayerManager().loadPlayers();
			plugin.sendPrefixMessage(sender, "chat.reload.players");

			//all done
			plugin.sendPrefixMessage(sender,"chat.reload.reloaded");
			
			return true;
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}