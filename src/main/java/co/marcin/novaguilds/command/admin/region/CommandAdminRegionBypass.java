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

package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminRegionBypass implements Executor {
	private final Commands command = Commands.ADMIN_REGION_BYPASS;

	public CommandAdminRegionBypass() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	/*
	* Changing bypass
	* no args - for sender
	* args[0] - for specified player
	* */
	@Override
	public void execute(CommandSender sender, String[] args) {
		HashMap<String,String> vars = new HashMap<>();

		if(args.length==0 || args[0].equalsIgnoreCase(sender.getName())) {
			if(!command.hasPermission(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

			nPlayer.toggleBypass();
			vars.put("BYPASS", Message.getOnOff(nPlayer.getBypass()));
			Message.CHAT_ADMIN_REGION_BYPASS_TOGGLED_SELF.vars(vars).send(sender);
		}
		else { //for other
			if(!Permission.NOVAGUILDS_ADMIN_REGION_BYPASS_OTHER.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(args[0]);

			if(nPlayer == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			nPlayer.toggleBypass();
			vars.put("PLAYER", nPlayer.getName());
			vars.put("BYPASS", Message.getOnOff(nPlayer.getBypass()));

			if(nPlayer.isOnline()) {
				Message.CHAT_ADMIN_REGION_BYPASS_NOTIFYOTHER.vars(vars).send(sender);
			}

			Message.CHAT_ADMIN_REGION_BYPASS_TOGGLED_OTHER.vars(vars).send(sender);
		}
	}
}
