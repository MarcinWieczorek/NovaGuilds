package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaRegion;
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
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length == 0) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.entername");
			return true;
		}

		String guildname = args[0];

		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

		if(guild == null) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}

		if(!guild.hasRegion()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.hasnoregion");
			return true;
		}

		NovaRegion region = plugin.getRegionManager().getRegionByGuild(guild);

		plugin.getRegionManager().removeRegion(region);
		guild.setRegion(null);
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.region.delete.success");
		return true;
	}
}
