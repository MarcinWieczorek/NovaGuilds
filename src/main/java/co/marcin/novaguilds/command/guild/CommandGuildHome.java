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

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class CommandGuildHome implements Executor {
	private final Commands command;

	public CommandGuildHome(Commands command) {
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

		Player player = (Player)sender;

		if(args.length>0 && args[0].equalsIgnoreCase("set")) {
			if(!Permission.NOVAGUILDS_GUILD_HOME_SET.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			if(!nPlayer.isLeader()) {
				Message.CHAT_GUILD_NOTLEADER.send(sender);
				return;
			}

			NovaRegion rgatloc = plugin.getRegionManager().getRegion(player.getLocation());

			if(rgatloc==null && nPlayer.getGuild().hasRegion()) {
				Message.CHAT_GUILD_SETHOME_OUTSIDEREGION.send(sender);
				return;
			}

			if(rgatloc != null && !rgatloc.getGuild().isMember(nPlayer)) {
				Message.CHAT_GUILD_SETHOME_OVERLAPS.send(sender);
				return;
			}

			nPlayer.getGuild().setSpawnPoint(player.getLocation());
			Message.CHAT_GUILD_SETHOME_SUCCESS.send(sender);
		}
		else {
			//items
			List<ItemStack> homeItems = plugin.getGroupManager().getGroup(sender).getGuildHomeItems();

			if(!homeItems.isEmpty()) {
				List<ItemStack> missingItems = InventoryUtils.getMissingItems(player.getInventory(), homeItems);

				if(!missingItems.isEmpty()) {
					//TODO: list missing items and test messages/make other msgs
					Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
					sender.sendMessage(StringUtils.getItemList(missingItems));

					return;
				}
			}

			//money
			double homeMoney = plugin.getGroupManager().getGroup(sender).getGuildHomeMoney();
			if(homeMoney > 0) {
//				if(plugin.econ.getBalance((Player)sender) < homeMoney) { //1.8
				if(!nPlayer.hasMoney(homeMoney)) { //1.7
					//TODO not enought money
					HashMap<String, String> vars = new HashMap<>();
					vars.put("REQUIREDMONEY", String.valueOf(homeMoney));
					Message.CHAT_GUILD_NOTENOUGHMONEY.vars(vars).send(sender);
					return;
				}
			}

			nPlayer.takeMoney(homeMoney);
			InventoryUtils.removeItems(player, homeItems);
			plugin.getGuildManager().delayedTeleport(player, nPlayer.getGuild().getSpawnPoint(), Message.CHAT_GUILD_HOME);
		}
	}
}
