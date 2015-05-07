package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class CommandAdminGuildList implements CommandExecutor {
	public final NovaGuilds plugin;

	public CommandAdminGuildList(NovaGuilds pl) {
		plugin = pl;
	}

	/*
	* List of guilds
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.list")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		int page = 1;
		if(args.length == 1) {
			if(StringUtils.isNumeric(args[0])) {
				page = Integer.parseInt(args[0]);
			}
		}

		if(page < 1) {
			page = 1;
		}

		int perpage = 10;
		int size = plugin.getGuildManager().getGuilds().size();
		int pages_number = size / perpage;
		if(size % perpage > 0) {
			pages_number++;
		}

		plugin.sendMessagesMsg(sender, plugin.getMessagesString("chat.admin.guild.list.header"));
		String rowformat = plugin.getMessagesString("chat.admin.guild.list.item");

		int i=0;
		boolean display = false;

		if(size>perpage) {
			String pagemsg = plugin.getMessagesString("chat.admin.guild.list.page.nonext");
			if(pages_number > page) {
				pagemsg = plugin.getMessagesString("chat.admin.guild.list.page.hasnext");
			}

			pagemsg = StringUtils.replace(pagemsg, "{PAGE}", page + "");
			pagemsg = StringUtils.replace(pagemsg, "{NEXT}", page + 1 + "");
			pagemsg = StringUtils.replace(pagemsg, "{PAGES}", pages_number + "");
			plugin.sendMessagesMsg(sender, StringUtils.fixColors(pagemsg));
		}

		for(Map.Entry<String, NovaGuild> row : plugin.getGuildManager().getGuilds()) {
			plugin.info(i+"");
			plugin.info(display+"");
			plugin.info(i+1+">"+(page-1)*perpage);

			if((i+1>(page-1)*perpage || page==1) && display==false) {
				display = true;
				i=0;
			}

			if(display) {
				String rowmsg = StringUtils.replace(rowformat, "{GUILDNAME}", row.getValue().getName());
				rowmsg = StringUtils.replace(rowmsg, "{PLAYERNAME}", row.getValue().getLeaderName());
				rowmsg = StringUtils.replace(rowmsg, "{TAG}", row.getValue().getTag());
				sender.sendMessage(StringUtils.fixColors(rowmsg));

				if(i+1 >= perpage) {
					plugin.info("break");
					break;
				}
			}

			i++;
		}
		return true;
	}
}
