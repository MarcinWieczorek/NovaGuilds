package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildList implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminGuildList(NovaGuilds pl) {
		plugin = pl;
	}

	/*
	* List of guilds
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.list")) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(plugin.getGuildManager().getGuilds().size() == 0) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.noguilds");
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

		plugin.getMessageManager().sendMessagesMsg(sender, plugin.getMessageManager().getMessagesString("chat.admin.guild.list.header"));
		String rowformat = plugin.getMessageManager().getMessagesString("chat.admin.guild.list.item");

		int i=0;
		boolean display = false;

		if(size>perpage) {
			String pagemsg = plugin.getMessageManager().getMessagesString("chat.admin.guild.list.page.nonext");
			if(pages_number > page) {
				pagemsg = plugin.getMessageManager().getMessagesString("chat.admin.guild.list.page.hasnext");
			}

			pagemsg = StringUtils.replace(pagemsg, "{PAGE}", page + "");
			pagemsg = StringUtils.replace(pagemsg, "{NEXT}", page + 1 + "");
			pagemsg = StringUtils.replace(pagemsg, "{PAGES}", pages_number + "");
			plugin.getMessageManager().sendMessagesMsg(sender, StringUtils.fixColors(pagemsg));
		}

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			plugin.debug(i+"");
			plugin.debug(display+"");
			plugin.debug(i+1+">"+(page-1)*perpage);

			if((i+1>(page-1)*perpage || page==1) && !display) {
				display = true;
				i=0;
			}

			if(display) {
				HashMap<String,String> vars = new HashMap<>();
				vars.put("GUILDNAME", guild.getName());
				vars.put("PLAYERNAME", guild.getLeaderName());
				vars.put("TAG", guild.getTag());
				vars.put("PLAYERSCOUNT", guild.getPlayers().size()+"");

				String rowmsg = StringUtils.replaceMap(rowformat,vars);
				sender.sendMessage(StringUtils.fixColors(rowmsg));

				if(i+1 >= perpage) {
					plugin.debug("break");
					break;
				}
			}

			i++;
		}
		return true;
	}
}
