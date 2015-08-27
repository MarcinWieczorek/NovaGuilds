package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetName implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetName(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/*
		* args:
		* 0 - new name
		* */

		if(!sender.hasPermission("novaguilds.admin.guild.setname")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}
		
		if(args.length == 0) { //no new name
			Message.CHAT_ADMIN_GUILD_SET_NAME_ENTERNEWNAME.send(sender);
			return true;
		}

		String newName = args[0];
		
		if(newName.length() < Config.GUILD_SETTINGS_NAME_MIN.getInt()) { //too short name
			Message.CHAT_CREATEGUILD_NAME_TOOSHORT.send(sender);
			return true;
		}
		
		if(newName.length() > Config.GUILD_SETTINGS_NAME_MAX.getInt()) { //too long name
			Message.CHAT_CREATEGUILD_NAME_TOOLONG.send(sender);
			return true;
		}
		
		if(plugin.getGuildManager().exists(newName)) { //name exists
			Message.CHAT_CREATEGUILD_NAMEEXISTS.send(sender);
			return true;
		}

		//all passed
		if(guild.hasRegion()) {
			guild.getRegion().setGuildName(newName);
		}

		plugin.getGuildManager().changeName(guild, newName);
		plugin.getHologramManager().refreshTopHolograms();

		Message.CHAT_ADMIN_GUILD_SET_NAME_SUCCESS.send(sender);
		
		return true;
	}
}
