package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuildCompass implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildCompass(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.compass")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(!(sender instanceof Player)) {
			plugin.sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		Player player = plugin.senderToPlayer(sender);
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);

		if(!nPlayer.hasGuild()) {
			plugin.sendMessagesMsg(sender,"chat.notinguild");
			return true;
		}

		player.setCompassTarget(nPlayer.getGuild().getSpawnPoint());
		plugin.sendMessagesMsg(sender,"chat.guild.compasstarget");

		return true;
	}
}
