package co.marcin.NovaGuilds.command;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.utils.StringUtils;

public class CommandAdminReload implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminReload(NovaGuilds plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.reload")) {
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.reloading")));
			
			plugin.saveDefaultConfig();

			//plugin's vars from config
			plugin.sqlp = plugin.getConfig().getString("mysql.prefix");
			plugin.savePeriod = plugin.getConfig().getInt("saveperiod");
			plugin.lang = plugin.getConfig().getString("lang");

			plugin.timeRest = plugin.getConfig().getLong("raid.timerest");
			plugin.distanceFromSpawn = plugin.getConfig().getLong("guild.fromspawn");
			plugin.timeInactive = plugin.getConfig().getLong("raid.timeinactive");
			//TODO

			plugin.useVault = plugin.getConfig().getBoolean("usevault");
			plugin.useTabAPI = plugin.getConfig().getBoolean("tabapi.enabled");
			plugin.useTagAPI = plugin.getConfig().getBoolean("tagapi.enabled");
			plugin.useHolographicDisplays = plugin.getConfig().getBoolean("holographicdisplays.enabled");

			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.config")));

			plugin.sqlp = plugin.getConfig().getString("mysql.prefix");
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.mysql")));

			plugin.loadMessages();

			//TODO: check and remove
			File msgFile = new File(plugin.getDataFolder()+"/lang",plugin.lang+".yml");
	        plugin.loadMessagesFile(msgFile);
	        
			plugin.prefix = plugin.getMessages().getString("chat.prefix");
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.messages")));
			
			plugin.getRegionManager().loadRegions();
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.regions")));
			
			plugin.getGuildManager().loadGuilds();
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.guilds")));
			
			plugin.getPlayerManager().loadPlayers();
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.players")));
			
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.reload.reloaded")));
			
			return true;
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}
