package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandAdminGuild implements Executor {
	private final Commands command;

	public static final Map<String, Commands> commandsMap = new HashMap<String, Commands>(){{
		put("tp", Commands.ADMIN_GUILD_TELEPORT);
		put("teleport", Commands.ADMIN_GUILD_TELEPORT);
		put("abandon", Commands.ADMIN_GUILD_ABANDON);

		put("setname", Commands.ADMIN_GUILD_SET_NAME);
		put("name", Commands.ADMIN_GUILD_SET_NAME);

		put("settag", Commands.ADMIN_GUILD_SET_TAG);
		put("tag", Commands.ADMIN_GUILD_SET_TAG);

		put("setpoints", Commands.ADMIN_GUILD_SET_POINTS);
		put("points", Commands.ADMIN_GUILD_SET_POINTS);

		put("setslots", Commands.ADMIN_GUILD_SET_SLOTS);
		put("slots", Commands.ADMIN_GUILD_SET_SLOTS);

		put("promote", Commands.ADMIN_GUILD_SET_LEADER);
		put("leader", Commands.ADMIN_GUILD_SET_LEADER);
		put("setleader", Commands.ADMIN_GUILD_SET_LEADER);

		put("invite", Commands.ADMIN_GUILD_INVITE);
		put("pay", Commands.ADMIN_GUILD_BANK_PAY);
		put("withdraw", Commands.ADMIN_GUILD_BANK_WITHDRAW);
		put("timerest", Commands.ADMIN_GUILD_SET_TIMEREST);
		put("liveregentime", Commands.ADMIN_GUILD_SET_LIVEREGENERATIONTIME);
		put("lives", Commands.ADMIN_GUILD_SET_LIVES);
		put("purge", Commands.ADMIN_GUILD_PURGE);
		put("list", Commands.ADMIN_GUILD_LIST);
		put("inactive", Commands.ADMIN_GUILD_INACTIVE);
		put("kick", Commands.ADMIN_GUILD_KICK);
	}};

	private static final List<Commands> noGuildCommands = new ArrayList<Commands>() {{
		add(Commands.ADMIN_GUILD_LIST);
		add(Commands.ADMIN_GUILD_KICK);
		add(Commands.ADMIN_GUILD_SET_LEADER);
		add(Commands.ADMIN_GUILD_PURGE);
		add(Commands.ADMIN_GUILD_INACTIVE);
	}};

	public CommandAdminGuild(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		//command list
		if(args.length == 0) {
			Message.CHAT_COMMANDS_ADMIN_GUILD_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_GUILD_ITEMS.send(sender);
			return;
		}

		NovaGuild guild = null;
		String subCmd = args[args.length == 1 || noGuildCommands.contains(commandsMap.get(args[0])) ? 0: 1];
		Commands subCommand = commandsMap.get(subCmd.toLowerCase());

		if(!noGuildCommands.contains(subCommand) && (args.length > 1 || !noGuildCommands.contains(subCommand))) {
			guild = plugin.getGuildManager().getGuildFind(args[0]);

			if(guild == null) {
				Message.CHAT_GUILD_COULDNOTFIND.send(sender);
				return;
			}
		}

		Executor executor = plugin.getCommandManager().getExecutor(subCommand);

		if(executor == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		int subArgsCut = 1;

		if(executor instanceof ExecutorReversedAdminGuild) {
			((ExecutorReversedAdminGuild) executor).guild(guild);
			subArgsCut = 2;
		}

		executor.execute(sender, StringUtils.parseArgs(args, subArgsCut));
	}
}
