package co.marcin.novaguilds.command;

import java.util.HashMap;

import co.marcin.novaguilds.event.GuildRemoveEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;

public class CommandAdminGuildAbandon implements CommandExecutor {
	private static NovaGuilds plugin;
	private final NovaGuild guild;
	
	public CommandAdminGuildAbandon(NovaGuilds novaGuilds, NovaGuild guild) {
		plugin = novaGuilds;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.abandon")) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.nopermissions");
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
				plugin.getMessageManager().broadcastMessage("broadcast.admin.guild.abandon", vars);
			}
		}
		else {
			plugin.getMessageManager().sendPrefixMessage(sender,"chat.guild.couldnotfind");
		}
		return true;
	}
	
}
