package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminHologram;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminRegion;
import org.bukkit.command.CommandSender;

public class CommandAdminRegionDelete implements Executor, ExecutorReversedAdminRegion {
	private final Commands command;
	private NovaRegion region;

	public CommandAdminRegionDelete(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void region(NovaRegion region) {
		this.region = region;
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

		plugin.getRegionManager().remove(region);
		Message.CHAT_ADMIN_REGION_DELETE_SUCCESS.send(sender);
	}
}
