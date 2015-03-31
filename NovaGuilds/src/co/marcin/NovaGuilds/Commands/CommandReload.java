package co.marcin.NovaGuilds.Commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandReload implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandReload(NovaGuilds plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.reload")) {
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.reloading")));
			
			plugin.saveDefaultConfig();
			plugin.config = plugin.getConfig();
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.config")));
			
			
			plugin.sqlp = plugin.config.getString("mysql.prefix");
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.mysql")));
			
			File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
	        if(!messagesFile.exists()) {
	        		plugin.saveResource("messages.yml", false);
	        		plugin.setMessages(YamlConfiguration.loadConfiguration(messagesFile));
	        		
	        		plugin.info("New messages file created");
	        		sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.reload.newmsgfile")));
	        }
	        
	        plugin.setMessages(YamlConfiguration.loadConfiguration(messagesFile));
	        
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
		return false;
	}
}
