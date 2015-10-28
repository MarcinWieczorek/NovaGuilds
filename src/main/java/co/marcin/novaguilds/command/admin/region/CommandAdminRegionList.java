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

package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminRegion;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminRegionList implements Executor, ExecutorReversedAdminRegion {
	private final Commands command;
	private NovaRegion region;

	public CommandAdminRegionList(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void region(NovaRegion region) {
		this.region = region;
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

		Message.CHAT_REGION_LIST_HEADER.send(sender);

		int perpage = 10;
		int size = plugin.getRegionManager().getRegions().size();
		int pages_number = size / perpage;
		if(size % perpage > 0) {
			pages_number++;
		}

		//pages
		int page = 1;
		if(args.length == 1) {
			if(NumberUtils.isNumeric(args[0])) {
				page = Integer.parseInt(args[0]);
			}
		}

		if(page < 1) {
			page = 1;
		}

		String rowformat = Message.CHAT_REGION_LIST_ITEM.get();
		int i=0;
		boolean display = false;
		HashMap<String,String> vars = new HashMap<>();

		if(size>perpage) {
			vars.put("PAGE", String.valueOf(page));
			vars.put("NEXT", String.valueOf(page+1));
			vars.put("PAGES", String.valueOf(pages_number));

			if(pages_number > page) {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_HASNEXT.vars(vars).send(sender);
			}
			else {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_NONEXT.vars(vars).send(sender);
			}
		}

		for(NovaRegion region : plugin.getRegionManager().getRegions()) {
			vars.clear();
			LoggerUtils.debug(i + "");
			LoggerUtils.debug(display+"");
			LoggerUtils.debug(i+1+">"+(page-1)*perpage);

			if((i+1>(page-1)*perpage || page==1) && !display) {
				display = true;
				i=0;
			}

			if(display) {
				vars.put("GUILDNAME", region.getGuild().getName());
				vars.put("X", region.getCorner(0).getBlockX()+"");
				vars.put("Z", region.getCorner(0).getBlockZ()+"");

				String rowmsg = StringUtils.replaceMap(rowformat,vars);
				sender.sendMessage(StringUtils.fixColors(rowmsg));

				if(i+1 >= perpage) {
					LoggerUtils.debug("break");
					break;
				}
			}

			i++;
		}
	}
}
