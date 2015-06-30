package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaRegion;

public class CommandAdminGuildSetPoints implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetPoints(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/*
		* args:
		* 0 - points
		* */

		if(!sender.hasPermission("novaguilds.admin.guild.setpoints")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length == 0) { //no new name
			plugin.getMessageManager().sendUsageMessage(sender,"nga.guild.setpoints");
			return true;
		}

		String newName = args[0];

		if(newName.length() < plugin.getConfig().getInt("guild.settings.name.min")) { //too short name
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.name.tooshort");
			return true;
		}

		if(newName.length() > plugin.getConfig().getInt("guild.settings.name.max")) { //too long name
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.name.toolong");
			return true;
		}

		if(plugin.getGuildManager().exists(newName)) { //name exists
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.nameexists");
			return true;
		}

		//all passed
		if(guild.hasRegion()) {
			NovaRegion region = plugin.getRegionManager().getRegionByGuild(guild);
			region.setGuildName(newName);
			plugin.getRegionManager().saveRegion(region);
		}

		guild.setName(newName);
		plugin.getGuildManager().changeName(guild, newName);

		//update players
		//TODO: probably is not useful, does nothing
//		for(NovaPlayer nP : guild.getPlayers()) {
//			nP.setGuild(guild);
//		}

		plugin.getMessageManager().sendMessagesMsg(sender, "chat.admin.guild.setname.success");

		return true;
	}
}
