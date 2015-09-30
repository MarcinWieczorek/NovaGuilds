package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminHologramList implements Executor {
	private final Commands command;

	public CommandAdminHologramList(Commands command) {
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

		Message.CHAT_ADMIN_HOLOGRAM_LIST_HEADER.send(sender);
		Map<String, String> vars = new HashMap<>();
		for(NovaHologram hologram : plugin.getHologramManager().getHolograms()) {
			vars.clear();
			vars.put("NAME", hologram.getName());
			vars.put("X", String.valueOf(hologram.getLocation().getBlockX()));
			vars.put("Y", String.valueOf(hologram.getLocation().getBlockY()));
			vars.put("Z", String.valueOf(hologram.getLocation().getBlockZ()));

			Message.CHAT_ADMIN_HOLOGRAM_LIST_ITEM.vars(vars).prefix(false).send(sender);
		}
	}
}
