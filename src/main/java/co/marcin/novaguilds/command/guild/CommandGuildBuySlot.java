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
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildBuySlot implements Executor {
	private final Command command = Command.GUILD_BUYSLOT;

	public CommandGuildBuySlot() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		NovaPlayer nPlayer = NovaPlayer.get(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.hasPermission(GuildPermission.BUYSLOT)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(nPlayer.getGuild().getSlots() >= Config.GUILD_SLOTS_MAX.getInt()) {
			Message.CHAT_GUILD_BUY_SLOT_MAXREACHED.send(sender);
			return;
		}

		NovaGroup group = NovaGroup.get(sender);
		double money = group.getGuildBuySlotMoney();
		List<ItemStack> items = group.getGuildBuySlotItems();

		if(money > 0 && !nPlayer.hasMoney(money)) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		if(items.size() > 0) {
			List<ItemStack> missing = InventoryUtils.getMissingItems(nPlayer.getPlayer().getInventory(), items);

			if(missing.size() > 0) {
				Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
				sender.sendMessage(StringUtils.getItemList(missing));
				return;
			}
		}

		nPlayer.getGuild().addSlot();
		Message.CHAT_GUILD_BUY_SLOT_SUCCESS.send(sender);
		TabUtils.refresh(nPlayer.getGuild());
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
