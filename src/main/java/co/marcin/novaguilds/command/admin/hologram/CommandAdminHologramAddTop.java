package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminHologramAddTop implements Executor {
	private final Commands command;

	public CommandAdminHologramAddTop(Commands command) {
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

		NovaHologram hologram = plugin.getHologramManager().addTopHologram(((Player) sender).getLocation());

		Map<String, String> vars = new HashMap<>();
		vars.put("NAME", hologram.getName());

		Message.CHAT_ADMIN_HOLOGRAM_ADD_SUCCESS.vars(vars).send(sender);
	}
}
