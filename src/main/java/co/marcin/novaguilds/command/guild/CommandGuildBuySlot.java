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

import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildBuySlot extends AbstractCommandExecutor {
	private static final Command command = Command.GUILD_BUYSLOT;

	public CommandGuildBuySlot() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

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

		NovaGroup group = GroupManager.getGroup(sender);
		double money = group.getDouble(NovaGroup.Key.BUY_SLOT_MONEY);
		List<ItemStack> items = group.getItemStackList(NovaGroup.Key.BUY_SLOT_ITEMS);

		if(money > 0 && !nPlayer.hasMoney(money)) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		if(!items.isEmpty()) {
			List<ItemStack> missing = InventoryUtils.getMissingItems(nPlayer.getPlayer().getInventory(), items);

			if(!missing.isEmpty()) {
				Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
				sender.sendMessage(StringUtils.getItemList(missing));
				return;
			}
		}

		nPlayer.getGuild().addSlot();
		Message.CHAT_GUILD_BUY_SLOT_SUCCESS.send(sender);
		TabUtils.refresh(nPlayer.getGuild());
	}
}
