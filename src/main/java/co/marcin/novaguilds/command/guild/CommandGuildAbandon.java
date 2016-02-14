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


import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuildAbandon implements CommandExecutor, Executor {
	private static Command command = Command.GUILD_ABANDON;
	
	public CommandGuildAbandon() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		NovaPlayer nPlayer = NovaPlayer.get(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		if(!nPlayer.hasPermission(GuildPermission.ABANDON)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return true;
		}

		command.execute(sender, args);
		return true;
	}
	
	public void execute(CommandSender sender, String args[]) {
		NovaPlayer nPlayer = NovaPlayer.get(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.ABANDON)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		//fire event
		GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.PLAYER);
		plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

		//if event is not cancelled
		if(!guildAbandonEvent.isCancelled()) {
			plugin.getGuildManager().delete(guild);

			Message.CHAT_GUILD_ABANDONED.send(sender);

			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.PLAYER, sender.getName());
			vars.put(VarKey.GUILDNAME, guild.getName());
			Message.BROADCAST_GUILD_ABANDONED.vars(vars).broadcast();
			TagUtils.refresh();
			TabUtils.refresh();
		}
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
