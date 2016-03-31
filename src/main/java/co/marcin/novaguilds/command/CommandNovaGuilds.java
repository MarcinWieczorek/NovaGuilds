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

package co.marcin.novaguilds.command;

import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.VersionUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandNovaGuilds extends AbstractCommandExecutor implements CommandExecutor {
	private static final Command command = Command.NOVAGUILDS;

	public CommandNovaGuilds() {
		super(command);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0) {
			Map<String, String[]> langInfo = new HashMap<>();
			String commit = VersionUtils.getCommit();
			
			langInfo.put("zh-cn", new String[]{
					Message.CHAT_PREFIX.get() + "NovaGuilds 公会插件信息",
					"&2NovaGuilds &6#&c" + VersionUtils.getBuildCurrent() + " &4(&e" + commit + "&4)",
					"&2作者: &6Marcin (CTRL) Wieczorek",
					"&22016 &4波&f兰",
					"&6网址: &bhttp://novaguilds.pl/",
					"&2最新插件构建: &6#&c" + VersionUtils.getBuildLatest()
			});
			
			langInfo.put("en-en", new String[]{
					Message.CHAT_PREFIX.get() + "NovaGuilds Information",
					"&2NovaGuilds &6#&c" + VersionUtils.getBuildCurrent() + " &4(&e" + commit + "&4)",
					"&2Author: &6Marcin (CTRL) Wieczorek",
					"&22016 &4Pol&fand",
					"&bhttp://novaguilds.pl/",
					"&2Latest plugin build: &6#&c" + VersionUtils.getBuildLatest()
			});

			langInfo.put("pl-pl", new String[]{
					Message.CHAT_PREFIX.get() + "NovaGuilds Informacje",
					"&2NovaGuilds &6#&c" + VersionUtils.getBuildCurrent() + " &4(&e" + commit + "&4)",
					"&2Autor: &6Marcin (CTRL) Wieczorek",
					"&22016 &4Pol&fska",
					"&bhttp://novaguilds.pl/",
					"&2Najnowsza wersja pluginu: &6#&c" + VersionUtils.getBuildLatest()
			});

			String[] info = langInfo.get(langInfo.containsKey(Config.LANG_NAME.getString().toLowerCase()) ? Config.LANG_NAME.getString().toLowerCase() : "en-en");

			for(String i : info) {
				sender.sendMessage(StringUtils.fixColors(i));
			}

			return;
		}

		switch(args[0].toLowerCase()) {
			case "tool":
				Command.TOOL_GET.execute(sender, args);
				break;
			case "bank":
				if(!sender.hasPermission("novaguilds.test.bank")) {
					Message.CHAT_NOPERMISSIONS.send(sender);
					return;
				}

				if(sender instanceof Player) {
					NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
					if(nPlayer.hasGuild()) {
						((Player) sender).getInventory().addItem(Config.VAULT_ITEM.getItemStack());
					}
				}
				break;
			case "admin":
				Command.ADMIN_ACCESS.execute(sender, StringUtils.parseArgs(args, 1));
				break;
			case "group":
				NovaGroup group = GroupManager.getGroup(sender);

				if(args.length > 1) {
					group = GroupManager.getGroup(args[1]);
					if(group == null) {
						sender.sendMessage("Invalid group");
						return;
					}
				}

				sender.sendMessage("name = " + group.getName());
				sender.sendMessage("guildCreateMoney = " + group.getGuildCreateMoney());
				sender.sendMessage("guildHomeMoney = " + group.getGuildHomeMoney());
				sender.sendMessage("guildJoinMoney = " + group.getGuildJoinMoney());
				sender.sendMessage("guildCreateItems = " + group.getGuildCreateItems().toString());
				sender.sendMessage("guildCreateSchematic = " + (group.getCreateSchematic() == null ? "no schematic" : group.getCreateSchematic().getName()));
				sender.sendMessage("guildHomeItems = " + group.getGuildHomeItems().toString());
				sender.sendMessage("guildJoinItems = " + group.getGuildJoinItems().toString());
				sender.sendMessage("guildEffectItems = " + group.getGuildEffectItems().toString());
				sender.sendMessage("guildEffectMoney = " + group.getGuildEffectPrice());
				sender.sendMessage("guildTeleportDelay = " + group.getGuildTeleportDelay() + "s");
				sender.sendMessage("regionCreateMoney = " + group.getRegionCreateMoney());
				sender.sendMessage("regionPricePerBlock = " + group.getRegionPricePerBlock());
				sender.sendMessage("regionAutoSize = " + group.getRegionAutoSize());
				break;
			case "g":
			case "guild":
				Command.GUILD_ACCESS.execute(sender, StringUtils.parseArgs(args, 1));
				break;
			case "tr":
				TabUtils.refresh();
				break;
			default:
				Message.CHAT_UNKNOWNCMD.send(sender);
				break;
		}
	}
}
