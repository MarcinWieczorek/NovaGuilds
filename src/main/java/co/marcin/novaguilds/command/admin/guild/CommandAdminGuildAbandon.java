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

package co.marcin.novaguilds.command.admin.guild;


import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.AbandonCause;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.event.GuildAbandonEvent;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildAbandon extends AbstractCommandExecutor.ReversedAdminGuild {
	private static final Command command = Command.ADMIN_GUILD_ABANDON;

	public CommandAdminGuildAbandon() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		//fire event
		GuildAbandonEvent guildAbandonEvent = new GuildAbandonEvent(guild, AbandonCause.ADMIN);
		plugin.getServer().getPluginManager().callEvent(guildAbandonEvent);

		//if event is not cancelled
		if(!guildAbandonEvent.isCancelled()) {
			plugin.getGuildManager().delete(guild);

			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.PLAYERNAME, sender.getName());
			vars.put(VarKey.GUILDNAME, guild.getName());
			Message.BROADCAST_ADMIN_GUILD_ABANDON.vars(vars).broadcast();
		}

		TagUtils.refresh(guild);
		TabUtils.refresh(guild);
	}
}
