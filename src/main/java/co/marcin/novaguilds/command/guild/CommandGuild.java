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

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuild implements CommandExecutor, Executor {
	private final Commands command = Commands.GUILD_ACCESS;

	public static final Map<String, Commands> commandsMap = new HashMap<String, Commands>(){{
		put("pay", Commands.GUILD_BANK_PAY);
		put("withdraw", Commands.GUILD_BANK_WITHDRAW);
		put("leader", Commands.GUILD_LEADER);
		put("info", Commands.GUILD_INFO);
		put("leave", Commands.GUILD_LEAVE);
		put("home", Commands.GUILD_HOME);
		put("buyregion", Commands.REGION_BUY);
		put("region", Commands.REGION_ACCESS);
		put("rg", Commands.REGION_ACCESS);
		put("ally", Commands.GUILD_ALLY);
		put("kick", Commands.GUILD_KICK);
		put("abandon", Commands.GUILD_ABANDON);
		put("invite", Commands.GUILD_INVITE);
		put("join", Commands.GUILD_JOIN);
		put("create", Commands.GUILD_CREATE);
		put("war", Commands.GUILD_WAR);
		put("compass", Commands.GUILD_COMPASS);
		put("effect", Commands.GUILD_EFFECT);
		put("top", Commands.GUILD_TOP);
		put("items", Commands.GUILD_REQUIREDITEMS);
		put("pvp", Commands.GUILD_PVPTOGGLE);
		put("buylife", Commands.GUILD_BUYLIFE);
		put("buyslot", Commands.GUILD_BUYSLOT);
		put("openinv", Commands.GUILD_OPENINVITATION);
	}};

	public CommandGuild() {
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

		if(args.length>0) {
			Commands command = commandsMap.get(args[0].toLowerCase());
			String[] newargs = StringUtils.parseArgs(args, 1);

			if(command == null) {
				Message.CHAT_UNKNOWNCMD.send(sender);
			}
			else {
				command.getExecutor().execute(sender, newargs);
			}
		}
		else {
			NovaPlayer nPlayer = NovaPlayer.get(sender);
			if(nPlayer.hasGuild()) {
				Message.CHAT_COMMANDS_GUILD_HASGUILD.prefix(false).send(sender);

				if(nPlayer.isLeader()) {
					Message.CHAT_COMMANDS_GUILD_LEADER.prefix(false).send(sender);
				}
			}
			else {
				Message.CHAT_COMMANDS_GUILD_NOGUILD.prefix(false).send(sender);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		execute(sender, args);
		return true;
	}
}
