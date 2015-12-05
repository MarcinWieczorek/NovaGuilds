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
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandGuildWar implements Executor {
	private final Commands command = Commands.GUILD_WAR;

	public CommandGuildWar() {
		plugin.getCommandManager().registerExecutor(command, this);
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

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length == 0) { //List wars
			Message.CHAT_GUILD_WAR_LIST_WARSHEADER.send(sender);
			String separator = Message.CHAT_GUILD_WAR_LIST_SEPARATOR.get();
			String guildnameformat = Message.CHAT_GUILD_WAR_LIST_ITEM.get();

			if(!guild.getWars().isEmpty()) {
				String warsstr = StringUtils.join(guild.getWarsNames(), guildnameformat, separator);
				MessageManager.sendPrefixMessage(sender, warsstr);
			}
			else {
				Message.CHAT_GUILD_WAR_LIST_NOWARS.send(sender);
			}

			if(!guild.getNoWarInvitations().isEmpty()) {
				Message.CHAT_GUILD_WAR_LIST_NOWARINVHEADER.send(sender);

				String nowarinvs = StringUtils.join(guild.getNoWarInvitations(), guildnameformat, separator);

				MessageManager.sendPrefixMessage(sender, nowarinvs);
			}

			return;
		}

		//adding wars and no war invs
		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		String guildname = args[0];

		NovaGuild cmdGuild = plugin.getGuildManager().getGuildFind(guildname);

		if(cmdGuild == null) {
			Message.CHAT_GUILD_COULDNOTFIND.send(sender);
			return;
		}

		if(guild.isWarWith(cmdGuild)) { //no war inv
			HashMap<String,String> vars = new HashMap<>();

			if(guild.isNoWarInvited(cmdGuild)) { //accepting no-war
				guild.removeNoWarInvitation(cmdGuild);
				guild.removeWar(cmdGuild);
				cmdGuild.removeWar(guild);

				//broadcast
				vars.put("GUILD1",guild.getName());
				vars.put("GUILD2", cmdGuild.getName());
				Message.BROADCAST_GUILD_NOWAR.vars(vars).broadcast();
			}
			else { //inviting to no-war
				cmdGuild.addNoWarInvitation(guild);
				vars.put("GUILDNAME", cmdGuild.getName());
				Message.CHAT_GUILD_WAR_NOWARINV_SUCCESS.vars(vars).send(sender);

				//notify the guild
				vars.clear();
				vars.put("GUILDNAME",guild.getName());
				Message.CHAT_GUILD_WAR_NOWARINV_NOTIFY.vars(vars).broadcast(cmdGuild);
			}
		}
		else { //new war
			if(guild.getName().equalsIgnoreCase(cmdGuild.getName())) {
				Message.CHAT_GUILD_WAR_YOURGUILDWAR.send(sender);
				return;
			}

			if(guild.isAlly(cmdGuild)) {
				Message.CHAT_GUILD_WAR_ALLY.send(sender);
				return;
			}

			guild.addWar(cmdGuild);
			cmdGuild.addWar(guild);

			//broadcasts
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILD1",guild.getName());
			vars.put("GUILD2", cmdGuild.getName());
			Message.BROADCAST_GUILD_WAR.vars(vars).broadcast();
			plugin.tagUtils.refreshAll();
			plugin.getRegionManager().checkRaidInit(nPlayer.getPlayer());
		}
	}
}
