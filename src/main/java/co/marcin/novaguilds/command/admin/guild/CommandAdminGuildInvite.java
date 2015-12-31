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

package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildInvite implements Executor.ReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command = Commands.ADMIN_GUILD_INVITE;

	public CommandAdminGuildInvite() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}
		
		if(args.length == 0) { //no player name
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return;
		}
		
		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playername);
		
		if(nPlayer == null) { //noplayer
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}
			
		if(nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASGUILD.send(sender);
			return;
		}
		
		if(nPlayer.isInvitedTo(guild)) {
			Message.CHAT_PLAYER_ALREADYINVITED.send(sender);
			return;
		}
		
		//all passed
		nPlayer.addInvitation(guild);
		Message.CHAT_PLAYER_INVITE_INVITED.send(sender);
		
		if(nPlayer.getPlayer() != null) {
			Map<String, String> vars = new HashMap<>();
			vars.put("GUILDNAME",guild.getName());
			Message.CHAT_PLAYER_INVITE_NOTIFY.vars(vars).send(sender);
		}
	}
}