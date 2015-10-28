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

import co.marcin.novaguilds.basic.NovaGroup;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.InventoryUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildBuyLife implements Executor {
	private final Commands command;

	public CommandGuildBuyLife(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		NovaGroup group = NovaGroup.get(sender);

		List<ItemStack> items = group.getGuildBuylifeItems();
		double money = group.getGuildBuylifeMoney();

		List<ItemStack> missingItems = InventoryUtils.getMissingItems(nPlayer.getPlayer().getInventory(), items);

		if(items.size() > 0 && missingItems.size() > 0) {
			Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
			return;
		}

		if(money > 0 && !nPlayer.hasMoney(money)) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		InventoryUtils.removeItems(nPlayer.getPlayer(), items);

		nPlayer.getGuild().addLive();

		sender.sendMessage("bought life TODO");
	}
}
