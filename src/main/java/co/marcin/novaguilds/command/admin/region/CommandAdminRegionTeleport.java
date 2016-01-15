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
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminRegionTeleport implements Executor.ReversedAdminRegion {
	private final Command command = Command.ADMIN_REGION_TELEPORT;
	private NovaRegion region;

	public CommandAdminRegionTeleport() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void region(NovaRegion region) {
		this.region = region;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		NovaPlayer nPlayerOther;
		Player player;

		if(args.length > 1) { //other
			if(!Permission.NOVAGUILDS_ADMIN_REGION_TELEPORT_SELF.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			nPlayerOther = plugin.getPlayerManager().getPlayer(args[1]);

			if(nPlayerOther == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			if(!nPlayerOther.isOnline()) {
				Message.CHAT_PLAYER_NOTONLINE.send(sender);
				return;
			}

			player = nPlayerOther.getPlayer();
		}
		else {
			player = (Player) sender;
		}

		Map<String, String> vars = new HashMap<>();
		vars.put("GUILDNAME", region.getGuild().getName());

		Location location = region.getCenter().clone();
		location.setY(location.getWorld().getHighestBlockYAt(location));

		if(!player.equals(sender)) {
			Message.CHAT_ADMIN_REGION_TELEPORT_OTHER.vars(vars).send(sender);
			Message.CHAT_ADMIN_REGION_TELEPORT_NOTIFYOTHER.vars(vars).send(player);
		}
		else {
			Message.CHAT_ADMIN_REGION_TELEPORT_SELF.vars(vars).send(sender);
		}

		player.teleport(location);
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
