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

import co.marcin.novaguilds.api.basic.NovaHologram;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminHologramList implements Executor {
	private final Command command = Command.ADMIN_HOLOGRAM_LIST;

	public CommandAdminHologramList() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Message.CHAT_ADMIN_HOLOGRAM_LIST_HEADER.send(sender);
		Map<VarKey, String> vars = new HashMap<>();
		for(NovaHologram hologram : plugin.getHologramManager().getHolograms()) {
			vars.clear();
			vars.put(VarKey.NAME, hologram.getName());
			vars.put(VarKey.X, String.valueOf(hologram.getLocation().getBlockX()));
			vars.put(VarKey.Y, String.valueOf(hologram.getLocation().getBlockY()));
			vars.put(VarKey.Z, String.valueOf(hologram.getLocation().getBlockZ()));

			Message.CHAT_ADMIN_HOLOGRAM_LIST_ITEM.vars(vars).prefix(false).send(sender);
		}
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
