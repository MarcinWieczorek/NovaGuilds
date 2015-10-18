package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildOpenInvitation implements Executor {
	private final Commands command;

	public CommandGuildOpenInvitation(Commands command) {
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

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		final boolean status = !nPlayer.getGuild().isOpenInvitation();
		nPlayer.getGuild().setOpenInvitation(status);

		Message.CHAT_GUILD_OPENINVITATION.vars(new HashMap<String, String>(){{put("STATUS", Message.getOnOff(status));}}).send(sender);
	}
}
