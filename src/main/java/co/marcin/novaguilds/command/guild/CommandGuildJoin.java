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
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandGuildJoin implements CommandExecutor, Executor {
	private final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Command command = Command.GUILD_JOIN;
	
	public CommandGuildJoin() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		List<NovaGuild> invitedTo = nPlayer.getInvitedTo();
		
		if(nPlayer.hasGuild()) {
			Message.CHAT_CREATEGUILD_HASGUILD.send(sender);
			return;
		}

		if(invitedTo.isEmpty() && args.length != 1) {
			Message.CHAT_PLAYER_INVITE_LIST_NOTHING.send(sender);
			return;
		}

		String guildname;

		//one or more guilds
		if(invitedTo.size() == 1) {
			if(args.length == 0) {
				guildname = invitedTo.get(0).getName();
			}
			else {
				guildname = args[0];
			}
		}
		else {
			if(args.length == 0) {
				Message.CHAT_PLAYER_INVITE_LIST_HEADER.send(sender);

				String invitedlist = "";
				int i = 0;
				for(NovaGuild invitedGuild : invitedTo) {
					String itemrow = Message.CHAT_PLAYER_INVITE_LIST_ITEM.get();
					itemrow = org.apache.commons.lang.StringUtils.replace(itemrow, "{GUILDNAME}", invitedGuild.getName());
					itemrow = org.apache.commons.lang.StringUtils.replace(itemrow, "{TAG}", invitedGuild.getTag());

					invitedlist += itemrow;

					if(i < invitedTo.size() - 1) {
						invitedlist += Message.CHAT_PLAYER_INVITE_LIST_SEPARATOR.get();
					}
					i++;
				}

				sender.sendMessage(StringUtils.fixColors(invitedlist));
				return;
			}
			else {
				guildname = args[0];
			}
		}

		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);

		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return;
		}

		if(!nPlayer.isInvitedTo(guild) && !guild.isOpenInvitation()) {
			Message.CHAT_PLAYER_INVITE_NOTINVITED.send(sender);
			return;
		}

		//items
		List<ItemStack> joinItems = plugin.getGroupManager().getGroup(sender).getGuildJoinItems();

		if(!joinItems.isEmpty()) {
			List<ItemStack> missingItems = InventoryUtils.getMissingItems(((Player) sender).getInventory(), joinItems);

			if(!missingItems.isEmpty()) {
				//TODO: list missing items and test messages/make other msgs
				Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
				sender.sendMessage(StringUtils.getItemList(missingItems));

				return;
			}
		}

		Map<String, String> vars = new HashMap<>();

		//money
		double joinMoney = plugin.getGroupManager().getGroup(sender).getGuildJoinMoney();
		if(joinMoney > 0) {
			//if(plugin.econ.getBalance((Player) sender) < joinMoney) { //1.8
			if(!nPlayer.hasMoney(joinMoney)) {
				//TODO not enought money msg
				vars.put("{REQUIREDMONEY}", joinMoney + "");
				Message.CHAT_GUILD_NOTENOUGHMONEY.vars(vars).send(sender);
				return;
			}
		}

		if(joinItems.size() > 0) {
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
		TagUtils.refreshAll();
		Message.CHAT_GUILD_JOINED.send(sender);
		guild.showVaultHologram(nPlayer.getPlayer());

		vars.clear();
		vars.put("PLAYER", sender.getName());
		vars.put("GUILDNAME", guild.getName());
		Message.BROADCAST_GUILD_JOINED.vars(vars).broadcast();
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
