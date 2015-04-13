package co.marcin.NovaGuilds.Commands;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminRegionDelete implements CommandExecutor {
	public NovaGuilds plugin;

	public CommandAdminRegionDelete(NovaGuilds pl) {
		plugin = pl;
	}

	/*
	* Removing region
	* args[0] - guild name
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.region.delete")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length == 0) {
			plugin.sendMessagesMsg(sender,"chat.guild.entername");
			return true;
		}

		String guildname = args[0];

		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

		if(!(guild instanceof NovaGuild)) {
			plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}

		if(!guild.hasRegion()) {
			plugin.sendMessagesMsg(sender,"chat.guild.hasnoregion");
			return true;
		}

		NovaRegion region = plugin.getRegionManager().getRegionByGuild(guild);

		plugin.getRegionManager().removeRegion(region);
		guild.setRegion(null);
		plugin.getGuildManager().saveGuildLocal(guild);
		plugin.sendMessagesMsg(sender,"chat.admin.region.delete.success");
		return true;
	}
}
