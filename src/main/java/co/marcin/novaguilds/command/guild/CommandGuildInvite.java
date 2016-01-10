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
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuildInvite implements CommandExecutor, Executor {
	private final Command command = Command.GUILD_INVITE;

	public CommandGuildInvite() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			Message.CHAT_USAGE_GUILD_INVITE.send(sender);
			return;
		}

		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.INVITE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		NovaPlayer invitePlayer = plugin.getPlayerManager().getPlayer(playername);

		if(invitePlayer == null) { //player exists
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}

		if(invitePlayer.hasGuild()) { //if player being invited has no guild
			Message.CHAT_PLAYER_HASGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();
		Map<String, String> vars = new HashMap<>();
		vars.put("GUILDNAME", guild.getName());
		vars.put("PLAYERNAME", invitePlayer.getName());

		if(!invitePlayer.isInvitedTo(guild)) { //invite
			invitePlayer.addInvitation(guild);
			Message.CHAT_PLAYER_INVITE_INVITED.vars(vars).send(sender);

			if(invitePlayer.isOnline()) {
				Message.CHAT_PLAYER_INVITE_NOTIFY.vars(vars).send(invitePlayer.getPlayer());
			}
		}
		else { //cancel invitation
			invitePlayer.deleteInvitation(guild);
			Message.CHAT_PLAYER_INVITE_CANCEL_SUCCESS.vars(vars).send(sender);

			if(invitePlayer.isOnline()) {
				Message.CHAT_PLAYER_INVITE_CANCEL_NOTIFY.vars(vars).send(invitePlayer.getPlayer());
			}
		}
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
