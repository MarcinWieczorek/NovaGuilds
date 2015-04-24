package co.marcin.NovaGuilds.command;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandAdminReload implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminReload(NovaGuilds plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("NovaGuilds.admin.reload")) {
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.reloading")));
			
			plugin.saveDefaultConfig();
			plugin.config = plugin.getConfig();
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.config")));
			
			
			plugin.sqlp = plugin.config.getString("mysql.prefix");
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.mysql")));
			
			File msgFile = new File(plugin.getDataFolder()+"/lang",plugin.lang+".yml");
	        plugin.loadMessagesFile(msgFile);
	        
			plugin.prefix = plugin.getMessages().getString("chat.prefix");
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.messages")));
			
			plugin.getRegionManager().loadRegions();
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.regions")));
			
			plugin.getGuildManager().loadGuilds();
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.guilds")));
			
			plugin.getPlayerManager().loadPlayers();
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.players")));
			
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.reloaded")));
			
			return true;
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}
