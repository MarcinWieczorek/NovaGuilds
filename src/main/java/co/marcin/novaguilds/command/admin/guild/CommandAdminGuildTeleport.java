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
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildTeleport implements Executor.ReversedAdminGuild {
	private NovaGuild guild;
	private final Command command = Command.ADMIN_GUILD_TELEPORT;

	public CommandAdminGuildTeleport() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Location home = guild.getSpawnPoint();

		Player player = (Player)sender;
		boolean other = false;

		Map<String, String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());

		if(args.length == 1) {
			if(!Permission.NOVAGUILDS_ADMIN_GUILD_TELEPORT_OTHER.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			String playerName = args[0];
			NovaPlayer nPlayerOther = plugin.getPlayerManager().getPlayer(playerName);

			if(nPlayerOther == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			if(!nPlayerOther.isOnline()) {
				Message.CHAT_PLAYER_NOTONLINE.send(sender);
				return;
			}

			player = nPlayerOther.getPlayer();
			other = true;
		}

		if(other) {
			vars.put("PLAYERNAME", player.getName());
			Message.CHAT_ADMIN_GUILD_TELEPORTED_OTHER.vars(vars).send(sender);
		}
		else {
			Message.CHAT_ADMIN_GUILD_TELEPORTED_SELF.vars(vars).send(sender);
		}

		player.teleport(home);
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
