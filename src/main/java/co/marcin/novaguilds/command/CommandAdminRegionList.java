package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminRegionList implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminRegionList(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.region.list")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
		}

		Message.CHAT_REGION_LIST_HEADER.send(sender);
//		HashMap<String,String> vars = new HashMap<>();
//		for(Entry<String, NovaRegion> r : plugin.getRegionManager().getRegions()) {
//			NovaRegion region = r.getValue();
//			vars.put("GUILDNAME",region.getGuildName());
//			vars.put("X",region.getCorner(0).getBlockX()+"");
//			vars.put("Z",region.getCorner(0).getBlockZ()+"");
//			plugin.getMessageManager().sendMessagesMsg(sender,"chat.region.list.item", vars);
//			vars.clear();
//		}

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
		return true;
	}
}
