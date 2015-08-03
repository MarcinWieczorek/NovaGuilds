package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuildCompass implements Executor {
	private final Commands command;

	public CommandGuildCompass(Commands command) {
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

		Player player = (Player)sender;
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(nPlayer.isCompassPointingGuild()) { //disable
			nPlayer.setCompassPointingGuild(false);
			player.setCompassTarget(player.getWorld().getSpawnLocation());
			Message.CHAT_GUILD_COMPASSTARGET_OFF.send(sender);
		}
		else { //enable
			nPlayer.setCompassPointingGuild(true);
			player.setCompassTarget(nPlayer.getGuild().getSpawnPoint());
			Message.CHAT_GUILD_COMPASSTARGET_ON.send(sender);
		}
	}
}
