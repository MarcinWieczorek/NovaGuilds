package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandGuildPvpToggle implements Executor {
	private final Commands command;

	public CommandGuildPvpToggle(Commands command) {
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

		HashMap<String,String> vars = new HashMap<>();
		nPlayer.getGuild().setFriendlyPvp(!nPlayer.getGuild().getFriendlyPvp());
		vars.put("FPVP", Message.getOnOff(nPlayer.getGuild().getFriendlyPvp()));
		Message.CHAT_GUILD_FPVPTOGGLED.vars(vars).send(sender);
	}
}
