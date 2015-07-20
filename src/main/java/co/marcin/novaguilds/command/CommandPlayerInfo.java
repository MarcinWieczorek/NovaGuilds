package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandPlayerInfo implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandPlayerInfo(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.region.create")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		NovaPlayer nCPlayer;
		if(args.length == 0) {
			nCPlayer = plugin.getPlayerManager().getPlayer(sender);
		}
		else {
			nCPlayer = plugin.getPlayerManager().getPlayer(args[0]);

			if(nCPlayer == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return true;
			}
		}

		plugin.getPlayerManager().sendPlayerInfo(sender, nCPlayer);

		return true;
	}
}
