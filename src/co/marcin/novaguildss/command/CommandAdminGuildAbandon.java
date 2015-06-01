package co.marcin.novaguildss.command;

import java.util.HashMap;

import co.marcin.novaguildss.event.GuildRemoveEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguildss.basic.NovaGuild;
import co.marcin.novaguildss.NovaGuilds;

public class CommandAdminGuildAbandon implements CommandExecutor {
	private static NovaGuilds plugin;
	private final NovaGuild guild;
	
	public CommandAdminGuildAbandon(NovaGuilds novaGuilds, NovaGuild guild) {
		plugin = novaGuilds;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.abandon")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(guild != null) {
			//fire event
			GuildRemoveEvent guildRemoveEvent = new GuildRemoveEvent(guild);
			plugin.getServer().getPluginManager().callEvent(guildRemoveEvent);

			//if event is not cancelled
			if(!guildRemoveEvent.isCancelled()) {
				//delete guild
				plugin.getGuildManager().deleteGuild(guild);

				HashMap<String, String> vars = new HashMap<>();
				vars.put("PLAYERNAME", sender.getName());
				vars.put("GUILDNAME", guild.getName());
				plugin.broadcastMessage("broadcast.admin.guild.abandon", vars);
			}
		}
		else {
			plugin.sendPrefixMessage(sender,"chat.guild.couldnotfind");
		}
		return true;
	}
	
}
