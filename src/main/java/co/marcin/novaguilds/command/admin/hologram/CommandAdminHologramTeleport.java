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

package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminHologramTeleport implements Executor.ReversedAdminHologram {
	private final Command command = Command.ADMIN_HOLOGRAM_TELEPORT;
	private NovaHologram hologram;

	public CommandAdminHologramTeleport() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = args.length == 0 ? (Player) sender : Bukkit.getPlayer(args[0]);

		if(player == null) {
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}

		player.teleport(hologram.getLocation());

		Map<String, String> vars = new HashMap<>();
		vars.put("PLAYERNAME", player.getName());
		vars.put("NAME", hologram.getName());

		if(sender.equals(player)) {
			Message.CHAT_ADMIN_HOLOGRAM_TELEPORT_SELF.vars(vars).send(sender);
		}
		else {
			Message.CHAT_ADMIN_HOLOGRAM_TELEPORT_OTHER.vars(vars).send(sender);
			Message.CHAT_ADMIN_GUILD_TELEPORTED_SELF.vars(vars).send(player);
		}
	}

	@Override
	public void hologram(NovaHologram hologram) {
		this.hologram = hologram;
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
