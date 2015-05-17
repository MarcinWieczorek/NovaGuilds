package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import co.marcin.NovaGuilds.event.GuildRemoveEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.utils.StringUtils;

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
		
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
		if(nPlayer.hasGuild()) {
			NovaGuild guild = nPlayer.getGuild();
			
			if(nPlayer.isLeader()) { //All passed
				//fire event
				GuildRemoveEvent guildRemoveEvent = new GuildRemoveEvent(guild);
				plugin.getServer().getPluginManager().callEvent(guildRemoveEvent);

				//if event is not cancelled
				if(!guildRemoveEvent.isCancelled()) {
					if(guild.hasRegion()) {
						plugin.getRegionManager().removeRegion(guild.getRegion());
					}

					plugin.getGuildManager().deleteGuild(guild);
					plugin.tagUtils.updatePrefix(plugin.senderToPlayer(sender));

					//delete guild from players
					for(NovaPlayer nP : guild.getPlayers()) {
						nP.setGuild(null);
						nP.setHasGuild(false);
					}

					plugin.sendMessagesMsg(sender, "chat.guild.abandoned");

					HashMap<String, String> vars = new HashMap<>();
					vars.put("PLAYER", sender.getName());
					vars.put("GUILDNAME", guild.getName());
					plugin.broadcastMessage("broadcast.guild.abandoned", vars);
				}
			}
			else {
				sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.guild.notleader")));
			}
		}
		else {
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.guild.notinguild")));
		}
		return true;
	}
	
}
