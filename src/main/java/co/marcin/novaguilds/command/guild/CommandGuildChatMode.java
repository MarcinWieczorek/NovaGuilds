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
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

public class CommandGuildChatMode extends AbstractCommandExecutor {
	private static final Command command = Command.GUILD_CHATMODE;

	public CommandGuildChatMode() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		final NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

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

		Message.CHAT_GUILD_CHATMODE_SUCCESS.setVar(VarKey.MODE, Message.getChatModeName(chatMode).get()).send(sender);
		TabUtils.refresh(nPlayer);
	}
}
