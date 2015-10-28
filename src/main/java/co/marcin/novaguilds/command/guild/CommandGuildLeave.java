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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildLeave implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildLeave(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!Commands.GUILD_LEAVE.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}
		
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		
		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(nPlayer.isLeader()) {
			Message.CHAT_GUILD_LEAVE_ISLEADER.send(sender);
			return true;
		}

		nPlayer.setGuild(null);
		guild.removePlayer(nPlayer);

		if(nPlayer.isOnline()) {
			guild.hideVaultHologram(nPlayer.getPlayer());
		}

		Message.CHAT_GUILD_LEAVE_LEFT.send(sender);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYER",sender.getName());
		vars.put("GUILDNAME",guild.getName());
		Message.BROADCAST_GUILD_LEFT.vars(vars).broadcast();

		plugin.tagUtils.refreshAll();
		
		return true;
	}
}
