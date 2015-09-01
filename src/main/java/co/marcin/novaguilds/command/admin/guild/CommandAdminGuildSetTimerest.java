package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetTimerest implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetTimerest(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.timerest")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		String timeString = "";
		if(args.length > 0) {
			timeString = StringUtils.join(args," ");
		}

		int iseconds = StringUtils.StringToSeconds(timeString);
		long seconds = Long.parseLong(iseconds+"");

		long newtimerest = NumberUtils.systemSeconds() - (Config.RAID_TIMEREST.getSeconds() - seconds);
		LoggerUtils.debug("new timerest = " + newtimerest);
		LoggerUtils.debug("add seconds = "+seconds);

		guild.setTimeRest(newtimerest);
		Message.CHAT_ADMIN_GUILD_TIMEREST_SET.send(sender);

		return true;
	}
}
