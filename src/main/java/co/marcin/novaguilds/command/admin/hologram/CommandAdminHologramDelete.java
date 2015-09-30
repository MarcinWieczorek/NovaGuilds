package co.marcin.novaguilds.command.admin.hologram;

import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

public class CommandAdminHologramDelete implements Executor {
	private final Commands command;

	public CommandAdminHologramDelete(Commands command) {
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

		if(args.length != 1) {
			Message.CHAT_ADMIN_HOLOGRAM_DELETE_ENTERNAME.send(sender);
			return;
		}

		NovaHologram hologram = plugin.getHologramManager().getHologram(args[0]);

		if(hologram == null) {
			Message.CHAT_ADMIN_HOLOGRAM_DELETE_INVALIDNAME.send(sender);
			return;
		}


	}
}

