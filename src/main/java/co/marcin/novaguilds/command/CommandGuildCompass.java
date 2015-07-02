package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
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
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		Player player = (Player)sender;
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.notinguild");
			return true;
		}

		player.setCompassTarget(nPlayer.getGuild().getSpawnPoint());
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.compasstarget");

		return true;
	}
}
