package co.marcin.novaguilds.command;

import java.util.HashMap;

import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;

public class CommandGuildAlly implements Executor {
	private final Commands command;

	public CommandGuildAlly(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(args.length != 1) {
			Message.CHAT_GUILD_ENTERNAME.send(sender);
			return;
		}

		String allyname = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();
		NovaGuild allyGuild = plugin.getGuildManager().getGuildFind(allyname);

		if(allyGuild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return;
		}

		if(allyGuild.equals(guild)) {
			Message.CHAT_GUILD_ALLY_SAMENAME.send(sender);
			return;
		}

		if(!guild.isLeader(sender)) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());
		vars.put("ALLYNAME", allyGuild.getName());

		if(!guild.isAlly(allyGuild)) {
			if(guild.isWarWith(allyGuild)) {
				Message.CHAT_GUILD_ALLY_WAR.vars(vars).send(sender);
				return;
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
	}
}
