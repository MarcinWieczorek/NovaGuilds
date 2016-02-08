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

package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminChatSpy implements Executor {
	private final Command command = Command.ADMIN_CHATSPY;

	public CommandAdminChatSpy() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		NovaPlayer nPlayer = NovaPlayer.get(sender);
		NovaPlayer nPlayerChange;

		if(args.length == 1) {
			if(!Permission.NOVAGUILDS_ADMIN_CHATSPY_OTHER.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			nPlayerChange = plugin.getPlayerManager().getPlayer(args[0]);

			if(nPlayerChange == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}
		}
		else {
			nPlayerChange = nPlayer;
		}

		nPlayerChange.setSpyMode(!nPlayerChange.getSpyMode());
		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.MODE, Message.getOnOff(nPlayerChange.getSpyMode()));

		//Notify message
		if(!nPlayer.equals(nPlayerChange)) {
			vars.put(VarKey.PLAYERNAME, nPlayerChange.getName());

			Message.CHAT_ADMIN_SPYMODE_NOTIFY.vars(vars).send(nPlayerChange);
			Message.CHAT_ADMIN_SPYMODE_SUCCESS_OTHER.vars(vars).send(sender);
			return;
		}

		Message.CHAT_ADMIN_SPYMODE_SUCCESS_SELF.vars(vars).send(sender);
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
