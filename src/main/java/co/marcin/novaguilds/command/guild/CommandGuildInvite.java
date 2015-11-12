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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandGuildInvite implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildInvite(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!Permission.NOVAGUILDS_GUILD_INVITE.has(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length != 1) {
			Message.CHAT_USAGE_GUILD_INVITE.send(sender);
			return true;
		}

		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		if(!nPlayer.isLeader()) { //only leaders can invite
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}

		NovaPlayer invitePlayer = plugin.getPlayerManager().getPlayer(playername);

		if(invitePlayer == null) { //player exists
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return true;
		}

		if(invitePlayer.hasGuild()) { //if player being invited has no guild
			Message.CHAT_PLAYER_HASGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();
		HashMap<String, String> vars = new HashMap<>();
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
		return true;
	}
}