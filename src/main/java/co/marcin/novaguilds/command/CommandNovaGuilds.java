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
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.VersionUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandNovaGuilds extends AbstractCommandExecutor {
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
				sender.sendMessage("guildCreateMoney = " + group.getDouble(NovaGroup.Key.CREATE_MONEY));
				sender.sendMessage("guildHomeMoney = " + group.getDouble(NovaGroup.Key.HOME_MONEY));
				sender.sendMessage("guildJoinMoney = " + group.getDouble(NovaGroup.Key.JOIN_MONEY));
				sender.sendMessage("guildCreateItems = " + group.getItemStackList(NovaGroup.Key.CREATE_ITEMS).toString());
				sender.sendMessage("guildCreateSchematic = " + (group.getCreateSchematic() == null ? "no schematic" : group.getCreateSchematic().getName()));
				sender.sendMessage("guildHomeItems = " + group.getItemStackList(NovaGroup.Key.HOME_ITEMS).toString());
				sender.sendMessage("guildJoinItems = " + group.getItemStackList(NovaGroup.Key.JOIN_ITEMS).toString());
				sender.sendMessage("guildBuyLifeItems = " + group.getItemStackList(NovaGroup.Key.BUY_LIFE_ITEMS).toString());
				sender.sendMessage("guildBuySlotItems = " + group.getItemStackList(NovaGroup.Key.BUY_SLOT_ITEMS).toString());
				sender.sendMessage("guildBuyBannerItems = " + group.getItemStackList(NovaGroup.Key.BUY_BANNER_ITEMS).toString());
				sender.sendMessage("guildBuyLifeMoney = " + group.getDouble(NovaGroup.Key.BUY_LIFE_MONEY));
				sender.sendMessage("guildBuySlotMoney = " + group.getDouble(NovaGroup.Key.BUY_SLOT_MONEY));
				sender.sendMessage("guildBuyBannerMoney = " + group.getDouble(NovaGroup.Key.BUY_BANNER_MONEY));
				sender.sendMessage("guildEffectMoney = " + group.getDouble(NovaGroup.Key.EFFECT_MONEY));
				sender.sendMessage("guildTeleportDelay = " + group.getInt(NovaGroup.Key.HOME_DELAY) + "s");
				sender.sendMessage("regionCreateMoney = " + group.getDouble(NovaGroup.Key.REGION_CREATE_MONEY));
				sender.sendMessage("regionPricePerBlock = " + group.getDouble(NovaGroup.Key.REGION_PRICEPERBLOCK));
				sender.sendMessage("regionAutoSize = " + group.getInt(NovaGroup.Key.REGION_AUTOSIZE));
				break;
			case "g":
			case "guild":
				Command.GUILD_ACCESS.execute(sender, StringUtils.parseArgs(args, 1));
				break;
			case "tr":
				TabUtils.refresh();
				break;
			case "confirm":
				Command.CONFIRM.execute(sender, args);
				break;
			default:
				Message.CHAT_UNKNOWNCMD.send(sender);
				break;
		}
	}
}
