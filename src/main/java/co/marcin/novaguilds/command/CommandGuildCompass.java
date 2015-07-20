package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
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

		if(nPlayer.isCompassPointingGuild()) { //disable
			nPlayer.setCompassPointingGuild(false);
			player.setCompassTarget(player.getWorld().getSpawnLocation());
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.compasstarget.off");
		}
		else { //enable
			nPlayer.setCompassPointingGuild(true);
			player.setCompassTarget(nPlayer.getGuild().getSpawnPoint());
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.compasstarget.on");
		}

		return true;
	}
}
