package co.marcin.NovaGuilds.command;

import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.utils.StringUtils;

public class CommandGuildList implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandGuildList(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { //TODO
		if(sender.hasPermission("NovaGuilds.admin.guild.list")) {
			for(Entry<String, NovaGuild> e : plugin.getGuildManager().getGuilds()) {
				NovaGuild guild = e.getValue();
				sender.sendMessage(StringUtils.fixColors(guild.getName()));
		    }
			
			sender.sendMessage("--------------------------");
			
			for(Entry<String, NovaPlayer> e : plugin.getPlayerManager().getPlayers()) {
				NovaPlayer player = e.getValue();
				String guildname;
				
				if(player.hasGuild()) guildname = player.getGuild().getName();
				else guildname = plugin.getMessages().getString("chat.noguild");
				
				sender.sendMessage(StringUtils.fixColors(player.getName() + " - " + guildname));
		    }
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		
		return true;
	}
}