package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandGuildAbandon implements CommandExecutor {
	private static NovaGuilds plugin;
	private static Commands command = Commands.GUILD_ABANDON;
	
	public CommandGuildAbandon(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		NovaPlayer nPlayer = NovaPlayer.get(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();
			
		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}

		//fire event
		GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.PLAYER);
		plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

		//if event is not cancelled
		if(!guildAbandonEvent.isCancelled()) {
			guild.getLeader().cancelToolProgress();
			plugin.getHologramManager().refreshTopHolograms();

			plugin.getGuildManager().delete(guild);

			Message.CHAT_GUILD_ABANDONED.send(sender);

			HashMap<String, String> vars = new HashMap<>();
			vars.put("PLAYER", sender.getName());
			vars.put("GUILDNAME", guild.getName());
			Message.BROADCAST_GUILD_ABANDONED.vars(vars).broadcast();
			plugin.tagUtils.refreshAll();
		}

		return true;
	}
	
}
