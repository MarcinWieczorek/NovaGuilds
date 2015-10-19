package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import org.bukkit.command.CommandSender;

public class CommandAdminGuildSetName implements Executor, ExecutorReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command;

	public CommandAdminGuildSetName(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}
		
		if(args.length == 0) { //no new name
			Message.CHAT_ADMIN_GUILD_SET_NAME_ENTERNEWNAME.send(sender);
			return;
		}

		String newName = args[0];
		
		if(newName.length() < Config.GUILD_SETTINGS_NAME_MIN.getInt()) { //too short name
			Message.CHAT_CREATEGUILD_NAME_TOOSHORT.send(sender);
			return;
		}
		
		if(newName.length() > Config.GUILD_SETTINGS_NAME_MAX.getInt()) { //too long name
			Message.CHAT_CREATEGUILD_NAME_TOOLONG.send(sender);
			return;
		}
		
		if(plugin.getGuildManager().exists(newName)) { //name exists
			Message.CHAT_CREATEGUILD_NAMEEXISTS.send(sender);
			return;
		}

		//all passed
		if(guild.hasRegion()) {
			guild.getRegion().setGuildName(newName);
		}

		plugin.getGuildManager().changeName(guild, newName);
		plugin.getHologramManager().refreshTopHolograms();

		Message.CHAT_ADMIN_GUILD_SET_NAME_SUCCESS.send(sender);
	}
}
