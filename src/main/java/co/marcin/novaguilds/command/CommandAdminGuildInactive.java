package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CommandAdminGuildInactive implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminGuildInactive(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(plugin.getGuildManager().getGuilds().isEmpty()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.noguilds");
			return true;
		}

		int page = 1;
		if(args.length == 1) {
			if(NumberUtils.isNumeric(args[0])) {
				page = Integer.parseInt(args[0]);
			}
			else if(args[0].equalsIgnoreCase("update")) {
				if(!sender.hasPermission("novaguilds.admin.guild.inactive.update")) {
					plugin.getMessageManager().sendNoPermissionsMessage(sender);
					return true;
				}

				int count = 0;
				for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
					guild.updateInactiveTime();
					count++;
				}
				HashMap<String,String> vars = new HashMap<>();
				vars.put("COUNT",count+"");
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.inactive.updated",vars);
				return true;
			}
			else if(args[0].equalsIgnoreCase("clean")) {
				if(!sender.hasPermission("novaguilds.admin.guild.inactive.clean")) {
					plugin.getMessageManager().sendNoPermissionsMessage(sender);
					return true;
				}

				return true;
			}
		}

		//list
		if(!sender.hasPermission("novaguilds.admin.guild.inactive.list")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
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

		plugin.getMessageManager().sendMessagesMsg(sender, plugin.getMessageManager().getMessagesString("chat.admin.guild.inactive.list.header"));
		String rowformat = plugin.getMessageManager().getMessagesString("chat.admin.guild.inactive.list.item");

		int i = 0;
		boolean display = false;

		if(size > perpage) {
			String pagemsg = plugin.getMessageManager().getMessagesString("chat.admin.guild.list.page.nonext");
			if(pages_number > page) {
				pagemsg = plugin.getMessageManager().getMessagesString("chat.admin.guild.list.page.hasnext");
			}

			pagemsg = StringUtils.replace(pagemsg, "{PAGE}", page + "");
			pagemsg = StringUtils.replace(pagemsg, "{NEXT}", page + 1 + "");
			pagemsg = StringUtils.replace(pagemsg, "{PAGES}", pages_number + "");
			plugin.getMessageManager().sendMessagesMsg(sender, StringUtils.fixColors(pagemsg));
		}

		for(NovaGuild guild : plugin.getGuildManager().getMostInactiveGuilds()) {
			LoggerUtils.debug(i + "");
			LoggerUtils.debug(display + "");
			LoggerUtils.debug(i + 1 + ">" + (page - 1) * perpage);

			if((i + 1 > (page - 1) * perpage || page == 1) && !display) {
				display = true;
				i = 0;
			}

			if(!guild.getOnlinePlayers().isEmpty()) {
				guild.updateInactiveTime();
			}

			if(display) {
				String inactiveString = StringUtils.secondsToString(NumberUtils.systemSeconds() - guild.getInactiveTime(), TimeUnit.SECONDS);

				String agonow = plugin.getMessageManager().getMessagesString("chat.admin.guild.inactive.list.ago");
				if(inactiveString.isEmpty()) {
					agonow = plugin.getMessageManager().getMessagesString("chat.admin.guild.inactive.list.now");
				}

				LoggerUtils.debug("leadernull="+(guild.getLeader()==null));

				HashMap<String, String> vars = new HashMap<>();
				vars.put("GUILDNAME", guild.getName());
				vars.put("PLAYERNAME", guild.getLeader().getName());
				vars.put("TAG", guild.getTag());
				vars.put("PLAYERSCOUNT", guild.getPlayers().size() + "");
				vars.put("AGONOW",agonow);
				vars.put("INACTIVE", inactiveString);

				String rowmsg = StringUtils.replaceMap(rowformat, vars);
				sender.sendMessage(StringUtils.fixColors(rowmsg));

				if(i + 1 >= perpage) {
					LoggerUtils.debug("break");
					break;
				}
			}

			i++;
		}

		return true;
	}
}
