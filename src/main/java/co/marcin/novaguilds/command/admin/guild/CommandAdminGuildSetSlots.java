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
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetSlots extends AbstractCommandExecutor.ReversedAdminGuild {
	private static final Command command = Command.ADMIN_GUILD_SET_SLOTS;

	public CommandAdminGuildSetSlots() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length != 1) {
			Message.CHAT_USAGE_NGA_GUILD_SET_SLOTS.send(sender);
			return;
		}

		if(!NumberUtils.isNumeric(args[0])) {
			Message.CHAT_ENTERINTEGER.send(sender);
			return;
		}

		int slots = Integer.parseInt(args[0]);

		if(slots <= 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		if(slots < guild.getPlayers().size()) {
			Message.CHAT_ADMIN_GUILD_SET_SLOTS_SMALLERTHANPLAYERS.send(sender);
			return;
		}

		guild.setSlots(slots);
		TabUtils.refresh(guild);

		Message.CHAT_ADMIN_GUILD_SET_SLOTS_SUCCESS.send(sender);
	}
}
