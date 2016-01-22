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
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuildChatMode implements Executor {
	private final Command command = Command.GUILD_CHATMODE;
	private static final Map<ChatMode, Message> chatModeMessages = new HashMap<ChatMode, Message>(){{
		put(ChatMode.NORMAL, Message.CHAT_GUILD_CHATMODE_NAMES_NORMAL);
		put(ChatMode.GUILD, Message.CHAT_GUILD_CHATMODE_NAMES_GUILD);
		put(ChatMode.ALLY, Message.CHAT_GUILD_CHATMODE_NAMES_ALLY);
	}};

	public CommandGuildChatMode() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		final NovaPlayer nPlayer = NovaPlayer.get(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		final ChatMode chatMode;
		if(args.length == 0) {
			chatMode = nPlayer.getChatMode().next();
		}
		else {
			chatMode = ChatMode.fromString(args[0]);
		}

		if(chatMode == null) {
			Message.CHAT_GUILD_CHATMODE_INVALID.send(sender);
			return;
		}

		nPlayer.setChatMode(chatMode);

		Map<String, String> vars = new HashMap<>();
		vars.put("MODE", chatModeMessages.get(chatMode).get());
		Message.CHAT_GUILD_CHATMODE_SUCCESS.vars(vars).send(sender);
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
