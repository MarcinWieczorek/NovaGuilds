package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetLiveRegenerationTime implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetLiveRegenerationTime(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.liveregenerationtime")) {
			plugin.sendMessagesMsg(sender, "chat.nopermissions");
			return true;
		}

		String timeString = "";
		if(args.length > 1) {
			timeString = StringUtils.join(args," ");
		}

		int iseconds = StringUtils.StringToSeconds(timeString);
		long seconds = Long.parseLong(iseconds+"");

		long newregentime = NovaGuilds.systemSeconds() + (seconds - plugin.liveRegenerationTime);
		plugin.debug("newregentime: "+newregentime);

		guild.setLostLiveTime(newregentime);
		plugin.sendMessagesMsg(sender,"chat.admin.guild.timerest.set");

		return true;
	}
}
