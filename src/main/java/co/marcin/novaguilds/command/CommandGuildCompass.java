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
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		Player player = (Player)sender;
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
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

		return true;
	}
}
