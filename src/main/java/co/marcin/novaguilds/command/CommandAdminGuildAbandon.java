package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildAbandon implements CommandExecutor {
	private static NovaGuilds plugin;
	private final NovaGuild guild;
	
	public CommandAdminGuildAbandon(NovaGuilds novaGuilds, NovaGuild guild) {
		plugin = novaGuilds;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.abandon")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		//fire event
		GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.ADMIN);
		plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

		//if event is not cancelled
		if(!guildAbandonEvent.isCancelled()) {
			//delete guild
			plugin.getGuildManager().deleteGuild(guild);

			HashMap<String, String> vars = new HashMap<>();
			vars.put("PLAYERNAME", sender.getName());
			vars.put("GUILDNAME", guild.getName());
			Message.BROADCAST_ADMIN_GUILD_ABANDON.vars(vars).broadcast();
		}

		plugin.tagUtils.refreshGuild(guild);
		return true;
	}
	
}
