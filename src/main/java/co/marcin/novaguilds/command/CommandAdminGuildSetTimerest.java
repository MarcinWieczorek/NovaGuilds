package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
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
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		String timeString = "";
		if(args.length > 0) {
			timeString = StringUtils.join(args," ");
		}

		int iseconds = StringUtils.StringToSeconds(timeString);
		long seconds = Long.parseLong(iseconds+"");

		long newtimerest = NovaGuilds.systemSeconds() - (plugin.getConfigManager().getRaidTimeRest() - seconds);
		plugin.debug("new timerest = "+newtimerest);
		plugin.debug("add seconds = "+seconds);

		guild.setTimeRest(newtimerest);
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.timerest.set");

		return true;
	}
}
