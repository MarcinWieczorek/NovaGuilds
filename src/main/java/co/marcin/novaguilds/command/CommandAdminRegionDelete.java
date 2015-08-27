package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminRegionDelete implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminRegionDelete(NovaGuilds pl) {
		plugin = pl;
	}

	/*
	* Removing region
	* args[0] - guild name
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.region.delete")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length == 0) {
			Message.CHAT_GUILD_ENTERNAME.send(sender);
			return true;
		}

		String guildname = args[0];

		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return true;
		}

		if(!guild.hasRegion()) {
			Message.CHAT_GUILD_HASNOREGION.send(sender);
			return true;
		}

		NovaRegion region = guild.getRegion();

		plugin.getRegionManager().removeRegion(region);
		guild.setRegion(null);
		Message.CHAT_ADMIN_REGION_DELETE_SUCCESS.send(sender);
		return true;
	}
}
