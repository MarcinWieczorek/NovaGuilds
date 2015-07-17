package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildAbandonAll implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminGuildAbandonAll(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.abandon")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(plugin.getGuildManager().getGuilds().isEmpty()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.noguilds");
			return true;
		}

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			//fire event
			GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.ADMIN_ALL);
			plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

			//if event is not cancelled
			if(!guildAbandonEvent.isCancelled()) {
				//delete guild
				plugin.getGuildManager().deleteGuild(guild);

				HashMap<String, String> vars = new HashMap<>();
				vars.put("PLAYERNAME", sender.getName());
				vars.put("GUILDNAME", guild.getName());
				plugin.getMessageManager().broadcastMessage("broadcast.admin.guild.abandon", vars);
			}
		}
		return true;
	}

}
