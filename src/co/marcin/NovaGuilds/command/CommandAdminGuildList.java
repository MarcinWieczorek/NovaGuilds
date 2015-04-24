package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;
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
		if(!sender.hasPermission("NovaGuilds.admin.guild.list")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		int page = 1;
		if(args.length == 1) {
			if(Utils.isNumeric(args[0])) {
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
			String pagemsg = plugin.getMessagesString("chat.admin.guild.list.page");
			pagemsg = Utils.replace(pagemsg,"{PAGE}",page+"");
			pagemsg = Utils.replace(pagemsg,"{NEXT}",page+1+"");
			pagemsg = Utils.replace(pagemsg,"{PAGES}",pages_number+"");
			plugin.sendMessagesMsg(sender,Utils.fixColors(pagemsg));
		}

		for(Map.Entry<String, NovaGuild> row : plugin.getGuildManager().getGuilds()) {
			if(i>(page-1)*perpage || page==1) {
				display = true;
				i=0;
			}

			if(display) {
				String rowmsg = Utils.replace(rowformat, "{GUILDNAME}", row.getValue().getName());
				rowmsg = Utils.replace(rowmsg, "{PLAYERNAME}", row.getValue().getLeaderName());
				rowmsg = Utils.replace(rowmsg, "{TAG}", row.getValue().getTag());
				sender.sendMessage(Utils.fixColors(rowmsg));

				if(i >= perpage)
					break;
			}

			i++;
		}
		return true;
	}
}
