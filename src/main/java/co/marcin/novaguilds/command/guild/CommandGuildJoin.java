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
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandGuildJoin extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
		List<NovaGuild> invitedTo = nPlayer.getInvitedTo();

		if(nPlayer.hasGuild()) {
			Message.CHAT_CREATEGUILD_HASGUILD.send(sender);
			return;
		}

		if(invitedTo.isEmpty() && args.length != 1) {
			Message.CHAT_PLAYER_INVITE_LIST_NOTHING.send(sender);
			return;
		}

		String guildName;

		//one or more guilds
		if(invitedTo.size() == 1) {
			if(args.length == 0) {
				guildName = invitedTo.get(0).getName();
			}
			else {
				guildName = args[0];
			}
		}
		else {
			if(args.length == 0) {
				Message.CHAT_PLAYER_INVITE_LIST_HEADER.send(sender);

				String invitedListString = "";
				int i = 0;
				for(NovaGuild invitedGuild : invitedTo) {
					String guildRow = Message.CHAT_PLAYER_INVITE_LIST_ITEM.get();
					guildRow = org.apache.commons.lang.StringUtils.replace(guildRow, "{GUILDNAME}", invitedGuild.getName());
					guildRow = org.apache.commons.lang.StringUtils.replace(guildRow, "{TAG}", invitedGuild.getTag());

					invitedListString += guildRow;

					if(i < invitedTo.size() - 1) {
						invitedListString += Message.CHAT_PLAYER_INVITE_LIST_SEPARATOR.get();
					}
					i++;
				}

				sender.sendMessage(StringUtils.fixColors(invitedListString));
				return;
			}
			else {
				guildName = args[0];
			}
		}

		NovaGuild guild = GuildManager.getGuildFind(guildName);

		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return;
		}

		if(!nPlayer.isInvitedTo(guild) && !guild.isOpenInvitation()) {
			Message.CHAT_PLAYER_INVITE_NOTINVITED.send(sender);
			return;
		}

		//items
		List<ItemStack> joinItems = GroupManager.getGroup(sender).getItemStackList(NovaGroup.Key.JOIN_ITEMS);

		if(!joinItems.isEmpty()) {
			List<ItemStack> missingItems = InventoryUtils.getMissingItems(((Player) sender).getInventory(), joinItems);

			if(!missingItems.isEmpty()) {
				Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
				sender.sendMessage(StringUtils.getItemList(missingItems));

				return;
			}
		}

		Map<VarKey, String> vars = new HashMap<>();

		//money
		double joinMoney = GroupManager.getGroup(sender).getDouble(NovaGroup.Key.JOIN_MONEY);
		if(joinMoney > 0 && !nPlayer.hasMoney(joinMoney)) {
			vars.put(VarKey.REQUIREDMONEY, String.valueOf(joinMoney));
			Message.CHAT_GUILD_NOTENOUGHMONEY.vars(vars).send(sender);
			return;
		}

		if(!joinItems.isEmpty()) {
			InventoryUtils.removeItems((Player) sender, joinItems);
		}

		if(joinMoney > 0) {
			nPlayer.takeMoney(joinMoney);
		}

		if(guild.isFull()) {
			Message.CHAT_GUILD_ISFULL.send(sender);
			return;
		}

		guild.addPlayer(nPlayer);
		nPlayer.deleteInvitation(guild);
		TagUtils.refresh();
		TabUtils.refresh();
		Message.CHAT_GUILD_JOINED.send(sender);
		guild.showVaultHologram(nPlayer.getPlayer());

		vars.clear();
		vars.put(VarKey.PLAYER, sender.getName());
		vars.put(VarKey.GUILDNAME, guild.getName());
		Message.BROADCAST_GUILD_JOINED.vars(vars).broadcast();
	}
}
