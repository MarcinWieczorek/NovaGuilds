package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminHologram;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandAdminHologram implements Executor {
	private final Commands command;

	private static final List<String> noHologramCommands = new ArrayList<String>() {{
		add("list");
		add("add");
		add("addtop");
	}};

	private static final HashMap<String, Commands> commandsMap = new HashMap<String, Commands>(){{
		put("list", Commands.ADMIN_HOLOGRAM_LIST);
		put("ls", Commands.ADMIN_HOLOGRAM_LIST);

		put("tp", Commands.ADMIN_HOLOGRAM_TELEPORT);
		put("teleport", Commands.ADMIN_HOLOGRAM_TELEPORT);

		put("add", Commands.ADMIN_HOLOGRAM_ADD);
		put("addtop", Commands.ADMIN_HOLOGRAM_ADDTOP);

		put("del", Commands.ADMIN_HOLOGRAM_DELETE);
		put("delete", Commands.ADMIN_HOLOGRAM_DELETE);

		put("tphere", Commands.ADMIN_HOLOGRAM_TELEPORT_HERE);
		put("teleporthere", Commands.ADMIN_HOLOGRAM_TELEPORT_HERE);
		put("movehere", Commands.ADMIN_HOLOGRAM_TELEPORT_HERE);
	}};

	public CommandAdminHologram(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
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

		if(args.length==0 || (args.length < 2 && !noHologramCommands.contains(args[0]))) {
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_HEADER.send(sender);
			Message.CHAT_COMMANDS_ADMIN_HOLOGRAM_ITEMS.send(sender);
			return;
		}

		Commands commands = commandsMap.get(args[noHologramCommands.contains(args[0]) ? 0 : 1].toLowerCase());

		if(commands == null) {
			Message.CHAT_UNKNOWNCMD.send(sender);
			return;
		}

		Executor executor = plugin.getCommandManager().getExecutor(commands);

		if(executor instanceof ExecutorReversedAdminHologram) {
			NovaHologram hologram = plugin.getHologramManager().getHologram(args[0]);

			if(hologram == null || hologram.isDeleted()) {
				Message.CHAT_ADMIN_HOLOGRAM_NOTFOUND.send(sender);
				return;
			}

			((ExecutorReversedAdminHologram) executor).hologram(hologram);
		}

		executor.execute(sender, StringUtils.parseArgs(args, 2));
	}
}
