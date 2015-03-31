package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class CommandGuildAbandon implements CommandExecutor {
	private static NovaGuilds plugin;
	
	public CommandGuildAbandon(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.info("Consoles cant have guilds!");
			return true;
		}
		
		NovaPlayer nplayer = plugin.getPlayerManager().getPlayerByName(sender.getName()); 
		if(nplayer.hasGuild()) {
			NovaGuild guild = nplayer.getGuild();
			
			if(guild.getLeaderName().equalsIgnoreCase(sender.getName())) {
				if(guild.hasRegion()) {
					plugin.getRegionManager().removeRegion(guild.getRegion());
				}
				
				plugin.getGuildManager().deleteGuild(guild);
				plugin.updateTabAll();
				plugin.updateTagPlayerToAll(plugin.senderToPlayer(sender));

				sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.guild.abandoned")));
				HashMap<String,String> vars = new HashMap<String,String>();
				vars.put("PLAYER",sender.getName());
				vars.put("GUILDNAME",guild.getName());
				plugin.broadcastMessage("broadcast.guild.abandoned", vars);
			}
			else {
				sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.guild.notleader")));
			}
		}
		else {
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.guild.notinguild")));
		}
		return true;
	}
	
}
