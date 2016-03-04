/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuild extends AbstractCommandExecutor implements CommandExecutor {
	private static final Command command = Command.GUILD_ACCESS;

	public static final Map<String, Command> commandsMap = new HashMap<String, Command>() {{
		put("pay", Command.GUILD_BANK_PAY);
		put("withdraw", Command.GUILD_BANK_WITHDRAW);
		put("leader", Command.GUILD_LEADER);
		put("info", Command.GUILD_INFO);
		put("leave", Command.GUILD_LEAVE);
		put("home", Command.GUILD_HOME);
		put("region", Command.REGION_ACCESS);
		put("rg", Command.REGION_ACCESS);
		put("ally", Command.GUILD_ALLY);
		put("kick", Command.GUILD_KICK);
		put("abandon", Command.GUILD_ABANDON);
		put("invite", Command.GUILD_INVITE);
		put("join", Command.GUILD_JOIN);
		put("create", Command.GUILD_CREATE);
		put("war", Command.GUILD_WAR);
		put("compass", Command.GUILD_COMPASS);
		put("effect", Command.GUILD_EFFECT);
		put("top", Command.GUILD_TOP);
		put("items", Command.GUILD_REQUIREDITEMS);
		put("pvp", Command.GUILD_PVPTOGGLE);
		put("buylife", Command.GUILD_BUYLIFE);
		put("buyslot", Command.GUILD_BUYSLOT);
		put("c", Command.GUILD_CHATMODE);
		put("chat", Command.GUILD_CHATMODE);
		put("chatmode", Command.GUILD_CHATMODE);
		put("openinv", Command.GUILD_OPENINVITATION);
		put("setname", Command.GUILD_SET_NAME);
		put("name", Command.GUILD_SET_NAME);
		put("settag", Command.GUILD_SET_TAG);
		put("tag", Command.GUILD_SET_TAG);
	}};

	public CommandGuild() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length > 0) {
			Command command = commandsMap.get(args[0].toLowerCase());
			String[] newArgs = StringUtils.parseArgs(args, 1);

			if(command == null) {
				Message.CHAT_UNKNOWNCMD.send(sender);
			}
			else {
				command.execute(sender, newArgs);
			}
		}
		else {
			NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
			if(nPlayer.hasGuild()) {
				for(String message : Message.CHAT_COMMANDS_GUILD_HASGUILD.getList()) {
					GuildPermission guildPermission = null;
					if(org.apache.commons.lang.StringUtils.startsWith(message, "{") && org.apache.commons.lang.StringUtils.contains(message, "}")) {
						message = message.substring(1);
						String[] split = org.apache.commons.lang.StringUtils.split(message, '}');
						guildPermission = GuildPermission.fromString(split[0]);

						if(split.length == 2) {
							message = split[1];
						}
						else {
							split[0] = "";
							message = StringUtils.join(split, "}");
						}
					}

					if(guildPermission == null || nPlayer.hasPermission(guildPermission)) {
						MessageManager.sendMessage(sender, message);
					}
				}
			}
			else {
				Message.CHAT_COMMANDS_GUILD_NOGUILD.prefix(false).send(sender);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}
}
