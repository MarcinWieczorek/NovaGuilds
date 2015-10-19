package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildSetTag implements Executor, ExecutorReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command;

	public CommandAdminGuildSetTag(Commands command) {
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

		if(args.length==0) {
			Message.CHAT_GUILD_ENTERTAG.send(sender);
			return;
		}

		final String newtag = args[0];

		if(plugin.getGuildManager().getGuildFind(newtag) != null) {
			Message.CHAT_CREATEGUILD_TAGEXISTS.send(sender);
			return;
		}

		//all passed
		guild.setTag(newtag);

		plugin.tagUtils.refreshAll();

		Message.CHAT_ADMIN_GUILD_SET_TAG.vars(new HashMap<String, String>(){{put("TAG", newtag);}}).send(sender);
	}
}
