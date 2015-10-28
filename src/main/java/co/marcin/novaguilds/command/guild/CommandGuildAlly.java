/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

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
				Message.BROADCAST_GUILD_ALLIED.vars(vars).broadcast();

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

			Message.BROADCAST_GUILD_ENDALLY.vars(vars).broadcast();

			plugin.tagUtils.refreshAll();
		}
	}
}
