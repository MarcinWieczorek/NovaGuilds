package co.marcin.novaguilds.command;

import java.util.HashMap;

import co.marcin.novaguilds.event.GuildRemoveEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;

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
				guildRemoveEvent.setCause(GuildRemoveEvent.AbandonCause.PLAYER);
				plugin.getServer().getPluginManager().callEvent(guildRemoveEvent);

				//if event is not cancelled
				if(!guildRemoveEvent.isCancelled()) {
					plugin.getGuildManager().deleteGuild(guild);

					plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.abandoned");

					HashMap<String, String> vars = new HashMap<>();
					vars.put("PLAYER", sender.getName());
					vars.put("GUILDNAME", guild.getName());
					plugin.getMessageManager().broadcastMessage("broadcast.guild.abandoned", vars);
					plugin.tagUtils.refreshAll();
				}
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notleader");
			}
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
		}
		return true;
	}
	
}
