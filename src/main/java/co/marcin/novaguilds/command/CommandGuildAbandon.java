package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildAbandon implements CommandExecutor {
	private static NovaGuilds plugin;
	
	public CommandGuildAbandon(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			LoggerUtils.info("Consoles cant have guilds!");
			return true;
		}
		
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		if(nPlayer.hasGuild()) {
			NovaGuild guild = nPlayer.getGuild();
			
			if(nPlayer.isLeader()) { //All passed
				//fire event
				GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.PLAYER);
				plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

				//if event is not cancelled
				if(!guildAbandonEvent.isCancelled()) {
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
