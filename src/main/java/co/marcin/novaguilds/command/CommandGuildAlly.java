package co.marcin.novaguilds.command;

import java.util.HashMap;

import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;

public class CommandGuildAlly implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildAlly(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		
		if(!sender.hasPermission("novaguilds.guild.ally")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length != 1) {
			Message.CHAT_GUILD_ENTERNAME.send(sender);
			return true;
		}

		String allyname = args[0];

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();
		NovaGuild allyGuild = plugin.getGuildManager().getGuildFind(allyname);

		if(allyGuild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return true;
		}

		if(allyGuild.equals(guild)) {
			Message.CHAT_GUILD_ALLY_SAMENAME.send(sender);
			return true;
		}

		if(!guild.isLeader(sender)) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());
		vars.put("ALLYNAME", allyGuild.getName());

		if(!guild.isAlly(allyGuild)) {
			if(guild.isWarWith(allyGuild)) {
				Message.CHAT_GUILD_ALLY_WAR.vars(vars).send(sender);
				return true;
			}

			if(guild.isInvitedToAlly(allyGuild)) { //Accepting
				allyGuild.addAlly(guild);
				guild.addAlly(allyGuild);
				guild.removeAllyInvitation(allyGuild);
				plugin.getMessageManager().broadcastMessage("broadcast.guild.allied", vars);

				Message.CHAT_GUILD_ALLY_ACCEPTED.vars(vars).send(sender);

				//tags
				plugin.tagUtils.refreshAll();
			}
			else { //Inviting
				if(!allyGuild.isInvitedToAlly(guild)) {
					allyGuild.addAllyInvitation(guild);
					Message.CHAT_GUILD_ALLY_INVITED.vars(vars).send(sender);
					Message.CHAT_GUILD_ALLY_NOTIFYGUILD.vars(vars).broadcast(allyGuild);
				}
				else { //cancel inv
					allyGuild.removeAllyInvitation(guild);

					Message.CHAT_GUILD_ALLY_CANCELED.vars(vars).send(sender);
					Message.CHAT_GUILD_ALLY_NOTIFYGUILDCANCELED.vars(vars).broadcast(allyGuild);
				}
			}
		}
		else { //UN-ALLY
			guild.removeAlly(allyGuild);
			allyGuild.removeAlly(guild);

			plugin.getMessageManager().broadcastMessage("broadcast.guild.endally",vars);

			plugin.tagUtils.refreshAll();
		}
		
		return true;
	}
}
