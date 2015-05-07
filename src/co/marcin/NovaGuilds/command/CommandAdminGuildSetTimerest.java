package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class CommandAdminGuildSetTimerest implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminGuildSetTimerest(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.timerest")) {
			plugin.sendMessagesMsg(sender, "chat.nopermissions");
			return true;
		}

		if(args.length == 0) {
			plugin.sendMessagesMsg(sender,"chat.guild.entername");
			return true;
		}

		String guildname = args[0];
		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);

		if(guild == null) {
			plugin.sendMessagesMsg(sender,"chat.guild.couldnotfind");
			return true;
		}

		String timeString = "";
		if(args.length > 1) {
			String[] timeargs = StringUtils.parseArgs(args,1);
			timeString = StringUtils.join(timeargs," ");
		}

		int iseconds = StringUtils.StringToSeconds(timeString);
		long seconds = Long.parseLong(iseconds+"");

		long newtimerest = plugin.systemSeconds() - (plugin.timeRest - seconds);

		guild.setTimeRest(newtimerest);
		plugin.sendMessagesMsg(sender,"chat.admin.guild.timerest.set");

		return true;
	}
}
