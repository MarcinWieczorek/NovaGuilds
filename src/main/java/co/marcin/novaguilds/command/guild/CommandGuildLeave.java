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


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuildLeave implements CommandExecutor, Executor {
	private final Command command = Command.GUILD_LEAVE;
	
	public CommandGuildLeave() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
		
		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(nPlayer.isLeader()) {
			Message.CHAT_GUILD_LEAVE_ISLEADER.send(sender);
			return;
		}

		guild.removePlayer(nPlayer);
		nPlayer.cancelToolProgress();

		if(nPlayer.isOnline()) {
			guild.hideVaultHologram(nPlayer.getPlayer());
		}

		Message.CHAT_GUILD_LEAVE_LEFT.send(sender);

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PLAYER, sender.getName());
		vars.put(VarKey.GUILDNAME, guild.getName());
		Message.BROADCAST_GUILD_LEFT.vars(vars).broadcast();

		TagUtils.refresh();
		TabUtils.refresh();
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
